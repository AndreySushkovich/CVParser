package org.ansu.cvparser.finder.entries;

/**
 * Simple {@link Entry} implementation with {@code canonical} equal to {@code original}
 *
 * @author Andrii Sushkovych
 * @since 11/21/16
 */
public class SimpleEntry implements Entry {
    private String original;

    public SimpleEntry(String original) {
        this.original = original;
    }

    @Override
    public String getOriginal() {
        return original;
    }

    @Override
    public String getCanonical() {
        return original;
    }
}
