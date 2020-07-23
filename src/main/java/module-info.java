module SharifAP {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires javafx.media;

    opens JavaProject to javafx.fxml;
    opens JavaProject.Controller to javafx.fxml, javafx.base;
    opens JavaProject.Model to javafx.base;
    opens JavaProject.Model.Account to com.google.gson, javafx.base;
    opens JavaProject.Model.Discount to com.google.gson, javafx.base;
    opens JavaProject.Model.Request to com.google.gson, javafx.base;
    opens JavaProject.Model.ProductOrganization to com.google.gson, javafx.base;
    opens JavaProject.Model.Log to com.google.gson, javafx.base;
    opens JavaProject.Model.Chat to com.google.gson;
    exports JavaProject;
}