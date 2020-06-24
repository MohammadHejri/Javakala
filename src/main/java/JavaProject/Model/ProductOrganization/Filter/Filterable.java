package JavaProject.Model.ProductOrganization.Filter;


import JavaProject.Model.ProductOrganization.Product;

public interface Filterable {
    String toString();
    String getName();
    boolean canPassFilter(Product product);
}
