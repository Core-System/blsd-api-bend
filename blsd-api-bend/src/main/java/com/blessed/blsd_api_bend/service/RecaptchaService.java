package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.config.RecaptchaCliente;
import com.blessed.blsd_api_bend.dto.recaptcha.RecaptchaResponseDTO;
import com.blessed.blsd_api_bend.exception.recaptcha.RecaptchaInvalidException;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class RecaptchaService {
    private static final Logger log = LoggerFactory.getLogger(RecaptchaService.class);
    private final RecaptchaCliente recaptchaCliente;

    @Value("${recaptcha.secret-key}")
    private String secretKey;

    @Value("${recaptcha.score-minimo:0.5}")
    private Double scoreMinimo;

    public RecaptchaService(RecaptchaCliente recaptchaCliente) {
        this.recaptchaCliente = recaptchaCliente;
    }

    public void validar(String token, String actionEsperada){
        if(token == null || token.isBlank()){
            throw new RecaptchaInvalidException("Verificação de segurança ausente");
        }

        RecaptchaResponseDTO resposta;
        try {
            String corpo = "secret=" + URLEncoder.encode(secretKey, StandardCharsets.UTF_8)
                    + "&response=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
            resposta = recaptchaCliente.verificar(corpo);
        } catch (Exception e) {
            log.error("Falha ao chamar a API do reCAPTCHA", e);
            throw new RecaptchaInvalidException("Não foi possível validar verificação de segurança.");
        }

        if (resposta == null || !resposta.isSuccess()){
            throw new RecaptchaInvalidException("Verificação de segurança inválida.");
        }

        if(actionEsperada != null && !actionEsperada.equals(resposta.getAction())){
            throw new RecaptchaInvalidException("Verificação de segurança não corresponde à ação esperada.");
        }

        if(resposta.getScore() < scoreMinimo){
            log.warn("reCaptcha com score baixo ({}) para action '{}'", resposta.getScore(), actionEsperada);
            throw new RecaptchaInvalidException("Atividade suspeita detectada. Tente novamente mais tarde.");
        }
    }


}
