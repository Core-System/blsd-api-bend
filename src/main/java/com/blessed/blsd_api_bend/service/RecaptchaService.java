package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.exception.usuario.RecaptchaValidationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class RecaptchaService {

    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Value("${recaptcha.secret-key:}")
    private String secretKey;

    public RecaptchaService() {
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newHttpClient();
    }

    public void validar(String token) {
        if (secretKey.isBlank()) {
            throw new RecaptchaValidationException("O CAPTCHA não está configurado no servidor.");
        }

        try {
            String body = "secret=" + encode(secretKey) + "&response=" + encode(token);
            HttpRequest request = HttpRequest.newBuilder(URI.create(VERIFY_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RecaptchaValidationException("Não foi possível validar o CAPTCHA.");
            }

            JsonNode resultado = objectMapper.readTree(response.body());
            if (!resultado.path("success").asBoolean(false)) {
                throw new RecaptchaValidationException("O CAPTCHA foi reprovado. Tente novamente.");
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RecaptchaValidationException("A validação do CAPTCHA foi interrompida.");
        } catch (IOException exception) {
            throw new RecaptchaValidationException("Não foi possível validar o CAPTCHA.");
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
