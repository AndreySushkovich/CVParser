package org.ansu.cvparser;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Andrii Sushkovych
 * Date: 11/20/16
 *
 * TODO 1. Add RegExp stuff here, like matching, replacement, etc
 * TODO 2. Create Keyword entity (instead of String)
 */
public class KeywordGroup {
    public static final char COMMENT_SYMBOL = '#';
    public static final char CONTROL_SYMBOL = '@';
    public static final char EVAL_SYMBOL    = '`';

    public static final String SET_CLAUSE = CONTROL_SYMBOL + "set\\s+(\\w+)\\s+=\\s+(\\S+)";

    private static Logger logger = LoggerFactory.getLogger(KeywordGroup.class);

    private List<String> words;
    private Map<String, String> variables;

    /**
     * Supports variables, like
     * >    @set JS = (J[Ss]|js)
     * >    ...
     * >    [Rr]eact\.`JS`?
     *
     * TODO 1. Support subgroups, like
     * TODO     @include tech/java/frameworks
     * TODO 2. Consider migrating from TXT extension
     */
    public static KeywordGroup read(String... resourceNames) throws IOException {
        Pattern patternSet = Pattern.compile(SET_CLAUSE);
        KeywordGroup group = new KeywordGroup();
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
                        }
                    } else if (!line.isEmpty() && !line.startsWith("" + COMMENT_SYMBOL)) {
                        group.add(line);
                    }
                }
            }
        }
        return group;
    }

    /**
     * No exception thrown, please use it cautiously!
     */
    public static KeywordGroup readSafe(String... resourceNames) {
        try {
            return KeywordGroup.read(resourceNames);
        } catch (IOException e) {
            logger.error("Cannot read one of the following resources: " + Arrays.toString(resourceNames));
            return new KeywordGroup();
        }
    }

    public KeywordGroup() {
        this(new ArrayList<>());
    }

    public KeywordGroup(List<String> words) {
        this.words = words;
        this.variables = new HashMap<>();
    }

    public void add(String wordToAdd) {
        for (Map.Entry<String, String> entry : this.variables.entrySet()) {
            String template = EVAL_SYMBOL + entry.getKey() + EVAL_SYMBOL; // like `VAR_NAME`
            wordToAdd = wordToAdd.replaceAll(template, entry.getValue());
        }
        String candidate = RegExp.uncaptured(wordToAdd); // TODO Should be keyword.apply(Action.Capture) or sth?
        for (String word : this.keywords()) {
            if (word.equals(candidate)) {
                // the word has already been added, return
                logger.warn("Ignoring a duplicate of '" + word + "', not added");
                return;
            }
        }
        this.words.add(candidate);
    }

    public void addAll(Collection<String> words) {
        for (String word : words) {
            this.add(word);
        }
    }

    public List<String> keywords() {
        return this.words;
    }

    protected void setVar(String name, String value) {
        this.variables.put(name, value);
    }
}
