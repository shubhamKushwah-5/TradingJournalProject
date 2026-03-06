package com.shubham.journal_api.config;

import com.shubham.journal_api.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
        throws ServletException, IOException{

        //Get Authorization header
        String authHeader = request.getHeader("Authorization");

        String token = null ;
        String username = null;

        //check if header exists and starts with "Bearer"
        if(authHeader != null && authHeader.startsWith("Bearer")) {
            token = authHeader.substring(7); //removing bearer space

            try{
                username = jwtUtil.extractUsername(token);
            } catch (Exception e){
                //Invalid token
                System.out.println("Invalid token: " + e.getMessage());
            }
        }

        // If token is valid and user not already authenticated
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            //Validate token
            if(jwtUtil.validateToken(token,username)) {
                //Create authentication object
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());

                //Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        //continue filter chain
        filterChain.doFilter(request, response);

    }
}
