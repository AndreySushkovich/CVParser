package org.ansu.cvparser.util;

/**
 * Created with IntelliJ IDEA.
 * User: Саня
 * Date: 13.11.16
 * Time: 22:00
 */
public class RegExpUtils {
    public static String FLAG_GLOBAL = "(?i)";
    public static interface Lexemes {
        public static String MonthName = "(January|February|March|April|June|July|August|September|October|November|December|Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)";
        public static String Today = "\\.\\.\\.?|today|present(\\W+?(time|moment))?";
        public static String OneOrTwoDigits = "\\d{1,2}";
        public static String TwoToFourDigits = "\\d{1,2}";
        public static String Hyphen = "[-‒–—―]";
        public static String NonWordOrDelim = "[\\s_\\W]"; // TODO Any better name?
        public static String NonWord = "\\W"; // TODO Any better name?
        public static String Letter = "[а-яєiА-ЯЄI]";
        public static String Word = "[а-яєiА-ЯЄIa-zA-Z]+";
        public static String WordDelim = "\\b";
        // TODO Try using \b (word boundary) instead of weird combinations for spaces/delimiters
        // Case for delimiters: \u200BJavaScript\u200B (\u200B is ZERO WIDTH SPACE, &#8203;)
        public static String SpaceOrDelim = "[\\s,\\.;\\\\/\\(\\)\\u200B]";
        public static String Space = "[\\s\\u200B]"; // TODO Is \n really a "space"? Doubt that
        public static String HSpace = "[^\\S\\x0a\\x0d\\r]";
        public static String HSpaceOrDelim = "(?:" + HSpace + "|[\\W_])"; // TODO This is freaky slow (test on cv7)
        public static String Delim = "[ \\xa0\\t\\n\\r\\f,\\.;:_\\\\\\/\\(\\)\\[\\]\\{\\}<>\\\u200B]";
        public static String Spaces = Space + "+";
        public static String SingleQuote = "['’]";
        public static String EOL = "\\n";
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
        return s + "*";
    }

    public static String uncaptureGroups(String re) {
        return re.replaceAll("(?!\\?:)\\(", "(?:");
    }

    public static String wordify(String re) {
        return "(" + Lexemes.SpaceOrDelim + "|^)" + wrap(re) + "(" + Lexemes.SpaceOrDelim + "|$)";
    }

    public static String linify(String re) {
        String spaces = Lexemes.Delim + "*";
        return "^" + spaces + wrap(re) + spaces + "$";
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
