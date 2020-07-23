package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Database.Database;
import JavaProject.Model.DualString;
import JavaProject.Model.ProductOrganization.Category;
import JavaProject.Model.ProductOrganization.Product;
import JavaProject.Model.Status.Status;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

// Client-Server : Done

public class ProductsManagementController implements Initializable {

    File imageFile;

    @FXML
    TableView<Product> productsTable;
    @FXML
    TableColumn<Product, String> nameColumn;
    @FXML
    TableColumn<Product, String> categoryColumn;
    @FXML
    TableColumn<Product, String> brandColumn;
    @FXML
    TableColumn<Product, String> sellerColumn;
    @FXML
    TableColumn<Product, String> dateColumn;
    @FXML
    TableColumn<Product, Double> priceColumn;
    @FXML
    TableColumn<Product, Integer> viewsColumn;
    @FXML
    TableColumn<Product, Double> scoreColumn;
    @FXML
    TableColumn<Product, Status> statusColumn;
    @FXML
    TableView<DualString> specsTable;
    @FXML
    TableColumn<DualString, String> specsColumn;
    @FXML
    TableColumn<DualString, String> infoColumn;
    @FXML
    TextArea descriptionArea;
    @FXML
    Button deleteProductButton;
    @FXML
    ImageView imageView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        deleteProductButton.setDisable(true);
        specsColumn.setCellValueFactory(new PropertyValueFactory<>("key"));
        infoColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("parentCategoryName"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        sellerColumn.setCellValueFactory(new PropertyValueFactory<>("sellerUsername"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        viewsColumn.setCellValueFactory(new PropertyValueFactory<>("views"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("averageMark"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        for (Product product : Database.getInstance().getAllProducts())
            productsTable.getItems().add(product);

        productsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectProduct();
            } else {
                deselectProduct();
            }
        });
    }

    @FXML
    public void deleteProduct(ActionEvent event) throws IOException {
        Product product = productsTable.getSelectionModel().getSelectedItem();
        String response = App.getResponseFromServer("deleteProduct", product.getName());
        if (response.startsWith("Success")) {
            new Alert(Alert.AlertType.INFORMATION, "Product successfully deleted").showAndWait();
            productsTable.getItems().clear();
            for (Product pro : Database.getInstance().getAllProducts())
                productsTable.getItems().add(pro);
        } else {
            new Alert(Alert.AlertType.ERROR, response).showAndWait();
        }
    }

    private void selectProduct() {
        Product product = productsTable.getSelectionModel().getSelectedItem();
        descriptionArea.setText(product.getDescription());
        deleteProductButton.setDisable(false);
        imageFile = new File(App.getFileData(product.getName(), "productPhoto"));
        imageView.setImage(new Image(imageFile.toURI().toString()));
        specsTable.getItems().clear();
        for (String key : product.getSpecs().keySet())
            specsTable.getItems().add(new DualString(key, product.getSpecs().get(key)));
    }

    private void deselectProduct() {
        descriptionArea.setText("");
        deleteProductButton.setDisable(true);

        imageView.setImage(null);

        specsTable.getItems().clear();
    }

}

