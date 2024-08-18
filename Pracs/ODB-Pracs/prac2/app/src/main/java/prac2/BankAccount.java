package prac2;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name = "bank_account")
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_number", nullable = false)
    private Long accountNumber;

    @Column(name = "account_holder_name", nullable = false)
    private String accountHolderName;

    @OneToMany(mappedBy = "senderAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Transaction> senderTransactions = new HashSet<>();

    @OneToMany(mappedBy = "receiverAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Transaction> receiverTransactions = new HashSet<>();

    public BankAccount() {
    }

    public BankAccount(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public Set<Transaction> getReceiverTransactions() {
        return receiverTransactions;
    }

    public void setReceiverTransactions(Set<Transaction> receiverTransactions) {
        this.receiverTransactions = receiverTransactions;
    }

    public Set<Transaction> getSenderTransactions() {
        return senderTransactions;
    }

    public void setSenderTransactions(Set<Transaction> senderTransactions) {
        this.senderTransactions = senderTransactions;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }
}
