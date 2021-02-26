package com.company.awms.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import org.json.JSONObject;

import javax.security.sasl.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

public class CaptchaAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private String processUrl;

    public CaptchaAuthenticationFilter(String defautFilterProcessesUrl, String failureUrl) {
        super(defautFilterProcessesUrl);
        this.processUrl = defautFilterProcessesUrl;
        setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler(failureUrl));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        
        if (this.processUrl.equals(req.getServletPath()) && "POST".equalsIgnoreCase(req.getMethod())) {
            try {
                String url = "https://www.google.com/recaptcha/api/siteverify",
                        params = "secret=6LcTHisaAAAAAKsAxdiz1_dGJ3TJFpio-WpCi4K9" + "&response="
                                + req.getParameter("g-recaptcha-response");

                HttpURLConnection http = (HttpURLConnection) new URL(url).openConnection();
                http.setDoOutput(true);
                http.setRequestMethod("POST");
                http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                OutputStream out = http.getOutputStream();
                out.write(params.getBytes("UTF-8"));
                out.flush();
                out.close();

                InputStream resHttp = http.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(resHttp, "UTF-8"));

                StringBuilder sb = new StringBuilder();
                int cp;
                while ((cp = rd.read()) != -1) {
                    sb.append((char) cp);
                }
                JSONObject json = new JSONObject(sb.toString());
                resHttp.close();
                if (!json.getBoolean("success")) {
                    String u = "https://"+InetAddress.getLocalHost().getHostName()+":8443";
                    res.reset();
                    res.setStatus(HttpServletResponse.SC_FOUND);
                    res.setHeader("Location", u);
                    return;
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        return null;
    }
}
