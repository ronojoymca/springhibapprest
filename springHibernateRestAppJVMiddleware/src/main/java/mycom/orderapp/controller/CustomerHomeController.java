package mycom.orderapp.controller;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import mycom.orderapp.DTO.CustomerLoginDTO;
import mycom.orderapp.DTO.CustomerRegistrationDTO;
import mycom.orderapp.DTO.CustomerStatusDto;
import mycom.orderapp.model.Customer;
import mycom.orderapp.model.ItemsTO;
import mycom.orderapp.model.OrderMaster;
import mycom.orderapp.service.CustomerHomeService;
import mycom.orderapp.utilities.TimeDifferenceCalculator;



@RestController
public class CustomerHomeController {
	
	static final Logger logger = LogManager.getLogger(CustomerRegistrationController.class.getName());
	
	@Autowired
	MessageSource messageSource;
	
	@Autowired
	CustomerHomeService customerHomeService;

	@RequestMapping(value = "/signin", headers = "Accept=application/json", method = RequestMethod.POST)
	public @ResponseBody JSONObject getQuestions(@RequestBody CustomerLoginDTO customerLoginDto) {
		JSONObject obj = new JSONObject();
		try {
			CustomerStatusDto status = new CustomerStatusDto();
			logger.info("Signing in.....");
			obj = customerHomeService.login(customerLoginDto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	@RequestMapping(value = "/addItems", headers = "Accept=application/json", method = RequestMethod.POST)
	public @ResponseBody JSONObject addItems(@RequestBody ItemsTO itemsTO) {
		JSONObject obj = new JSONObject();
		try {
			CustomerStatusDto status = new CustomerStatusDto();
			logger.info("Adding vegetables.....");
			obj = customerHomeService.addItems(itemsTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	@RequestMapping(value = "/fetchAllCustomers", method = RequestMethod.GET)
	public @ResponseBody JSONObject fetchAllCustomers() {
		JSONObject obj = new JSONObject();
		try {
			CustomerStatusDto status = new CustomerStatusDto();
			logger.info("Fetching customer for Admin.....");
			obj = customerHomeService.fetchAllCustomers();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	@RequestMapping(value = "/fetchCustomerItems", headers = "Accept=application/json", method = RequestMethod.POST)
	public @ResponseBody JSONObject fetchCustomerItems(@RequestBody OrderMaster orderMaster) {
		JSONObject obj = new JSONObject();
		try {
			CustomerStatusDto status = new CustomerStatusDto();
			logger.info("Fetching customer items for.....");
			obj = customerHomeService.fetchCustomerItems(orderMaster);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	@RequestMapping(value = "/placeOrder", headers = "Accept=application/json", method = RequestMethod.POST)
	public @ResponseBody JSONObject placeOrder(@RequestBody OrderMaster orderMaster) {
		JSONObject obj = new JSONObject();
		try {
			TimeDifferenceCalculator tdC=new TimeDifferenceCalculator();
			/*CustomerStatusDto status = new CustomerStatusDto();
			String reqOrderTime=orderMaster.getOrderTime();
			String reqOrderDate=orderMaster.getOrderDate();
			String reqDelivType=orderMaster.getDeliveryType();
			String reqDelivTime=orderMaster.getDelivery_time();
			String reqDelivDate=orderMaster.getDelivery_time();*/
			if(!orderMaster.getItemsTO().isEmpty()) {
				if(orderMaster.getOrderId()!=0) {
					              logger.info("Updating previous order to NTBC. Request coming in from edit history.....");
							boolean updateRevStatus=customerHomeService.updatePreviousOrderSetPreviousOrderNTBC(orderMaster);
							if(updateRevStatus==true) {
								logger.info("Placing order from edit history.....");
							obj = customerHomeService.placeOrder(orderMaster);
							}
				}else {
					     logger.info("Placing order directly.....");
						obj = customerHomeService.placeOrder(orderMaster);
				}
			}else {
				logger.info("No order items found.....");
				obj.put("orderSaveStatus", "Please add at least one vegetable for order !");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	@RequestMapping(value = "/getOrderHistory", headers = "Accept=application/json", method = RequestMethod.POST)
	public @ResponseBody JSONObject getOrderHistory(@RequestBody OrderMaster orderMaster) {
		JSONObject obj = new JSONObject();
		try {
			CustomerStatusDto status = new CustomerStatusDto();
			obj = customerHomeService.getOrderHistory(orderMaster.getCustomerId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	@RequestMapping(value = "/collateOrder", method = RequestMethod.GET)
	public @ResponseBody JSONObject collateOrder() {
		JSONObject obj = new JSONObject();
		try {
			CustomerStatusDto status = new CustomerStatusDto();
			obj = customerHomeService.collateOrder();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	@RequestMapping(value = "/fetchItemsForAdmin",  method = RequestMethod.GET)
	public @ResponseBody JSONObject fetchItemsForAdmin() {
		JSONObject obj = new JSONObject();
		try {
			CustomerStatusDto status = new CustomerStatusDto();
			obj = customerHomeService.fetchItemsForAdmin();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	@RequestMapping(value = "/deactivateItem", headers = "Accept=application/json", method = RequestMethod.POST)
	public @ResponseBody JSONObject deactivateItem(@RequestBody ItemsTO itemsTO) {
		JSONObject obj = new JSONObject();
		try {
			CustomerStatusDto status = new CustomerStatusDto();
			/*boolean validateItemDeactivation=customerHomeService.doesItemExistInOrder(itemsTO.getItemId());
			if(validateItemDeactivation==true) {
				obj.put("status", "Vegetable can't be deactivated as Order Exists for Customer With the vegetable !!");
			}else {*/
			obj = customerHomeService.deactivateItem(itemsTO);
		/*	}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	@RequestMapping(value = "/deactivateCustomer", headers = "Accept=application/json", method = RequestMethod.POST)
	public @ResponseBody JSONObject deactivateCustomer(@RequestBody Customer customerTO) {
		JSONObject obj = new JSONObject();
		try {
			CustomerStatusDto status = new CustomerStatusDto();
			/*boolean validateCustomerDeactivation=customerHomeService.doesCustomerExistInOrder(customerTO.getCustomerId());
			if(validateCustomerDeactivation==true) {
				obj.put("status", "Customer can't be deactivated as Customer Exists for whom orders are to be delivered !!");
			}else {*/
			obj = customerHomeService.deactivateCustomer(customerTO);
			/*}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	@RequestMapping(value = "/activateItem", headers = "Accept=application/json", method = RequestMethod.POST)
	public @ResponseBody JSONObject activateItem(@RequestBody ItemsTO itemsTO) {
		JSONObject obj = new JSONObject();
		try {
			CustomerStatusDto status = new CustomerStatusDto();
			obj = customerHomeService.activateItem(itemsTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	@RequestMapping(value = "/activateCustomer", headers = "Accept=application/json", method = RequestMethod.POST)
	public @ResponseBody JSONObject activateCustomer(@RequestBody Customer customerTO) {
		JSONObject obj = new JSONObject();
		try {
			CustomerStatusDto status = new CustomerStatusDto();
			obj = customerHomeService.activateCustomer(customerTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	@RequestMapping(value = "/addAdmin", headers = "Accept=application/json", method = RequestMethod.POST)
	public @ResponseBody JSONObject addAdmin(@RequestBody CustomerRegistrationDTO customerTO) {
		JSONObject obj = new JSONObject();
		try {
			CustomerStatusDto status = new CustomerStatusDto();
			obj = customerHomeService.addAdmin(customerTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	
	@RequestMapping(value = "/findAllAdmin", method = RequestMethod.GET)
	public @ResponseBody JSONObject findAllAdmin() {
		JSONObject obj = new JSONObject();
		try {
			CustomerStatusDto status = new CustomerStatusDto();
			obj = customerHomeService.findAllAdmin();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	@RequestMapping(value = "/cancelOrder", headers = "Accept=application/json", method = RequestMethod.POST)
	public @ResponseBody JSONObject cancelOrder(@RequestBody OrderMaster orderMaster) {
		JSONObject obj = new JSONObject();
		try {
			CustomerStatusDto status = new CustomerStatusDto();
			boolean updateRevStatus=customerHomeService.updatePreviousOrderSetPreviousOrderNTBC(orderMaster);
			obj = customerHomeService.cancelOrder(orderMaster);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	
	@RequestMapping(value = "/validateOrderPossiblitiy", headers = "Accept=application/json", method = RequestMethod.POST)
	public @ResponseBody JSONObject validateOrderPossiblitiy(@RequestBody OrderMaster orderMaster) {
		JSONObject obj = new JSONObject();
		try {
			CustomerStatusDto status = new CustomerStatusDto();
			String reqOrderTime=orderMaster.getOrderTime();
			String reqOrderDate=orderMaster.getOrderDate();
			String reqDelivType=orderMaster.getDeliveryType();
			String reqDelivTime=orderMaster.getDelivery_time();
			String reqDelivDate=orderMaster.getDeliveryDate();
			TimeDifferenceCalculator tdC=new TimeDifferenceCalculator();
			obj=tdC.validateOrderPoss(reqOrderDate,reqOrderTime,reqDelivType,reqDelivTime,reqDelivDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	
	@RequestMapping(value = "/getCustomerAddress", headers = "Accept=application/json", method = RequestMethod.POST)
	public @ResponseBody JSONObject getCustomerAddress(@RequestBody Customer customer) {
		JSONObject obj = new JSONObject();
		try {
			obj=customerHomeService.getCustomerAddress(customer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	@RequestMapping(value = "/editCustomerDetails", headers = "Accept=application/json", method = RequestMethod.POST)
	public @ResponseBody JSONObject editCustomerDetails(@RequestBody CustomerRegistrationDTO customerTO) {
		JSONObject obj = new JSONObject();
		try {
			obj=customerHomeService.editCustomerDetails(customerTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	

}
