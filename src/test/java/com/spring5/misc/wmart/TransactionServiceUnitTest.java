/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.misc.wmart;

import com.stripe.service.financialconnections.TransactionService;
import io.r2dbc.postgresql.message.backend.ReadyForQuery.TransactionStatus;
import java.util.UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.neo4j.kernel.impl.transaction.tracing.TransactionEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class TransactionServiceUnitTest {

    /*
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void processTransaction_ValidTransaction_ShouldPublishEvent() {
        // Given
        TransactionRequest request = new TransactionRequest("user123", 100.0, "USD");
        TransactionEntity savedEntity = TransactionEntity.builder()
            .id(UUID.randomUUID())
            .amount(100.0)
            .currency("USD")
            .userId("user123")
            .status(TransactionStatus.PENDING)
            .build();

        when(transactionRepository.save(any(TransactionEntity.class)))
            .thenReturn(savedEntity);

        // When
        TransactionResponse response = transactionService.processTransaction(request);

        // Then
        assertThat(response.getStatus()).isEqualTo(TransactionStatus.PENDING);
        verify(kafkaTemplate).send(eq("transactions"), anyString(), any(TransactionEvent.class));
        verify(transactionRepository).save(any(TransactionEntity.class));
    }

    @Test
    void getTransaction_ExistsInCache_ShouldReturnCached() {
        // Given
        String transactionId = "txn123";
        TransactionResponse cachedResponse = new TransactionResponse(transactionId, 100.0,
            "USD", "user123", TransactionStatus.COMPLETED);

        when(redisTemplate.opsForValue().get("transaction:" + transactionId))
            .thenReturn(cachedResponse);

        // When
        TransactionResponse result = transactionService.getTransaction(transactionId);

        // Then
        assertThat(result).isEqualTo(cachedResponse);
        verify(transactionRepository, never()).findById(anyString());
    }
    // */
}
