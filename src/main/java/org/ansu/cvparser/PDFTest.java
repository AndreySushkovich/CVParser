package org.ansu.cvparser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ansu.cvparser.text.DefaultTextStripper;
import org.ansu.cvparser.text.PDFTextStripper;
import org.ansu.cvparser.text.TextStripper;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.ansu.cvparser.RegExp.*;
import static org.ansu.cvparser.RegExp.Lexemes.*;

/**
 * Author: Andrii Sushkovych
 * Date: 11/6/16
 */
public class PDFTest {
    private static Logger logger = LoggerFactory.getLogger(PDFTest.class);

    public static TextStripper getStripper(File file) {
        String name = file.getName();
        if (name.endsWith(".pdf")) {
            return new PDFTextStripper();
        } else {
            return new DefaultTextStripper();
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
                String text = getStripper(file).textify(file);
                String fileName = dstDir + "/" + FilenameUtils.removeExtension(file.getName()) + ".txt";
                try (PrintWriter writer = new PrintWriter(fileName, "UTF-8")) {
                    writer.print(text);
                } catch (FileNotFoundException | UnsupportedEncodingException e) {
                    logger.error(e.getMessage(), e);
                }
            } else {
                // ignore
                logger.warn("Found an unknown type of file (directory), ignoring: " + file.getName());
            }
        }
    }

    private static String[] keywordsVCS = { "Git", "SVN", "Subversion", "Mercurial", "Hg", "Perforce", "Concurrent Version Systems", "CVS" };
    private static String[] keywordsLangBE = { "Java", "Python", "Ruby", "PHP", "C++", "C", "Shell", "C#", "Objective-C", "Go", "Perl", "Scala", "Haskell", "Lisp", "Lua", "Clojure", "Groovy", "Arduino", "Erlang", "1C", "1С" };
    private static String[] keywordsLangFE = { "JavaScript", "EcmaScript(?:[\\d\\.]{1,4})?", "HTML(?:[45])?", "DHTML", "XML", "CoffeeScript", "CSS(?:[23])?", "TypeScript" };

    private static int countGroup(String text, String[] names) {
        int count = 0;
        for (String name : names) {
            String re = "(?i)\\W" + wrap(name) + "\\W";
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
     * TODO - Experience: Date ranges + Job titles + Technologies
     * TODO - Education : Date ranges + University/Institute/Bachelor/Degree
     * TODO - Trainings : Dates + Training/Certificate/Course + Technologies
     * TODO ...
     */

    private static void highlightEntries() {
        String[] reDateRange = new String[] {
                // M Y - ((M Y)|T)
                // ex: Jan 14 - May '15
                // ex: February 2015 - present time
                RegExp.build(maybe(wrap(MonthName + "\\W+?")) + maybe(wrap("['’]?\\d{2,4}\\W*?")) + Hyphen + "\\W*?(" + MonthName + "\\W+?['’]?\\d{2,4}|" + Today + ")"),
                // dd?/dd(dd)? - ((dd?/dd(dd)?)|T)
                // ex: 01/13 - 5/2014
                RegExp.build("\\d{1,2}[/\\.]\\W*?\\d{2,4}\\W*?" + Hyphen + "\\W*?(\\d{1,2}[/\\.]\\W*?\\d{2,4}|" + Today + ")"),
                // ex: 2012 - 15
                RegExp.build("(19|20)\\d{2}\\W*?" + Hyphen + "\\W*?" + or(or("19", "20") + "\\d{2}", "\\d{2}", "\\d") + many(SpaceOrDelim))
        };
        String reTitleExperience = "(?im)\\n[\\s_\\W]*?((?:work\\s)?\\W*?experience[\\s_\\W]*?)$";
        String reTitleEducation = "(?im)\\n[\\s_\\W]*?(" + or("education", "academic[\\s_\\W]*?record") + "[^:]*?:?[\\s_\\W]*?)$";
        String reTitleEducationAsKeyValue = "(?im)\\n[\\s_\\W]*?(education\\s*?:)\\s*?(.*?)$";
//        String reTitleCourses = "(?im)\\n[\\s_\\W]*?(training?????????[\\s_\\W]*?.*?)$";
        for (int i = 1;; i++) {
            try {
                File input = new File("examples\\txt\\cv" + i + ".txt");
                if (!input.exists()) {
                    break;
                }
                String contents = new String(Files.readAllBytes(Paths.get(input.getPath())));

                System.out.println("\n--------- Resume " + i + " ---------");
                System.out.println("VCS: " + countGroup(contents, keywordsVCS));
                System.out.println("Backend: " + countGroup(contents, keywordsLangBE));
                System.out.println("Frontend: " + countGroup(contents, keywordsLangFE));

                BufferedWriter wr = new BufferedWriter(new PrintWriter("examples\\cv" + i + ".html", "UTF-8"));
                wr.write("<style type=\"text/css\">.date-range {\n\tbackground-color: yellow;\n}\n"
                        + ".title {\n\tbackground-color: cyan;\n}\n"
                        + "</style>\n");
                for (String regExp : reDateRange) {
                    contents = contents.replaceAll(regExp, "<span class=\"date-range\">$1</span>");
                }

                contents = contents.replaceAll(reTitleExperience, "<span class=\"title\">$0</span>");
                contents = contents.replaceAll(reTitleEducation, "<span class=\"title\">$0</span>");
                contents = contents.replaceAll(reTitleEducationAsKeyValue, "<span class=\"title\">$0</span>");

                wr.write(contents);
                wr.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
//        pdfToTxt();
        highlightEntries();
    }

}