package org.ansu.cvparser.parser.entries;

/**
 * Simple {@link Part} implementation with {@code canonical} equal to {@code original}
 *
 * @author Andrii Sushkovych
 * @since 11/21/16
 */
public class AtomicEntry implements Part {
    private String original;

    public AtomicEntry(String original) {
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
