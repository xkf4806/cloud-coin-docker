package com.ourdax.coindocker.common.clients.achain.http.client;

import com.ourdax.coindocker.common.clients.achain.AchainClientException;
import com.ourdax.coindocker.common.clients.achain.common.ClientProperties;
import com.ourdax.coindocker.common.clients.achain.common.Constants;
import com.ourdax.coindocker.common.clients.achain.common.DataFormats;
import com.ourdax.coindocker.common.clients.achain.common.ErrorCode;
import com.ourdax.coindocker.common.clients.achain.common.HttpConstants;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AchainHttpClientImpl implements AchainHttpClient {
	
	private static final Logger LOG =
			LoggerFactory.getLogger(AchainHttpClientImpl.class);
	
	private CloseableHttpClient provider;
	private Properties clientConfig;


	public AchainHttpClientImpl(CloseableHttpClient httpClient, Properties clientConfig) {
		this.provider = httpClient;
		this.clientConfig = clientConfig;
	}

	public AchainHttpClientImpl(Properties clientConfig) {
		this.provider = HttpClients.createDefault();
		this.clientConfig = clientConfig;
	}

	@Override
	public String execute(String reqMethod, String reqPayload) throws AchainClientException {
		CloseableHttpResponse response = null;
		try {
			response = provider.execute(getNewRequest(reqMethod, reqPayload), new BasicHttpContext());
			response = checkResponse(response);
			HttpEntity respPayloadEntity = response.getEntity();
			String respPayload = Constants.STRING_EMPTY;
			if (respPayloadEntity != null) {
				respPayload = EntityUtils.toString(respPayloadEntity);
				EntityUtils.consume(respPayloadEntity);
			}
			LOG.debug("-- execute(..): '{}' response payload received for HTTP '{}' request with " 
					+ "status line '{}'", ((respPayloadEntity == null) ? "null" : "non-null"), 
					reqMethod, response.getStatusLine());
			return respPayload;
		} catch (ClientProtocolException e) {
			throw new AchainClientException(ErrorCode.REQUEST_HTTP_FAULT, e);
		} catch (IOException e) {
			throw new AchainClientException(ErrorCode.IO_UNKNOWN, e);
		} catch (URISyntaxException e) {
			throw new AchainClientException(ErrorCode.PARSE_URI_FAILED, e);
		}catch (Exception e) {
			e.printStackTrace();
			throw new AchainClientException(ErrorCode.IO_UNKNOWN, e);
		} finally {
			if (response != null) {
				try {
					LOG.debug("-- execute(..): attempting to recycle old HTTP response (reply to a "
							+ "'{}' request) with status line '{}'", reqMethod, response
							.getStatusLine());
					response.close();
				} catch (IOException e) {
					LOG.warn("<< execute(..): failed to recycle old HTTP response, message was: "
							+ "'{}'", e.getMessage());
				}
			}
		}
	}
	
	@Override
	public void close() {
		try {
			LOG.info(">> close(..): attempting to shut down the underlying HTTP provider");
			provider.close();
		} catch (IOException e) {
			LOG.warn("<< close(..): failed to shut down the underlying HTTP provider, message was: "
					+ "'{}'", e.getMessage());
		}
	}

	private HttpRequestBase getNewRequest(String reqMethod, String reqPayload)
			throws URISyntaxException {
		HttpRequestBase request;
		if (reqMethod.equals(HttpConstants.REQ_METHOD_POST)) {
			HttpPost postRequest = new HttpPost();
			postRequest.setEntity(new StringEntity(reqPayload, ContentType.create(
					DataFormats.JSON.getMediaType(), Constants.UTF_8)));
			request = postRequest;
		} else {
			throw new IllegalArgumentException(ErrorCode.ARGS_HTTP_METHOD_UNSUPPORTED.getDescription());
		}
		request.setURI(new URI(String.format("%s://%s:%s/rpc",
					clientConfig.getProperty(ClientProperties.RPC_PROTOCOL.getKey()),
					clientConfig.getProperty(ClientProperties.RPC_HOST.getKey()),
					clientConfig.getProperty(ClientProperties.RPC_PORT.getKey()))));
		String authScheme = clientConfig.getProperty(ClientProperties.HTTP_AUTH_SCHEME.getKey());
		request.addHeader(resolveAuthHeader(authScheme));
		LOG.debug("<< getNewRequest(..): returning a new HTTP '{}' request with target endpoint "
				+ "'{}' and headers '{}'", reqMethod, request.getURI(), request.getAllHeaders());
		return request;
	}

	private Header resolveAuthHeader(String authScheme) {
		if (authScheme.equals(HttpConstants.AUTH_SCHEME_NONE)) {
			return null;
		}
		if (authScheme.equals(HttpConstants.AUTH_SCHEME_BASIC)) {
			return new BasicHeader(HttpConstants.HEADER_AUTH, HttpConstants.AUTH_SCHEME_BASIC
					+ " " + getCredentials(HttpConstants.AUTH_SCHEME_BASIC));
		}
		return null;
	}

	private String getCredentials(String authScheme) {
		if (authScheme.equals(HttpConstants.AUTH_SCHEME_NONE)) {
			return Constants.STRING_EMPTY;
		} else if (authScheme.equals(HttpConstants.AUTH_SCHEME_BASIC)) {
			return Base64.encodeBase64String((clientConfig.getProperty(ClientProperties.RPC_USER.getKey())
					+ ":" + clientConfig.getProperty(ClientProperties.RPC_PASSWORD.getKey())).getBytes());
		}
		throw new IllegalArgumentException(ErrorCode.ARGS_HTTP_AUTHSCHEME_UNSUPPORTED.getDescription());
	}
	
	private CloseableHttpResponse checkResponse(CloseableHttpResponse response)
			throws AchainClientException {
		LOG.debug(">> checkResponse(..): checking HTTP response for non-OK status codes & "
				+ "unexpected header values");
		StatusLine statusLine = response.getStatusLine();
		if ((statusLine.getStatusCode() >= 400) && (statusLine.getStatusCode() <= 499)) {
			throw new AchainClientException(ErrorCode.RESPONSE_HTTP_CLIENT_FAULT, statusLine.toString());
		}
		if ((statusLine.getStatusCode() == 500)) {
			return response;
		}
		if ((statusLine.getStatusCode() >= 501) && (statusLine.getStatusCode() <= 599)) {
			throw new AchainClientException(ErrorCode.RESPONSE_HTTP_SERVER_FAULT, statusLine.toString());
		}	
		return response;
	}
}