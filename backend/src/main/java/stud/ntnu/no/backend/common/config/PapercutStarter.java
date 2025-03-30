package stud.ntnu.no.backend.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        String userHome = System.getProperty("user.home");
        Path papercutPath = Paths.get(userHome, "Papercut", "Papercut.exe");

        if (papercutPath.toFile().exists()) {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    papercutPath.toString(),
                    "--port=2525",
                    "--ip=127.0.0.1"
            );
            processBuilder.redirectErrorStream(true);
            papercutProcess = processBuilder.start();
        } else {
            log.warn("Papercut executable not found at: {}", papercutPath);
            log.warn("Please install Papercut from https://github.com/ChangemakerStudios/Papercut-SMTP");
        }
    }

    @Override
    public void destroy() {
        if (papercutProcess != null && papercutProcess.isAlive()) {
            papercutProcess.destroy();
            log.info("Papercut SMTP server stopped");
        }
    }
} 