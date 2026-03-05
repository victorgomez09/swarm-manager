package com.vira.paas.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vira.paas.models.ApplicationModel;
import com.vira.paas.models.EnvVariableModel;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SwarmService {

    private final String NETWORK_NAME = "paas-public";
    private final String VOLUME_PREFIX = "paas_vol_";

    @PostConstruct
    public void init() {
        ensureNetworkExists();
    }

    private void ensureNetworkExists() {
        try {
            Process check = Runtime.getRuntime().exec("docker network inspect " + NETWORK_NAME);
            if (check.waitFor() != 0) {
                log.info("Creando red overlay: {}", NETWORK_NAME);
                execute(List.of("docker", "network", "create", "--driver", "overlay", "--attachable", NETWORK_NAME));
            }
        } catch (Exception e) {
            log.error("Error al inicializar la red: {}", e.getMessage());
        }
    }

    public void deployOrUpdate(ApplicationModel app) {
        String serviceName = "paas-" + app.getName();
        String volumeName = VOLUME_PREFIX + app.getName();

        try {
            ensureVolumeExists(volumeName);

            if (serviceExists(serviceName)) {
                updateService(app, serviceName, volumeName);
            } else {
                createService(app, serviceName, volumeName);
            }
        } catch (Exception e) {
            log.error("Fallo en el despliegue: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String getServiceStatus(String appName) {
        String serviceName = "paas-" + appName;
        try {
            // Consultamos el estado de las tareas del servicio
            ProcessBuilder pb = new ProcessBuilder(
                    "docker", "service", "ps", serviceName,
                    "--format", "{{.CurrentState}}",
                    "--no-trunc");
            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String lastState = reader.readLine();
                return (lastState != null) ? lastState : "UNKNOWN";
            }
        } catch (Exception e) {
            return "SERVICE_NOT_FOUND";
        }
    }

    private void ensureVolumeExists(String volumeName) throws Exception {
        Process check = Runtime.getRuntime().exec("docker volume inspect " + volumeName);
        if (check.waitFor() != 0) {
            log.info("Creando volumen persistente: {}", volumeName);
            execute(List.of("docker", "volume", "create", volumeName));
        }
    }

    private void createService(ApplicationModel app, String serviceName, String volumeName) throws Exception {
        List<String> cmd = new ArrayList<>(List.of(
                "docker", "service", "create",
                "--name", serviceName,
                "--network", NETWORK_NAME,
                "--replicas", String.valueOf(app.getReplicas()),
                "--limit-cpu", String.valueOf(app.getCpuLimit()),
                "--limit-memory", app.getMemLimitMb() + "mb",
                "--mount", "type=volume,source=" + volumeName + ",target=" + app.getStoragePath(),
                "--health-cmd", "curl -f http://localhost:" + app.getExposedPort() + "/ || exit 1",
                "--health-interval", "30s",
                "--health-retries", "3",
                "--health-timeout", "5s",
                "--health-start-period", "60s",
                "--label", "traefik.enable=true",
                "--label", "traefik.http.routers." + app.getName() + ".rule=Host(`" + app.getName() + ".local`) bridge",
                "--label",
                "traefik.http.services." + app.getName() + ".loadbalancer.server.port=" + app.getExposedPort(),
                app.getLastImageTag()));

        if (app.getEnvVariables() != null) {
            for (EnvVariableModel var : app.getEnvVariables()) {
                cmd.add("--env");
                cmd.add(var.getEnvKey() + "=" + var.getEnvValue());
            }
        }

        cmd.add(app.getLastImageTag());
        execute(cmd);
    }

    private void updateService(ApplicationModel app, String serviceName, String volumeName) throws Exception {
        List<String> cmd = new ArrayList<>(List.of(
                "docker", "service", "update",
                "--image", app.getLastImageTag(),
                "--replicas", String.valueOf(app.getReplicas()),
                "--limit-cpu", String.valueOf(app.getCpuLimit()),
                "--limit-memory", app.getMemLimitMb() + "mb",
                // Estrategia de actualización:
                "--update-parallelism", "1", // Actualiza de 1 en 1
                "--update-delay", "10s", // Espera 10s entre cada contenedor
                "--update-failure-action", "rollback", // Si falla, vuelve a la versión anterior automáticamente
                "--update-order", "start-first", // Levanta el nuevo antes de matar el viejo (Zero Downtime)
                // Actualizamos el mount por si cambió la ruta storagePath
                "--mount-add", "type=volume,source=" + volumeName + ",target=" + app.getStoragePath(),
                serviceName));
        
        if (app.getEnvVariables() != null) {
            for (EnvVariableModel var : app.getEnvVariables()) {
                cmd.add("--env-add");
                cmd.add(var.getEnvKey() + "=" + var.getEnvValue());
            }
        }

        cmd.add(serviceName);
        execute(cmd);
    }

    private boolean serviceExists(String name) {
        try {
            return Runtime.getRuntime().exec("docker service inspect " + name).waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void execute(List<String> command) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String l;
            while ((l = r.readLine()) != null)
                log.debug("[DOCKER] {}", l);
        }
        if (p.waitFor() != 0)
            throw new RuntimeException("Comando falló: " + String.join(" ", command));
    }
}