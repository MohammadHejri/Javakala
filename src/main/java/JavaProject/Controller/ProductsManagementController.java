package JavaProject.Controller;

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

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ProductsManagementController implements Initializable {


    @FXML
    Label statusLabel;
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
    TextField parentField;
    @FXML
    TextField specsField;
    @FXML
    TextArea descriptionArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
        if (product == null) {
            statusLabel.setText("Choose product");
        } else {
            Database.getInstance().deleteProduct(product);
            statusLabel.setText("Deleted successfully");
            productsTable.getItems().clear();
            for (Product pro : Database.getInstance().getAllProducts())
                productsTable.getItems().add(pro);
        }
    }

    @FXML
    public void editSpecs(ActionEvent event) throws IOException {
        Product product = productsTable.getSelectionModel().getSelectedItem();
        DualString dualString = specsTable.getSelectionModel().getSelectedItem();
        String info = specsField.getText();
        if (dualString == null || info.isBlank()) {
            statusLabel.setText("Choose spec & enter info");
        } else {
            product.getSpecs().replace(dualString.getKey(), dualString.getValue(), info);
            specsTable.getItems().clear();
            for (String key : product.getSpecs().keySet())
                specsTable.getItems().add(new DualString(key, product.getSpecs().get(key)));
        }
        Database.getInstance().updateCategories();
    }

    @FXML
    public void changeCategory(ActionEvent event) throws IOException {
        Product product = productsTable.getSelectionModel().getSelectedItem();
        String parentName = parentField.getText();
        Category newParent = Database.getInstance().getCategoryByName(parentName);
        if (product == null) {
            statusLabel.setText("Choose product");
        } else if (parentName.isBlank() || newParent == null) {
            statusLabel.setText("Enter valid category");
        } else if (!parentName.equals(product.getParentCategoryName())) {
            Database.getInstance().getCategoryByName(product.getParentCategoryName()).getProducts().remove(product);
            newParent.getProducts().add(product);
            HashMap<String, String> hashMap = new HashMap<>();
            for (String key : Database.getInstance().getAllFeatures(newParent)) {
                hashMap.put(key, product.getSpecs().getOrDefault(key, ""));
            }
            product.setSpecs(hashMap);
            product.setParentCategoryName(parentName);
            Database.getInstance().updateCategories();
            statusLabel.setText("success");
            deselectProduct();
            productsTable.getItems().clear();
            for (Product pro : Database.getInstance().getAllProducts())
                productsTable.getItems().add(pro);
        }
    }

    private void selectProduct() {
        Product product = productsTable.getSelectionModel().getSelectedItem();
        parentField.setText(product.getParentCategoryName());
        descriptionArea.setText(product.getDescription());
        specsTable.getItems().clear();
        for (String key : product.getSpecs().keySet())
            specsTable.getItems().add(new DualString(key, product.getSpecs().get(key)));
    }

    private void deselectProduct() {
        parentField.setText("");
        descriptionArea.setText("");
        specsTable.getItems().clear();
    }

}

