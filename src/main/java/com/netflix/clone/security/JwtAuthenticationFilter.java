package com.netflix.clone.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String jwtToken = extractJwtToken(request);

        String username = null;

        // ✅ Prevent NullPointerException
        if (jwtToken != null) {
            try {
                username = jwtUtil.getUsernameFromToken(jwtToken);
            } catch (Exception e) {
                // Optional: log exception
                System.out.println("Invalid JWT Token: " + e.getMessage());
            }
        }

        // ✅ Only authenticate if needed
        if (shouldProcessAuthentication(username)) {
            processAuthentication(request, jwtToken, username);
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtToken(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();

        // ✅ Case 1: Authorization Header
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }

        // ✅ Case 2: Token in URL (Fixed precedence bug)
        if ((requestURI.contains("/api/files/video/") ||
                requestURI.contains("/api/files/image/"))
                && request.getParameter("token") != null) {

            return request.getParameter("token");
        }

        return null;
    }

    private boolean shouldProcessAuthentication(String username) {
        return username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private void processAuthentication(HttpServletRequest request,
                                       String jwtToken,
                                       String username) {

        // ✅ Validate token safely
        if (jwtToken != null && jwtUtil.ValidateToken(jwtToken)) {

            UserDetails userDetails = createUserDetailsFromToken(jwtToken, username);
            setAuthenticationContext(request, userDetails);
        }
    }

    private UserDetails createUserDetailsFromToken(String jwtToken, String username) {
        String role = jwtUtil.getRoleFromToken(jwtToken);

        if (role == null) {
            throw new RuntimeException("Invalid JWT: Role not found");
        }

        // ✅ Ensure ROLE_ prefix
        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;

        return User.builder()
                .username(username)
                .password("N/A")
                .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority(authority)
                ))
                .build();
    }

    private void setAuthenticationContext(HttpServletRequest request,
                                          UserDetails userDetails) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authenticationToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}