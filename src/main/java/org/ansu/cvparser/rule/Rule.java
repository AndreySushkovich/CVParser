package org.ansu.cvparser.rule;

import org.ansu.cvparser.finder.entries.Entry;
import org.ansu.cvparser.finder.entries.MultipleEntry;
import org.ansu.cvparser.finder.entries.SimpleEntry;
import org.ansu.cvparser.finder.entries.SimpleMultipleEntry;
import org.ansu.cvparser.replacer.DefaultReplacer;
import org.ansu.cvparser.replacer.Replacer;
import org.ansu.cvparser.util.RegExpUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.ansu.cvparser.util.RegExpUtils.linify;
import static org.ansu.cvparser.util.RegExpUtils.uncaptureGroups;

/**
 * Author: Andrii Sushkovych
 * Date: 11/20/16
 */
public class Rule {

    static final int DEFAULT_MATCHING_FLAG = Pattern.MULTILINE | Pattern.UNICODE_CASE | Pattern.CANON_EQ;
    static final MatchingMode DEFAULT_MATCHING_MODE = MatchingMode.Part;

    static final char EVAL_SYMBOL    = '`';

    private static Logger logger = LoggerFactory.getLogger(Rule.class);

    private Collection<String> expressions;
    private Collection<java.util.regex.Pattern> patterns;

    private String tokenDelimiter;
    private int matchingFlags = 0;
    private MatchingMode matchingMode;

    private Set<String> words;
    private Map<String, String> variables;

    private KeywordProcessor processor;

    public static enum MatchingMode {
        Whole,
        Part,
        Exact
    }

    protected static interface KeywordProcessor {
        String process(String word);
    }

    Rule() {
        this(new ArrayList<>());
    }

    Rule(List<String> expressions) {
        this(expressions, RuleFactory.TokenDelimiter.Word, DEFAULT_MATCHING_MODE);
    }

    Rule(String tokenDelimiter, MatchingMode mode, int matchingFlags) {
        this(new ArrayList<>(), tokenDelimiter, mode, matchingFlags);
    }

    Rule(List<String> expressions, String tokenDelimiter, MatchingMode mode) {
        this(expressions, tokenDelimiter, mode, DEFAULT_MATCHING_FLAG);
    }

    // TODO Check what happens if we pass here MatchingMode.Part and Rule.LITERAL at the same time
    Rule(List<String> expressions, String tokenDelimiter, MatchingMode mode, int matchingFlags) {
        this.expressions = new ArrayList<>();
        this.patterns = new ArrayList<>();
        this.tokenDelimiter = tokenDelimiter;
        this.matchingFlags = DEFAULT_MATCHING_FLAG | matchingFlags; // applying default PLUS custom
        this.variables = new HashMap<>();
        this.words = new HashSet<>();
        this.matchingMode = mode;
        if (this.matchingMode == MatchingMode.Exact) {
            this.matchingFlags |= java.util.regex.Pattern.LITERAL;
        }
        this.processor = getKeywordProcessor();
        this.addAll(expressions);
    }

    public void add(String wordToAdd) {
        for (Map.Entry<String, String> entry : this.variables.entrySet()) {
            String template = EVAL_SYMBOL + entry.getKey() + EVAL_SYMBOL; // `VAR_NAME`
            wordToAdd = wordToAdd.replace(template, entry.getValue());
        }
        String candidate = processor.process(wordToAdd);
        for (String word : this.expressions()) {
            if (word.equals(candidate)) {
                // the word has already been added, return
                logger.warn("Ignoring a duplicate of '" + word + "', not added");
                return;
            }
        }
        this.expressions.add(candidate);
        this.patterns.add(java.util.regex.Pattern.compile(candidate, matchingFlags));
    }

    public void addAll(Collection<String> words) {
        for (String word : words) {
            this.add(word);
        }
    }

    public Collection<String> expressions() {
        return this.expressions;
    }

    public Set<String> keywords() {
        return this.words;
    }

    // TODO Create Counter that inherits from the same ancestor Replacer does;
    // TODO move the logic of iteration to their parent
    public int count(String text) {
        int n = 0;
        StringTokenizer tokenizer = getTokenizer(text);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (isPotentialMatch(token)) {
                for (Pattern pattern : patterns) {
                    Matcher matcher = pattern.matcher(token);
                    if (matcher.find()) {
                        ++n;
                    }
                }
            }
        }
        return n;
    }

    public Replacer replacer(String text) {
        return new DefaultReplacer(text, this);
    }

    public boolean isPotentialMatch(String token) {
        return !isDelimiter(token) && (!hasKeywords() || containsKeywords(token));
    }

    public boolean hasKeywords() {
        return !this.words.isEmpty();
    }

    public Collection<Pattern> patterns() {
        return Collections.unmodifiableCollection(this.patterns);
    }

    // TODO Cache tokenizers (by text's hashes)
    public StringTokenizer getTokenizer(String text) {
        return new StringTokenizer(text, this.tokenDelimiter, true);
    }

    public Entry entry(String text) {
        return entries(text, true);
    }

    public Entry entries(String text) {
        return entries(text, false);
    }

    protected Entry entries(String text, boolean firstOnly) {
        MultipleEntry entry = null;
        for (java.util.regex.Pattern pattern : this.patterns) {
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                String finding = matcher.group();
                if (firstOnly) {
                    return new SimpleEntry(finding);
                }
                if (entry == null) {
                    entry = new SimpleMultipleEntry(finding);
                } else {
                    entry = entry.followedBy(new SimpleMultipleEntry(finding));
                }
            }
        }
        return entry;
    }

    protected boolean isDelimiter(String c) {
        return c.length() <= 1 && this.tokenDelimiter.contains(c);
    }

    protected boolean containsKeywords(String token) {
        for (String word : this.words) {
            // TODO Migrate to regular expressions? (with cached Pattern's)
            // TODO See http://stackoverflow.com/questions/6991038/case-insensitive-storage-and-unicode-compatibility/6996550#6996550
            if (StringUtils.containsIgnoreCase(token, word)) {
                return true;
            }
        }
        return false;
    }

    protected KeywordProcessor getKeywordProcessor() {
        switch (this.matchingMode) {
            case Exact:
                return word -> word;
            case Whole:
                return word -> linify(uncaptureGroups(word));
            default:
                return RegExpUtils::uncaptureGroups;
//            case Part:
//            default:
//                return word -> wordify(uncaptureGroups(word));
        }
    }

    void setVar(String name, String value) {
        this.variables.put(name, value);
    }
}
