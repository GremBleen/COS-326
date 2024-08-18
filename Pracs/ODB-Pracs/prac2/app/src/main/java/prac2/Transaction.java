package prac2;

import java.time.LocalDate;

import javax.persistence.*;

@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id", nullable = false)
    private Long transactionId;

    @Column(name = "transaction_date", nullable = false)
    private String transactionDate;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_account_account_number", nullable = false)
    private BankAccount senderAccount;

    @ManyToOne(optional = false)
    @JoinColumn(name = "receiver_account_account_number", nullable = false)
    private BankAccount receiverAccount;

    public Transaction() {
    }

    public Transaction(LocalDate transactionDate, Double amount, String transactionType, BankAccount senderAccount,
            BankAccount receiverAccount) {
        this.transactionDate = transactionDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.amount = amount;
        this.transactionType = transactionType;
        this.senderAccount = senderAccount;
        this.receiverAccount = receiverAccount;
    }

    public BankAccount getReceiverAccount() {
        return receiverAccount;
    }

    public void setReceiverAccount(BankAccount receiverAccount) {
        this.receiverAccount = receiverAccount;
    }

    public BankAccount getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(BankAccount senderAccount) {
        this.senderAccount = senderAccount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }
}