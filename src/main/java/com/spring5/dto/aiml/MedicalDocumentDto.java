/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dto.aiml;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;

@Data
@Builder(toBuilder = true)
public class MedicalDocumentDto {

    @Schema(description = "Unique identifier of the project", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Version
    private Long version; // For optimistic locking

    @Schema(description = "Title of the document", example = "Medical Document Dashboard", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Title is mandatory")
    private String title;

    @Column("text_content")
    private String textContent;

    private String specialty;

    @Column("document_type")
    private String documentType;

    @Schema(description = "Timestamp when the medical document was created", example = "2024-10-27T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    @Column("publication_date")
    private OffsetDateTime publicationDate;

    @Column("embedding")
    private float[] embedding;

    @Column("pdf_content")
    private byte[] pdfContent;

    public void setEmbeddingFromList(List<Double> embeddingList) {
        this.embedding = new float[embeddingList.size()];
        for (int i = 0; i < embeddingList.size(); i++) {
            this.embedding[i] = embeddingList.get(i).floatValue();
        }
    }
}
