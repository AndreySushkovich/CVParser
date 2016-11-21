package org.ansu.cvparser.parser;

import org.ansu.cvparser.parser.entries.SimpleEntry;
import org.ansu.cvparser.parser.entries.Entry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Andrii Sushkovych
 * Date: 11/21/16
 */
public abstract class SimpleFinder implements Finder {
    protected abstract Pattern getPattern();

    @Override
    public Entry find(String text) {
        Matcher matcher = getPattern().matcher(text);
        if (matcher.find()) {
            return new SimpleEntry(matcher.group(0));
        }
        return null;
    }
}
