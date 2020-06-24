package JavaProject.Model.Account;

public class Manager extends Account {

    public Manager(String username, String password, String firstName, String lastName, String emailAddress, String phoneNumber, String imagePath) {
        super(username, password, firstName, lastName, emailAddress, phoneNumber, imagePath);
    }

    @Override
    public String toString() {
        return "Manager{} " + super.toString();
    }

}
