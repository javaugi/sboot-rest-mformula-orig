/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.unstructureddata;

import dev.langchain4j.model.input.structured.StructuredPrompt;

@StructuredPrompt(
        """
    Extract the following from the resume:
    - Name: {{name}}
    - Email: {{email}}
    - Skills: {{skills}}
    - Education: {{education}}
    - Work Experience: {{work_experience}}
    Return JSON.
""")
public class ResumeExtractionPrompt {

    private final String resumeText;

    public ResumeExtractionPrompt(String resumeText) {
        this.resumeText = resumeText;
    }

    public String getResumeText() {
        return resumeText;
    }
}
