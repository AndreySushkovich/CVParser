package org.ansu.cvparser.rule;

import org.ansu.cvparser.replacer.Replacer;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.ansu.cvparser.rule.Rule.MatchingMode;
import static org.junit.Assert.assertEquals;

/**
 * Author: Andrii Sushkovych
 * Date: 11/22/16
 */

public class RuleTest {
    private Rule groupW, groupL;

    @Before
    public void init() {
        groupW = new Rule(Arrays.asList("(CSS|css)3?"), RuleFactory.TokenDelimiter.Word, MatchingMode.Part);
        groupL = new Rule(Arrays.asList("Courses[^\\S\\x0a\\x0d\\r]*([,\\.&\\+]|and)[^\\S\\x0a\\x0d\\r]*Trainings?"),
                RuleFactory.TokenDelimiter.Word, MatchingMode.Part);
    }

    @Test
    public void testGroupWords() {
        String original = "with СSS3, CSS and CSS3, also  css. Fixed CSSReplacer and CSS";
        String replaced = "with СSS3, (CSS) and (CSS3), also  (css). Fixed CSSReplacer and (CSS)";
        // first occurrence of "CSS3" actually starts with a Cyrillic character -> not replaced

        Replacer replacer = groupW.replacer(original);
        String result = replacer.all("$1($2)$3");
        System.out.println(result);
        assertEquals(replaced, result);
    }

    @Test
    public void testGroupLines() {
        String original = "Gap\nCourses & Trainings​: _______________________________________________________ \nGap";
        String replaced = "Gap\n(Courses & Trainings​: _______________________________________________________ )\nGap";

        Replacer replacer = groupL.replacer(original);
        String result = replacer.all("($0)");
        System.out.println(result);
        assertEquals(replaced, result);
    }
}
