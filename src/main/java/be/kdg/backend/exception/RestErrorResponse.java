package be.kdg.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class RestErrorResponse {
    private int status;
    private String error;
    private String message;
    private Map<String, String> fieldErrors;
}
