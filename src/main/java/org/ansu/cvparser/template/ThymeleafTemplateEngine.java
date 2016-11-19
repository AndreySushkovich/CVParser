package org.ansu.cvparser.template;

import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.Locale;
import java.util.Map;

/**
 * Author: Andrii Sushkovych
 * Date: 11/19/16
 */
public class ThymeleafTemplateEngine implements TemplateEngine {
    private static org.thymeleaf.TemplateEngine templateEngine;

    private org.thymeleaf.TemplateEngine getTemplateEngine() {
        if (templateEngine == null) {
            templateEngine = new org.thymeleaf.TemplateEngine();
            StringTemplateResolver templateResolver = new StringTemplateResolver();
            templateResolver.setTemplateMode(TemplateMode.HTML);
            templateEngine.setTemplateResolver(templateResolver);
        }
        return templateEngine;
    }

    @Override
    public String process(String template, Map<String, String> attributes) {
        templateEngine = getTemplateEngine();
        String text = null;
        final Context ctx = new Context(Locale.getDefault());
        if (attributes != null) {
            attributes.forEach(ctx::setVariable);
        }
        if (templateEngine != null) {
            text = templateEngine.process(template, ctx);
        }
        return text;
    }
}
