package com.szy.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.szy.reggie.common.BaseContext;
import com.szy.reggie.common.R;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;

/**
 * 检查是否登录
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    static AntPathMatcher PATH_MATCH = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // get request URI
        String urlReq = request.getRequestURI();
        log.info("拦截到请求：{}", request.getRequestURI());
        // urls we don't block
        String[] urls = {
            "/employee/login",
            "/employee/logout",
            "/backend/**",
            "/front/**",
            "/user/sendMsg",
            "/user/login"
        };
        for (String url: urls) {
            if (PATH_MATCH.match(url, urlReq)) {
                log.info("请求不需要拦截： {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }
        }
        // pc: check if logged in
        if (request.getSession().getAttribute("employee") != null) {
            log.info("用户已登录，id为{}", request.getSession().getAttribute("employee"));

            Long empID = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empID);

            filterChain.doFilter(request, response);
            return;
        }
        // mob: check if logged in
        if (request.getSession().getAttribute("user") != null) {
            log.info("用户已登录，id为{}", request.getSession().getAttribute("user"));

            Long userID = (Long) request.getSession().getAttribute("user");
            // 每次请求都会设置
            BaseContext.setCurrentId(userID);

            filterChain.doFilter(request, response);
            return;
        }
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        // 在response中写入JSON化的R
    }
}
