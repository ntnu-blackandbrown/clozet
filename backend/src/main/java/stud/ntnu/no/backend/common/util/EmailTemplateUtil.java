package stud.ntnu.no.backend.common.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

/**
 * Utility class for loading and processing email templates.
 *
 * <p>Provides methods to load HTML templates, inject CSS styles, and replace placeholders with
 * actual values.
 */
public class EmailTemplateUtil {

  private static final String TEMPLATE_DIR = "templates/email/";
  private static final String CSS_PATH = TEMPLATE_DIR + "css/email-styles.css";
  private static String cachedCss = null;

  /**
   * Loads an email template from the resources directory.
   *
   * @param templateName the filename without path or extension (e.g., "verification" for
   *     "templates/email/verification.html")
   * @return the template content as a string
   * @throws IOException if the template cannot be read
   */
  public static String loadTemplate(String templateName) throws IOException {
    Resource resource = new ClassPathResource(TEMPLATE_DIR + templateName + ".html");
    try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
      String template = FileCopyUtils.copyToString(reader);
      // Replace the style tag with the common CSS
      return injectCss(template);
    }
  }

  /**
   * Loads CSS style and injects it into the template.
   *
   * @param template the HTML template
   * @return the template with injected CSS
   * @throws IOException if the CSS file cannot be read
   */
  private static String injectCss(String template) throws IOException {
    if (cachedCss == null) {
      Resource cssResource = new ClassPathResource(CSS_PATH);
      try (Reader reader =
          new InputStreamReader(cssResource.getInputStream(), StandardCharsets.UTF_8)) {
        cachedCss = FileCopyUtils.copyToString(reader);
      }
    }

    // Replace the style tag content with our common CSS
    return template.replaceFirst(
        "<style>[\\s\\S]*?</style>", "<style>\n" + cachedCss + "\n</style>");
  }

  /**
   * Processes a template by replacing placeholders with values.
   *
   * @param template the template string
   * @param variables a map of placeholder names and their values
   * @return the processed template
   */
  public static String processTemplate(String template, Map<String, String> variables) {
    String processedTemplate = template;
    for (Map.Entry<String, String> entry : variables.entrySet()) {
      processedTemplate = processedTemplate.replace("${" + entry.getKey() + "}", entry.getValue());
    }
    return processedTemplate;
  }

  /**
   * Creates a variables map with common values.
   *
   * @return a map with common template variables
   */
  public static Map<String, String> createCommonVariables() {
    Map<String, String> variables = new HashMap<>();
    variables.put("currentYear", String.valueOf(Year.now().getValue()));
    return variables;
  }
}
