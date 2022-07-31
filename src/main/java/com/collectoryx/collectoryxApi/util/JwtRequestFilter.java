package com.collectoryx.collectoryxApi.util;

import com.collectoryx.collectoryxApi.user.service.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;
import java.util.Arrays;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

  private final JwtUserDetailsService jwtUserDetailsService;
  private final JwtTokenUtil jwtTokenUtil;

  public JwtRequestFilter(JwtUserDetailsService jwtUserDetailsService, JwtTokenUtil jwtTokenUtil) {
    this.jwtUserDetailsService = jwtUserDetailsService;
    this.jwtTokenUtil = jwtTokenUtil;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain)
      throws ServletException, IOException {

    if (Arrays.asList("/login", "/register").contains(request.getRequestURI())) {
      chain.doFilter(request, response);
      return;
    }

    final String requestTokenHeader = request.getHeader("Authorization");
    if (StringUtils.startsWith(requestTokenHeader, "Bearer ") && requestTokenHeader != null) {
      String jwtToken = requestTokenHeader.substring(7);
      try {
        String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
        if (StringUtils.isNotEmpty(username)
            && null == SecurityContextHolder.getContext().getAuthentication()) {
          UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
          if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            usernamePasswordAuthenticationToken
                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext()
                .setAuthentication(usernamePasswordAuthenticationToken);
          }
        }
      } catch (ExpiredJwtException e) {
        logger.error("JWT Token is expired");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return;
      } catch (Exception e) {
        logger.error(e.getMessage());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        throw e;
      }
    }
    if (requestTokenHeader != null && !StringUtils.startsWith(requestTokenHeader, "Bearer ")) {
      logger.warn("JWT Token does not begin with Bearer String");
    }

    if (requestTokenHeader == null) {
      logger.error("JWT Token is not present");
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      return;
    }
    chain.doFilter(request, response);
  }

}