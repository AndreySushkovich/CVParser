package org.ansu.cvparser;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Andrii Sushkovych
 * Date: 11/19/16
 */
public class Section {
    private String content;

    private Section parent;
    private List<Section> children;

    public Section(String content) {
        this.content = content;
        this.children = new ArrayList<Section>();
    }

    @Deprecated
    // TODO Replace with specific methods like find(), replaceAll(), etc
    public String getContent() {
        return this.content;
    }
}
