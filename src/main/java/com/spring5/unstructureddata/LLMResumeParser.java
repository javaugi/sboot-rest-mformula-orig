/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.unstructureddata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.hadoop.shaded.org.apache.http.entity.StringEntity;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author javaugi
 */
public class LLMResumeParser {
    //private static final String API_KEY = "your_api_key_here";
    //private static final String API_URL = "https://api.deepseek.com/v1/chat/completions"; // Hypothetical endpoint
        
    @Value("${spring.ai.deepseek.openai.base-url}")
    private String dsBaseUrl;
    @Value("${spring.ai.deepseek.openai.api-key}")
    private String dsApiKey;
    @Value("${spring.ai.deepseek.openai.chat.options.model}")
    private String dsModelDefault;

    private static final String INPUT_DIR = "resumes/resume052025.docx";
    private static final String OUTPUT_DIR = "outputres/resume052025_data.json";

    public static void main(String[] args) {
        LLMResumeParser main = new LLMResumeParser();
        main.processFileExtraction();
    }

    private void processFileExtraction() {
        String filePath = INPUT_DIR; // or "resume.docx"
        String jsonOutputPath = OUTPUT_DIR;

        try {
            // Step 1: Extract text from file
            String resumeText = extractTextFromFile(filePath);

            // Step 2: Call LLM API to structure the data
            String llmResponse = callLLMAPI(resumeText);

            // Step 3: Parse LLM response to JSON
            JsonNode structuredData = new ObjectMapper().readTree(llmResponse);

            // Save to file
            new ObjectMapper().writerWithDefaultPrettyPrinter()
                    .writeValue(new File(jsonOutputPath), structuredData);
            
            System.out.println("Structured resume data saved to " + jsonOutputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Extract text from PDF or DOCX
    private String extractTextFromFile(String filePath) throws IOException {
        if (filePath.endsWith(".pdf")) {
            try (PDDocument doc = Loader.loadPDF(new File(filePath))) {
                return new PDFTextStripper().getText(doc);
            }
        } else if (filePath.endsWith(".docx")) {
            try (XWPFDocument doc = new XWPFDocument(new FileInputStream(filePath))) {
                return new XWPFWordExtractor(doc).getText();
            }
        } else {
            throw new IllegalArgumentException("Unsupported file format. Use PDF or DOCX.");
        }
    }

    // Call LLM API with structured prompt
    private String callLLMAPI(String resumeText) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(dsBaseUrl);
            httpPost.setHeader("Authorization", "Bearer " + dsApiKey);
            httpPost.setHeader("Content-Type", "application/json");

            // Structured prompt to guide the LLM
            String prompt = """
                Extract the following details from this resume in JSON format:
                {
                    "name": "Full Name",
                    "email": "Email Address",
                    "phone": "Phone Number",
                    "education": [
                        {
                            "degree": "Degree Name",
                            "university": "University Name",
                            "year": "Graduation Year"
                        }
                    ],
                    "work_experience": [
                        {
                            "company": "Company Name",
                            "position": "Job Title",
                            "duration": "Employment Period"
                        }
                    ],
                    "skills": ["List", "of", "Skills"]
                }
                Resume Text: """ + resumeText;

            String payload = String.format("""
                {
                    "model": "deepseek-chat",
                    "messages": [{"role": "user", "content": "%s"}],
                    "temperature": 0.3
                }""", escapeJson(prompt));

            httpPost.setEntity((HttpEntity) new StringEntity(payload));
            return EntityUtils.toString(httpClient.execute(httpPost).getEntity());
        }
    }

    // Escape JSON strings to avoid syntax errors
    private static String escapeJson(String input) {
        return input.replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }    
}

/*
Step 1: Choose an LLM API
We'll use DeepSeek's API (hypothetical example) to process the resume.
(Replace with actual API endpoints if available.)

📌 Required Libraries
python
pip install requests python-docx PyPDF2  # For file handling
🔹 Step 2: Extract Text from Resume (PDF/DOCX)
Before sending to the LLM, extract raw text from the resume file.

📜 Python Code (Text Extraction)
python
from docx import Document
import PyPDF2
import re

def extract_text_from_pdf(pdf_path):
    with open(pdf_path, 'rb') as file:
        reader = PyPDF2.PdfReader(file)
        text = ""
        for page in reader.pages:
            text += page.extract_text()
    return text

def extract_text_from_docx(docx_path):
    doc = Document(docx_path)
    return "\n".join([para.text for para in doc.paragraphs])

# Example usage:
resume_text = extract_text_from_pdf("resume.pdf")  # or extract_text_from_docx("resume.docx")
print(resume_text[:500])  # Preview first 500 chars
🔹 Step 3: Call LLM API to Extract Structured Data
We’ll send the extracted text to the LLM with a structured prompt.

📜 Python Code (LLM API Call)
python
import requests
import json

def parse_resume_with_llm(resume_text, api_key):
    API_URL = "https://api.deepseek.com/v1/chat/completions"  # Hypothetical endpoint
    
    headers = {
        "Authorization": f"Bearer {api_key}",
        "Content-Type": "application/json"
    }
    
    # Structured prompt to guide the LLM
    prompt = """
    Extract the following details from this resume in JSON format:
    {
        "name": "Full Name",
        "email": "Email Address",
        "phone": "Phone Number",
        "linkedin": "LinkedIn Profile (if available)",
        "education": [
            {
                "degree": "Degree Name",
                "university": "University Name",
                "year": "Graduation Year"
            }
        ],
        "work_experience": [
            {
                "company": "Company Name",
                "position": "Job Title",
                "duration": "Employment Period",
                "responsibilities": "Key Responsibilities"
            }
        ],
        "skills": ["List", "of", "Technical", "Skills"]
    }

    Resume Text:
    """ + resume_text

    payload = {
        "model": "deepseek-chat",  # Hypothetical model name
        "messages": [{"role": "user", "content": prompt}],
        "temperature": 0.3  # Lower = more deterministic output
    }

    response = requests.post(API_URL, headers=headers, json=payload)
    
    if response.status_code == 200:
        llm_output = response.json()["choices"][0]["message"]["content"]
        # Extract JSON part from LLM's response (sometimes includes extra text)
        json_str = re.search(r'\{.*\}', llm_output, re.DOTALL).group()
        return json.loads(json_str)
    else:
        raise Exception(f"API Error: {response.text}")

# Example usage:
api_key = "your_api_key_here"
structured_data = parse_resume_with_llm(resume_text, api_key)

# Save to JSON file
with open("resume_data.json", "w") as f:
    json.dump(structured_data, f, indent=2)

print("Structured Resume Data Saved!")
🔹 Step 4: Example Output (resume_data.json)
json
{
  "name": "John Doe",
  "email": "john.doe@gmail.com",
  "phone": "+1 (123) 456-7890",
  "linkedin": "https://linkedin.com/in/johndoe",
  "education": [
    {
      "degree": "BSc Computer Science",
      "university": "Stanford University",
      "year": "2020"
    }
  ],
  "work_experience": [
    {
      "company": "Google",
      "position": "Software Engineer",
      "duration": "2020 - 2023",
      "responsibilities": "Developed backend services using Java and Spring Boot."
    }
  ],
  "skills": ["Python", "Java", "SQL", "AWS"]
}
🔹 Key Enhancements
Use a Fine-Tuned Model

If the API supports fine-tuning (e.g., OpenAI’s fine-tuning), train a model specifically for resume parsing.

Handle Multi-Page Resumes

Split large resumes into chunks and process them sequentially.

Post-Processing Validation

Use regex to validate emails, phone numbers, etc.

Batch Processing

Loop through a folder of resumes and extract data in bulk.

🔹 Alternative LLM APIs
If DeepSeek’s API is unavailable, you can use:

OpenAI GPT-4 (gpt-4-turbo)

Anthropic Claude (claude-3-opus)

Google Gemini (gemini-pro)

Just replace the API endpoint and payload structure accordingly.

🚀 Final Thoughts
This approach leverages LLMs for structured extraction without complex NLP pipelines.
Would you like help optimizing the prompt further or handling edge cases? 😊

could you give me a java code version to use LLM api?
Here’s a Java implementation that uses an LLM API (like DeepSeek, OpenAI, or Anthropic) to extract structured data from resumes (PDF/DOCX) and output it in JSON format.

📌 Key Steps
Extract text from PDF/DOCX using Apache POI or PDFBox.

Call the LLM API with a structured prompt.

Parse the LLM’s response into JSON.

🔧 Dependencies (pom.xml)
xml
<dependencies>
    <!-- Apache POI for DOCX -->
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi-ooxml</artifactId>
        <version>5.2.5</version>
    </dependency>
    <!-- PDFBox for PDF -->
    <dependency>
        <groupId>org.apache.pdfbox</groupId>
        <artifactId>pdfbox</artifactId>
        <version>3.0.0</version>
    </dependency>
    <!-- Jackson for JSON -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.16.1</version>
    </dependency>
    <!-- HTTP Client -->
    <dependency>
        <groupId>org.apache.httpcomponents</groupId>
        <artifactId>httpclient</artifactId>
        <version>4.5.13</version>
    </dependency>
</dependencies>
📜 Java Code (LLM Resume Parser)
java
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.*;

public class LLMResumeParser {

    private static final String API_KEY = "your_api_key_here";
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions"; // Hypothetical endpoint

    public static void main(String[] args) {
        String filePath = "resume.pdf"; // or "resume.docx"
        String jsonOutputPath = "resume_data.json";

        try {
            // Step 1: Extract text from file
            String resumeText = extractTextFromFile(filePath);

            // Step 2: Call LLM API to structure the data
            String llmResponse = callLLMAPI(resumeText);

            // Step 3: Parse LLM response to JSON
            JsonNode structuredData = new ObjectMapper().readTree(llmResponse);

            // Save to file
            new ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValue(new File(jsonOutputPath), structuredData);

            System.out.println("Structured resume data saved to " + jsonOutputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Extract text from PDF or DOCX
    private static String extractTextFromFile(String filePath) throws IOException {
        if (filePath.endsWith(".pdf")) {
            try (PDDocument doc = PDDocument.load(new File(filePath))) {
                return new PDFTextStripper().getText(doc);
            }
        } else if (filePath.endsWith(".docx")) {
            try (XWPFDocument doc = new XWPFDocument(new FileInputStream(filePath))) {
                return new XWPFWordExtractor(doc).getText();
            }
        } else {
            throw new IllegalArgumentException("Unsupported file format. Use PDF or DOCX.");
        }
    }

    // Call LLM API with structured prompt
    private static String callLLMAPI(String resumeText) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(API_URL);
            httpPost.setHeader("Authorization", "Bearer " + API_KEY);
            httpPost.setHeader("Content-Type", "application/json");

            // Structured prompt to guide the LLM
            String prompt = """
                Extract the following details from this resume in JSON format:
                {
                    "name": "Full Name",
                    "email": "Email Address",
                    "phone": "Phone Number",
                    "education": [
                        {
                            "degree": "Degree Name",
                            "university": "University Name",
                            "year": "Graduation Year"
                        }
                    ],
                    "work_experience": [
                        {
                            "company": "Company Name",
                            "position": "Job Title",
                            "duration": "Employment Period"
                        }
                    ],
                    "skills": ["List", "of", "Skills"]
                }
                Resume Text: """ + resumeText;

            String payload = String.format("""
                {
                    "model": "deepseek-chat",
                    "messages": [{"role": "user", "content": "%s"}],
                    "temperature": 0.3
                }""", escapeJson(prompt));

            httpPost.setEntity(new StringEntity(payload));
            return EntityUtils.toString(httpClient.execute(httpPost).getEntity());
        }
    }

    // Escape JSON strings to avoid syntax errors
    private static String escapeJson(String input) {
        return input.replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }
}
📂 Example Output (resume_data.json)
json
{
  "name" : "John Doe",
  "email" : "john.doe@gmail.com",
  "phone" : "+1 (123) 456-7890",
  "education" : [ {
    "degree" : "BSc Computer Science",
    "university" : "Stanford University",
    "year" : "2020"
  } ],
  "work_experience" : [ {
    "company" : "Google",
    "position" : "Software Engineer",
    "duration" : "2020-2023"
  } ],
  "skills" : [ "Java", "Python", "AWS" ]
}
🔹 Key Notes
API Flexibility:

Replace API_URL and model with the correct LLM API (e.g., OpenAI’s gpt-4-turbo or Anthropic’s claude-3).

Adjust the prompt for better extraction (e.g., ask for "key achievements").

Error Handling:

Add retry logic for API failures.

Validate JSON responses.

Performance:

For batch processing, use a thread pool to parallelize API calls.

Alternatives:

Use LangChain4J for easier LLM integration.

Would you like to extend this for multi-resume processing or add NLP post-processing?
*/