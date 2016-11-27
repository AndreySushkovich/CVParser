package org.ansu.cvparser.finder;

import java.util.regex.Pattern;

import static org.ansu.cvparser.util.RegExpUtils.or;
import static org.ansu.cvparser.util.RegExpUtils.wrap;

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
public class PhoneFinder extends RegExpFinder {

    public static final String COUNTRY_AND_CARRIER = "(\\+38\\s*)?(\\(0\\d{2}\\)|0\\d{2})\\s*-?\\s*";

    public static final String PHONE_NUMBER_1 = "\\d{2}\\s*\\d{2}\\s*\\d{3}";
    public static final String PHONE_NUMBER_2 = "\\d{2}\\s*-\\s*\\d{2}\\s*-\\s*\\d{3}";
    public static final String PHONE_NUMBER_3 = "\\d{3}\\s*\\d{2}\\s*\\d{2}";
    public static final String PHONE_NUMBER_4 = "\\d{3}\\s*-\\s*\\d{2}\\s*-\\s*\\d{2}";
    public static final String PHONE_NUMBER_5 = "\\d{7}";

    public static final String PHONE_FULL = COUNTRY_AND_CARRIER + or(wrap(PHONE_NUMBER_1), wrap(PHONE_NUMBER_2),
            wrap(PHONE_NUMBER_3), wrap(PHONE_NUMBER_4), wrap(PHONE_NUMBER_5));

    @Override
    public String getName() {
        return "Phone";
    }

    @Override
    protected Pattern getPattern() {
        return Pattern.compile(PHONE_FULL);
    }
}
