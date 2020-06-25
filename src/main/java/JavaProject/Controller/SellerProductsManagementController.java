package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.Seller;
import JavaProject.Model.Database.Database;
import JavaProject.Model.DualString;
import JavaProject.Model.ProductOrganization.Category;
import JavaProject.Model.ProductOrganization.Product;
import JavaProject.Model.Request.Request;
import JavaProject.Model.Request.Subject;
import JavaProject.Model.Status.Status;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class SellerProductsManagementController implements Initializable {

    Category selectedCategory;
    Product selectedProduct;
    HashMap<String, String> specs = new HashMap<>();
    String defaultProductImagePath = "src/main/resources/Images/product.png";
    Category root = Database.getInstance().getCategoryByName("root");

    @FXML
    TreeView<String> categoriesTree;
    @FXML
    TableView<DualString> specsTable;
    @FXML
    TableColumn<DualString, String> specsColumn;
    @FXML
    TableColumn<DualString, String> infoColumn;
    @FXML
    ListView<String> productsList;
    @FXML
    ListView<String> buyersList;
    @FXML
    TextField nameField;
    @FXML
    TextField brandField;
    @FXML
    TextField priceField;
    @FXML
    TextField remainingField;
    @FXML
    TextArea descriptionArea;
    @FXML
    ImageView imageView;
    @FXML
    TextField infoField;
    @FXML
    Button addButton;
    @FXML
    Button editButton;
    @FXML
    Button deleteButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File file = new File(defaultProductImagePath);
        imageView.setImage(new Image(file.toURI().toString()));

        addButton.setDisable(false);
        editButton.setDisable(true);
        deleteButton.setDisable(true);

        specsColumn.setCellValueFactory(new PropertyValueFactory<>("key"));
        infoColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        selectedCategory = root;
        categoriesTree.setRoot(getTreeItemByCategory(root));
        categoriesTree.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) selectCategory();
            else deselectCategory();
        });

        for (String ID : ((Seller) App.getSignedInAccount()).getProductsID()) {
            Product product = Database.getInstance().getProductByID(ID);
            if (product.getStatus().equals(Status.ACCEPTED))
                productsList.getItems().add(product.getName());
        }
        productsList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) selectProduct();
            else deselectProduct();
        });
    }

    private TreeItem<String> getTreeItemByCategory(Category current) {
        TreeItem<String> treeItem = new TreeItem<>(current.getName());
        for (Category category : current.getSubCategories())
            treeItem.getChildren().add(getTreeItemByCategory(category));
        return treeItem;
    }

    private void updateSpecsTable() {
        specsTable.getItems().clear();
        specs.clear();
        if (selectedCategory != null && selectedProduct != null) {
            for (String key : Database.getInstance().getAllFeatures(selectedCategory)) {
                String value = selectedProduct.getSpecs().getOrDefault(key, "");
                specsTable.getItems().add(new DualString(key, value));
                specs.put(key, value);
            }
        } else if (selectedCategory == null && selectedProduct != null) {
            for (String key : selectedProduct.getSpecs().keySet()) {
                String value = selectedProduct.getSpecs().get(key);
                specsTable.getItems().add(new DualString(key, value));
                specs.put(key, value);
            }
        } else if (selectedCategory != null && selectedProduct == null) {
            for (String key : Database.getInstance().getAllFeatures(selectedCategory)) {
                specsTable.getItems().add(new DualString(key, ""));
                specs.put(key, "");
            }
        }
    }

    @FXML
    public void changeInfo(ActionEvent event) {
        DualString dualString = specsTable.getSelectionModel().getSelectedItem();
        String info = infoField.getText().trim();
        if (dualString == null || info.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Choose spec and enter info").showAndWait();
        } else {
            specs.replace(dualString.getKey(), dualString.getValue(), info);
            specsTable.getItems().clear();
            infoField.setText("");
            for (String key : specs.keySet())
                specsTable.getItems().add(new DualString(key, specs.get(key)));
        }
    }

    private boolean isSpecsTableComplete() {
        for (String key : specs.keySet())
            if(specs.get(key).isBlank())
                return false;
        return true;
    }

    private void selectCategory() {
        String name = categoriesTree.getSelectionModel().getSelectedItem().getValue();
        selectedCategory = Database.getInstance().getCategoryByName(name);
        updateSpecsTable();
    }

    private void deselectCategory() {
        String name = selectedProduct == null ? "root" : selectedProduct.getParentCategoryName();
        selectedCategory = Database.getInstance().getCategoryByName(name);
        updateSpecsTable();
    }

    private void selectProduct() {
        String name = productsList.getSelectionModel().getSelectedItem();
        selectedProduct = Database.getInstance().getProductByName(name);
        selectedCategory = Database.getInstance().getCategoryByName(selectedProduct.getParentCategoryName());
        updateSpecsTable();
        addButton.setDisable(true);
        editButton.setDisable(false);
        deleteButton.setDisable(false);
        nameField.setText(selectedProduct.getName());
        brandField.setText(selectedProduct.getBrand());
        priceField.setText(String.valueOf(selectedProduct.getPrice()));
        remainingField.setText(String.valueOf(selectedProduct.getRemainingItems()));
        descriptionArea.setText(selectedProduct.getDescription());
        imageView.setImage(new Image(selectedProduct.getImagePath()));
        buyersList.getItems().clear();
        for (String buyer : selectedProduct.getBuyers())
            buyersList.getItems().add(buyer);
    }

    private void deselectProduct() {
        selectedProduct = null;
        deselectCategory();
        addButton.setDisable(false);
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        nameField.setText("");
        brandField.setText("");
        priceField.setText("");
        remainingField.setText("");
        descriptionArea.setText("");
        resetImageToDefault(null);
        buyersList.getItems().clear();
    }

    @FXML
    public void requestAddProduct(ActionEvent event) throws IOException {
        String name = nameField.getText().trim();
        String brand = brandField.getText().trim();
        String priceStr = priceField.getText().trim();
        String remainingStr = remainingField.getText().trim();
        String description = descriptionArea.getText().trim();
        String imagePath = imageView.getImage().getUrl();
        Product product = Database.getInstance().getProductByName(name);
        if (name.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Enter name").showAndWait();
        } else if (brand.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Enter brand").showAndWait();
        } else if (priceStr.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Enter price").showAndWait();
        } else if (remainingStr.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Enter quantity").showAndWait();
        } else if (description.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Enter description").showAndWait();
        } else if (!isSpecsTableComplete()) {
            new Alert(Alert.AlertType.ERROR, "Complete specs table").showAndWait();
        } else if (!priceStr.matches("(\\d+)(\\.\\d+)?")) {
            new Alert(Alert.AlertType.ERROR, "Use DOUBLE for price").showAndWait();
        } else if (!remainingStr.matches("(\\d+)")) {
            new Alert(Alert.AlertType.ERROR, "Use INTEGER for quantity").showAndWait();
        } else if (product != null) {
            new Alert(Alert.AlertType.ERROR, "Product name not available").showAndWait();
        } else {
            double price = Double.parseDouble(priceStr);
            int remainingItems = Integer.parseInt(remainingStr);
            Seller seller = (Seller) App.getSignedInAccount();
            product = new Product(name, brand, price, seller.getUsername(), remainingItems, selectedCategory.getName(), description, new HashMap<>(specs), imagePath);
            selectedCategory.getProducts().add(product);
            seller.getProductsID().add(product.getID());
            Database.getInstance().updateCategories();
            Database.getInstance().saveAccount(seller);
            Request request = new Request(Subject.ADD_PRODUCT, product.toString());
            request.setProduct(product);
            Database.getInstance().saveRequest(request);
            new Alert(Alert.AlertType.INFORMATION, "Adding product requested").showAndWait();
        }
    }

    @FXML
    public void requestEditProduct(ActionEvent event) throws IOException {
        String name = nameField.getText().trim();
        String brand = brandField.getText().trim();
        String priceStr = priceField.getText().trim();
        String remainingStr = remainingField.getText().trim();
        String description = descriptionArea.getText().trim();
        String imagePath = imageView.getImage().getUrl();
        if (name.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Enter name").showAndWait();
        } else if (brand.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Enter brand").showAndWait();
        } else if (priceStr.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Enter price").showAndWait();
        } else if (remainingStr.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Enter quantity").showAndWait();
        } else if (description.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Enter description").showAndWait();
        } else if (!isSpecsTableComplete()) {
            new Alert(Alert.AlertType.ERROR, "Complete specs table").showAndWait();
        } else if (!priceStr.matches("(\\d+)(\\.\\d+)?")) {
            new Alert(Alert.AlertType.ERROR, "Use DOUBLE for price").showAndWait();
        } else if (!remainingStr.matches("(\\d+)")) {
            new Alert(Alert.AlertType.ERROR, "Use INTEGER for quantity").showAndWait();
        } else if (!selectedProduct.getName().equals(name) && Database.getInstance().getProductByName(name) != null) {
            new Alert(Alert.AlertType.ERROR, "Product name not available").showAndWait();
        } else {
            double price = Double.parseDouble(priceStr);
            int remainingItems = Integer.parseInt(remainingStr);
            Seller seller = (Seller) App.getSignedInAccount();
            Product product = new Product(name, brand, price, seller.getUsername(), remainingItems, selectedCategory.getName(), description, new HashMap<>(specs), imagePath);
            product.setID(selectedProduct.getID());
            product.setViews(selectedProduct.getViews());
            product.setDate(selectedProduct.getDate());
            product.setAverageMark(selectedProduct.getAverageMark());
            product.setStatus(selectedProduct.getStatus());
            product.setRates(selectedProduct.getRates());
            product.setComments(selectedProduct.getComments());
            if (selectedProduct.getAuctionID() != null)
                product.setAuctionID(selectedProduct.getAuctionID());
            Request request = new Request(Subject.EDIT_PRODUCT, "Edited " + product.toString() +  "\n" + "Current" + selectedProduct.toString());
            request.setProduct(product);
            Database.getInstance().saveRequest(request);
            new Alert(Alert.AlertType.INFORMATION, "Editing product requested").showAndWait();
        }
    }

    @FXML
    public void requestDeleteProduct(ActionEvent event) throws IOException {
        Product product = Database.getInstance().getProductByName(productsList.getSelectionModel().getSelectedItem());
        Request request = new Request(Subject.DELETE_PRODUCT, product.toString());
        request.setProduct(product);
        Database.getInstance().saveRequest(request);
        new Alert(Alert.AlertType.INFORMATION, "Deleting product requested").showAndWait();
    }

    @FXML
    public void chooseImage(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null)
            imageView.setImage(new Image(file.toURI().toString()));
    }

    @FXML
    public void resetImageToDefault(ActionEvent event) {
        File file = new File(defaultProductImagePath);
        imageView.setImage(new Image(file.toURI().toString()));
    }


}
