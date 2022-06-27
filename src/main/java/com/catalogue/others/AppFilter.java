package com.catalogue.others;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

@Configuration
public class AppFilter implements Filter {

    @Autowired
    Environment environment;

    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterchain) throws ServletException, IOException {

        HttpServletRequest request1=(HttpServletRequest)  request;
        HttpServletResponse response1=(HttpServletResponse) response;
        ObjectMapper mapper=new ObjectMapper();

        if (request1.getHeader("authorization").equals(environment.getProperty("authorization")))
            filterchain.doFilter(request,response);
        else{
            response1.setStatus(HttpStatus.UNAUTHORIZED.value());
            response1.setContentType(MediaType.APPLICATION_JSON_VALUE);
            String responseMessage="{\n \"error\" : \"unauthorized to access this resource\" \n}";
            mapper.writeValue(response1.getWriter(),mapper.readValue(responseMessage, JsonNode.class));
        }
    }

    @Override
    public void init(FilterConfig filterconfig) throws ServletException {}
}