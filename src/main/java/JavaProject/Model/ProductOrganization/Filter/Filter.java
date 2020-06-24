package JavaProject.Model.ProductOrganization.Filter;

import JavaProject.Model.Database.Database;
import JavaProject.Model.ProductOrganization.Category;
import JavaProject.Model.ProductOrganization.Product;
import JavaProject.Model.ProductOrganization.Sort.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Filter {
    private ArrayList<Filterable> allFilters;
    private Category currentCategory;
    private Sort sort;

    public Filter() {
        this.allFilters = new ArrayList<>();
        this.currentCategory = Database.getInstance().getCategoryByName("root");
        this.sort = new Sort();
    }

    public ArrayList<String> getSpecsFilters() {
        return new ArrayList<>(Database.getInstance().getAllFeatures(currentCategory));
    }

    public void addSelectiveFilter(String name, String value) {
        SelectiveFilter prevFilter = (SelectiveFilter) getFilterByName(name);
        ArrayList<String> selectedValues = new ArrayList<>();
        if (prevFilter == null) {
            selectedValues.add(value);
            allFilters.add(new SelectiveFilter(name, selectedValues));
        } else {
            prevFilter.getSelectedValues().add(value);
        }
    }

    public void removeSelectiveFilter(String name, String value) {
        SelectiveFilter prevFilter = (SelectiveFilter) getFilterByName(name);
        prevFilter.getSelectedValues().remove(value);
        if (prevFilter.getSelectedValues().isEmpty())
            allFilters.remove(prevFilter);
    }

    public void addRangeFilter(String name, double minValue, double maxValue) {
        allFilters.add(new RangeFilter(name, minValue, maxValue));
    }

    public void removeRangeFilter(String name) {
        RangeFilter rangeFilter = (RangeFilter) getFilterByName(name);
        allFilters.remove(rangeFilter);
    }

    public ArrayList<Product> getFilteredProducts() {
        ArrayList<Product> products = Database.getInstance().getAllProductsRecursively(currentCategory);
        ArrayList<Product> filteredProducts = new ArrayList<>();
        for (Product product : products) {
            boolean canPassFilter = true;
            for (Filterable filter : allFilters) {
                if (!filter.canPassFilter(product)) {
                    canPassFilter = false;
                    break;
                }
            }
            if (canPassFilter)
                filteredProducts.add(product);
        }
        return sort.getSortedProducts(filteredProducts);
    }

    public Filterable getFilterByName(String name) {
        for (Filterable filter : allFilters)
            if (filter.getName().equals(name))
                return filter;
        return null;
    }

    public void setCurrentCategory(Category currentCategory) {
        this.currentCategory = currentCategory;
    }

    public boolean checkRangeFilters(String name){
        for (Filterable filter : allFilters) {
            if (filter.getName().equals(name))
                if (filter instanceof RangeFilter)
                    return true;
        }
        return false;
    }

    public ArrayList<Filterable> getAllFilters() {
        return allFilters;
    }

    public void setAllFilters(ArrayList<Filterable> allFilters) {
        this.allFilters = allFilters;
    }

    public Category getCurrentCategory() {
        return currentCategory;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }
}
