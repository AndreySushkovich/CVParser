package org.ansu.cvparser.replacer;

/**
 * Author: Andrii Sushkovych
 * Date: 11/26/16
 */
public interface Replacer {
    String all(String replacement);
    String first(String replacement);
}
