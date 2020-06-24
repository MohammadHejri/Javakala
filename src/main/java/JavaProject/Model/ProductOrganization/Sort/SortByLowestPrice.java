package JavaProject.Model.ProductOrganization.Sort;

import JavaProject.Model.ProductOrganization.Product;

import java.util.Comparator;

public class SortByLowestPrice implements Comparator<Product> {
    public int compare(Product product1, Product product2) {
        return Double.compare(product1.getPrice(), product2.getPrice());
    }
}
