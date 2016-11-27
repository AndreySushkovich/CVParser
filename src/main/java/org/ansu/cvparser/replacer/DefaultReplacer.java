package org.ansu.cvparser.replacer;

import org.ansu.cvparser.rule.Rule;

import java.util.Collection;
import java.util.StringTokenizer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Andrii Sushkovych
 * Date: 11/26/16
 */
public class DefaultReplacer implements Replacer {
    private Rule rule;
    private Collection<Pattern> patterns;
    private StringTokenizer tokenizer;

    public DefaultReplacer(String text, Rule rule) {
        this.rule = rule;
        this.patterns = rule.patterns();
        this.tokenizer = rule.getTokenizer(text);
    }

    @Override
    public String all(String replacement) {
        return replace(matcher -> matcher.replaceAll(replacement));
    }

    @Override
    public String first(String replacement) {
        return replace(matcher -> matcher.replaceFirst(replacement));
    }

    private String replace(Function<Matcher, String> behavior) {
        StringBuilder builder = new StringBuilder();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (rule.isPotentialMatch(token)) {
                for (Pattern pattern : patterns) {
                    token = behavior.apply(pattern.matcher(token));
                }
            }
            builder.append(token);
        }
        return builder.toString();
    }

}
