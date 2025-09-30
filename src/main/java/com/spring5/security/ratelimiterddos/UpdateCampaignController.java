/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.security.ratelimiterddos;

import com.spring5.entity.Campaign;
import com.spring5.repository.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/*
Q: How would you design a REST API for vehicle update status reporting?
Sample Answer:
"I would create a RESTful API with these key endpoints:
    POST /api/vehicles/{vin}/updates - To initiate an update
    GET /api/vehicles/{vin}/updates - To check update status
    GET /api/updates - For aggregated update statistics

Each update would have states like QUEUED, DOWNLOADING, VERIFYING, INSTALLING, SUCCESS, FAILED. I'd use Spring's
    @RestController with proper HTTP status codes, and implement HATEOAS for discoverability. For performance with
    many vehicles, I'd include pagination and filtering capabilities."
 */
@RestController
@RequestMapping("/api/campaigns")
public class UpdateCampaignController {

    @Autowired
    private CampaignRepository campaignRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Campaign> getCampaign(@PathVariable Long id) {
        Campaign campaign
                = campaignRepository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        campaign
                = campaignRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campaign not found"));
        // Entity Tags - an opaque string that represents the state of a resource at a specific point in
        // time
        String etag = calculateETag(campaign);

        return ResponseEntity.ok().eTag(etag).body(campaign);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCampaign(
            @PathVariable Long id,
            @RequestBody Campaign updatedCampaign,
            @RequestHeader("If-Match") String ifMatch) {

        Campaign existingCampaign
                = campaignRepository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));

        String currentETag = calculateETag(existingCampaign);

        if (!currentETag.equals(ifMatch)) {
            return ResponseEntity.status(412) // Precondition Failed
                    .body("{\"error\": \"ETag mismatch. Resource was modified by another request.\"}");
        }

        // Update the campaign
        existingCampaign.setName(updatedCampaign.getName());
        existingCampaign.setTargetVersion(updatedCampaign.getTargetVersion());
        // ... other fields

        Campaign savedCampaign = campaignRepository.save(existingCampaign);
        String newETag = calculateETag(savedCampaign);

        return ResponseEntity.ok().eTag(newETag).body(savedCampaign);
    }

    private String calculateETag(Campaign campaign) {
        // Create a hash of the important fields that determine consistency
        String input
                = campaign.getId() + ":" + campaign.getVersion() + ":" + campaign.getLastModified();
        return "\"" + DigestUtils.md5DigestAsHex(input.getBytes()) + "\"";
    }
}
