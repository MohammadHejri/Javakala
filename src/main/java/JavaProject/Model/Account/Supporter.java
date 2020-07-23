package JavaProject.Model.Account;

public class Supporter extends Account {
    public Supporter(String username, String password, String firstName, String lastName, String emailAddress, String phoneNumber, String imagePath) {
        super(username, password, firstName, lastName, emailAddress, phoneNumber, imagePath);
    }

    @Override
    public String toString() {
        return "Supporter{} " + super.toString();
    }
}
