package org.ansu.cvparser.parser;

import org.ansu.cvparser.parser.parts.Part;
import org.ansu.cvparser.parser.parts.SimplePart;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Andrii Sushkovych
 * Date: 11/21/16
 */
public abstract class SimpleFinder implements Parser {
    protected abstract Pattern getPattern();

    @Override
    public Part find(String text) {
        Matcher matcher = getPattern().matcher(text);
        if (matcher.find()) {
            return new SimplePart(matcher.group(0));
        }
        return null;
    }
}
