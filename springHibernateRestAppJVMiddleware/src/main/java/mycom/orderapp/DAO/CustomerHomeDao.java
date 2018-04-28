package mycom.orderapp.DAO;

import java.util.List;

import org.json.simple.JSONObject;

import mycom.orderapp.model.Customer;
import mycom.orderapp.model.CustomerAddress;
import mycom.orderapp.model.CustomerDeliveryAddress;
import mycom.orderapp.model.ItemsTO;
import mycom.orderapp.model.OrderDetails;
import mycom.orderapp.model.OrderHistoryTO;
import mycom.orderapp.model.OrderMaster;
import mycom.orderapp.model.OrderRevisionMappingTO;


public interface CustomerHomeDao {

	Customer verifyLogin(String password, String emailId);

	Customer updateDeviceId(Customer customer);

	ItemsTO addItems(ItemsTO itemsTO);

	List<Customer> fetchAllCustomers();

	List<OrderMaster> fetchCustomerItems(int customerId);

	List<ItemsTO> fetchOrderDetails(int customerId);

	List<ItemsTO> fetchItemsOriginal();

	ItemsTO fetchPrefItemsById(Integer ps);

	OrderMaster saveToOrder(OrderMaster orderMaster);

	OrderDetails saveToOrderDetails(OrderDetails orderDetails);

	List<String> getAdminMailId();

	String getCustomerPrimaryMailId(int customerId);

	List<OrderDetails> getOrderDetails(int orderId);

	ItemsTO getItemDetails(int itemId);

	OrderHistoryTO saveHistory(OrderHistoryTO orderHistoryTO);

	List<OrderHistoryTO> getHistory(int customerId);

	String getCustomerName(int customerId);

	List<OrderHistoryTO> collateAllOrders();

	String getSecondaryOneMailId(int customerId);

	String getSecondaryTwoMailId(int customerId);

	List<ItemsTO> fetchprefeItems(int CustomerId);

	List<ItemsTO> fetchrestItems(int customerId);

	List<ItemsTO> fetchadminitems();

	boolean updateItem(int itemId);

	boolean updateCustomer(int customerID);

	boolean updateacItem(int itemId);

	boolean updateacCustomer(int customerID);

	Customer addAdmin(Customer customerTO);

	List<Customer> findAllAdmins();

	List<OrderMaster> fetchOrderMaster(int customerId);

	boolean setUpdateRevStatus(int orderId);

	OrderRevisionMappingTO saveNewMap(OrderRevisionMappingTO orMT);

	int getNoOfRevisions(int oldOrderId);

	boolean updateEditStatus(int oldOrderId);

	int getSourceOrderId(int oldOrderId);

	boolean updateRevStatusForAll();

	boolean cancelOrder(int orderId);

	OrderMaster getOrderMasterForCancellation(int orderId);

	List<String> deviceIdListOfAdmins();

	List<OrderMaster> getOrderMasterList(int customerId);

	List<OrderDetails> getOrderDetailsList(int orderId);

	List<ItemsTO> getItemsList(int itemId);

	boolean validateItemExist(String itemName);

	ItemsTO getItemName(int itemId);

	boolean custExist(int customerId);

	boolean validateAddItems(String itemNameEnglish, String itemNameOdiya);

	List<OrderRevisionMappingTO> revisionMappingHistory(int orderId);

	List<OrderMaster> getOMList();

	Customer getCustomerAddress(int customerId);

	CustomerDeliveryAddress updateDeliveryAddress(CustomerDeliveryAddress custDelAddress);

	List<OrderMaster> fetchOrderMasterWithId(int orderId);

	Customer updateCustInfo(Customer customer);




	

}
