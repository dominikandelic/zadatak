package hr.dandelic.zadatak.api;

import lombok.Data;

@Data
public class RestApiError {
    private String message;

    public RestApiError(String message) {
        this.message = message;
    }
}
