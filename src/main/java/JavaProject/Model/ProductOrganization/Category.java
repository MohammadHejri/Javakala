package JavaProject.Model.ProductOrganization;

import java.util.ArrayList;

public class Category {

    private String name;
    private String parentName;
    private ArrayList<Category> subCategories;
    private ArrayList<Product> products;
    private ArrayList<String> features;

    public Category(String name, String parentName, ArrayList<Category> subCategories, ArrayList<Product> products, ArrayList<String> features) {
        this.name = name;
        this.parentName = parentName;
        this.subCategories = subCategories;
        this.products = products;
        this.features = features;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public ArrayList<Category> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ArrayList<Category> subCategories) {
        this.subCategories = subCategories;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public ArrayList<String> getFeatures() {
        return features;
    }

    public void setFeatures(ArrayList<String> features) {
        this.features = features;
    }

    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                ", parent=" + parentName +
                ", subCategories=" + subCategories +
                ", products=" + products +
                ", features=" + features +
                '}';
    }
}
