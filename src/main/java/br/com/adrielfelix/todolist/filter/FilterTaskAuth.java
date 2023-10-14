package br.com.adrielfelix.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.adrielfelix.todolist.user.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
@AllArgsConstructor
public class FilterTaskAuth extends OncePerRequestFilter {

    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var servletPath = request.getServletPath();
        if(servletPath.startsWith("/api/v1/task")) {
            var authHeader = request.getHeader("Authorization");
            if(authHeader == null) {
                response.sendError(400);
            } else {
                var credentials = getCredentialsFromHeader(request);
                var username = credentials[0];
                var password = credentials[1];
                var foundUser = userRepository.findByUsername(username);
                if(foundUser.isEmpty()) {
                    response.sendError(401);
                } else {
                    var user = foundUser.get();
                    var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword().toCharArray());
                    if(passwordVerify.verified) {
                        request.setAttribute("userId", user.getId());
                        filterChain.doFilter(request, response);
                    } else {
                        response.sendError(401);
                    }
                }
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private String[] getCredentialsFromHeader(HttpServletRequest request) {
        var authorization = request.getHeader("Authorization").replace("Basic ", "");
        var decodedAuth = new String(Base64.getDecoder().decode(authorization));
        return decodedAuth.split(":");
    }

}
