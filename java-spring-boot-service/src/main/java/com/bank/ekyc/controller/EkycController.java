package com.bank.ekyc.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/banking/ekyc")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EkycController {

    @Value("${app.python-ai-url}")
    private String pythonAiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/submit")
    public ResponseEntity<?> submitEkyc(
            @RequestParam("customerId") String customerId,
            @RequestParam("idCard") MultipartFile idCard,
            @RequestParam("selfie") MultipartFile selfie) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            
            // ⚠️ ប្តូរពី "id_card" មកជា "idCard" វិញ ដើម្បីឱ្យត្រូវគ្នាជាមួយ Python FastAPI Parameter
            body.add("idCard", new ByteArrayResource(idCard.getBytes()) {
                @Override
                public String getFilename() {
                    return idCard.getOriginalFilename();
                }
            });
            
            body.add("selfie", new ByteArrayResource(selfie.getBytes()) {
                @Override
                public String getFilename() {
                    return selfie.getOriginalFilename();
                }
            });

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> aiResponse = restTemplate.postForEntity(pythonAiUrl, requestEntity, String.class);

            return ResponseEntity.status(aiResponse.getStatusCode()).body(aiResponse.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}