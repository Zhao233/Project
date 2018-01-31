package com.duckduckgogogo.security;

import com.duckduckgogogo.domain.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class CustomMvcConfigurer extends WebMvcConfigurerAdapter {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);

        // registry.addInterceptor(new SecurityInterceptor()).addPathPatterns("/console/**");
    }

    static class SecurityInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Object handler) throws Exception {
            User user = (User) request.getSession().getAttribute("user");
            if (user != null) {
                String role = user.getRole();
                String uri = request.getRequestURI();
                System.out.println(uri.indexOf("/console/profile"));
                if (uri.equals("/console") || uri.equals("/console/")) return true;
                if (uri.contains("/console/project_management") || uri.contains("/console/profile")) {
                    return true;
                }
                if (role.equals(User.ROLE_ADMINISTRATOR) && uri.contains("/console/user_management")) {
                    return true;
                } else if (role.equals(User.ROLE_CUSTOMER) && uri.contains("/console/task_schedule_tracking")) {
                    return true;
                } else if (role.equals(User.ROLE_SUPPLIER) && uri.contains("/console/task_management")) {
                    return true;
                } else if (role.equals(User.ROLE_PROJECT_MANAGEMENT) && uri.contains("/console/task_allocation")) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public void postHandle(HttpServletRequest request,
                               HttpServletResponse response,
                               Object handler,
                               ModelAndView modelAndView) throws Exception {

        }

        @Override
        public void afterCompletion(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Object handler, Exception ex) throws Exception {

        }
    }
}
