package com.ourdax.coindocker.common.clients.usdt.omni;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neemre.btcdcli4j.core.common.Defaults;
import com.neemre.btcdcli4j.core.domain.Entity;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OmniTransaction extends Entity {

	@JsonProperty("txid")
	private String txId;
	@JsonProperty("sendingaddress")
	private String sendingAddress;
	@JsonProperty("referenceaddress")
	private String referenceAddress;
	@JsonProperty("ismine")
	private boolean isMine;
	private int version;
	@JsonProperty("type_int")
	private int typeInt;
	@JsonProperty("type")
	private String type;
	@JsonProperty("propertyid")
	private long propertyId;
	@JsonProperty("divisible")
	private boolean divisible;

	@Setter(AccessLevel.NONE)
	private BigDecimal amount;
	@Setter(AccessLevel.NONE)
	private BigDecimal fee;

	private boolean valid;
	@JsonProperty("blockhash")
	private String blockHash;
	@JsonProperty("blocktime")
	private Long blockTime;
	@JsonProperty("positioninblock")
	private Long positioninblock;
	private Long block;
	private Integer confirmations;

	public void setAmount(BigDecimal amount) {
		this.amount = amount.setScale(Defaults.DECIMAL_SCALE, Defaults.ROUNDING_MODE);
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee.setScale(Defaults.DECIMAL_SCALE, Defaults.ROUNDING_MODE);
	}
}