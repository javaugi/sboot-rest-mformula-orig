/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.unstructureddata;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import opennlp.tools.namefind.*;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;

public class ResumeParser {
    private static final String INPUT_DIR = "resumes/David Lee_res25f6.pdf";
    private static final String OUTPUT_DIR = "outputres/resume25f6_poi_data.json" ;
    
    public static void main(String[] args) {
        String docxPath = INPUT_DIR;
        String jsonPath = OUTPUT_DIR;

        try {
            // 1. Extract raw text from DOCX
            String resumeText = readDocxFile(docxPath);

            // 2. Parse useful info
            ObjectNode candidateData = extractCandidateInfo(resumeText);

            // 3. Save to JSON
            new ObjectMapper().writeValue(new File(jsonPath), candidateData);
            System.out.println("Resume data extracted successfully!");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static String readDocxFile(String filePath) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(filePath))) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
            return extractor.getText();
        }
    }

    private static ObjectNode extractCandidateInfo(String text) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode data = mapper.createObjectNode();

        // Extract Name (using NLP or RegEx)
        data.put("name", extractName(text));

        // Extract Email (RegEx)
        data.put("email", extractEmail(text));

        // Extract Phone (RegEx)
        data.put("phone", extractPhone(text));

        // Extract Skills (Keyword Matching)
        data.putPOJO("skills", extractSkills(text));

        // Extract Experience (RegEx + NLP)
        data.put("experience_years", extractExperience(text));

        return data;
    }

    // Helper Methods
    private static String extractName(String text) {
        // Simple NLP-based name extraction (for better accuracy, train a model)
        try {
            NameFinderME nameFinder = new NameFinderME(
                new TokenNameFinderModel(new File("en-ner-person.bin")) // Download from OpenNLP
            );
            String[] tokens = SimpleTokenizer.INSTANCE.tokenize(text);
            Span[] names = nameFinder.find(tokens);
            return names.length > 0 ? tokens[names[0].getStart()] : "Unknown";
        } catch (Exception e) {
            return "Unknown (Error: " + e.getMessage() + ")";
        }
    }

    private static String extractEmail(String text) {
        Matcher matcher = Pattern.compile(
            "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}"
        ).matcher(text);
        return matcher.find() ? matcher.group() : "Not Found";
    }

    private static String extractPhone(String text) {
        Matcher matcher = Pattern.compile(
            "(\\+\\d{1,3}[- ]?)?\\d{10}"  // Supports +91 1234567890
        ).matcher(text);
        return matcher.find() ? matcher.group() : "Not Found";
    }

    private static List<String> extractSkills(String text) {
        Set<String> skills = new HashSet<>(Arrays.asList(
            "Java", "Python", "SQL", "Spring", "Machine Learning", "AWS"
        )); // Add more skills here
        List<String> foundSkills = new ArrayList<>();
        for (String skill : skills) {
            if (text.toLowerCase().contains(skill.toLowerCase())) {
                foundSkills.add(skill);
            }
        }
        return foundSkills;
    }

    private static String extractExperience(String text) {
        Matcher matcher = Pattern.compile(
            "(\\d+)(\\+)?\\s*(years?|yrs?)"  // Matches "5 years", "3+ yrs"
        ).matcher(text);
        return matcher.find() ? matcher.group() : "Not Specified";
    }    
}
/*
Java Program: Resume Parser for Candidate Filtering
This program reads a Word document (DOCX), extracts key details (Name, Email, Skills, Experience), and outputs them in a structured format (JSON).

1. Required Dependencies (Maven pom.xml)
xml
<dependencies>
    <!-- Apache POI for Word DOCX parsing -->
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi-ooxml</artifactId>
        <version>5.2.5</version>
    </dependency>
    <!-- Jackson for JSON output -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.16.1</version>
    </dependency>
    <!-- OpenNLP for NLP (Name, Skills Extraction) -->
    <dependency>
        <groupId>org.apache.opennlp</groupId>
        <artifactId>opennlp-tools</artifactId>
        <version>2.3.0</version>
    </dependency>
</dependencies>
2. Java Code: Extract Resume Data
java
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import opennlp.tools.namefind.*;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;

public class ResumeParser {

    public static void main(String[] args) {
        String docxPath = "resume.docx";
        String jsonPath = "candidate_data.json";

        try {
            // 1. Extract raw text from DOCX
            String resumeText = readDocxFile(docxPath);

            // 2. Parse useful info
            ObjectNode candidateData = extractCandidateInfo(resumeText);

            // 3. Save to JSON
            new ObjectMapper().writeValue(new File(jsonPath), candidateData);
            System.out.println("Resume data extracted successfully!");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static String readDocxFile(String filePath) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(filePath))) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
            return extractor.getText();
        }
    }

    private static ObjectNode extractCandidateInfo(String text) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode data = mapper.createObjectNode();

        // Extract Name (using NLP or RegEx)
        data.put("name", extractName(text));

        // Extract Email (RegEx)
        data.put("email", extractEmail(text));

        // Extract Phone (RegEx)
        data.put("phone", extractPhone(text));

        // Extract Skills (Keyword Matching)
        data.putPOJO("skills", extractSkills(text));

        // Extract Experience (RegEx + NLP)
        data.put("experience_years", extractExperience(text));

        return data;
    }

    // Helper Methods
    private static String extractName(String text) {
        // Simple NLP-based name extraction (for better accuracy, train a model)
        try {
            NameFinderME nameFinder = new NameFinderME(
                new TokenNameFinderModel(new File("en-ner-person.bin")) // Download from OpenNLP
            );
            String[] tokens = SimpleTokenizer.INSTANCE.tokenize(text);
            Span[] names = nameFinder.find(tokens);
            return names.length > 0 ? tokens[names[0].getStart()] : "Unknown";
        } catch (Exception e) {
            return "Unknown (Error: " + e.getMessage() + ")";
        }
    }

    private static String extractEmail(String text) {
        Matcher matcher = Pattern.compile(
            "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}"
        ).matcher(text);
        return matcher.find() ? matcher.group() : "Not Found";
    }

    private static String extractPhone(String text) {
        Matcher matcher = Pattern.compile(
            "(\\+\\d{1,3}[- ]?)?\\d{10}"  // Supports +91 1234567890
        ).matcher(text);
        return matcher.find() ? matcher.group() : "Not Found";
    }

    private static List<String> extractSkills(String text) {
        Set<String> skills = new HashSet<>(Arrays.asList(
            "Java", "Python", "SQL", "Spring", "Machine Learning", "AWS"
        )); // Add more skills here
        List<String> foundSkills = new ArrayList<>();
        for (String skill : skills) {
            if (text.toLowerCase().contains(skill.toLowerCase())) {
                foundSkills.add(skill);
            }
        }
        return foundSkills;
    }

    private static String extractExperience(String text) {
        Matcher matcher = Pattern.compile(
            "(\\d+)(\\+)?\\s*(years?|yrs?)"  // Matches "5 years", "3+ yrs"
        ).matcher(text);
        return matcher.find() ? matcher.group() : "Not Specified";
    }
}
🔹 Key Features of This Parser
Extracted Field	Method Used	Example Output
Name	OpenNLP Name Entity Recognition	"John Doe"
Email	Regex	"john@example.com"
Phone	Regex	"+91 9876543210"
Skills	Keyword Matching	["Java", "Spring", "AWS"]
Experience	Regex + NLP	"5+ years"
📂 Expected JSON Output (candidate_data.json)
json
{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+91 9876543210",
  "skills": ["Java", "Spring", "SQL"],
  "experience_years": "5 years"
}
🚀 Possible Enhancements
Use Machine Learning (e.g., spaCy, NLTK) for better entity recognition.

Parse Tables in DOCX (common in resumes) using XWPFTable.

Integrate with a Database to store candidate profiles.

Add a Scoring System to rank candidates based on skills/experience.
*/