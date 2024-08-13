module prac1 {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires transitive javax.persistence;
    requires java.sql;
    requires objectdb;

    opens prac1 to javafx.fxml, objectdb;
    exports prac1;
}
