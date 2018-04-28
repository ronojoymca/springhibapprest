package mycom.orderapp.DAO;

import mycom.orderapp.model.Customer;

public interface CustomerRegistrationDao {

	Customer insertNewCustomer(Customer customer);

	boolean validateCustomer(String primaryEmail);

}
