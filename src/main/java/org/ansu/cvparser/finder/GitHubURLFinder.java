package org.ansu.cvparser.finder;

import java.util.regex.Pattern;

/**
 * Author: Andrii Sushkovych
 * Date: 11/21/16
 */
public class GitHubURLFinder extends RegExpFinder {
    public static final String URL = "(?<=\\s)(https?://)?([a-zA-Z-\\d]+\\.)*github.(com|io)(/[\\w-]+)*";

    @Override
    public String getName() {
        return "GitHub";
    }

    @Override
    protected Pattern getPattern() {
        return Pattern.compile(URL);
    }

}
