package prac2;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.*;

@Entity
public class Transaction implements Serializable {
    private final static long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long transactionId;

    private String transactionDate;
    private double amount;

    @ManyToOne
    @JoinColumn(name = "senderAccount", referencedColumnName = "accountNumber")
    private BankAccount senderAccount;

    @ManyToOne
    @JoinColumn(name = "receiverAccount", referencedColumnName = "accountNumber")
    private BankAccount receiverAccount;

    private String transactionType;

    // Default constructor
    public Transaction() {
    }

    // Parameterized constructor
    public Transaction(LocalDate transactionDate, double amount, BankAccount senderAccount, BankAccount receiverAccount,
            String transactionType) {
        this.transactionDate = transactionDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.amount = amount;
        this.senderAccount = senderAccount;
        this.receiverAccount = receiverAccount;
        this.transactionType = transactionType;
    }

    // Getters and Setters
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public BankAccount getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(BankAccount senderAccount) {
        this.senderAccount = senderAccount;
    }

    public BankAccount getReceiverAccount() {
        return receiverAccount;
    }

    public void setReceiverAccount(BankAccount receiverAccount) {
        this.receiverAccount = receiverAccount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public String toString(){
        return "Transaction ID: " + transactionId + "\n" +
               "Transaction Date: " + transactionDate + "\n" +
               "Amount: " + amount + "\n" +
               "Sender Account Number: " + senderAccount + "\n" +
               "Receiver Account Number: " + receiverAccount + "\n" +
               "Transaction Type: " + transactionType + "\n";
    }

}