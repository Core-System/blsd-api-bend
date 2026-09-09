package com.blessed.blsd_api_bend.exception.recaptcha;

public class RecaptchaInvalidException extends RuntimeException {
    public RecaptchaInvalidException(String message) {
        super(message);
    }
}
