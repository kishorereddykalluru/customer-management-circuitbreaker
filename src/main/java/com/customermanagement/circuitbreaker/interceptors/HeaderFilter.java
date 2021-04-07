package com.customermanagement.circuitbreaker.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Slf4j
//@Component
@Order(1)
public class HeaderFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        ForwardedHeaderFilterRemovingRequest forwardedHeaderFilterRemovingRequest = new ForwardedHeaderFilterRemovingRequest(httpServletRequest);

        filterChain.doFilter(forwardedHeaderFilterRemovingRequest, httpServletResponse);
    }

    private static class ForwardedHeaderFilterRemovingRequest extends HttpServletRequestWrapper{
        private final Map<String, List<String>> headers;

        public ForwardedHeaderFilterRemovingRequest(HttpServletRequest request){
            super(request);
            this.headers = initHeaders(request);
        }

        private Map<String, List<String>> initHeaders(HttpServletRequest request) {
            Map<String, List<String>> headers = new LinkedCaseInsensitiveMap<>(Locale.ENGLISH);
            Enumeration names = request.getHeaderNames();

            while(names.hasMoreElements()){
                String name = (String) names.nextElement();
                if(!name.equalsIgnoreCase("forwarded")){
                    headers.put(name, Collections.list(request.getHeaderNames()));
                }
                //Add Host, port and proto to MDC for hateos link
                if(name.toLowerCase().contains("forwarded")){
                    MDC.put(name.toLowerCase(), request.getHeader(name));
                }
            }

            return headers;
        }

        @Override
        public String getHeader(String name) {
            List<String> value = this.headers.get(name);
            return CollectionUtils.isEmpty(value) ? null : value.get(0);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            List<String> value = this.headers.get(name);
            return Collections.enumeration((Collection) (value != null ? value : Collections.emptySet()));
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return Collections.enumeration(this.headers.keySet());
        }
    }
}
