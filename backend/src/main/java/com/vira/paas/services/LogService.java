package com.vira.paas.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class LogService {

    // Pool de hilos para manejar múltiples conexiones de logs simultáneas
    private final ExecutorService logExecutor = Executors.newCachedThreadPool();

    public SseEmitter streamServiceLogs(String appName) {
        String serviceName = "paas-" + appName;
        // Timeout de 30 minutos para la conexión de logs
        SseEmitter emitter = new SseEmitter(1800000L);

        logExecutor.execute(() -> {
            ProcessBuilder pb = new ProcessBuilder(
                    "docker", "service", "logs", "-f", "--tail", "100", serviceName);
            pb.redirectErrorStream(true);

            try {
                Process process = pb.start();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Enviamos la línea al cliente SSE
                        emitter.send(SseEmitter.event()
                                .name("log-event")
                                .data(line));
                    }
                }
                process.waitFor();
            } catch (Exception e) {
                emitter.completeWithError(e);
            } finally {
                emitter.complete();
            }
        });

        return emitter;
    }
}