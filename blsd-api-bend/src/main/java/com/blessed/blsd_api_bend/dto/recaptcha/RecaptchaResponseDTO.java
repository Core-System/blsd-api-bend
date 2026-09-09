package com.blessed.blsd_api_bend.dto.recaptcha;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RecaptchaResponseDTO {
    private boolean success;
    private Double score;
    private String action;

    @JsonProperty("challenge_ts")
    private String challengeTs;

    private String hostname;

    @JsonProperty("error-codes")
    private List<String> errorsCodes;
}
