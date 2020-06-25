package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Database.Database;
import JavaProject.Model.ProductOrganization.Category;
import JavaProject.Model.ProductOrganization.Filter.Filter;
import JavaProject.Model.ProductOrganization.Product;
import JavaProject.Model.Status.Status;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ProductsPageController implements Initializable {

    public static String prevFXML;
    public static Parent prevPane;
    private static Filter filter = new Filter();
    private VBox sellerFilterVBox = new VBox();
    private VBox brandFilterVBox = new VBox();
    private VBox availabilityFilterVBox = new VBox();
    private VBox promotionFilterVBox = new VBox();
    private HashMap<String, VBox> specsFilterVBoxes = new HashMap<>();
    private Category current = filter.getCurrentCategory();
    private String searchedName;
    private boolean filteredPrice;

    @FXML
    TreeView<String> categoriesTree;
    @FXML
    Accordion accordion;
    @FXML
    TextField productNameField;
    @FXML
    TitledPane categoryPane;
    @FXML
    TextField minPriceField;
    @FXML
    TextField maxPriceField;
    @FXML
    RadioButton mostViewedButton;
    @FXML
    RadioButton mostPopularButton;
    @FXML
    RadioButton highestPriceButton;
    @FXML
    RadioButton lowestPriceButton;
    @FXML
    RadioButton newestButton;
    @FXML
    Pagination pagination;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initFiltersSection();
        pagination.setMaxPageIndicatorCount(3);
        pagination.setPageFactory(this::createPage);

    }

    private ScrollPane createPage(int pageIndex) {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        gridPane.setPadding(new Insets(20,20,20,20));
        try {
            ArrayList<Product> products = getAcceptedProducts();
            pagination.setPageCount((int) Math.ceil((double) products.size() / 3));
            for (int i = pageIndex * 3; i < 3 + pageIndex * 3; i++) {
                if (i >= products.size()) break;
                Product product = products.get(i);
                FXMLLoader loader = new FXMLLoader(App.class.getResource("productView.fxml"));
                AnchorPane anchorPane = loader.load();
                anchorPane.setOnMouseClicked(e -> {
                    try {
                        FXMLLoader productLoader = new FXMLLoader(App.class.getResource("productPage.fxml"));
                        ScrollPane productPane = productLoader.load();
                        ProductPageController controller = productLoader.getController();
                        ProductPageController.prevPane = (AnchorPane) App.getProductsPage();
                        controller.initPage(product);
                        App.setRoot(productPane);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                });
                gridPane.add(anchorPane, i % 3, 0);
                ProductViewController controller = loader.getController();
                controller.initProduct(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ScrollPane(gridPane);
    }

    private void initFiltersSection() {
        // category section
        categoryPane.setText("Category (" + current.getName() + ")");
        categoriesTree.setRoot(getTreeItemByCategory(Database.getInstance().getCategoryByName("root")));
        categoriesTree.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) selectCategory();
        });

        // brand section
        accordion.getPanes().add(new TitledPane("Brand", brandFilterVBox));
        for (String value : Database.getInstance().getBrandsFromCategory(current)) {
            RadioButton radioButton = new RadioButton(value);
            radioButton.setOnAction(e -> {
                if (radioButton.isSelected())
                    addFilter("Brand", value);
                else removeFilter("Brand", value);
            });
            brandFilterVBox.getChildren().add(radioButton);
        }

        // seller section
        accordion.getPanes().add(new TitledPane("Seller", sellerFilterVBox));
        for (String value : Database.getInstance().getSellersFromCategory(current)) {
            RadioButton radioButton = new RadioButton(value);
            radioButton.setOnAction(e -> {
                if (radioButton.isSelected())
                    addFilter("Seller", value);
                else removeFilter("Seller", value);
            });
            sellerFilterVBox.getChildren().add(radioButton);
        }

        // availability section
        accordion.getPanes().add(new TitledPane("Availability", availabilityFilterVBox));
        RadioButton availabilityRadioButton = new RadioButton("Only available products");
        availabilityRadioButton.setOnAction(e -> {
            if (availabilityRadioButton.isSelected())
                addFilter("Availability", "Only available products");
            else removeFilter("Availability", "Only available products");
        });
        availabilityFilterVBox.getChildren().add(availabilityRadioButton);

        // promotion section
        accordion.getPanes().add(new TitledPane("Promotion", promotionFilterVBox));
        RadioButton promotionRadioButton = new RadioButton("Only products on sale");
        promotionRadioButton.setOnAction(e -> {
            if (promotionRadioButton.isSelected())
                addFilter("Auction", "Only products on sale");
            else removeFilter("Auction", "Only products on sale");
        });
        promotionFilterVBox.getChildren().add(promotionRadioButton);

        // specs section
        ArrayList<String> specsFilters = filter.getSpecsFilters();
        for (String string : specsFilters) {
            VBox vBox = new VBox();
            specsFilterVBoxes.put(string, vBox);
            accordion.getPanes().add(new TitledPane(string, vBox));
            for (String value : Database.getInstance().getSpecsInfoFromCategory(current, string)) {
                RadioButton radioButton = new RadioButton(value);
                radioButton.setOnAction(e -> {
                    if (radioButton.isSelected())
                        addFilter(string, value);
                    else removeFilter(string, value);
                });
                vBox.getChildren().add(radioButton);
            }
        }
    }

    private ArrayList<Product> getAcceptedProducts() {
        ArrayList<Product> products = new ArrayList<>();
        for (Product product : filter.getFilteredProducts())
            if (product.getStatus().equals(Status.ACCEPTED))
                products.add(product);
        return products;
    }

    private void addFilter(String filterName, String filterValue) {
        filter.addSelectiveFilter(filterName, filterValue);
        pagination.setPageFactory(this::createPage);
    }

    private void removeFilter(String filterName, String filterValue) {
        filter.removeSelectiveFilter(filterName, filterValue);
        pagination.setPageFactory(this::createPage);
    }

    private void selectCategory() {
        current = Database.getInstance().getCategoryByName(categoriesTree.getSelectionModel().getSelectedItem().getValue());
        filter = new Filter();
        filter.setCurrentCategory(current);
        App.initProductsPage();
        App.setRoot(App.getProductsPage());
    }

    @FXML
    private void filterByName() {
        if (!productNameField.getText().isBlank()) {
            if (searchedName != null)
                removeFilter("Name", searchedName);
            addFilter("Name", productNameField.getText());
            searchedName = productNameField.getText();
        }
    }

    @FXML
    private void resetName() {
        if (searchedName != null) {
            removeFilter("Name", searchedName);
            searchedName = null;
        }
        productNameField.setText("");
    }

    @FXML
    private void filterByPrice(ActionEvent event) {
        if (minPriceField.getText().matches("(\\d+)(\\.\\d+)?") && maxPriceField.getText().matches("(\\d+)(\\.\\d+)?")) {
            if (filteredPrice)
                filter.removeRangeFilter("Price");
            filter.addRangeFilter("Price", Double.parseDouble(minPriceField.getText()), Double.parseDouble(maxPriceField.getText()));
            pagination.setPageFactory(this::createPage);
            filteredPrice = true;
        }
    }

    @FXML
    private void resetPrice(ActionEvent event) {
        if (filteredPrice) {
            filter.removeRangeFilter("Price");
            pagination.setPageFactory(this::createPage);
            filteredPrice = false;
        }
        minPriceField.setText("");
        maxPriceField.setText("");
    }

    @FXML
    private void updateSort(ActionEvent event) {
        if (mostViewedButton.isSelected())
            filter.getSort().setSortType("MostViewed");
        if (mostPopularButton.isSelected())
            filter.getSort().setSortType("BestScore");
        if (highestPriceButton.isSelected())
            filter.getSort().setSortType("HighestPrice");
        if (lowestPriceButton.isSelected())
            filter.getSort().setSortType("LowestPrice");
        if (newestButton.isSelected())
            filter.getSort().setSortType("Newest");
        pagination.setPageFactory(this::createPage);
    }

    private TreeItem<String> getTreeItemByCategory(Category current) {
        TreeItem<String> treeItem = new TreeItem<>(current.getName());
        for (Category category : current.getSubCategories())
            treeItem.getChildren().add(getTreeItemByCategory(category));
        treeItem.setExpanded(true);
        return treeItem;
    }

    @FXML
    private void openCartSection(MouseEvent event) throws IOException {
        App.setRoot("cart");
        CartController.prevFXML = "productsPage";
    }

    @FXML
    private void changeToPrevPane(MouseEvent event) throws IOException {
        App.setRoot(prevPane);
    }

}
