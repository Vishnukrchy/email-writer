package com.email.email_writer_sb.email_writer_app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class EmailGeneratorServiceImpl implements EmailGeneratorService {
    @Value("${gemini.api.url}")
    private String geminiAPIUrl;

    @Value("${gemini.api.key}")
    private String geminiAPIKey;
    // WebClient is reactive web client library and its used to make http requests instead of RestTemplate inside Spring boot application
    private final WebClient webClient;

    public EmailGeneratorServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }


    public String generateEmailResponse(EmailRequest emailRequest) {
        // logic to generate email response
        // Step 1 : Build Prompt
        String prompt = buildPrompt(emailRequest);

     /* Step 2 : Call OpenAI API  Or craft a request like gemini api
        {
            "contents": [{
            "parts":[{
                "text":"Explain how AI works"
            }]
        }]
        }
         */
        /*
        Map<String,Object> requestBody = Map.of("contents", Map.of("parts",Map.of("text",prompt)));
        Map<String, Object> requestBody = new HashMap<>();
           requestBody.put("contents", new Object[] {
             new HashMap<String, Object>() {{
        put("parts", new Object[] {
            new HashMap<String, Object>() {{
                put("text", prompt);
            }}
        });
    }}
});
*/


        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]
                                {Map.of("text", prompt)})});
        // Step 3 : Return Email Response Do Request and get response
        /*
        now we will call gemini api by posting the request body
         */
       // String response = webClient.post().uri(geminiAPIUrl).header("Content-Type", "application/json").header("Authorization", geminiAPIKey).bodyValue(requestBody).retrieve().bodyToMono(String.class).block();

        String response = webClient.post()
                .uri(geminiAPIUrl + geminiAPIKey)
                .header("Content-Type","application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();


        // Step 4 : Extract Email Response
        return extractEmailResponse(response);

    }

    private String extractEmailResponse(String response) {
        // logic to extract email response
        try {
            // ObjectMapper is used to convert json to java object and vice versa its deals with json
            ObjectMapper objectMapper = new ObjectMapper();
            //JasonNode is used with ObjectMapper to
            // readTree is used to convert json to java object that turs it tree like structure thats help us to navigate
            JsonNode rootNode = objectMapper.readTree(response);
            // get the first element of the array
            // JsonNode firstElement = jasonNode.get(0);

            return rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to extract email response" + e.getMessage());


        }


    }

    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a professional email reply for hte following email content. Please don't generate a subject line ");
        if (emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
            prompt.append("Use a ").append(emailRequest.getTone()).append(" tone.");
        }
        prompt.append("\nOriginal email: \n").append(emailRequest.getEmailContent());
        return prompt.toString();
    }

}
