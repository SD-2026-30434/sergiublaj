package en.sd.chefmgmt.security;

import jakarta.servlet.http.HttpServletResponse;

public interface CookieService {

    String ACCESS_TOKEN_COOKIE = "access_token";

    void addAccessTokenCookie(HttpServletResponse response, String token);

    void clearAccessTokenCookie(HttpServletResponse response);
}
