package org.ansu.cvparser.converter;

import org.ansu.cvparser.exception.ConversionException;

import java.io.*;

/**
 * Author: Andrii Sushkovych
 * Date: 11/19/16
 */
public abstract class TextConverter implements Converter {

    public abstract String textify(File file) throws ConversionException;

    @Override
    public void convert(File src, File dst) throws ConversionException {
        String text = textify(src);
        try (PrintWriter writer = new PrintWriter(dst, "UTF-8")) {
            writer.print(text);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new ConversionException(e);
        }
    }
}
