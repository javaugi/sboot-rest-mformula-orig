package com.spring5.utils.common;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orchestration_context")
public class OrchestrationContextEntity {

    @Id
    private String correlationId;

    @Lob
    //@Convert(converter = ClaimEventConverter.class)
    private ReactiveClaimEvent claimEvent;

    @Lob
    //@Convert(converter = HashMapConverter.class)
    private Map<String, Object> processVariables;

    private LocalDateTime timestamp;
    private OrchestrationStatus status;

    //@ElementCollection
    //private List<OrchestrationStepEntity> completedSteps;

    // Getters and setters
}
