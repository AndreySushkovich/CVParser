package org.ansu.cvparser;

/**
 * Created with IntelliJ IDEA.
 * User: Саня
 * Date: 13.11.16
 * Time: 22:00
 */
public class RegExp {
    public static String FLAG_GLOBAL = "(?i)";
    public static interface Lexemes {
        public static String MonthName = "(January|February|March|April|June|July|August|September|October|November|December|Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)";
        public static String Today = "\\.\\.\\.?|today|present(\\W+?(time|moment))?";
        public static String OneOrTwoDigits = "\\d{1,2}";
        public static String TwoToFourDigits = "\\d{1,2}";
        public static String Hyphen = "[-‒–—―]";
        public static String SpaceOrDelim = "[\\s_\\W]";
        public static String Space = "\\W";
        public static String SingleQuote = "['’]";
    }

    public static String wrap(String s) {
        return "(" + s + ")";
    }

    public static String maybe(String s) {
        return s + "?";
    }

    public static String many(String s) {
        return s + "+?";
    }

    public static String maybeMany(String s) {
        return s + "*?";
    }

    public static String or(String... args) {
        StringBuilder builder = new StringBuilder();
        for (String s : args) {
            if (builder.length() > 0) {
                builder.append("|");
            }
            builder.append(s);
        }
        return wrap(builder.toString());
    }

    public static String build(String re) {
        return FLAG_GLOBAL + wrap(re);
    }
}
