package ua.org.gorbounov.sod;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class MyAccessDeniedHandler implements AccessDeniedHandler {
	 
 //   private static Logger logger = LoggerFactory.getLogger(MyAccessDeniedHandler.class);
 
    @Override
    public void handle(HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse,
                       AccessDeniedException e) throws IOException, ServletException {
 
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            log.info("User '" + auth.getName() + "' attempted to access the protected URL: " + httpServletRequest.getRequestURI());
        }
        httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/403");
    }


}
