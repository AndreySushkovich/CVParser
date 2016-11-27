package org.ansu.cvparser.rule;

import java.io.IOException;

/**
 * Author: Andrii Sushkovych
 * Date: 11/26/16
 */
public interface RuleFactory {
    interface TokenDelimiter {
        String Line = "\n\r";
        String Word = " \u00A0\t\n\r\f,.;\\/()[]{}<>\u200B";
        String None = "";
    }

    Rule read(String... resourceNames) throws IOException;
    Rule read(Rule.MatchingMode mode, String... resourceNames) throws IOException;
    Rule read(Rule.MatchingMode mode, int matchingFlags, String... resourceNames) throws IOException;

    Rule readSafe(String... resourceNames);
    Rule readSafe(Rule.MatchingMode mode, String... resourceNames);
    Rule readSafe(Rule.MatchingMode mode, int matchingFlags, String... resourceNames);
}
