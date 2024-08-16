package prac2;

import java.util.Set;

import javax.persistence.*;

@Entity
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long accountNumber;
    private String accountHolderName;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Transaction> transactions;

    BankAccount() {
    }

    BankAccount(String accountHolderName) {
        this.accountHolderName = accountHolderName;
        this.transactions = new java.util.HashSet<>();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }
}
