package com.urlshortner.urlshortner.config;
import com.urlshortner.urlshortner.entities.User;
import com.urlshortner.urlshortner.exception.UnAuthorizedException;
import com.urlshortner.urlshortner.repository.UserRepository;
import com.urlshortner.urlshortner.util.AuthUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final AuthUtil authUtil;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
@Override
protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    return path.startsWith("/login") || path.startsWith("/register");
//    return path.startsWith("/api/v1/oauth2/") || path.startsWith("/api/v1/auth/");
//            || path.startsWith("/api/v1/login/oauth2/")
//            || path.startsWith("/api/v1/auth/");
// "/api/v1/short-urls"
}

AntPathMatcher matcher = new AntPathMatcher();
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    String path = request.getRequestURI();
    boolean isPublic =
            (("GET".equals(request.getMethod()) || "POST".equals(request.getMethod())) &&
                    path.startsWith("/api/v1/short-urls"))
                    || "/register".equals(path)
                    || "/login".equals(path);

    if (isPublic) {
        filterChain.doFilter(request, response);
        return;
    }
    try {
            log.info("incoming request: {}", request.getRequestURI());
            final String requestTokenHeader = request.getHeader("Authorization");
            if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                throw new UnAuthorizedException("Missing or invalid Authorization header Bearer Token is Missing.");
            }
            String token = requestTokenHeader.split("Bearer ")[1];
            String username = authUtil.getUsernameFromToken(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("User not Found"));
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                        = new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority(user.getRole().name())));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }
}
