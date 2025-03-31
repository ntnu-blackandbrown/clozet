package stud.ntnu.no.backend.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PapercutStarter implements InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(PapercutStarter.class);
    private Process papercutProcess;

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            startPapercut();
            log.info("Papercut SMTP server started on port 2525");
        } catch (Exception e) {
            log.error("Failed to start Papercut SMTP server", e);
            log.warn("Email sending will not work in development. Install Papercut manually to fix this issue.");
        }
    }

    private void startPapercut() throws IOException {
        // Liste over mulige steder Papercut kan være installert
        List<Path> possiblePaths = new ArrayList<>();

        String userHome = System.getProperty("user.home");
        String programFiles = System.getenv("ProgramFiles");
        String programFilesX86 = System.getenv("ProgramFiles(x86)");

        // Legg til alle mulige stier
        possiblePaths.add(Paths.get(userHome, "Papercut", "Papercut.exe"));
        possiblePaths.add(Paths.get(programFiles, "Papercut", "Papercut.exe"));
        possiblePaths.add(Paths.get(programFilesX86, "Papercut", "Papercut.exe"));
        possiblePaths.add(Paths.get("C:", "Program Files", "Papercut", "Papercut.exe"));
        possiblePaths.add(Paths.get("C:", "Program Files (x86)", "Papercut", "Papercut.exe"));

        // Søk etter kjørbar fil på disk basert på filnavn
        try {
            Path papercut = Files.find(Paths.get("C:\\"), 5,
                    (path, attr) -> path.getFileName().toString().equals("Papercut.exe"))
                .findFirst()
                .orElse(null);

            if (papercut != null) {
                possiblePaths.add(papercut);
            }
        } catch (Exception e) {
            log.warn("Error searching for Papercut.exe: {}", e.getMessage());
        }

        // Sjekk alle stier
        for (Path path : possiblePaths) {
            if (Files.exists(path)) {
                log.info("Found Papercut at: {}", path);
                ProcessBuilder processBuilder = new ProcessBuilder(
                    path.toString(),
                    "--port=2525",
                    "--ip=127.0.0.1"
                );
                processBuilder.redirectErrorStream(true);
                papercutProcess = processBuilder.start();
                return;
            }
        }

        log.warn("Papercut executable not found in any standard location");
        log.warn("Please install Papercut from https://github.com/ChangemakerStudios/Papercut-SMTP");
    }

    @Override
    public void destroy() {
        if (papercutProcess != null && papercutProcess.isAlive()) {
            papercutProcess.destroy();
            log.info("Papercut SMTP server stopped");
        }
    }
} 