package be.kdg.backend.util;

import be.kdg.backend.dto.restreamer.AuthenticationRequest;
import be.kdg.backend.dto.restreamer.AuthenticationResponse;
import be.kdg.backend.exception.AuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;

@Slf4j
@Component
public class AuthenticationHelper {
    private static final String LOGIN_ENDPOINT = "/api/login";
    private static final String REFRESH_ENDPOINT = "/api/login/refresh";
    private final RestTemplate restTemplate;
    @Value("${app.stream-connection.http-url}")
    private String STREAMING_SERVER_URL;
    @Value("${app.stream-connection.username}")
    private String USERNAME;
    @Value("${app.stream-connection.password}")
    private String PASSWORD;
    private String accessToken;
    private String refreshToken;
    private LocalDateTime accessTokenExpirationTime;
    private LocalDateTime refreshTokenExpirationTime;

    public AuthenticationHelper(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Returns the access token.
     * If the access token is expired, it will be refreshed using the refresh token.
     *
     * @return the access token
     */
    public String getAccessToken() {
        if (accessToken == null || refreshToken == null || refreshTokenExpirationTime.isBefore(LocalDateTime.now().plusMinutes(1))) {
            login();
        } else if (accessTokenExpirationTime.isBefore(LocalDateTime.now().plusMinutes(1))) {
            refreshToken();
        }
        return accessToken;
    }

    /**
     * Refreshes the access token using the refresh token.
     * The access token and refresh tokens are decoded and the expiration times are set.
     */
    public void refreshToken() {
        log.info("Refreshing JWT token");
        var headers = new HttpHeaders();
        headers.setBearerAuth(refreshToken);
        var request = new HttpEntity<>(null, headers);

        try {
            var response = restTemplate.exchange(STREAMING_SERVER_URL + REFRESH_ENDPOINT, HttpMethod.GET, request, AuthenticationResponse.class);
            var responseBody = response.getBody();
            if (response.getStatusCode() != HttpStatus.OK || responseBody == null) {
                log.error("Tried to refresh JWT token, but failed: status code = %s, response body = %s".formatted(response.getStatusCode().value(), response.getBody()));
                throw new AuthenticationException("Refresh token failed: status code = %s, response body = %s".formatted(response.getStatusCode().value(), response.getBody()));
            }

            accessToken = responseBody.getAccessToken();
            accessTokenExpirationTime = getExpirationTime(true);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                login();
            }
            log.error("Tried to refresh JWT token, but failed: %s".formatted(e.getMessage()));
            throw new AuthenticationException("Error refreshing token: %s".formatted(e.getMessage()));
        }
    }

    /**
     * When the refresh token is expired or when there are no access tokens available, the user will be logged in.
     * Logs in to the streaming server and sets the access and refresh tokens.
     * The response from the server is a JSON object containing the access and refresh tokens.
     */
    private void login() {
        log.info("Logging in to Restreamer");
        var request = new HttpEntity<>(new AuthenticationRequest(USERNAME, PASSWORD));

        try {
            var response = restTemplate.exchange(STREAMING_SERVER_URL + LOGIN_ENDPOINT, HttpMethod.POST, request, AuthenticationResponse.class);
            var responseBody = response.getBody();
            if (response.getStatusCode() != HttpStatus.OK || responseBody == null) {
                log.error("Tried to login, but failed: status code = %s, response body = %s".formatted(response.getStatusCode().value(), responseBody));
                throw new AuthenticationException("Login failed: status code = %s, response body = %s".formatted(response.getStatusCode().value(), responseBody));
            }

            accessToken = responseBody.getAccessToken();
            refreshToken = responseBody.getRefreshToken();
            accessTokenExpirationTime = getExpirationTime(true);
            refreshTokenExpirationTime = getExpirationTime(false);
        } catch (RestClientResponseException e) {
            log.error("Tried to login, but failed: %s".formatted(e.getMessage()));
            throw new AuthenticationException("Error logging in: %s".formatted(e.getMessage()));
        }
    }

    /**
     * Returns the expiration time of the JWT token.
     *
     * @param token the access token
     * @return the expiration time of the access token as a LocalDateTime
     */
    private LocalDateTime getExpirationTime(String token) {
        var decoder = Base64.getUrlDecoder();
        var payload = new String(decoder.decode(token.split("\\.")[1]));
        var expirationTimeLong = Long.parseLong(payload.split(",")[1].split(":")[1]);
        var zoneOffset = ZoneId.of("Europe/Brussels").getRules().getOffset(LocalDateTime.now());
        var expirationTimestamp = LocalDateTime.ofEpochSecond(expirationTimeLong, 0, zoneOffset);
        log.info("Current Time: %s, Expiration Time: %s".formatted(LocalDateTime.now(), expirationTimestamp));
        return expirationTimestamp;
    }

    /**
     * Knowing each token is valid for 10 minutes, we set the expiration time to 9 minutes from now.
     *
     * @return the expiration time of the access token as a LocalDateTime
     */
    private LocalDateTime getExpirationTime(boolean isAccessToken) {
        var expirationTimestamp = isAccessToken ? LocalDateTime.now().plusMinutes(9) : LocalDateTime.now().plusHours(23);
        log.info("Current Time: %s, Expiration Time: %s".formatted(LocalDateTime.now(), expirationTimestamp));
        return expirationTimestamp;
    }
}
