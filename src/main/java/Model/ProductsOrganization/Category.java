package Model.ProductsOrganization;

import java.util.ArrayList;
import java.util.HashMap;

public class Category {
    private String name;
    private Category parent;
    private HashMap<String,Category> allSubCategories;


    public Category(String name, Category parent) {
        this.name = name;
        this.parent = parent;
    }

    public void addSubCategory(Category category){}
    public void addProduct (Product product){}

    @Override
    public String toString() {
        return "";
    }
}
