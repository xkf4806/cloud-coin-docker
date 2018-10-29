package com.ourdax.coindocker.common.clients.achain.jsonrpc.client;

import com.ourdax.coindocker.common.clients.achain.AchainClientException;
import com.ourdax.coindocker.common.clients.achain.common.Constants;
import com.ourdax.coindocker.common.clients.achain.common.ErrorCode;
import com.ourdax.coindocker.common.clients.achain.common.HttpConstants;
import com.ourdax.coindocker.common.clients.achain.http.client.AchainHttpClient;
import com.ourdax.coindocker.common.clients.achain.http.client.AchainHttpClientImpl;
import com.ourdax.coindocker.common.clients.achain.jsonrpc.JsonMapper;
import com.ourdax.coindocker.common.clients.achain.jsonrpc.JsonPrimitiveParser;
import com.ourdax.coindocker.common.clients.achain.jsonrpc.domain.JsonRpcError;
import com.ourdax.coindocker.common.clients.achain.jsonrpc.domain.JsonRpcRequest;
import com.ourdax.coindocker.common.clients.achain.jsonrpc.domain.JsonRpcResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonRpcClientImpl implements JsonRpcClient {


	private static final Logger LOG =
			LoggerFactory.getLogger(com.neemre.btcdcli4j.core.jsonrpc.client.JsonRpcClientImpl.class);
	
	private AchainHttpClient achainHttpClient;
	private JsonPrimitiveParser parser;
	private JsonMapper mapper;


	public JsonRpcClientImpl(CloseableHttpClient httpClient, Properties nodeConfig) {
		achainHttpClient = new AchainHttpClientImpl(httpClient, nodeConfig);
		parser = new JsonPrimitiveParser();
		mapper = new JsonMapper();
	}

	@Override
	public String execute(String method) throws AchainClientException {
		return execute(method, Collections.emptyList());
	}

	@Override
	public <T> String execute(String method, T param) throws AchainClientException{
		List<T> params = new ArrayList<T>();
		params.add(param);
		return execute(method, params);
	}

	@Override
	public <T> String execute(String method, List<T> params) throws AchainClientException {
		LOG.debug(">> execute(..): invoking 'achain' JSON-RPC API command '{}' with params: '{}'", method, params);
		String requestUuid = getNewUuid();
		JsonRpcRequest<T> request = getNewRequest(method, params, requestUuid);
		String requestJson = mapper.mapToJson(request);
		LOG.debug("-- execute(..): sending JSON-RPC request as (raw): '{}'", requestJson.trim());
		String responseJson = achainHttpClient.execute(HttpConstants.REQ_METHOD_POST, requestJson);
		LOG.debug("-- execute(..): received JSON-RPC response as (raw): '{}'", responseJson.trim());
		JsonRpcResponse response = mapper.mapToEntity(responseJson, JsonRpcResponse.class);
		response = verifyResponse(request, response);
		response = checkResponse(response);
		LOG.debug("<< execute(..): returning result for 'achain' API command '{}' as: '{}'",
				method, response.getResult());
		return response.getResult();
	}

	@Override
	public JsonPrimitiveParser getParser() {
		return parser;
	}

	@Override
	public JsonMapper getMapper() {
		return mapper;
	}

	@Override
	public void close() {
		achainHttpClient.close();
	}
	
	private <T> JsonRpcRequest<T> getNewRequest(String method, List<T> params, String id) {
		JsonRpcRequest<T> rpcRequest = new JsonRpcRequest<T>();
		rpcRequest.setJsonrpc(Constants.JSON_RPC_VERSION);
		rpcRequest.setMethod(method);
		rpcRequest.setParams(params);
		rpcRequest.setId(id);
		return rpcRequest;
	}

	private JsonRpcResponse getNewResponse(String result, JsonRpcError error, String id) {
		JsonRpcResponse rpcResponse = new JsonRpcResponse();
		rpcResponse.setJsonrpc(Constants.JSON_RPC_VERSION);
		rpcResponse.setResult(result);
		rpcResponse.setError(error);
		rpcResponse.setId(id);
		return rpcResponse;
	}

	private String getNewUuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	private <T> JsonRpcResponse verifyResponse(JsonRpcRequest<T> request, JsonRpcResponse response)
			throws AchainClientException {
		LOG.debug(">> verifyResponse(..): verifying JSON-RPC response for basic protocol conformance");
		if (response == null) {
			throw new AchainClientException(ErrorCode.RESPONSE_JSONRPC_NULL);
		}
		if (response.getId() == null) {
			throw new AchainClientException(ErrorCode.RESPONSE_JSONRPC_NULL_ID);
		}
		//todo
//		if (!response.getId().equals(request.getId())) {
//			throw new AchainClientException(ErrorCode.RESPONSE_JSONRPC_UNEQUAL_IDS);
//		}
		if ((response.getJsonrpc() != null) && (!response.getJsonrpc().equals(Constants.JSON_RPC_VERSION))) {
			LOG.warn("-- verifyResponse(..): JSON-RPC version mismatch - client optimized for '{}'"
					+ ", node responded in '{}'", Constants.JSON_RPC_VERSION, response.getJsonrpc());
		}
		return response;
	}

	private <T> JsonRpcResponse checkResponse(JsonRpcResponse response) throws AchainClientException {
		if (!(response.getError() == null)) {
			JsonRpcError jsonRpcError = response.getError();
			throw new AchainClientException(ErrorCode.RESPONSE_JSONRPC_ERROR,
					String.format("Error: %s", jsonRpcError.getMessage()));
		}
		return response;
	}
}