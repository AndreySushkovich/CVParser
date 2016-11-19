package org.ansu.cvparser.template;

import java.util.Map;

/**
 * Author: Andrii Sushkovych
 * Date: 11/19/16
 */
public interface TemplateEngine {
    static String TEMPLATE_PATH = "templates/";

    String process(String template, Map<String, String> attributes);
}
