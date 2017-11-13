package io.union.js.controller;

import net.sf.json.JSONObject;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.*;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.*;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.LineParser;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class APIController {
    private final static Logger logger = LoggerFactory.getLogger(APIController.class);

    public static String dspJSONapiRequest(String uri, Map<String, String> params) {
        JSONObject result = new JSONObject();
        try {
            //logger.info("api start: " + uri); // 打印日志，可统计请求次数
            HttpMessageParserFactory<HttpResponse> responseParserFactory = new DefaultHttpResponseParserFactory() {
                @Override
                public HttpMessageParser<HttpResponse> create(SessionInputBuffer buffer, MessageConstraints constraints) {
                    LineParser lineParser = new BasicLineParser() {
                        @Override
                        public Header parseHeader(final CharArrayBuffer buffer) {
                            try {
                                return super.parseHeader(buffer);
                            } catch (ParseException ex) {
                                return new BasicHeader(buffer.toString(), null);
                            }
                        }
                    };
                    return new DefaultHttpResponseParser(buffer, lineParser, DefaultHttpResponseFactory.INSTANCE, constraints) {
                        @Override
                        protected boolean reject(final CharArrayBuffer line, int count) {
                            return false;
                        }
                    };
                }
            };
            HttpMessageWriterFactory<HttpRequest> requestWriterFactory = new DefaultHttpRequestWriterFactory();
            HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory = new ManagedHttpClientConnectionFactory(
                    requestWriterFactory, responseParserFactory);
            SSLContext sslcontext = SSLContexts.createSystemDefault();
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslcontext))
                    .build();
            DnsResolver dnsResolver = new SystemDefaultDnsResolver() {
                @Override
                public InetAddress[] resolve(final String host) throws UnknownHostException {
                    return super.resolve(host);
                }
            };
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry, connFactory, dnsResolver);
            SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
            connManager.setDefaultSocketConfig(socketConfig);
            connManager.setValidateAfterInactivity(1000);
            MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(200).setMaxLineLength(2000).build();
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                    .setMalformedInputAction(CodingErrorAction.IGNORE)
                    .setUnmappableInputAction(CodingErrorAction.IGNORE)
                    .setCharset(Consts.UTF_8)
                    .setMessageConstraints(messageConstraints)
                    .build();
            connManager.setDefaultConnectionConfig(connectionConfig);
            connManager.setMaxTotal(100);
            connManager.setDefaultMaxPerRoute(10);
            CookieStore cookieStore = new BasicCookieStore();

            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setCookieSpec(CookieSpecs.DEFAULT)
                    .setExpectContinueEnabled(true)
                    .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                    .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                    .build();
            CloseableHttpClient httpclient = HttpClients.custom()
                    .setConnectionManager(connManager)
                    .setDefaultCookieStore(cookieStore)
                    .setDefaultCredentialsProvider(credentialsProvider)
                    .setDefaultRequestConfig(defaultRequestConfig)
                    .build();

            try {
                String userAgent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36";
                HttpPost httpPost = new HttpPost(uri);
                List<NameValuePair> paris = new ArrayList<>();
                params.forEach((k, v) -> paris.add(new BasicNameValuePair(k, v)));
                httpPost.setEntity(new UrlEncodedFormEntity(paris));
                RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig).setSocketTimeout(5000).setConnectTimeout(5000).setConnectionRequestTimeout(5000).build();

                httpPost.setConfig(requestConfig);
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
                httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
                httpPost.setHeader("Connection", "keep-alive");
                httpPost.setHeader("Referer", "");
                httpPost.setHeader("User-Agent", userAgent);

                HttpClientContext context = HttpClientContext.create();
                context.setCookieStore(cookieStore);
                context.setCredentialsProvider(credentialsProvider);
                CloseableHttpResponse response = httpclient.execute(httpPost, context);
                try {
                    result = JSONObject.fromObject(EntityUtils.toString(response.getEntity()).trim());
                    //logger.info("api success: " + result); // 打印请求成功日志，可用于统计

                    context.getRequest();
                    // Execution route
                    context.getHttpRoute();
                    // Target auth state
                    context.getTargetAuthState();
                    // Proxy auth state
                    context.getTargetAuthState();
                    // Cookie origin
                    context.getCookieOrigin();
                    // Cookie spec used
                    context.getCookieSpec();
                    // User security token
                    context.getUserToken();
                } finally {
                    response.close();
                }
            } finally {
                httpclient.close();
            }
        } catch (Exception e) {
            logger.error("dsp error: ", e);
        }
        return result.toString();
    }
}
