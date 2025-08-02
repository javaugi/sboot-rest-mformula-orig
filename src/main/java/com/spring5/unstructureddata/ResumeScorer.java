/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.unstructureddata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

/*
1. Resume Scoring System - Concept
Assign scores based on:
    Skills Match (e.g., "Java" = +10 points)
    Experience Years (e.g., "5+ years" = +20 points)
    Education (e.g., "Master’s Degree" = +15 points)
*/
public class ResumeScorer {
    private static final Map<String, Integer> SKILL_WEIGHTS = Map.of(
        "Java", 10,
        "Python", 8,
        "AWS", 12,
        "Spring", 10
    );

    private static final int YEAR_MULTIPLIER = 5; // 5 points per year

    public static int calculateScore(JsonNode resume) {
        int score = 0;

        // 1. Skills Matching
        JsonNode skills = resume.get("skills");
        if (skills != null) {
            for (JsonNode skill : skills) {
                String skillName = skill.asText().toLowerCase();
                score += SKILL_WEIGHTS.entrySet().stream()
                    .filter(e -> skillName.contains(e.getKey().toLowerCase()))
                    .mapToInt(Map.Entry::getValue)
                    .sum();
            }
        }

        // 2. Experience (Years)
        JsonNode workExp = resume.get("work_experience");
        if (workExp != null) {
            for (JsonNode job : workExp) {
                String duration = job.get("duration").asText();
                int years = parseYears(duration);
                score += years * YEAR_MULTIPLIER;
            }
        }

        // 3. Education Bonus
        JsonNode education = resume.get("education");
        if (education != null && education.isArray()) {
            if (education.size() > 0) {
                score += 15; // Bonus for having education
            }
        }

        return score;
    }

    private static int parseYears(String duration) {
        // Example: "2020-2023" → 3 years
        if (duration.matches("\\d{4}-\\d{4}")) {
            String[] parts = duration.split("-");
            return Integer.parseInt(parts[1]) - Integer.parseInt(parts[0]);
        }
        return 0; // Default if parsing fails
    }

    public static void main(String[] args) throws Exception {
        String resumeJson = """
            {
                "skills": ["Java", "Spring Boot"],
                "work_experience": [
                    { "duration": "2020-2023" }
                ],
                "education": [{ "degree": "BSc" }]
            }
            """;
        JsonNode resume = new ObjectMapper().readTree(resumeJson);
        System.out.println("Resume Score: " + calculateScore(resume)); // Output: 45 (Java=10, Spring=10, 3y*5=15, Education=10)
    }
}
