package mycom.orderapp.service;

import java.sql.Time;

import org.json.simple.JSONObject;

import mycom.orderapp.DTO.CustomerLoginDTO;
import mycom.orderapp.DTO.CustomerRegistrationDTO;
import mycom.orderapp.model.Customer;
import mycom.orderapp.model.ItemsTO;
import mycom.orderapp.model.OrderMaster;


public interface CustomerHomeService {

	JSONObject login(CustomerLoginDTO customerLoginDto);

	JSONObject addItems(ItemsTO itemsTO);

	JSONObject fetchAllCustomers();

	JSONObject fetchCustomerItems(OrderMaster orderMaster);

	JSONObject placeOrder(OrderMaster orderMaster);

	JSONObject getOrderHistory(int customerId);

	JSONObject collateOrder();

	JSONObject fetchItemsForAdmin();

	JSONObject deactivateItem(ItemsTO itemsTO);

	JSONObject deactivateCustomer(Customer customerTO);

	JSONObject activateItem(ItemsTO itemsTO);

	JSONObject activateCustomer(Customer customerTO);

	JSONObject addAdmin(CustomerRegistrationDTO customerTO);

	JSONObject findAllAdmin();

	boolean updatePreviousOrderSetPreviousOrderNTBC(OrderMaster orderMaster);

	void updateRevStatusForAll();

	JSONObject cancelOrder(OrderMaster orderMaster);

	boolean doesItemExistInOrder(int itemId);

	boolean doesCustomerExistInOrder(int customerId);

	JSONObject getCustomerAddress(Customer customer);

	JSONObject editCustomerDetails(CustomerRegistrationDTO customerTO);

	


}
