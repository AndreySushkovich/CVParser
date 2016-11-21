package org.ansu.cvparser.parser;

import org.ansu.cvparser.parser.parts.Part;

/**
 * Author: Andrii Sushkovych
 * Date: 11/21/16
 */
public interface Finder {
    String getName();
    Part find(String text);
}
