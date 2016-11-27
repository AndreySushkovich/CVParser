package org.ansu.cvparser.converter;

import org.ansu.cvparser.exception.ConversionException;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.*;

/**
 * Author: Andrii Sushkovych
 * Date: 11/19/16
 */
public class PDFTextConverter extends TextConverter {
    @Override
    public String textify(File file) throws ConversionException {
        if (!file.exists()) {
            return null;
        }
        Writer writer = new StringWriter();
        try {
            PDDocument doc = PDDocument.load(file);
            org.apache.pdfbox.text.PDFTextStripper stripper = new org.apache.pdfbox.text.PDFTextStripper();
            stripper.writeText(doc, writer);
            doc.close();
            writer.close();
        } catch (IOException e) {
            throw new ConversionException(e);
        }
        return writer.toString();
    }
}
