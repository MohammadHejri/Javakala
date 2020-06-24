package JavaProject.Model.ProductOrganization.Sort;

import JavaProject.Model.ProductOrganization.Product;

import java.util.Comparator;

public class SortByDate implements Comparator<Product> {
    public int compare(Product product1, Product product2) {
        return product2.getDate().compareTo(product1.getDate());
    }
}
