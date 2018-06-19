package com.capitalone.dashboard.util;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;


/**
 * Supplier that returns a new {@link RestTemplate}.
 */
@Component
public class RestAPISupplier implements Supplier<RestOperations> {
	private static final Logger LOGGER = LoggerFactory.getLogger(RestAPISupplier.class);
    @Override
    public RestOperations get() {
    	SSLContext sslContext = null;
    	X509HostnameVerifier allowAll = new X509HostnameVerifier() {

			@Override
			public boolean verify(String hostname, SSLSession session) {
				 return true;
			}

			@Override
			public void verify(String host, SSLSocket ssl) throws IOException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void verify(String host, X509Certificate cert)
					throws SSLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void verify(String host, String[] cns, String[] subjectAlts)
					throws SSLException {
				// TODO Auto-generated method stub
				
			}

    	};
		try {
			sslContext = SSLContext.getInstance("SSL");
		} catch (NoSuchAlgorithmException e1) {
			ApplicationDBLogger.log(HygieiaConstants.API,
					"RestAPISupplier.get", e1.getMessage(), e1);
			// TODO Auto-generated catch block
			//LOGGER.error("Error while parsing Elastic response", e1);
		}

    	// set up a TrustManager that trusts everything
    	try {
			sslContext.init(null, new TrustManager[] { new X509TrustManager() {
			            public X509Certificate[] getAcceptedIssuers() {
			            	LOGGER.debug("getAcceptedIssuers =============");
			                    return new X509Certificate[1];
			            }

			            public void checkClientTrusted(X509Certificate[] certs,
			                            String authType) {
			            	LOGGER.debug("checkClientTrusted =============");
			            }

			            public void checkServerTrusted(X509Certificate[] certs,
			                            String authType) {
			            	LOGGER.debug("checkServerTrusted =============");
			            }
			} }, new SecureRandom());
		} catch (KeyManagementException e) {
			ApplicationDBLogger.log(HygieiaConstants.API,
					"RestAPISupplier.get", e.getMessage(), e);
			// TODO Auto-generated catch block
			LOGGER.error("Error while parsing Elastic response", e);
		}

    	SSLSocketFactory sf = new SSLSocketFactory(sslContext);
    	sf.setHostnameVerifier(allowAll);
    	Scheme httpsScheme = new Scheme("https", 443, sf);
    	SchemeRegistry schemeRegistry = new SchemeRegistry();
    	schemeRegistry.register(httpsScheme);

    	// apache HttpClient version >4.2 should use BasicClientConnectionManager
    	ClientConnectionManager cm = new SingleClientConnManager(schemeRegistry);
    	HttpClient httpClient = new DefaultHttpClient(cm);
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(120000);
        requestFactory.setReadTimeout(120000);
        requestFactory.setHttpClient(httpClient);
        return new RestTemplate(requestFactory);
    }
}
