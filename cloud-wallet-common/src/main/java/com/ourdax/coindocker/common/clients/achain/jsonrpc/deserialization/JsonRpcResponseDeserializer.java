package com.ourdax.coindocker.common.clients.achain.jsonrpc.deserialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.ourdax.coindocker.common.clients.achain.jsonrpc.domain.JsonRpcError;
import com.ourdax.coindocker.common.clients.achain.jsonrpc.domain.JsonRpcResponse;
import java.io.IOException;

public class JsonRpcResponseDeserializer extends JsonDeserializer<JsonRpcResponse> {

	@Override
	public JsonRpcResponse deserialize(JsonParser parser, DeserializationContext context)
			throws IOException  {
		RawJsonRpcResponse rawRpcResponse = parser.readValueAs(RawJsonRpcResponse.class);

		return rawRpcResponse.toJsonRpcResponse();
	}

	private static class RawJsonRpcResponse {
		public JsonNode result;
		public JsonRpcError error;
		public String id;


		private JsonRpcResponse toJsonRpcResponse() {
			JsonRpcResponse rpcResponse = new JsonRpcResponse();
			if (result != null) {
				rpcResponse.setResult(result.toString());
			}
			rpcResponse.setError(error);
			rpcResponse.setId(id);
			return rpcResponse;
		} 
	}
}