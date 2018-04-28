package mycom.orderapp.serviceImpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mycom.orderapp.DAO.CustomerRegistrationDao;
import mycom.orderapp.DTO.CustomerRegistrationDTO;
import mycom.orderapp.DTO.CustomerStatusDto;
import mycom.orderapp.constants.CustomerRegistrationConstants;
import mycom.orderapp.controller.CustomerRegistrationController;
import mycom.orderapp.model.Customer;
import mycom.orderapp.model.CustomerAddress;
import mycom.orderapp.model.CustomerDeliveryAddress;
import mycom.orderapp.service.CustomerRegistrationService;



@Service
public class CustomerRegistrationServiceImpl implements CustomerRegistrationService{
	@Autowired
	CustomerRegistrationDao customerRegistrationDao;

	@Override
	public JSONObject saveRegistrationDetails(CustomerRegistrationDTO customerRegistration) {
		JSONObject object = new JSONObject();
		try {
			CustomerStatusDto status = new CustomerStatusDto();
			boolean emailval = customerRegistrationDao.validateCustomer(customerRegistration.getPrimaryEmail());
			if(true==emailval) {
			Customer customer=new Customer();
			CustomerAddress address=new CustomerAddress();
			CustomerDeliveryAddress deladdress=new CustomerDeliveryAddress();
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Date dt=new Date();
			String reportDate = df.format(dt);
			address.setLine1(customerRegistration.getLine1());
			address.setLine2(customerRegistration.getLine2());
			address.setLine3(customerRegistration.getLine3());
			address.setCity(customerRegistration.getCity());
			address.setCountry(customerRegistration.getCountry());
			address.setDistrict(customerRegistration.getDist());
			address.setPincode(customerRegistration.getPin());
			address.setState(customerRegistration.getState());
			address.setCreatedBy(CustomerRegistrationConstants.CREATED_BY);
			address.setCreatedDate(reportDate);
			deladdress.setLine1(customerRegistration.getLine1());
			deladdress.setLine2(customerRegistration.getLine2());
			deladdress.setLine3(customerRegistration.getLine3());
			deladdress.setCity(customerRegistration.getCity());
			deladdress.setCountry(customerRegistration.getCountry());
			deladdress.setDistrict(customerRegistration.getDist());
			deladdress.setPincode(customerRegistration.getPin());
			deladdress.setState(customerRegistration.getState());
			deladdress.setCreatedBy(CustomerRegistrationConstants.CREATED_BY);
			deladdress.setCreatedDate(reportDate);
			customer.setDeladdress(deladdress);
			customer.setAddress(address);
			customer.setName(customerRegistration.getName());
			customer.setEmail(customerRegistration.getPrimaryEmail());
			customer.setMobileNumber(customerRegistration.getMobile());
			customer.setPassword(customerRegistration.getPassword());
			customer.setSecEmailOne(customerRegistration.getSecondaryEmail1());
			customer.setSecEmailTwo(customerRegistration.getSecondaryEmail2());
			customer.setCustomerActiveStatus(CustomerRegistrationConstants.ACTIVE);
			customer.setCreatedBy(CustomerRegistrationConstants.CREATED_BY);
			customer.setCreatedDate(reportDate);
			customer.setRole(1);
			customer = customerRegistrationDao.insertNewCustomer(customer);
			if(customer.getCustomerId()!=0) {
				status.setStatusCode(CustomerRegistrationConstants.REGISTRATION_PROCESS_COMPLETED_STATUS_CODE);
				status.setStatusMessage(CustomerRegistrationConstants.REGISTRATION_PROCESS_COMPLETED_STATUS_MESSAGE);
				object.put("Status", status);
			}else {
				status.setStatusCode(CustomerRegistrationConstants.REGISTRATION_PROCESS_INCOMPLETE_STATUS_CODE);
				status.setStatusMessage(CustomerRegistrationConstants.REGISTRATION_PROCESS_INCOMPLETE_STATUS_MESSAGE);
				object.put("Status", status);
			}
			}else {
				status.setStatusCode(CustomerRegistrationConstants.REGISTRATION_PROCESS_INCOMPLETE_STATUS_CODE);
				status.setStatusMessage(CustomerRegistrationConstants.MOBILE_NUMBER_EXISTS);
				object.put("Status", status);
			}
			
		}catch(Exception e) {
			object.put("Error", e);
		}
		
		return object;
	}

}
