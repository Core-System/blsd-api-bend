package com.blessed.blsd_api_bend.config;

import com.blessed.blsd_api_bend.dto.recaptcha.RecaptchaResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "recaptcha-client", url = "https://www.google.com/recaptcha/api")
public interface RecaptchaCliente {

    @PostMapping(value = "/siteverify", consumes = "application/x-www-form-urlencoded")
    RecaptchaResponseDTO verificar(@RequestBody String corpoFormUrlEncoded);
}
