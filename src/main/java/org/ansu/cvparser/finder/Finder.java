package org.ansu.cvparser.finder;

import org.ansu.cvparser.finder.entries.Entry;

/**
 * TODO It's not obvious that it only finds FIRST OCCURRENCE
 * TODO Rather it should be configurable
 *
 * Author: Andrii Sushkovych
 * Date: 11/21/16
 */
public interface Finder {
    String getName();
    Entry find(String text);
}
