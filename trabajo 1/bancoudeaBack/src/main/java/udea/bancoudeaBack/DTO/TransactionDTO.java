package udea.bancoudeaBack.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDTO {

    private Long id;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private BigDecimal amount;
    private LocalDateTime timestamp;

    public TransactionDTO() {
    }

    public TransactionDTO(Long id, String senderAccountNumber, String receiverAccountNumber, BigDecimal amount,
            LocalDateTime timestamp) {
        this.id = id;
        this.senderAccountNumber = senderAccountNumber;
        this.receiverAccountNumber = receiverAccountNumber;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderAccountNumber() {
        return senderAccountNumber;
    }

    public void setSenderAccountNumber(String senderAccountNumber) {
        this.senderAccountNumber = senderAccountNumber;
    }

    public String getReceiverAccountNumber() {
        return receiverAccountNumber;
    }

    public void setReceiverAccountNumber(String receiverAccountNumber) {
        this.receiverAccountNumber = receiverAccountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TransactionDTO that = (TransactionDTO) o;
        return Objects.equals(id, that.id)
                && Objects.equals(senderAccountNumber, that.senderAccountNumber)
                && Objects.equals(receiverAccountNumber, that.receiverAccountNumber)
                && (amount == that.amount
                        || (amount != null && that.amount != null && amount.compareTo(that.amount) == 0))
                && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        BigDecimal normalizedAmount = amount != null ? amount.stripTrailingZeros() : null;
        return Objects.hash(id, senderAccountNumber, receiverAccountNumber, normalizedAmount, timestamp);
    }

}
