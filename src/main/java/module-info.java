module org.example.catan {
    requires javafx.controls;
    requires javafx.fxml;

    requires static lombok;

    opens org.example.catan to javafx.fxml;
    exports org.example.catan;
    exports org.example.catan.buildings;
    opens org.example.catan.buildings to javafx.fxml;
    exports org.example.catan.gamepieces;
    opens org.example.catan.gamepieces to javafx.fxml;
}
