package org.ansu.cvparser.finder;

import org.ansu.cvparser.rule.Rule;
import org.ansu.cvparser.finder.entries.SimpleEntry;
import org.ansu.cvparser.finder.entries.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static org.ansu.cvparser.util.RegExpUtils.Lexemes.HSpace;

/**
 * TODO We should rather search the location in these 2 sections only:
 * TODO   - General Information (1st section)
 * TODO   - Education (location of the last University)
 * TODO (Currently it's searched through the whole text.)
 *
 * Author: Andrii Sushkovych
 * Date: 11/21/16
 */
public class LocationFinder implements Finder {

    public static final String LOCATION = "[A-Z][a-z]+((-|[" + HSpace + "]+)[A-Z][a-z]+)?";

    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile(LOCATION);
    private static final Logger logger = LoggerFactory.getLogger(LocationFinder.class);

    private final Rule locations; // initialized in constructor

    /**
     * TODO Do not mix up cities and countries! First, look up a city,
     * TODO and only when you didn't succeed do you fall back on countries.
     * TODO Consider the case: "Ukraine, Kiev"
     * TODO As we return always the 1st finding, the result will be "Ukraine",
     * TODO and not "Kiev"
     */
    public LocationFinder(Rule rule) {
        this.locations = rule;
    }

    @Override
    public String getName() {
        return "Location";
    }

    @Override
    public Entry find(String text) {
        List<String> candidates = new ArrayList<>();
        Matcher matcher = PATTERN.matcher(text);
        while (matcher.find()) {
            candidates.add(matcher.group(0));
        }

        for (String candidate : candidates) {
            for (String keyword : locations.expressions()) {
                if (candidate.matches(keyword)) {
                    return new SimpleEntry(candidate);
                }
            }
        }
        // if the location was not found in our dictionary, all is over;
        // there's no way we can distinguish a city with a general regex
        return null;
    }
}
