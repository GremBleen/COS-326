package prac1;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.*;

@Entity
public class Transaction implements Serializable {
    private final static long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private long transactionId;

    private String transactionDate;
    private double amount;
    private long senderAccountNumber;
    private long receiverAccountNumber;
    private String transactionType;

    // Default constructor
    public Transaction() {
    }

    // Parameterized constructor
    public Transaction(LocalDate transactionDate, double amount, int senderAccountNumber, int receiverAccountNumber,
            String transactionType) {
        this.transactionDate = transactionDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.amount = amount;
        this.senderAccountNumber = senderAccountNumber;
        this.receiverAccountNumber = receiverAccountNumber;
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

    public Long getSenderAccountNumber() {
        return senderAccountNumber;
    }

    public void setSenderAccountNumber(Long senderAccountNumber) {
        this.senderAccountNumber = senderAccountNumber;
    }

    public Long getReceiverAccountNumber() {
        return receiverAccountNumber;
    }

    public void setReceiverAccountNumber(Long receiverAccountNumber) {
        this.receiverAccountNumber = receiverAccountNumber;
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
               "Sender Account Number: " + senderAccountNumber + "\n" +
               "Receiver Account Number: " + receiverAccountNumber + "\n" +
               "Transaction Type: " + transactionType + "\n";
    }

}