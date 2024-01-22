package com.example.demo.security.filter;

import com.example.demo.dto.account.UserDto;
import com.example.demo.util.JwtUtil;
import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@Log4j2
public class JwtCheckFilter extends OncePerRequestFilter {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // 해당 경로들은 jwt 토큰 체크를 진행하지 않고 통과시킨다
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        // Preflight요청은 체크하지 않음
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        String path = request.getRequestURI();

        if (path.startsWith("/api/login")) {
            return true;
        }

        if (path.startsWith("/api/account/sign-up")) {
            return true;
        }

        if (path.startsWith("/api/account/refresh")) {
            return true;
        }

        if (pathMatcher.match("/api/study/*/get-study", path)) {
            return true;
        }

        if (path.startsWith("/api/study/get-study-list")) {
            return true;
        }

        if (path.startsWith("/api/account/check-email-token")) {
            return true;
        }

        if (path.startsWith("/swagger-ui/")) {
            return true;
        }

        if (path.startsWith("/api-docs/") || path.startsWith("/v3/api-docs/")) {
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeaderStr = request.getHeader("Authorization");
        // Bearer // 7 Jwt 문자열
        try {
            String accessToken = authHeaderStr.substring(7);
            Map<String, Object> claims = JwtUtil.validateToken(accessToken);

            String username = (String) claims.get("username");
            String password = (String) claims.get("password");
            List<String> roleNames = (List<String>) claims.get("roleNames");

            UserDto userDTO = new UserDto(username, password, roleNames);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDTO, password, userDTO.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            e.printStackTrace();

            Gson gson = new Gson();
            String msg = gson.toJson(Map.of("error", "ERROR_ACCESS_TOKEN"));

            response.setContentType("application/json");
            PrintWriter printWriter = response.getWriter();
            printWriter.println(msg);
            printWriter.close();
        }

    }

}
