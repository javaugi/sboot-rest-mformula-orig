/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.unstructureddata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 *
 * @author javaugi
 */
public class PdfToJasonConverter {
    
    public static void main(String[] args) {
        String pdfPath = "input.pdf";    // Path to your PDF file
        String jsonPath = "output.json"; // Path to save JSON file

        try {
            // Step 1: Read PDF file
            String pdfText = readPdf(pdfPath);

            // Step 2: Convert text to JSON structure
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode jsonNode = mapper.createObjectNode();
            jsonNode.put("filename", pdfPath);
            jsonNode.put("content", pdfText);

            // Step 3: Write JSON to file
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(jsonPath), jsonNode);

            System.out.println("Successfully converted PDF to JSON!");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static String readPdf(String pdfPath) throws IOException {
        try (PDDocument document = Loader.loadPDF(new File(pdfPath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }    
}

/*
Below is a Java program that uses the Apache PDFBox library to extract text from a PDF file and then writes the extracted content into a JSON file using the Jackson library.

📜 Java Program: PDF to JSON Converter
java
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;

public class PdfToJsonConverter {

    public static void main(String[] args) {
        String pdfPath = "input.pdf";    // Path to your PDF file
        String jsonPath = "output.json"; // Path to save JSON file

        try {
            // Step 1: Read PDF file
            String pdfText = readPdf(pdfPath);

            // Step 2: Convert text to JSON structure
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode jsonNode = mapper.createObjectNode();
            jsonNode.put("filename", pdfPath);
            jsonNode.put("content", pdfText);

            // Step 3: Write JSON to file
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(jsonPath), jsonNode);

            System.out.println("Successfully converted PDF to JSON!");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static String readPdf(String pdfPath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(pdfPath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
}
📌 Steps to Run the Program
Add Required Dependencies (Maven pom.xml):

xml
<dependencies>
    <!-- Apache PDFBox for PDF parsing -->
    <dependency>
        <groupId>org.apache.pdfbox</groupId>
        <artifactId>pdfbox</artifactId>
        <version>3.0.0</version>
    </dependency>
    <!-- Jackson for JSON handling -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.16.1</version>
    </dependency>
</dependencies>
(If you're not using Maven, download the JARs from PDFBox and Jackson.)

Place a PDF file named input.pdf in your project directory (or modify pdfPath).

Run the program, and it will generate output.json with:

json
{
  "filename" : "input.pdf",
  "content" : "Extracted text from PDF goes here..."
}
🔹 Possible Enhancements
Extract structured data (tables, metadata) instead of raw text.

Split into sections (e.g., paragraphs, headings) for better JSON structure.

Handle large PDFs with pagination.
*/