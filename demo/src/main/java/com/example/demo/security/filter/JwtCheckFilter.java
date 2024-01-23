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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
public class JwtCheckFilter extends OncePerRequestFilter {

    // 해당 경로들은 jwt 토큰 체크를 진행하지 않고 통과시킨다
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        // Preflight요청은 체크하지 않음
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        String path = request.getRequestURI();

        if (path.startsWith("/api")) {
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
            List<LinkedHashMap<String, String>> authorityClaims = (List<LinkedHashMap<String, String>>) claims.get("roleNames");
            List<String> roleNames = authorityClaims.stream()
                    .map(authMap -> authMap.get("authority"))
                    .collect(Collectors.toList());

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
