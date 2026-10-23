package udea.bancoudeaBack.DTO;

import java.math.BigDecimal;
import java.util.Objects;

public class CustomerDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String accountNumber;
    private BigDecimal balance;

    public CustomerDTO() {
    }

    public CustomerDTO(Long id, String firstName, String lastName, String accountNumber, BigDecimal balance) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CustomerDTO that = (CustomerDTO) o;
        return Objects.equals(id, that.id)
                && Objects.equals(firstName, that.firstName)
                && Objects.equals(lastName, that.lastName)
                && Objects.equals(accountNumber, that.accountNumber)
                && (balance == that.balance
                        || (balance != null && that.balance != null && balance.compareTo(that.balance) == 0));
    }

    @Override
    public int hashCode() {
        BigDecimal normalizedBalance = balance != null ? balance.stripTrailingZeros() : null;
        return Objects.hash(id, firstName, lastName, accountNumber, normalizedBalance);
    }
}
