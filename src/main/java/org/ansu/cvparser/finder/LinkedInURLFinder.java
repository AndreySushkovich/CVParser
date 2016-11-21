package org.ansu.cvparser.finder;

import java.util.regex.Pattern;

/**
 * Author: Andrii Sushkovych
 * Date: 11/21/16
 */
public class LinkedInURLFinder extends RegExpFinder {
    public static final String URL = "(?<=\\s)(https?://)?([a-zA-Z]+\\.)*linkedin.com/in/[\\w-]+";

    @Override
    public String getName() {
        return "LinkedIn";
    }

    @Override
    protected Pattern getPattern() {
        return Pattern.compile(URL);
    }

}
