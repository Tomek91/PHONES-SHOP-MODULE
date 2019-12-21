package pl.com.app.security;

public interface SecurityConfig {
    String SECRET_KEY = "SecretKey16072019TomaszIwaniuk";
    long ACCESS_TOKEN_EXPIRATION_TIME = 300_000; // 5 min
    long REFRESH_TOKEN_EXPIRATION_TIME = 32400_000; // 9h
    String TOKEN_PREFIX = "Bearer ";
    String TOKEN_HEADER = "Authorization";
}
