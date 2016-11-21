package org.ansu.cvparser.parser;

import org.ansu.cvparser.parser.parts.Part;
import org.ansu.cvparser.parser.parts.SimplePart;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Andrii Sushkovych
 * Date: 11/21/16
 */
public class SkypeFinder implements Parser {
    public static final String REG_EXP = "(?mi)^\\s*skype\\s*:?\\s*(\\S+)\\s";
    private static final Pattern PATTERN = Pattern.compile(REG_EXP);

    @Override
    public String getName() {
        return "Skype";
    }

    // Do not replace with SimpleParser's find() as the group number differs
    @Override
    public Part find(String text) {
        Matcher matcher = PATTERN.matcher(text);
        if (matcher.find()) {
            return new SimplePart(matcher.group(1));
        }
        return null;
    }
}
