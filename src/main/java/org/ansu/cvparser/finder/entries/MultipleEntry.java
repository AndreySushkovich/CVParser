package org.ansu.cvparser.finder.entries;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Author: Andrii Sushkovych
 * Date: 11/22/16
 */
public abstract class MultipleEntry implements Entry, Iterable<Entry> {
    private static class EntryIterator implements Iterator<Entry> {
        private MultipleEntry cursor;

        public EntryIterator(MultipleEntry head) {
            this.cursor = head;
        }
        @Override

        public boolean hasNext() {
            return this.cursor.next != null;
        }

        @Override
        public Entry next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            MultipleEntry current = this.cursor;
            this.cursor = this.cursor.next;
            return current;
        }
    }

    private MultipleEntry next;

    @Override
    public Iterator<Entry> iterator() {
        return new EntryIterator(this);
    }

    public MultipleEntry followedBy(MultipleEntry entry) {
        this.next = entry;
        return entry;
    }

    public boolean isSingle() {
        return this.next == null;
    }
}
