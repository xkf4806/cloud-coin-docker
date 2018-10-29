package com.ourdax.coindocker.common.clients.achain.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author think on 1/2/2018
 */

@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@lombok.Data
@ToString
public class AchainBlock {

  @JsonProperty("block_size")
  private String blockSize;

  @JsonProperty("previous")
  private String previous;

  @JsonProperty("previous_secret")
  private String previousSecret;

  @JsonProperty("transaction_digest")
  private String transactionDigest;

  @JsonProperty("id")
  private String id;

  @JsonProperty("timestamp")
  private String timestamp;

  @JsonProperty("signee_fees_destroyed")
  private String signeeFeesDestroyed;

  @JsonProperty("user_transaction_ids")
  private List<String> userTransactionIds;

  @JsonProperty("latency")
  private String latency;

  @JsonProperty("signee_fees_collected")
  private String signeeFeesCollected;

  @JsonProperty("signee_shares_issued")
  private String signeeSharesIssued;

  @JsonProperty("block_num")
  private String blockNum;

  @JsonProperty("delegate_signature")
  private String delegateSignature;

  @JsonProperty("processing_time")
  private String processingTime;

  @JsonProperty("next_secret_hash")
  private String nextSecretHash;

  @JsonProperty("random_seed")
  private String randomSeed;

}
