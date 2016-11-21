package org.ansu.cvparser.parser;

import java.util.regex.Pattern;

/**
 * International (Microsoft) format: Ukraine
 * +380 (99) 1234567
 * [Country Code] (Carrier Code) Phone Number
 *
 * Examples:
 *  (1)   +38 (099) 12 34 567
 *  (2)   +38 (099) 12-34-567
 *  (3)   +380 (99) 123-45-67
 *  (4)   +380 99 123 45 67
 *  (5)   +380991234567
 *  (6)   099-123-45-67
 *  (7)   0991234567
 *  (8)   099 123 45 67
 *
 * Author: Andrii Sushkovych
 * Date: 11/21/16
 */
public class LinkedInURLFinder extends SimpleFinder {
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
