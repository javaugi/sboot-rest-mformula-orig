/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.unstructureddata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class FileParser {

    public static String extractText(String filePath) throws IOException {
        if (filePath.endsWith(".pdf")) {
            try (PDDocument doc = Loader.loadPDF(new File(filePath))) {
                return new PDFTextStripper().getText(doc);
            }
        } else if (filePath.endsWith(".docx")) {
            try (XWPFDocument doc = new XWPFDocument(new FileInputStream(filePath))) {
                return new XWPFWordExtractor(doc).getText();
            }
        }
        throw new IllegalArgumentException("Unsupported file format");
    }
}
