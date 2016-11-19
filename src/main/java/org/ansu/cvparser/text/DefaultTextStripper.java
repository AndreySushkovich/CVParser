package org.ansu.cvparser.text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;

/**
 * Author: Andrii Sushkovych
 * Date: 11/19/16
 */
public class DefaultTextStripper implements TextStripper {
    private Logger logger = LoggerFactory.getLogger(DefaultTextStripper.class);

    @Override
    public String textify(File file) {
        if (!file.exists()) {
            return null;
        }
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
