package prac1;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class MainController implements Initializable {
    //Table View
    @FXML
    TableView<Transaction> transactionTable;

    ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    @FXML
    TableColumn<Transaction, Long> transactionIdColumn;

    @FXML
    TableColumn<Transaction, String> transactionDateColumn;

    @FXML
    TableColumn<Transaction, Double> amountColumn;

    @FXML
    TableColumn<Transaction, Long> senderAccountNumberColumn;

    @FXML
    TableColumn<Transaction, Long> receiverAccountNumberColumn;

    @FXML
    TableColumn<Transaction, String> transactionTypeColumn;

    // Controlls
    @FXML
    Button searchButton;

    @FXML
    Button insertButton;

    @FXML
    Button deleteButton;

    @FXML
    Button updateButton;

    @FXML
    TextField transactionIdField;

    @FXML
    DatePicker transactionDateField;

    @FXML
    TextField senderAccountNumberField;

    @FXML
    TextField receiverAccountNumberField;

    @FXML
    Spinner<Double> amountField;

    @FXML
    ChoiceBox<String> transactionTypeField;

    int selectedRow = -1;

    // Methods to Search
    public void search() {
        if (!validateFieldsSearch()) {
            return;
        }

        String query = "SELECT t FROM Transaction t";
        ArrayList<String> queryParts = new ArrayList<String>();

        if (transactionIdField.getText() != null && !transactionIdField.getText().isEmpty()) {
            queryParts.add("t.transactionId = " + transactionIdField.getText());
        }

        if (transactionDateField.getValue() != null) {
            queryParts.add("t.transactionDate = '"
                    + transactionDateField.getValue().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    + "'");
        }

        if (senderAccountNumberField.getText() != null && !senderAccountNumberField.getText().isEmpty()) {
            queryParts.add("t.senderAccountNumber = " + senderAccountNumberField.getText());
        }

        if (receiverAccountNumberField.getText() != null && !receiverAccountNumberField.getText().isEmpty()) {
            queryParts.add("t.receiverAccountNumber = " + receiverAccountNumberField.getText());
        }

        if (amountField.getValue() != null && amountField.getValue() > 0) {
            queryParts.add("t.amount = " + amountField.getValue());
        }

        if (!transactionTypeField.getValue().equals("Select Transaction Type")) {
            queryParts.add("t.transactionType = '" + transactionTypeField.getValue() + "'");
        }

        if (queryParts.size() > 0) {
            query += " WHERE ";
            query += String.join(" AND ", queryParts);
        }

        EntityManager em = ObjectDBManager.getInstance().getEM();
        em.getTransaction().begin();

        TypedQuery<Transaction> typedQuery = em.createQuery(query, Transaction.class);
        transactions.clear();
        transactions = FXCollections.observableArrayList(typedQuery.getResultList());

        em.getTransaction().commit();
        ObjectDBManager.getInstance().closeEM();

        refreshTable(false);
        showPopUp("Search Completed!");
    }

    public boolean validateFieldsSearch() {
        if (transactionIdField.getText() != null && !transactionIdField.getText().isEmpty()) {
            try {
                Long.parseLong(transactionIdField.getText());
            } catch (NumberFormatException e) {
                showAlert("Transaction ID must be a valid number.");
                return false;
            }
        }

        if (senderAccountNumberField.getText() != null && !senderAccountNumberField.getText().isEmpty()) {
            try {
                Long.parseLong(senderAccountNumberField.getText());
            } catch (NumberFormatException e) {
                showAlert("Sender account number must be a valid number.");
                return false;
            }
        }

        if (receiverAccountNumberField.getText() != null && !receiverAccountNumberField.getText().isEmpty()) {
            try {
                Long.parseLong(receiverAccountNumberField.getText());
            } catch (NumberFormatException e) {
                showAlert("Receiver account number must be a valid number.");
                return false;
            }
        }

        return true;
    }

    // Methods to Insert
    public void insert() {
        if (!validateFieldsInsert()) {
            return;
        }

        // Transaction transaction = new Transaction(LocalDate.now(), 100.0, 654321, 5654, "Deposit");
        Transaction t = makeTransaction();

        EntityManager em = ObjectDBManager.getInstance().getEM();
        em.getTransaction().begin();
        em.persist(t);
        em.getTransaction().commit();
        ObjectDBManager.getInstance().closeEM();

        clearFields();
        showPopUp("Insert Completed!");
    }

    private Transaction makeTransaction() {
        Transaction transaction = new Transaction();
        transaction.setTransactionDate(transactionDateField.getValue());
        transaction.setSenderAccountNumber(Long.parseLong(senderAccountNumberField.getText()));
        transaction.setReceiverAccountNumber(Long.parseLong(receiverAccountNumberField.getText()));
        transaction.setAmount(amountField.getValue());
        transaction.setTransactionType(transactionTypeField.getValue());
        return transaction;
    }

    private boolean validateFieldsInsert() {
        if (transactionIdField.getText() == null || !transactionIdField.getText().isEmpty()) {
            transactionIdField.clear();
        }

        // Validate transaction date
        if (transactionDateField.getValue() == null) {
            showAlert("Transaction date is required.");
            return false;
        }

        // Validate sender account number
        String senderAccountNumberText = senderAccountNumberField.getText();
        if (senderAccountNumberText == null || senderAccountNumberText.isEmpty()) {
            showAlert("Sender account number is required.");
            return false;
        }
        try {
            Long.parseLong(senderAccountNumberText);
        } catch (NumberFormatException e) {
            showAlert("Sender account number must be a valid number.");
            return false;
        }

        // Validate receiver account number
        String receiverAccountNumberText = receiverAccountNumberField.getText();
        if (receiverAccountNumberText == null || receiverAccountNumberText.isEmpty()) {
            showAlert("Receiver account number is required.");
            return false;
        }
        try {
            Long.parseLong(receiverAccountNumberText);
        } catch (NumberFormatException e) {
            showAlert("Receiver account number must be a valid number.");
            return false;
        }

        // Validate amount
        Double amount = amountField.getValue();
        if (amount == null || amount <= 0) {
            showAlert("Amount must be a positive number.");
            return false;
        }

        // Validate transaction type
        String transactionType = transactionTypeField.getValue();
        if (transactionType == null || transactionType.isEmpty()) {
            showAlert("Transaction type is required.");
            return false;
        } else if (transactionType.equals("Select Transaction Type")) {
            showAlert("Please select a valid transaction type.");
            return false;
        }
        return true;
    }

    // Methods to Delete
    public void delete() {
        if (selectedRow <= -1) {
            showAlert("Please select a row to delete.");
            return;
        }

        EntityManager em = ObjectDBManager.getInstance().getEM();
        em.getTransaction().begin();

        Transaction t = transactionTable.getItems().get(selectedRow);
        Transaction transaction = em.find(Transaction.class, t.getTransactionId());
        em.remove(transaction);

        em.getTransaction().commit();
        ObjectDBManager.getInstance().closeEM();

        clearFields();
        showPopUp("Delete Completed!");
    }

    // Methods to Update
    public void update() {
        if (selectedRow <= -1) {
            showAlert("Please select a row to update.");
            return;
        }

        if (!validateFieldsInsert()) {
            return;
        }

        EntityManager em = ObjectDBManager.getInstance().getEM();
        em.getTransaction().begin();

        Transaction t = transactionTable.getItems().get(selectedRow);
        Transaction transaction = em.find(Transaction.class, t.getTransactionId());
        transaction.setTransactionDate(transactionDateField.getValue());
        transaction.setSenderAccountNumber(Long.parseLong(senderAccountNumberField.getText()));
        transaction.setReceiverAccountNumber(Long.parseLong(receiverAccountNumberField.getText()));
        transaction.setAmount(amountField.getValue());
        transaction.setTransactionType(transactionTypeField.getValue());

        em.getTransaction().commit();
        ObjectDBManager.getInstance().closeEM();

        clearFields();
        showPopUp("Update Completed!");
    }

    public void calculateTotal() {
        EntityManager em = ObjectDBManager.getInstance().getEM();
        em.getTransaction().begin();

        TypedQuery<Transaction> query = em.createQuery("SELECT t FROM Transaction t", Transaction.class);
        List<Transaction> transactions = query.getResultList();

        double total = 0.0;
        for (Transaction t : transactions) {
            total += t.getAmount();
        }

        em.getTransaction().commit();
        ObjectDBManager.getInstance().closeEM();

        showPopUp("Total amount in the database: " + total);
    }

    @FXML
    private void getSelectedRow(MouseEvent e) {
        selectedRow = transactionTable.getSelectionModel().getSelectedIndex();

        if (selectedRow <= -1) {
            return;
        }

        Transaction t = transactionTable.getItems().get(selectedRow);
        transactionIdField.setText(String.valueOf(t.getTransactionId()));
        transactionDateField.setValue(LocalDate.parse(t.getTransactionDate()));
        amountField.getValueFactory().setValue(t.getAmount());
        senderAccountNumberField.setText(String.valueOf(t.getSenderAccountNumber()));
        receiverAccountNumberField.setText(String.valueOf(t.getReceiverAccountNumber()));
        transactionTypeField.setValue(t.getTransactionType());
    }

    public void clearFields() {
        transactionIdField.clear();
        transactionDateField.setValue(null);
        senderAccountNumberField.clear();
        receiverAccountNumberField.clear();
        amountField.getValueFactory().setValue(0.0);
        transactionTypeField.setValue("Select Transaction Type");

        refreshTable();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showPopUp(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void refreshTable() {
        refreshTable(true);
    }

    private void refreshTable(boolean clear) {
        transactionTable.getItems().clear();

        transactionIdColumn.setCellValueFactory(new PropertyValueFactory<Transaction, Long>("transactionId"));
        transactionDateColumn.setCellValueFactory(new PropertyValueFactory<Transaction, String>("transactionDate"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<Transaction, Double>("amount"));
        senderAccountNumberColumn
                .setCellValueFactory(new PropertyValueFactory<Transaction, Long>("senderAccountNumber"));
        receiverAccountNumberColumn
                .setCellValueFactory(new PropertyValueFactory<Transaction, Long>("receiverAccountNumber"));
        transactionTypeColumn.setCellValueFactory(new PropertyValueFactory<Transaction, String>("transactionType"));

        EntityManager em = ObjectDBManager.getInstance().getEM();
        em.getTransaction().begin();

        if (clear) {
            TypedQuery<Transaction> query = em.createQuery("SELECT t FROM Transaction t", Transaction.class);
            transactions.clear();
            transactions = FXCollections.observableArrayList(query.getResultList());
        }

        em.getTransaction().commit();
        ObjectDBManager.getInstance().closeEM();

        transactionTable.setItems(transactions);
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        refreshTable();
        transactionTypeField.getItems().addAll("Select Transaction Type", "Deposit", "Withdrawal", "Transfer");
        transactionTypeField.setValue("Select Transaction Type");
        amountField
                .setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.00, Double.MAX_VALUE, 0.00, 0.1));
    }
}
