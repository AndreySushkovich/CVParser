package org.ansu.cvparser.finder;

import java.util.regex.Pattern;

/**
 * Author: Andrii Sushkovych
 * Date: 11/21/16
 */
public class URLFinder extends RegExpFinder {
    public static final String URL = "(?<=\\s)https?://(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";

    @Override
    public String getName() {
        return "URL";
    }

    @Override
    protected Pattern getPattern() {
        return Pattern.compile(URL);
    }

}
