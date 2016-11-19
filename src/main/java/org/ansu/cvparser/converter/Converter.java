package org.ansu.cvparser.converter;

import org.ansu.cvparser.exception.ConversionException;

import java.io.File;

/**
 * Author: Andrii Sushkovych
 * Date: 11/19/16
 */
public interface Converter {
    void convert(File src, File dst) throws ConversionException;
}
