package com.photoapp.zuul.api.gateway.websecurity;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    private Environment environment;
    @Autowired
    public AuthorizationFilter(AuthenticationManager authenticationManager, Environment environment) {
        super(authenticationManager);
        this.environment = environment;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String authorizationHeader = request.getHeader(environment.getProperty("authorization.token.header.name"));
        if(authorizationHeader == null || !authorizationHeader.startsWith(environment.getProperty("authorization.token.header.prefix"))){
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken userToken = getAuthentication(request);

        SecurityContextHolder.getContext().setAuthentication(userToken);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request){
        String authenticationHeader = request.getHeader(environment.getProperty("authorization.token.header.name"));
        if(null == authenticationHeader){
            return null;
        }
        String token = authenticationHeader.replace(environment.getProperty("authorization.token.header.prefix"), "");
        String userId = Jwts.parser()
                .setSigningKey(environment.getProperty("token.secret"))
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        if(null == userId){
            return null;
        }
    UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
        return userToken;

    }

}
