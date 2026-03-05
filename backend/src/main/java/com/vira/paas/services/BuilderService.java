package com.vira.paas.services;

import com.vira.paas.enums.ApplicationStatusEnum;
import com.vira.paas.models.ApplicationModel;
import com.vira.paas.repositories.ApplicationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BuilderService {

    private final ApplicationRepository repository;
    private final SwarmService swarmService;
    private final String BASE_WORK_DIR = "/tmp/paas-builds/";

    @Async("taskExecutor")
    public void processBuild(ApplicationModel app) {
        Path workDir = Path.of(BASE_WORK_DIR, app.getId().toString());
        String imageName = "paas/" + app.getName().toLowerCase() + ":latest";

        try {
            updateStatus(app, ApplicationStatusEnum.BUILDING);
            Files.createDirectories(workDir);

            // Clonar rama específica de forma segura
            executeCommand(
                    List.of("git", "clone", "--branch", app.getGitBranch(), "--depth", "1", app.getGitUrl(), "."),
                    workDir.toFile());

            // Build con Nixpacks
            log.info("Iniciando Nixpacks en {}", app.getBuildPath());
            executeCommand(List.of("nixpacks", "build", app.getBuildPath(), "--name", imageName), workDir.toFile());

            app.setLastImageTag(imageName);
            updateStatus(app, ApplicationStatusEnum.READY);

            // DESPLIEGUE AUTOMÁTICO
            swarmService.deployOrUpdate(app);

        } catch (Exception e) {
            log.error("Error en pipeline: {}", e.getMessage());
            updateStatus(app, ApplicationStatusEnum.FAILED);
        } finally {
            FileSystemUtils.deleteRecursively(workDir.toFile());
        }
    }

    private void updateStatus(ApplicationModel app, ApplicationStatusEnum status) {
        app.setStatus(status);
        repository.save(app);
    }

    private void executeCommand(List<String> command, File directory) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(directory);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode != 0)
            throw new RuntimeException("Fallo: " + String.join(" ", command));
    }
}