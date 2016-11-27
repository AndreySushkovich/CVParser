package org.ansu.cvparser.rule;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Andrii Sushkovych
 * Date: 11/26/16
 */
public class RuleFactoryImpl implements RuleFactory {
    protected static final char COMMENT_SYMBOL = '#';
    protected static final char CONTROL_SYMBOL = '@';

    protected static final String SET_CLAUSE  = "^\\s*" + CONTROL_SYMBOL + "set\\s+(\\w+)\\s+=\\s+(\\S+)";
    protected static final String WORD_CLAUSE = "^\\s*" + CONTROL_SYMBOL + "word\\s*:\\s*(\\S+)";
    // TODO Support @include

    private static final Logger logger = LoggerFactory.getLogger(RuleFactoryImpl.class);

    private String tokenDelimiter;

    private Rule.MatchingMode defaultMatchingMode;
    private int defaultMatchingFlags;

    public RuleFactoryImpl() {
        this(TokenDelimiter.Line);
    }

    public RuleFactoryImpl(String tokenDelimiter) {
        this(tokenDelimiter, Rule.DEFAULT_MATCHING_MODE);
    }

    public RuleFactoryImpl(String tokenDelimiter, Rule.MatchingMode defaultMatchingMode) {
        this(tokenDelimiter, defaultMatchingMode, Rule.DEFAULT_MATCHING_FLAG);
    }

    public RuleFactoryImpl(String tokenDelimiter, Rule.MatchingMode defaultMatchingMode, int defaultMatchingFlags) {
        this.tokenDelimiter = tokenDelimiter;
        this.defaultMatchingMode = defaultMatchingMode;
        this.defaultMatchingFlags = defaultMatchingFlags;
    }

    /**
     * Supports variables, like
     * >    @set JS = (J[Ss]|js)
     * >    ...
     * >    [Rr]eact\.`JS`?
     *
     * TODO 1. Support subgroups, like
     * TODO     @include tech/java/frameworks
     * TODO 2. Consider migrating from TXT extension
     * TODO 3. Refactor
     */
    @Override
    public Rule read(Rule.MatchingMode mode, int matchingFlags, String... resourceNames)
            throws IOException {
        Pattern patternSet  = Pattern.compile(SET_CLAUSE, Pattern.CASE_INSENSITIVE);
        Pattern patternWord = Pattern.compile(WORD_CLAUSE, Pattern.CASE_INSENSITIVE);
        // TODO Add words support to finders!

        Rule group = new Rule(tokenDelimiter, mode, matchingFlags);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        for (String resourceName : resourceNames) {
            InputStream stream = classLoader.getResourceAsStream(resourceName + ".txt");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = StringUtils.strip(line);
                    // TODO Create line parser, to handle cases like
                    // TODO   - line is empty
                    // TODO   - line starts with #
                    // TODO   - line starts with @, etc
                    if (line.startsWith("" + CONTROL_SYMBOL)) {
                        Matcher matcher = patternSet.matcher(line);
                        if (matcher.find()) {
                            group.setVar(matcher.group(1), matcher.group(2));
                        } else {
                            matcher = patternWord.matcher(line);
                            if (matcher.find()) {
                                group.keywords().add(matcher.group(1));
                            }
                        }
                    } else if (!line.isEmpty() && !line.startsWith("" + COMMENT_SYMBOL)) {
                        group.add(line);
                    }
                }
            }
        }
        return group;
    }

    @Override
    public Rule read(Rule.MatchingMode mode, String... resourceNames) throws IOException {
        return read(mode, this.defaultMatchingFlags, resourceNames);
    }

    @Override
    public Rule read(String... resourceNames) throws IOException {
        return read(this.defaultMatchingMode, this.defaultMatchingFlags, resourceNames);
    }

    /**
     * No exception thrown, please use it cautiously!
     */
    @Override
    public Rule readSafe(Rule.MatchingMode mode, int matchingFlags, String... resourceNames) {
        try {
            return read(mode, matchingFlags, resourceNames);
        } catch (IOException e) {
            logger.error("Cannot read one of the following resources: "
                    + Arrays.toString(resourceNames));
            return new Rule(tokenDelimiter, defaultMatchingMode, defaultMatchingFlags);
        }
    }

    @Override
    public Rule readSafe(Rule.MatchingMode mode, String... resourceNames) {
        return readSafe(mode, this.defaultMatchingFlags, resourceNames);
    }

    @Override
    public Rule readSafe(String... resourceNames) {
        return readSafe(this.defaultMatchingMode, this.defaultMatchingFlags, resourceNames);
    }
}
