package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Database.Database;
import JavaProject.Model.Discount.DiscountCode;
import JavaProject.Model.ProductOrganization.Category;
import JavaProject.Model.ProductOrganization.Product;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

// Client-Server : Done

public class CategoriesManagementController implements Initializable {

    private ArrayList<String> features = new ArrayList<>();

    @FXML
    TreeView<String> categoriesTable;
    @FXML
    TextField nameField;
    @FXML
    TextField parentField;
    @FXML
    TextField featureField;
    @FXML
    ListView<String> productsList;
    @FXML
    ListView<String> featuresList;
    @FXML
    Button createButton;
    @FXML
    Button editButton;
    @FXML
    Button deleteButton;
    @FXML
    Button deleteFeatureButton;
    @FXML
    Button addFeatureButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Category root = Database.getInstance().getCategoryByName("root");
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        deleteFeatureButton.setDisable(true);
        categoriesTable.setRoot(getTreeItemByCategory(root));
        categoriesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectCategory();
            } else {
                deselectCategory();
            }
        });
        featuresList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                addFeatureButton.setDisable(true);
                deleteFeatureButton.setDisable(false);
            } else {
                addFeatureButton.setDisable(false);
                deleteFeatureButton.setDisable(true);
            }
        });
    }

    private TreeItem<String> getTreeItemByCategory(Category current) {
        TreeItem<String> treeItem = new TreeItem<>(current.getName());
        for (Category category : current.getSubCategories())
            treeItem.getChildren().add(getTreeItemByCategory(category));
        return treeItem;
    }

    private void selectCategory() {
        createButton.setDisable(true);
        editButton.setDisable(false);
        deleteButton.setDisable(false);
        Category category = Database.getInstance().getCategoryByName(categoriesTable.getSelectionModel().getSelectedItem().getValue());
        nameField.setText(category.getName());
        parentField.setText(category.getParentName() == null ? "" : category.getParentName());
        productsList.getItems().clear();
        features.clear();
        features.addAll(category.getFeatures());
        featuresList.getItems().clear();
        for (Product product : category.getProducts())
            productsList.getItems().add(product.getName());
        for (String feature : category.getFeatures())
            featuresList.getItems().add(feature);
    }

    private void deselectCategory() {
        createButton.setDisable(false);
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        nameField.setText("");
        parentField.setText("");
        productsList.getItems().clear();
        featuresList.getItems().clear();
        features.clear();
    }

    @FXML
    private void addFeature(ActionEvent event) {
        String feature = featureField.getText();
        if (feature.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Enter valid feature").showAndWait();
            return;
        }
        for (String str : featuresList.getItems()) {
            if (str.equals(feature)) {
                new Alert(Alert.AlertType.ERROR, "Feature already added").showAndWait();
                return;
            }
        }
        featureField.setText("");
        features.add(feature);
        featuresList.getItems().add(feature);
    }

    @FXML
    private void deleteFeature(ActionEvent event) {
        String feature = featuresList.getSelectionModel().getSelectedItem();
        features.remove(feature);
        featuresList.getItems().clear();
        for (String str : features)
            featuresList.getItems().add(str);
    }

    @FXML
    private void createOrEditCategory(ActionEvent event) throws IOException {
        String name = nameField.getText().trim();
        String parent = parentField.getText().trim();
        String prevName = null;
        if (categoriesTable.getSelectionModel().getSelectedItem() != null)
            prevName = categoriesTable.getSelectionModel().getSelectedItem().getValue();
        features.clear();
        features.addAll(featuresList.getItems());

        String response = App.getResponseFromServer("createOrEditCategory", name, parent, prevName, App.objectToString(features));
        if (response.startsWith("Success")) {
            Category root = Database.getInstance().getCategoryByName("root");
            categoriesTable.setRoot(getTreeItemByCategory(root));
            deselectCategory();
            new Alert(Alert.AlertType.INFORMATION, "Category created | edited successfully").showAndWait();
        } else {
            new Alert(Alert.AlertType.ERROR, response).showAndWait();
        }
    }

    @FXML
    private void deleteCategory(ActionEvent event) throws IOException {
        for (TreeItem<String> item : categoriesTable.getSelectionModel().getSelectedItems()) {
            String response = App.getResponseFromServer("deleteCategory", item.getValue());
            if (response.startsWith("Success")) {
                new Alert(Alert.AlertType.INFORMATION, "Category deleted successfully").showAndWait();
            } else {
                new Alert(Alert.AlertType.ERROR, response).showAndWait();
            }
        }
        Category root = Database.getInstance().getCategoryByName("root");
        categoriesTable.setRoot(getTreeItemByCategory(root));
    }
}
