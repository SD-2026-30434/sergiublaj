package en.sd.chefmgmt.security;

import tools.jackson.databind.ObjectMapper;
import en.sd.chefmgmt.dto.user.LoginRequestDTO;
import en.sd.chefmgmt.exception.ExceptionBody;
import en.sd.chefmgmt.exception.ExceptionCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import java.io.IOException;
import java.io.UncheckedIOException;

@Slf4j
public class LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final JwtService jwtService;
    private final CookieService cookieService;
    private final ObjectMapper objectMapper;

    public LoginAuthenticationFilter(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            CookieService cookieService,
            ObjectMapper objectMapper,
            String loginUrl
    ) {
        super(PathPatternRequestMatcher.pathPattern(HttpMethod.POST, loginUrl));
        setAuthenticationManager(authenticationManager);
        this.jwtService = jwtService;
        this.cookieService = cookieService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response
    ) throws AuthenticationException {
        try {
            LoginRequestDTO loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequestDTO.class);

            AbstractAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());

            return getAuthenticationManager().authenticate(authenticationToken);
        } catch (Exception e) {
            throw new BadCredentialsException(ExceptionCode.MISSING_CREDENTIALS.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain,
            Authentication authResult
    ) {
        UserDetailsImpl principal = (UserDetailsImpl) authResult.getPrincipal();
        log.info("[AUTH] User '{}' logged in", principal.getEmail());

        String token = jwtService.generateToken(principal);
        cookieService.addAccessTokenCookie(response, token);

        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(
            @NonNull HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed
    ) {
        log.warn("[AUTH] Login failed: {}", failed.getMessage());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        try {
            objectMapper.writeValue(response.getWriter(), ExceptionBody.of(ExceptionCode.INVALID_CREDENTIALS));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
