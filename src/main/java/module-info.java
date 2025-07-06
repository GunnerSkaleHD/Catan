module org.example.catan {
    requires javafx.controls;
    requires javafx.fxml;

    requires static lombok;

    opens org.example.catan to javafx.fxml;
    exports org.example.catan;
}
