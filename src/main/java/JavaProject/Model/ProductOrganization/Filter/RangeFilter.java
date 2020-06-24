package JavaProject.Model.ProductOrganization.Filter;

import JavaProject.Model.ProductOrganization.Product;

public class RangeFilter implements Filterable {
    private String name;
    private double minValue;
    private double maxValue;

    public RangeFilter(String name, double minValue, double maxValue) {
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public boolean canPassFilter(Product product) {
        if ("Price".equals(name)) {
            double price = product.getPrice();
            return (price >= minValue) && (price <= maxValue);
        }
        if (product.getSpecs().containsKey(name)) {
            String value = product.getSpecs().get(name);
            try {
                double doubleValue = Double.parseDouble(value);
                return (doubleValue >= minValue) && (doubleValue <= maxValue);
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + ": " + minValue + " to " + maxValue;
    }
}
