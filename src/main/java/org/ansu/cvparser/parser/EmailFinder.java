package org.ansu.cvparser.parser;

import org.ansu.cvparser.parser.parts.Part;
import org.ansu.cvparser.parser.parts.SimplePart;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.ansu.cvparser.RegExp.or;
import static org.ansu.cvparser.RegExp.wrap;

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
public class EmailFinder extends SimpleParser {
    public static final String EMAIL = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    @Override
    public String getName() {
        return "Email";
    }

    @Override
    protected Pattern getPattern() {
        return Pattern.compile(EMAIL);
    }

}
