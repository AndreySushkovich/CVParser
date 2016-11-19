package org.ansu.cvparser.text;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Author: Andrii Sushkovych
 * Date: 11/19/16
 */
public class PDFTextStripper implements TextStripper {
    private Logger logger = LoggerFactory.getLogger(PDFTextStripper.class);

    @Override
    public String textify(File file) {
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
            logger.error(e.getMessage(), e);
        }
        return writer.toString();
    }
}
