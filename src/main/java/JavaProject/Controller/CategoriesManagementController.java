package JavaProject.Controller;

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
    Label statusLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Category root = Database.getInstance().getCategoryByName("root");
        categoriesTable.setRoot(getTreeItemByCategory(root));
        categoriesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectCategory();
            } else {
                deselectCategory();
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
        Category category = Database.getInstance().getCategoryByName(categoriesTable.getSelectionModel().getSelectedItem().getValue());
        nameField.setText(category.getName());
        parentField.setText(category.getParentName());
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
        nameField.setText("");
        parentField.setText("");
        productsList.getItems().clear();
        featuresList.getItems().clear();
        features.clear();
    }

    @FXML
    private void deleteProduct(ActionEvent event) throws IOException {
        String categoryName = categoriesTable.getSelectionModel().getSelectedItem().getValue();
        Category category = Database.getInstance().getCategoryByName(categoryName);
        String productName = productsList.getSelectionModel().getSelectedItem();
        if (productName == null) return;
        Product product = Database.getInstance().getProductByName(productName);
        category.getProducts().remove(product);
        product.setParentCategoryName(category.getParentName());
        Category parentCat = Database.getInstance().getCategoryByName(category.getParentName());
        parentCat.getProducts().add(product);
        Database.getInstance().updateCategories();
        productsList.getItems().clear();
        for (Product pro : category.getProducts())
            productsList.getItems().add(pro.getName());
    }

    @FXML
    private void addFeature(ActionEvent event) {
        String feature = featureField.getText();
        if (feature.isBlank()) {
            statusLabel.setText("enter feature in field");
            return;
        }
        for (String str : featuresList.getItems()) {
            if (str.equals(feature)) {
                statusLabel.setText("already added feature");
                return;
            }
        }
        features.add(feature);
        featuresList.getItems().add(feature);
    }

    @FXML
    private void deleteFeature(ActionEvent event) {
        String feature = featuresList.getSelectionModel().getSelectedItem();
        if (feature == null) {
            statusLabel.setText("first choose a feature");
            return;
        }
        features.remove(feature);
        featuresList.getItems().clear();
        for (String str : features)
            featuresList.getItems().add(str);
    }

    @FXML
    private void createOrEditCategory(ActionEvent event) throws IOException {
        String name = nameField.getText().trim();
        String parent = parentField.getText().trim();
        if (name.isBlank()) {
            statusLabel.setText("Enter name");
        } else if (parent.isBlank()) {
            statusLabel.setText("Enter parent name");
        } else if (Database.getInstance().getCategoryByName(parent) == null) {
            statusLabel.setText("No parent found");
        } else {
            String prevName = null;
            if (categoriesTable.getSelectionModel().getSelectedItem() != null)
                prevName = categoriesTable.getSelectionModel().getSelectedItem().getValue();
            Category category = Database.getInstance().getCategoryByName(name);
            Category parentCat = Database.getInstance().getCategoryByName(parent);
            features.clear();
            features.addAll(featuresList.getItems());
            if (prevName == null && category != null) {
                statusLabel.setText("Category name exists");
                return;
            } else if (prevName == null && category == null) {
                ArrayList<String> catFeatures = new ArrayList<>(features);
                category = new Category(name, parent, new ArrayList<>(), new ArrayList<>(), catFeatures);
                parentCat.getSubCategories().add(category);
            } else if (prevName != null && category != null) {
                if (prevName.equals(name)) {
                    category.setFeatures(new ArrayList<>(features));
                    Category prevParentCat = Database.getInstance().getCategoryByName(category.getParentName());
                    if (!category.getParentName().equals(parent)) {
                        prevParentCat.getSubCategories().remove(category);
                        parentCat.getSubCategories().add(category);
                        category.setParentName(parent);
                    }
                } else {
                    statusLabel.setText("Category name exists");
                    return;
                }
            } else {
                category = Database.getInstance().getCategoryByName(prevName);
                category.setName(name);
                category.setFeatures(new ArrayList<>(features));
                Category prevParentCat = Database.getInstance().getCategoryByName(category.getParentName());
                if (!category.getParentName().equals(parent)) {
                    prevParentCat.getSubCategories().remove(category);
                    parentCat.getSubCategories().add(category);
                    category.setParentName(parent);
                }
            }
            Category root = Database.getInstance().getCategoryByName("root");
            categoriesTable.setRoot(getTreeItemByCategory(root));
            deselectCategory();
            Database.getInstance().updateCategories();
        }
    }

    @FXML
    private void deleteCategory(ActionEvent event) throws IOException {
        for (TreeItem<String> item : categoriesTable.getSelectionModel().getSelectedItems()) {
            Category selected = Database.getInstance().getCategoryByName(item.getValue());
            if (!selected.getName().equals("root"))
                Database.getInstance().deleteCategory(selected);
        }
        statusLabel.setText("Deleted successfully");
        Category root = Database.getInstance().getCategoryByName("root");
        categoriesTable.setRoot(getTreeItemByCategory(root));
    }
}
