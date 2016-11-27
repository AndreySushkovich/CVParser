package org.ansu.cvparser.finder.entries;

/**
 * Author: Andrii Sushkovych
 * Date: 11/22/16
 */
public class SimpleMultipleEntry extends MultipleEntry {
    private String original;

    public SimpleMultipleEntry(String original) {
        this.original = original;
    }

    @Override
    public String getOriginal() {
        return this.original;
    }

    @Override
    public String getCanonical() {
        return this.original;
    }
}
