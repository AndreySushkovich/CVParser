package org.ansu.cvparser.parser;

import org.ansu.cvparser.KeywordGroup;
import org.ansu.cvparser.parser.parts.Part;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Andrii Sushkovych
 * Date: 11/21/16
 */
public class LocationFinder implements Finder {

    public static final String NAME = "([А-ЯЄIA-Z](?:[а-яєiА-ЯЄIa-zA-Z]+|[А-ЯЄIA-Z]+))";
    public static final String FULL_NAME = "(?m)^\\s*" + NAME + "\\s+" + NAME + "\\s";

    private static final Pattern PATTERN = Pattern.compile(FULL_NAME);
    private static Logger logger = LoggerFactory.getLogger(LocationFinder.class);

    private final KeywordGroup keywordGroupNames; // initialized in constructor

    /**
     * Author: Andrii Sushkovych
     * Date: 11/21/16
     */
    public static class Name implements Part {
        private List<String> names; // [0] is the original

        private String original;
        private String canonical;

        private Name(String original, String canonical, List<String> names) {
            this.original = original;
            this.canonical = canonical;
            this.names = names;
        }

        @Override
        public String getOriginal() {
            return original;
        }

        @Override
        public String getCanonical() {
            return canonical;
        }

        public List<String> getNames() {
            return names;
        }
    }

    public LocationFinder(String... resources) {
        keywordGroupNames = KeywordGroup.readSafe(resources);
    }

    @Override
    public String getName() {
        return "Name";
    }

    @Override
    public Part find(String text) {
        List<List<String>> nameCandidates = new ArrayList<>();
        Matcher matcher = PATTERN.matcher(text);
        while (matcher.find()) {
            List<String> names = new ArrayList<>();
            for (int i = 0; i <= matcher.groupCount(); i++) { // [0] is the original, full name
                names.add(matcher.group(i));
            }
            nameCandidates.add(names);
        }

        for (List<String> nameCandidate : nameCandidates) {
            int namesCount = nameCandidate.size() - 1; // [0] is the original

            // trim the full name
            String fullName = StringUtils.trim(nameCandidate.get(0));
            nameCandidate.set(0, fullName);

            for (int i = 1; i < namesCount; i++) {
                String name = nameCandidate.get(i);
                boolean found = matchesKeyword(name, keywordGroupNames);
                if (found) {
                    // We assume for now that only 2 names are present: Name and Surname
                    // Try and get the next name; if absent, the previous one
                    String otherName = null;
                    if (i + 1 <= namesCount) { // [0] is the original
                        otherName = nameCandidate.get(i + 1);
                    } else if (i - 1 > 0) { // [0] is the original
                        otherName = nameCandidate.get(i - 1);
                    }
                    String canonicalName = name + (otherName != null ? " " + otherName : "");
                    return new Name(fullName, canonicalName, nameCandidate);
                }
            }
        }
        if (!nameCandidates.isEmpty()) {
            List<String> firstCandidate = nameCandidates.get(0);
            String name = firstCandidate.get(0);
            logger.warn("Not certain about the name, return the 1st candidate: " + name);
            return new Name(name, name, firstCandidate);
        }
        return null;
    }

    // TODO Create Keyword entity (instead of String), move this method there
    private boolean matchesKeyword(String word, KeywordGroup group) {
        for (String keyword : group.keywords()) {
            if (word.matches(keyword)) {
                return true;
            }
        }
        return false;
    }

}
