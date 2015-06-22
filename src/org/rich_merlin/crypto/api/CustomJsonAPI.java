/* 
 * Copyright 2015 Sergey Orlov <serg.merlin@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rich_merlin.crypto.api;

import java.io.*;
import java.net.* ;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.lang.reflect.Array;

/**
 *
 * @author serg.merlin
 */
abstract class CustomJsonAPI<RESULT> {
    
    private static final int CONNECT_TIMEOUT = 10 * 1000;
    private static final int SOCKET_TIMEOUT = 30 * 1000;
    private static final String ENC_GZIP = "gzip";
    protected static final String ENC_UTF8 = "UTF-8";
    // User agent of Firefox 3.6.3
    private static final String USER_AGENT = 
            "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.3) Gecko/20100401";
    
    private final Map<String, Object> apiParams;
    private final Map<String, Object> apiHeaders;
    
    private static Boolean validateApiURL(final String apiURL) {
        Boolean valide = false;
        try {
            URI uri = new URI(apiURL);
            // perform checks for scheme, host, etc.
            if (("http".equals(uri.getScheme())
                    || "https".equals(uri.getScheme()))
                    && !uri.getHost().isEmpty()) {
                valide = true;
            }
        } catch (URISyntaxException | NullPointerException ex) {
        }
        return valide;
    }
    
    public CustomJsonAPI() {
        apiParams = new LinkedHashMap<>();
        apiHeaders = new LinkedHashMap<>();
    }
    
    protected String getUserAgent() {
        return USER_AGENT;
    }
    
    private void clearQuery() {
        apiParams.clear();
        apiHeaders.clear();
    }
    
    protected final void setParam(String paramName, Object paramValue) {
        if (paramName == null || paramValue == null) {
            throw new IllegalStateException("Parameter '" + paramName + "' is incorrect");
        }
        apiParams.put(paramName, paramValue);
    }
    
    protected final void setHeader(String headerName, Object headerValue) {
        if (headerName == null || headerValue == null) {
            throw new IllegalStateException("Header '" + headerName + "' is incorrect");
        }
        apiHeaders.put(headerName, headerValue);
    }
    
    protected abstract String getApiURL();
    
    private String buildURL(String params) {
        String apiURL = getApiURL();
        int delim = apiURL.indexOf('?');
        
        if (delim >= 0) {
            apiURL = String.copyValueOf(apiURL.toCharArray(), 0, delim - 1);
        }
        if (!validateApiURL(apiURL)) {
            throw new IllegalArgumentException("Illegal API URL:" + apiURL);
        }
        if (params.isEmpty()) {
            return apiURL;
        } else {
            return apiURL + '?' + params;
        }
    }
    
    protected String buildParams() {
        StringBuilder params = new StringBuilder();
        Map<String, Object> sortedParams = new TreeMap<>(apiParams);
        
        for (Map.Entry<String, Object> apiParam : sortedParams.entrySet()) {
            StringBuilder param = new StringBuilder();
            try {
                param.append(URLEncoder.encode(apiParam.getKey(), ENC_UTF8));
                param.append('=');
                param.append(URLEncoder.encode(String.valueOf(apiParam.getValue()), ENC_UTF8));
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            }
            if (params.length() != 0) {
                params.append('&');
            }
            params.append(param);
        }
        return params.toString();
    }
    
    protected void setupHeadersInternal(final byte[] postData) {
        setHeader("User-Agent", getUserAgent());
        if (Array.getLength(postData) > 0) {
            setHeader("Content-Type", "application/x-www-form-urlencoded");
            setHeader("Content-Length", String.valueOf(postData.length));
        }
        if (isGzipEncoding()) {
            setHeader("Accept-Encoding", ENC_GZIP);
        }
        //setHeader("Connection", "close");
    }
    
    private String readStreamToString(InputStream in) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, ENC_UTF8));
        String line;
        
        try {
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            return buffer.toString();
        }
        finally {
            in.close();
        }
    }
    
    private String doHttpQuery(final String url, final byte[] postData) {
        if (Array.getLength(postData) == 0
                && isHttpPostQuery()) {
            throw new IllegalStateException("There is no data for POST query");
        }
        int httpAnswerCode;
        String content = null;
        // We have to accept cookies
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        HttpURLConnection connection = null;
        
        try {
            URL urlConnection = new URL(url);
            connection = (HttpURLConnection)urlConnection.openConnection();
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(SOCKET_TIMEOUT);
            // Setup additional headers
            for (Map.Entry<String, Object> header : apiHeaders.entrySet()) {
                connection.setRequestProperty(header.getKey(), 
                        String.valueOf(header.getValue()));
            }
            if (Array.getLength(postData) == 0) {
                // Do HTTP Get query
                connection.setRequestMethod("GET");
            } else {
                // Do HTTP Post query
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                // Send Post data
                connection.getOutputStream().write(postData);
                connection.getOutputStream().flush();
                connection.getOutputStream().close();
            }
            httpAnswerCode = connection.getResponseCode();
            boolean gzipEncoding = ENC_GZIP.equals(connection.getContentEncoding());
            
            if (httpAnswerCode / 100 == 2) {
                content = readStreamToString(gzipEncoding
                        ? new GZIPInputStream(connection.getInputStream())
                        : connection.getInputStream());
            } else {
                String msg = readStreamToString(gzipEncoding
                        ? new GZIPInputStream(connection.getErrorStream())
                        : connection.getErrorStream());
                throw new RuntimeException("Server error code " + httpAnswerCode
                        + " answer\n" + msg);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Can't connect to server", ex);
        } finally {
            if (connection != null) {
                connection.disconnect(); 
            }
        }
        return content;
    }
    
    protected abstract RESULT createResult();
    
    protected abstract Boolean beforeQuery();
    
    protected abstract void afterQuery(String answer, RESULT result);
    
    protected abstract Boolean isHttpPostQuery();
    
    protected void setupHeaders(final byte[] postData) {
    }
    
    protected abstract Boolean isGzipEncoding();
    
    public final RESULT doQuery() {
        String answer;
        clearQuery();
        RESULT result = createResult();
        
        if (isHttpPostQuery()) {
            if (beforeQuery()) {
                try {
                    String params = buildParams();
                    byte[] postData = params.getBytes(ENC_UTF8);
                    setupHeadersInternal(postData);
                    setupHeaders(postData);
                    if ((answer = doHttpQuery(buildURL(""), postData)) != null) {
                        afterQuery(answer, result);
                    }
                } catch (UnsupportedEncodingException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } else {
            byte[] postData = new byte[0];
            setupHeadersInternal(postData);
            setupHeaders(postData);
            if (beforeQuery()) {
                if ((answer = doHttpQuery(buildURL(buildParams()), postData)) != null) {
                    afterQuery(answer, result);
                }
            }
        }
        return result;
    }
}
