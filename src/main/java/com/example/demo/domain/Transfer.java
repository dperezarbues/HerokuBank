package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Transfer {

  @JsonIgnore
  private static AtomicLong counter = new AtomicLong(0);
  
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  @NotNull
  private final Long transferId;
  
  @NotNull
  @NotEmpty
  private final String senderAccountId;
  @NotNull
  @NotEmpty
  private final String receiverAccountId;
  @NotNull
  @Min(value = 0, message = "Amount to transfer must be positive.")
  private final BigDecimal amount;
  private Status status;
  private String failureCause;

    public Status getStatus(){
        return this.status;
    }
    
    public String getFailureCause(){
        return this.failureCause;
    }   

    public BigDecimal getAmount(){
        return this.amount;
    }   

    public String getReceiverAccountId(){
        return this.receiverAccountId;
    } 
    public String getSenderAccountId(){
        return this.senderAccountId;
    }   
    public Long getTransferId (){
        return this.transferId;
    }
    
    public void setFailureCause(String failureCause){
        this.failureCause=failureCause;
    }   
    
  @JsonCreator
  public Transfer(@JsonProperty("senderAccountId") String senderAccountId,
                  @JsonProperty("receiverAccountId") String receiverAccountId,
                  @JsonProperty("amount") BigDecimal amount) {

    this.transferId = counter.addAndGet(1);
    this.senderAccountId = senderAccountId;
    this.receiverAccountId = receiverAccountId;
    this.amount = amount;
    this.status = Status.PENDING;

  }
  
  public Transfer(){
    this.transferId = counter.addAndGet(1);
    this.senderAccountId = "";
    this.receiverAccountId = "";
    this.amount = new BigDecimal(0);
    this.status = Status.FAILED;
  }

  public void setStatus(Status status) {
    if (this.status == Status.COMPLETED) {
      throw new UnsupportedOperationException("Not allowed to revert a completed transference to another status");
    }
    this.status = status;

  }

  public enum Status {
    COMPLETED("Completed"),
    PENDING("Pending"),
    FAILED("Failed");
    private String description;

    Status(String description) {
      this.description = description;
    }

    @JsonValue
    public String getDescription() {
      return this.description;
    }
  }

}
