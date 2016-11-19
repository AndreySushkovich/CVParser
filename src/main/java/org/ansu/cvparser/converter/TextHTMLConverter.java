package org.ansu.cvparser.converter;

import org.ansu.cvparser.exception.ConversionException;
import org.ansu.cvparser.template.TemplateEngine;
import org.ansu.cvparser.template.ThymeleafTemplateEngine;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Andrii Sushkovych
 * Date: 11/19/16
 */
public class TextHTMLConverter extends DefaultTextConverter {
    private static TemplateEngine templateEngine = new ThymeleafTemplateEngine();

    private String templateName;

    public TextHTMLConverter(String templateName) {
        this.templateName = templateName;
    }

    @Override
    public String textify(File file) throws ConversionException {
        String text = super.textify(file);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("file", FilenameUtils.removeExtension(file.getName()));
        return convert(text, attributes);
    }

    public String convert(String content) throws ConversionException {
        String template = null;
        try {
            template = loadTemplate();
        } catch (IOException e) {
            throw new ConversionException("Cannot load template " + templateName, e);
        }
        Map<String, String> attributes = new HashMap<>();
        attributes.put("content", content);
        return templateEngine.process(template, attributes);
    }

    public String convert(String content, Map<String, String> attributes) throws ConversionException {
        String template = null;
        try {
            template = loadTemplate();
        } catch (IOException e) {
            throw new ConversionException("Cannot load template " + templateName, e);
        }
        attributes.put("content", content);
        return templateEngine.process(template, attributes);
    }

    /**
     * TODO Cache templates
     */
    private String loadTemplate() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream in = classLoader.getResourceAsStream(getFullTemplateName());
        return IOUtils.toString(in);
    }

    private String getFullTemplateName() {
        return TemplateEngine.TEMPLATE_PATH + templateName + ".html";
    }
}
