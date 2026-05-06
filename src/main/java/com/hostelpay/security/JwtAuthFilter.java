package com.hostelpay.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hostelpay.repositories.HostelRepository;
import com.hostelpay.entities.Hostel;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private HostelRepository hostelRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (jwtUtil.validateToken(token)) {
                    UUID userId = jwtUtil.extractUserId(token);
                    String email = jwtUtil.extractEmail(token);
                    UUID hostelId = jwtUtil.extractHostelId(token);
                    String role = jwtUtil.extractRole(token);

                    // Check subscription for OWNER role
                    if ("OWNER".equals(role) && hostelId != null) {
                        var hostelOpt = hostelRepository.findByIdIncludingInactive(hostelId);
                        if (hostelOpt.isPresent() && !hostelOpt.get().getSubscriptionActive()) {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Subscription inactive. Please renew your plan.\"}");
                            return;
                        }
                    }

                    JwtPrincipal principal = new JwtPrincipal(userId, email, hostelId, role);

                    var authority = new SimpleGrantedAuthority("ROLE_" + role);
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, Collections.singletonList(authority));

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("JWT validated for user: {} role: {} hostel: {}", email, role, hostelId);
                }
            }

        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
