package org.ansu.cvparser.converter;

import org.ansu.cvparser.exception.ConversionException;

import java.io.*;
import java.nio.file.Files;

/**
 * Author: Andrii Sushkovych
 * Date: 11/19/16
 */
public class DefaultTextConverter extends TextConverter {
    @Override
    public String textify(File file) throws ConversionException {
        if (!file.exists()) {
            return null;
        }
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new ConversionException(e);
        }
    }
}
