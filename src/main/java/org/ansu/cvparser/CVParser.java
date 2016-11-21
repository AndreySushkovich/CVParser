package org.ansu.cvparser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ansu.cvparser.converter.DefaultTextConverter;
import org.ansu.cvparser.converter.PDFTextConverter;
import org.ansu.cvparser.converter.TextConverter;
import org.ansu.cvparser.converter.TextHTMLConverter;
import org.ansu.cvparser.exception.ConversionException;
import org.ansu.cvparser.parser.*;
import org.ansu.cvparser.parser.entries.Entry;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.ansu.cvparser.RegExp.*;
import static org.ansu.cvparser.RegExp.Lexemes.*;

/**
 * Author: Andrii Sushkovych
 * Date: 11/6/16
 */
public class CVParser {
    private static Logger logger = LoggerFactory.getLogger(CVParser.class);

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

    public static String highlightAll(String html, String regex, String cssClass) {
        String template = "<span class=\"" + cssClass + "\">$0</span>";
        return html.replaceAll(regex, template);
    }

    public static String highlightAll(String html, KeywordGroup keywordGroup, String cssClass) {
        String template = "$1<span class=\"" + cssClass + "\">$2</span>$3";
        for (String word : keywordGroup.keywords()) {
            // TODO Move this RegExp-related stuff somewhere?
            String regex = wrap(SpaceOrDelim) + wrap(word) + wrap(SpaceOrDelim);
            html = html.replaceAll(regex, template);
        }
        return html;
    }

    private static int countGroup(String text, List<String> names) {
        int count = 0;
        for (String name : names) {
            String re = SpaceOrDelim + wrap(name) + SpaceOrDelim;
            Matcher matcher = Pattern.compile(re).matcher(text);
            while (matcher.find()) {
                count++;
            }
        }
        return count;
    }

    /**
     * TODO
     * TODO Detect sections judging by the number of occurrences of "keywords", like:
     * TODO - Experience: Date ranges + Job titles + Technologies + Verbs (worked, created, fixed, implemented)
     * TODO - Education : Date ranges + University/Institute/Bachelor/Degree
     * TODO - Trainings : Dates + Training/Certificate/Course + Technologies
     * TODO ...
     */
    private static void highlightEntries() {

        List<Finder> finders = new ArrayList<>();
        finders.add(new NameFinder(
                "keywords/names/male/en_ru/names",
                "keywords/names/male/en_ua/names",
                "keywords/names/female/en_ru/names",
                "keywords/names/female/en_ua/names"
        ));
        finders.add(new PhoneFinder());
        finders.add(new EmailFinder());
        finders.add(new SkypeFinder());
        finders.add(new LinkedInURLFinder());
        finders.add(new GitHubURLFinder());
        finders.add(new LocationFinder(
                "keywords/locations/en_ua/cities"
        ));

        // TODO Make static
        KeywordGroup keywordsBE = KeywordGroup.readSafe("keywords/tech/backend");
        KeywordGroup keywordsFE = KeywordGroup.readSafe("keywords/tech/frontend");

        TextHTMLConverter converter = new TextHTMLConverter("debug/parsed");
        String[] reDateRange = new String[] {
                // pattern: M Y - ((M Y)|T)
                // ex: Jan 14 - May '15
                // ex: February 2015 - present time
                RegExp.build(maybe(wrap(MonthName + "\\W+?")) + maybe(wrap("['’]?\\d{2,4}\\W*?")) + Hyphen + "\\W*?(" + MonthName + "\\W+?['’]?\\d{2,4}|" + Today + ")"),
                // pattern: dd?/dd(dd)? - ((dd?/dd(dd)?)|T)
                // ex: 01/13 - 5/2014
                RegExp.build("\\d{1,2}[/\\.]\\W*?\\d{2,4}\\W*?" + Hyphen + "\\W*?(\\d{1,2}[/\\.]\\W*?\\d{2,4}|" + Today + ")"),
                // pattern: (19|20)dd - ((19|20)dd|dd|d)
                // ex: 2012 - 15
                RegExp.build("(19|20)\\d{2}\\W*?" + Hyphen + "\\W*?" + or(or("19", "20") + "\\d{2}", "\\d{2}", "\\d") + many(NonWordOrDelim))
        };
        // pattern: work (experience)? :?
        String reTitleExperience = "(?im)\\n[\\s_\\W]*?((?:work\\s)?\\W*?experience[\\s_\\W]*?)$";
        // pattern: (education|(academic record)) :?
        String reTitleEducation = "(?im)\\n[\\s_\\W]*?(" + or("education", "academic[\\s_\\W]*?record") + "[^:]*?:?[\\s_\\W]*?)$";
        String reTitleEducationAsKeyValue = "(?im)\\n[\\s_\\W]*?(education\\s*?:)\\s*?(.*?)$";
        // keywords: trainings?, courses, certifications?
//        String reTitleCourses = "(?im)\\n[\\s_\\W]*?(training?????????[\\s_\\W]*?.*?)$";
        for (int i = 1;; i++) {
            try {
                File input = new File("examples\\txt\\cv" + i + ".txt");
                if (!input.exists()) {
                    break;
                }
                String contents = new String(Files.readAllBytes(Paths.get(input.getPath())));

                // Parse general information: Name, Email, etc
                System.out.println("\n--------- Resume " + i + " ---------");
                for (Finder finder : finders) {
                    Entry entry = finder.find(contents);
                    if (entry != null) {
                        contents = highlight(contents, entry.getOriginal(), "info"); // using replace() for strings
                        System.out.println(finder.getName() + ": " + entry.getCanonical());
                    }
                }

                BufferedWriter wr = new BufferedWriter(new PrintWriter("examples\\cv" + i + ".html", "UTF-8"));

                System.out.println("Backend: "  + countGroup(contents, keywordsBE.keywords()));
                System.out.println("Frontend: " + countGroup(contents, keywordsFE.keywords()));

                for (String regExp : reDateRange) {
                    contents = highlightAll(contents, regExp, "date-range");
                }

                contents = highlightAll(contents, reTitleExperience, "title");
                contents = highlightAll(contents, reTitleEducation, "title");
                contents = highlightAll(contents, reTitleEducationAsKeyValue, "title");

                contents = highlightAll(contents, keywordsBE, "tech--be");
                contents = highlightAll(contents, keywordsFE, "tech--fe");

                Map<String, String> attributes = new HashMap<>();
                attributes.put("file", "cv" + i);
                wr.write(converter.convert(contents, attributes));

                wr.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
//        textifyAndSave();
        highlightEntries();
    }

}