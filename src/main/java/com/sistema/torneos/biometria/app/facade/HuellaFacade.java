package com.sistema.torneos.biometria.app.facade;

import com.sistema.torneos.biometria.app.web.model.response.HuellaResponse;
import com.sistema.torneos.biometria.app.web.model.response.LectorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class HuellaFacade {

    @Value("${biometria.capture.jar}")
    private String biometriaJar;

    @Value("${biometria.sdk.path}")
    private String sdkPath;

    private volatile boolean escuchando = false;
    private volatile Process procesoCaptura;
    private volatile String ultimoTemplateBase64;
    private volatile String ultimoFeatureBase64;
    private volatile String ultimoDedo;
    private volatile String ultimoMensaje = "No se ha capturado ninguna huella.";

    public synchronized LectorResponse escucharLector() {

        return iniciarCapturaExterna(false);
    }

    public synchronized LectorResponse escucharLectorVerificacion() {

        return iniciarCapturaExterna(true);
    }

    private LectorResponse iniciarCapturaExterna(boolean verificacion) {

        if (escuchando) {
            return LectorResponse.builder()
                    .success(true)
                    .mensaje("Ya hay una captura en proceso.")
                    .escuchando(true)
                    .build();
        }

        escuchando = true;
        ultimoTemplateBase64 = null;
        ultimoFeatureBase64 = null;
        ultimoDedo = null;
        ultimoMensaje = verificacion
            ? "Abriendo capturador biométrico de verificación..."
            : "Abriendo capturador biométrico...";

        new Thread(() -> {
            try {
            ProcessBuilder processBuilder;

            if (verificacion) {
                processBuilder = new ProcessBuilder(
                    "java",
                    "-Djava.awt.headless=false",
                    "-Djava.library.path=" + sdkPath,
                    "-jar",
                    biometriaJar,
                    "verify"
                );
            } else {
                processBuilder = new ProcessBuilder(
                    "java",
                    "-Djava.awt.headless=false",
                    "-Djava.library.path=" + sdkPath,
                    "-jar",
                    biometriaJar
                );
            }

                processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            procesoCaptura = process;

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {

                    String line;

                    while ((line = reader.readLine()) != null) {
                        System.out.println("[BIOMETRIA] " + line);

                        if (line.startsWith("OK|")) {
                            String[] partes = line.split("\\|", 3);

                            if (partes.length == 3) {
                                ultimoDedo = partes[1];

                                if (verificacion) {
                                    ultimoFeatureBase64 = partes[2];
                                    ultimoMensaje = "Huella de verificación capturada correctamente.";
                                } else {
                                    ultimoTemplateBase64 = partes[2];
                                    ultimoMensaje = "Huella capturada correctamente.";
                                }
                            }
                        }

                        if (line.startsWith("ERROR|")) {
                            ultimoMensaje = line.substring("ERROR|".length());
                        }
                    }
                }

                int exitCode = process.waitFor();

                if (exitCode != 0
                        && ultimoTemplateBase64 == null
                        && ultimoFeatureBase64 == null) {
                    ultimoMensaje = "El capturador finalizó con error.";
                }

            } catch (Exception e) {
                ultimoMensaje = "Error ejecutando capturador biométrico: " + e.getMessage();
                e.printStackTrace();

            } finally {
                procesoCaptura = null;
                escuchando = false;
            }
        }).start();

        return LectorResponse.builder()
                .success(true)
                .mensaje(verificacion
                        ? "Capturador biométrico de verificación abierto. Coloca el dedo en el lector."
                        : "Capturador biométrico abierto. Coloca el dedo en el lector.")
                .escuchando(true)
                .build();
    }

    public HuellaResponse obtenerHuella() {

        if (ultimoTemplateBase64 == null || ultimoTemplateBase64.isBlank()) {
            return HuellaResponse.builder()
                    .success(false)
                    .mensaje(ultimoMensaje)
                    .dedo(null)
                    .templateBase64(null)
                    .build();
        }

        return HuellaResponse.builder()
                .success(true)
                .mensaje("Huella obtenida correctamente.")
                .dedo(ultimoDedo)
                .templateBase64(ultimoTemplateBase64)
                .build();
    }

    public synchronized HuellaResponse obtenerHuellaVerificacion() {

        if (ultimoFeatureBase64 == null || ultimoFeatureBase64.isBlank()) {
            return HuellaResponse.builder()
                    .success(false)
                    .mensaje(ultimoMensaje)
                    .dedo(null)
                    .templateBase64(null)
                    .build();
        }

        String featureActual = ultimoFeatureBase64;
        String dedoActual = ultimoDedo;

        // Consumimos la captura para evitar procesar la misma huella varias veces.
        ultimoFeatureBase64 = null;
        ultimoDedo = null;
        ultimoMensaje = "Huella entregada. Inicia una nueva escucha para otra verificación.";

        System.out.println("[HUELLA][VERIFICACION] Huella entregada al cliente para identificacion.");

        return HuellaResponse.builder()
                .success(true)
                .mensaje("Huella de verificación obtenida correctamente.")
                .dedo(dedoActual)
                .templateBase64(featureActual)
                .build();
    }

    public synchronized LectorResponse detenerLector() {

        System.out.println("[HUELLA][VERIFICACION] Solicitud para detener lector.");

        if (procesoCaptura != null && procesoCaptura.isAlive()) {
            procesoCaptura.destroy();
        }

        procesoCaptura = null;

        escuchando = false;
        ultimoMensaje = "Captura detenida.";

        return LectorResponse.builder()
                .success(true)
                .mensaje(ultimoMensaje)
                .escuchando(false)
                .build();
    }
}
