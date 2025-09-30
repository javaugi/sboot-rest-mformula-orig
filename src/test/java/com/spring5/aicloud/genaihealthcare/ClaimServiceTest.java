/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare;

import com.spring5.aicloud.genaihealthcare.cccr.Claim;
import com.spring5.aicloud.genaihealthcare.cccr.ClaimRepository;
import com.spring5.aicloud.genaihealthcare.cccr.ClaimService;
import com.spring5.aicloud.genaihealthcare.cccr.ClaimType;
import com.spring5.aicloud.genaihealthcare.cccr.PrepayPublisher;
import java.time.LocalDate;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClaimServiceTest {

    // @Test
    public void professionalBeforeFacility_detectsPrepay() {
        ClaimRepository repo = mock(ClaimRepository.class);
        PrepayPublisher pub = mock(PrepayPublisher.class);

        Claim prof
                = Claim.builder()
                        .encounterId("prof-1")
                        .patientId("pi")
                        .claimType(ClaimType.PROFESSIONAL)
                        .claimDate(LocalDate.of(2025, 1, 1))
                        .build();

        // repo.save returns same claim (simplified)
        when(repo.findByExternalId(prof.externalId)).thenReturn(java.util.Optional.empty());
        when(repo.save(prof)).thenReturn(prof);
        when(repo.findByPatientAndTypeBetween(eq("p1"), eq("FACILITY"), any(), any()))
                .thenReturn(Collections.singletonList(Claim.builder().build()));

        ClaimService svc = new ClaimService(repo, pub);
        svc.ingestClaim(prof);

        verify(pub, times(1)).publishPrepayEvent(eq(prof), anyList());
    }
}
