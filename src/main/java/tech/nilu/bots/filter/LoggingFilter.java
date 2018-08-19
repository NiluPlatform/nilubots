package tech.nilu.bots.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    public LoggingFilter() {

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long id = System.currentTimeMillis();
        RequestLoggingWrapper requestLoggingWrapper = new RequestLoggingWrapper(id, request);
        ResponseLoggingWrapper responseLoggingWrapper = new ResponseLoggingWrapper(id, response);
        try {
            filterChain.doFilter(requestLoggingWrapper, responseLoggingWrapper);
        } finally {
            doLog(id, requestLoggingWrapper, responseLoggingWrapper);
        }

    }

    private void doLog(long id
            , RequestLoggingWrapper request
            , ResponseLoggingWrapper response) {
        try {
            if ( log.isDebugEnabled()) {
                long now = System.currentTimeMillis();
                StringBuilder msg = new StringBuilder();
                msg.append(id).append("-request: uri=").append(request.getRequestURI());
                String wrapper;
                wrapper = request.getQueryString();
                if (wrapper != null) {
                    msg.append('?').append(wrapper);
                }
                wrapper = request.getRemoteAddr();
                if (StringUtils.hasLength(wrapper)) {
                    msg.append(";client=").append(wrapper);
                }
                HttpSession buf = request.getSession(false);
                if (buf != null) {
                    msg.append(";session=").append(buf.getId());
                }
                String length = request.getRemoteUser();
                if (length != null) {
                    msg.append(";user=").append(length);
                }
                msg.append(";headers=").append((new ServletServerHttpRequest(request)).getHeaders());
                msg.append(";body=").append(new String(request.toByteArray()));
                msg.append("\n").append(id).append("-response:");
                msg.append("status=").append(response.getStatus());
                byte[] toLog = response.toByteArray();
                if (toLog != null && toLog.length > 0)
                    msg.append(";body=").append(new String(toLog));
                msg.append(";duration=").append((now - id)).append("ms");
                log.debug(msg.toString());
            }
        } catch (Exception e) {

        }
    }



}
