package org.ansu.cvparser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

import org.ansu.cvparser.converter.DefaultTextConverter;
import org.ansu.cvparser.converter.PDFTextConverter;
import org.ansu.cvparser.converter.TextConverter;
import org.ansu.cvparser.converter.TextHTMLConverter;
import org.ansu.cvparser.exception.ConversionException;
import org.ansu.cvparser.finder.*;
import org.ansu.cvparser.finder.entries.Entry;
import org.ansu.cvparser.rule.RuleFactory;
import org.ansu.cvparser.rule.RuleFactoryImpl;
import org.ansu.cvparser.rule.Rule;
import org.ansu.cvparser.template.TemplateEngine;
import org.ansu.cvparser.template.ThymeleafTemplateEngine;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Andrii Sushkovych
 * Date: 11/6/16
 */
public class CVParser {
    private static Logger logger = LoggerFactory.getLogger(CVParser.class);
    private static TemplateEngine templateEngine = new ThymeleafTemplateEngine();

    public static TextConverter getConverter(File file) {
        String name = file.getName();
        if (name.endsWith(".pdf")) {
            return new PDFTextConverter();
        } else {
            return new DefaultTextConverter();
        }
    }

    public static void textifyAndSave(File srcDir, File dstDir) {
        File[] list = srcDir.listFiles();
        if (list == null) {
            logger.error("No files found");
            return;
        }
        for (File file : list) {
            if (file.isFile()) {
                String fileName = dstDir + "/" + FilenameUtils.removeExtension(file.getName()) + ".txt";
                try (PrintWriter writer = new PrintWriter(fileName, "UTF-8")) {
                    String text = getConverter(file).textify(file);
                    writer.print(text);
                } catch (FileNotFoundException | UnsupportedEncodingException | ConversionException e) {
                    logger.error(e.getMessage(), e);
                }
            } else {
                // ignore
                logger.warn("Found an unknown type of file (directory), ignoring: " + file.getName());
            }
        }
    }

    public static String highlight(String html, String textToReplace, String cssClass) {
        String template = "<span class=\"" + cssClass + "\">" + textToReplace + "</span>";
        return html.replace(textToReplace, template);
    }

    public static String highlightAll(String html, Rule group, String cssClass) {
        String template = "<span class=\"" + cssClass + "\">$0</span>";
        return group.replacer(html).all(template);
    }

    /**
     * TODO
     * TODO Detect sections judging by the number of occurrences of "expressions", like:
     * TODO - Experience: Date ranges + Job titles + Technologies + Verbs (worked, created, fixed, implemented)
     * TODO - Education : Date ranges + University/Institute/Bachelor/Degree
     * TODO - Trainings : Dates + Training/Certificate/Course + Technologies
     * TODO ...
     */
    private static void highlightEntries() {
        RuleFactory titleFactory = new RuleFactoryImpl(RuleFactory.TokenDelimiter.Line,
                Rule.MatchingMode.Whole, Pattern.CASE_INSENSITIVE);
        RuleFactory entryRuleFactory = new RuleFactoryImpl(RuleFactory.TokenDelimiter.None,
                Rule.MatchingMode.Part);
        RuleFactory keywordRuleFactory = new RuleFactoryImpl(RuleFactory.TokenDelimiter.Word,
                Rule.MatchingMode.Whole);

        List<Finder> finders = new ArrayList<>();

        finders.add(new NameFinder(entryRuleFactory.readSafe(
                "keywords/names/male/en_ru/names",
                "keywords/names/male/en_ua/names",
                "keywords/names/female/en_ru/names",
                "keywords/names/female/en_ua/names"
        )));
        finders.add(new PhoneFinder());
        finders.add(new EmailFinder());
        finders.add(new SkypeFinder());
        finders.add(new LinkedInURLFinder());
        finders.add(new GitHubURLFinder());
        finders.add(new LocationFinder(entryRuleFactory.readSafe(
                "keywords/locations/en_ua/cities"
        )));

        // TODO Make static
        Rule ruleBE = keywordRuleFactory.readSafe("keywords/technologies/backend");
        Rule ruleFE = keywordRuleFactory.readSafe("keywords/technologies/frontend");
        Rule ruleDates = entryRuleFactory.readSafe("keywords/dates");

        Collection<Rule> titles = new ArrayList<>();
        titles.add(titleFactory.readSafe("keywords/sections/courses"));
        titles.add(titleFactory.readSafe("keywords/sections/experience"));
        titles.add(titleFactory.readSafe("keywords/sections/education"));

        TextHTMLConverter converter = new TextHTMLConverter("debug/parsed");

        String reportTemplate = null;
        try {
            reportTemplate = loadTemplate("report/report");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 1;; i++) {
            try {
                File input = new File("examples\\txt\\cv" + i + ".txt");
                if (!input.exists()) {
                    break;
                }
                String contents = new String(Files.readAllBytes(Paths.get(input.getPath())));

                int countBE = ruleBE.count(contents);
                int countFE = ruleFE.count(contents);

                Map<String, String> reportContext = new HashMap<>();
                // Parse general information: Name, Email, etc
                System.out.println("\n--------- Resume " + i + " ---------");
                for (Finder finder : finders) {
                    Entry entry = finder.find(contents);
                    if (entry != null) {
                        contents = highlight(contents, entry.getOriginal(), "info"); // using replace() for strings
                        reportContext.put(finder.getName(), entry.getCanonical());
                        System.out.println(finder.getName() + ": " + entry.getCanonical());
                    }
                }

                String cvFileName = "cv" + i + ".html";
                BufferedWriter writerDebug = new BufferedWriter(new PrintWriter("examples\\" + cvFileName, "UTF-8"));
                BufferedWriter writerReport = new BufferedWriter(new PrintWriter("examples\\reports\\" + cvFileName,
                        "UTF-8"));

                System.out.println("Backend: "  + countBE);
                System.out.println("Frontend: " + countFE);

                if (reportTemplate != null) {
                    reportContext.put("cvName", cvFileName + " - Report");
                    writerReport.write(templateEngine.process(reportTemplate, reportContext));
                }

                contents = highlightAll(contents, ruleDates, "date-range");

                for (Rule group : titles) {
                    contents = highlightAll(contents, group, "title");
                }

                contents = highlightAll(contents, ruleBE, "tech--be");
                contents = highlightAll(contents, ruleFE, "tech--fe");

                Map<String, String> attributes = new HashMap<>();
                attributes.put("file", "cv" + i);
                writerDebug.write(converter.convert(contents, attributes));

                writerDebug.close();
                writerReport.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    // TODO It doesn't belong here
    private static String loadTemplate(String templateName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream in = classLoader.getResourceAsStream(TemplateEngine.TEMPLATE_PATH + templateName + ".html");
        return IOUtils.toString(in);
    }

    public static void main(String[] args){
//        textifyAndSave();
        highlightEntries();
    }

}