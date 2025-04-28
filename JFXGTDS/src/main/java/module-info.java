module com.example.jfxgtds {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.jfxgtds to javafx.fxml;
    exports com.example.jfxgtds;
}