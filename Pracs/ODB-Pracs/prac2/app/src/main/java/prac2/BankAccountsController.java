package prac2;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.persistence.*;

import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;

public class BankAccountsController implements Initializable {

    @FXML
    private TableView<BankAccount> bankAccountsTable;

    ObservableList<BankAccount> bankAccounts = FXCollections.observableArrayList();

    @FXML
    private TableColumn<BankAccount, Long> accountNumberColumn;

    @FXML
    private TableColumn<BankAccount, String> accountHolderNameColumn;

    @FXML
    private TextField accountNumberField;

    @FXML
    private TextField accountHolderNameField;

    @FXML
    private Button clearButton2;

    @FXML
    private Button deleteButton2;

    @FXML
    private Button insertButton2;

    @FXML
    private Button searchButton2;

    @FXML
    private Button updateButton2;

    int selectedRow = -1;

    public void search() {
        if (!validateFieldsSearch()) {
            return;
        }

        String query = "SELECT ba FROM BankAccount ba";
        ArrayList<String> conditions = new ArrayList<String>();

        if (accountNumberField.getText() != null && !accountNumberField.getText().isEmpty()) {
            conditions.add("ba.accountNumber = " + accountNumberField.getText());
        }

        // if (accountHolderNameField.getText() != null && !accountHolderNameField.getText().isEmpty()) {
        //     conditions.add("ba.accountHolderName LIKE '%" + accountHolderNameField.getText() + "%'");
        // }

        if (conditions.size() > 0) {
            query += " WHERE " + String.join(" AND ", conditions);
        }

        EntityManager em = ObjectDBManager.getInstance().getEM();
        em.getTransaction().begin();

        TypedQuery<BankAccount> q = em.createQuery(query, BankAccount.class);
        bankAccounts.clear();
        bankAccounts = FXCollections.observableArrayList(q.getResultList());

        em.getTransaction().commit();
        ObjectDBManager.getInstance().closeEM();

        refreshTable(false);
        showPopUp("Search successful");
    }

    private boolean validateFieldsSearch() {
        if (accountNumberField.getText() != null && !accountNumberField.getText().isEmpty()) {
            try {
                Long.parseLong(accountNumberField.getText());
            } catch (NumberFormatException e) {
                showAlert("Account number must be a valid number.");
                return false;
            }
        }

        if (accountHolderNameField.getText() != null && !accountHolderNameField.getText().isEmpty()) {
            if (accountHolderNameField.getText().length() < 3) {
                showAlert("Account holder name must be at least 3 characters long.");
                return false;
            }
        }

        return true;
    }

    public void insert() {
        if (!validateFieldsInsert()) {
            return;
        }

        BankAccount b = makeBankAccount();

        EntityManager em = ObjectDBManager.getInstance().getEM();
        em.getTransaction().begin();
        em.persist(b);
        em.getTransaction().commit();
        ObjectDBManager.getInstance().closeEM();

        clearFields();
        showPopUp("Insert successful");
    }

    private boolean validateFieldsInsert() {
        if (accountNumberField.getText() == null || !accountNumberField.getText().isEmpty()) {
            accountNumberField.clear();
        }

        String accountHolderNameText = accountHolderNameField.getText();
        if (accountHolderNameText == null || accountHolderNameText.isEmpty()) {
            showAlert("Account holder name is required.");
            return false;
        }
        if(accountHolderNameText.length() < 3) {
            showAlert("Account holder name must be at least 3 characters long.");
            return false;
        }

        return true;
    }

    private BankAccount makeBankAccount() {
        BankAccount b = new BankAccount(accountHolderNameField.getText());
        return b;
    }

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

        BankAccount b = bankAccountsTable.getItems().get(selectedRow);
        BankAccount bankAccount = em.find(BankAccount.class, b.getAccountNumber());
        bankAccount.setAccountHolderName(accountHolderNameField.getText());

        em.getTransaction().commit();
        ObjectDBManager.getInstance().closeEM();

        clearFields();
        showPopUp("Update successful");
    }

    public void delete() {
        if (selectedRow <= -1) {
            showAlert("Please select a row to delete.");
            return;
        }

        boolean confirmation = showConfirmationDialog("Are you sure you want to delete this Bank Account?");
        if (!confirmation) {
            return;
        }

        EntityManager em = ObjectDBManager.getInstance().getEM();
        em.getTransaction().begin();

        BankAccount b = bankAccountsTable.getItems().get(selectedRow);
        BankAccount bankAccount = em.find(BankAccount.class, b.getAccountNumber());

        em.remove(bankAccount);

        em.getTransaction().commit();
        ObjectDBManager.getInstance().closeEM();

        clearFields();
        showPopUp("Delete Completed!");
    }

    public void clearFields() {
        accountNumberField.clear();
        accountHolderNameField.clear();

        selectedRow = -1;
        refreshTable();
    }

    public void getSelectedRow() {
        selectedRow = bankAccountsTable.getSelectionModel().getSelectedIndex();

        if (selectedRow <= -1) {
            return;
        }

        BankAccount b = bankAccounts.get(selectedRow);
        accountNumberField.setText(String.valueOf(b.getAccountNumber()));
        accountHolderNameField.setText(b.getAccountHolderName());
    }

    public void showTransactions() {
        if (selectedRow <= -1) {
            showAlert("Please select a row to view transactions.");
            return;
        }

        BankAccount b = bankAccounts.get(selectedRow);
        EntityManager em = ObjectDBManager.getInstance().getEM();
        em.getTransaction().begin();

        BankAccount bankAccount = em.find(BankAccount.class, b.getAccountNumber());
        // USe the 

        switchView();
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

    private boolean showConfirmationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

        if (alert.getResult().getText().equals("OK")) {
            return true;
        }
        return false;
    }

    private void refreshTable() {
        refreshTable(true);
    }

    private void refreshTable(boolean clear) {
        bankAccountsTable.getItems().clear();

        accountNumberColumn.setCellValueFactory(new PropertyValueFactory<BankAccount, Long>("accountNumber"));
        accountHolderNameColumn.setCellValueFactory(new PropertyValueFactory<BankAccount, String>("accountHolderName"));

        if (clear) {
            EntityManager em = ObjectDBManager.getInstance().getEM();
            em.getTransaction().begin();

            TypedQuery<BankAccount> query = em.createQuery("SELECT ba FROM BankAccount ba", BankAccount.class);
            bankAccounts.clear();
            bankAccounts.addAll(query.getResultList());
            
            em.getTransaction().commit();
            ObjectDBManager.getInstance().closeEM();
        }

        bankAccountsTable.setItems(bankAccounts);
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        refreshTable();
    }

    public void switchView() {
        try {
            App.setRoot("transactions");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
