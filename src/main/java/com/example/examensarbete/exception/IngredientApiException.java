package com.example.examensarbete.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IngredientApiException extends RuntimeException {

    private final HttpStatusCode statusCode;
    private final String statusText;
    private final byte[] responseBody;

    public IngredientApiException(HttpStatusCode statusCode, String statusText, byte[] responseBody) {
        super("Ingredient API exception: " + statusText);
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.responseBody = responseBody;
    }
}
