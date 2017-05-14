package ch.synox.forward;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.ProxySelector;

class ForwardServlet extends HttpServlet {

    private final HttpHost target;
    private final CloseableHttpClient httpclient;


    public ForwardServlet(String baseUrl) {
        this.target = HttpHost.create(baseUrl);

        // Automatic Proxy: https://hc.apache.org/httpcomponents-client-4.5.x/tutorial/html/connmgmt.html#d5e485
        SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(ProxySelector.getDefault());
        httpclient = HttpClients.custom().setRoutePlanner(routePlanner)
                .build();
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse servletResponse) throws ServletException, IOException {
        String pathWithQuery = new UrlPathHelper().getPathWithinApplication(req) + "?" + req.getQueryString();

        CloseableHttpResponse clientResponse = httpclient.execute(target, new HttpGet(pathWithQuery));
        try {
            servletResponse.setContentType(clientResponse.getFirstHeader("content-type").getValue());
            servletResponse.setStatus(clientResponse.getStatusLine().getStatusCode());
            HttpEntity entity = clientResponse.getEntity();
            entity.writeTo(servletResponse.getOutputStream());
            // ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            clientResponse.close();
        }
    }
}
