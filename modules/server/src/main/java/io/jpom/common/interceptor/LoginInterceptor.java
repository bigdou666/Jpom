package io.jpom.common.interceptor;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.URLEncoder;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.jiangzeyin.common.JsonMessage;
import cn.jiangzeyin.common.interceptor.InterceptorPattens;
import cn.jiangzeyin.common.spring.SpringUtil;
import io.jpom.common.BaseServerController;
import io.jpom.model.data.UserModel;
import io.jpom.service.user.UserService;
import io.jpom.system.ExtConfigBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;

/**
 * 登录拦截器
 *
 * @author jiangzeyin
 * @date 2017/2/4.
 */
@InterceptorPattens(sort = -1, exclude = "/api/**")
public class LoginInterceptor extends BaseJpomInterceptor {
    /**
     * session
     */
    public static final String SESSION_NAME = "user";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
        HttpSession session = getSession();
        // 记录请求类型
        boolean isPage = isPage(handlerMethod);
        request.setAttribute("Page_Req", isPage);
        //
        NotLogin notLogin = handlerMethod.getMethodAnnotation(NotLogin.class);
        if (notLogin == null) {
            notLogin = handlerMethod.getBeanType().getAnnotation(NotLogin.class);
        }
        if (notLogin == null) {
            UserModel user = (UserModel) session.getAttribute(SESSION_NAME);
            if (user == null) {
                this.responseLogin(request, response, handlerMethod);
                return false;
            }
            // 用户信息
            UserService userService = SpringUtil.getBean(UserService.class);
            UserModel newUser = userService.getItem(user.getId());
            if (newUser == null) {
                // 用户被删除
                this.responseLogin(request, response, handlerMethod);
                return false;
            }
            if (user.getModifyTime() != newUser.getModifyTime()) {
                // 被修改过
                this.responseLogin(request, response, handlerMethod);
                return false;
            }
        }
        reload();
        //
        return true;
    }


    /**
     * 提示登录
     *
     * @param request       req
     * @param response      res
     * @param handlerMethod 方法
     * @throws IOException 异常
     */
    private void responseLogin(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws IOException {
        if (isPage(handlerMethod)) {
            String url = "/login.html?";
            String uri = request.getRequestURI();
            if (StrUtil.isNotEmpty(uri) && !StrUtil.SLASH.equals(uri)) {
                String queryString = request.getQueryString();
                if (queryString != null) {
                    uri += "?" + queryString;
                }
                url += "&url=" + URLUtil.encodeAll(uri);
            }
            String header = request.getHeader(HttpHeaders.REFERER);
            if (header != null) {
                url += "&r=" + header;
            }
            sendRedirects(request, response, url);
            return;
        }
        ServletUtil.write(response, JsonMessage.getString(800, "登录信息已失效,重新登录"), MediaType.APPLICATION_JSON_UTF8_VALUE);
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
        HttpSession session;
        try {
            session = getSession();
        } catch (Exception ignored) {
            return;
        }
        try {
            // 静态资源地址参数
            session.setAttribute("staticCacheTime", DateUtil.currentSeconds());
            // 代理二级路径
            Object jpomProxyPath = session.getAttribute("jpomProxyPath");
            if (jpomProxyPath == null) {
                String path = getHeaderProxyPath(request);
                session.setAttribute("jpomProxyPath", path);
            }
        } catch (Exception ignored) {
        }
        try {
            // 统一的js 注入
            String jsCommonContext = (String) session.getAttribute("jsCommonContext");
            if (jsCommonContext == null) {
                String path = ExtConfigBean.getInstance().getPath();
                File file = FileUtil.file(String.format("%s/script/common.js", path));
                if (file.exists()) {
                    jsCommonContext = FileUtil.readString(file, CharsetUtil.CHARSET_UTF_8);
                    jsCommonContext = URLEncoder.DEFAULT.encode(jsCommonContext, CharsetUtil.CHARSET_UTF_8);
                }
                session.setAttribute("jsCommonContext", jsCommonContext);
            }
        } catch (IllegalStateException ignored) {
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        BaseServerController.remove();
    }


}
