package be.kdg.backend.dto.restreamer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticationRequest {
    private String username;
    private String password;
}
