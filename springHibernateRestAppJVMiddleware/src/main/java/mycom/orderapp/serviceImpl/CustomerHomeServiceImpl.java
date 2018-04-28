package mycom.orderapp.serviceImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mycom.orderapp.DAO.CustomerHomeDao;
import mycom.orderapp.DAO.CustomerRegistrationDao;
import mycom.orderapp.DTO.CustomerLoginDTO;
import mycom.orderapp.DTO.CustomerRegistrationDTO;
import mycom.orderapp.DTO.CustomerStatusDto;
import mycom.orderapp.DTO.ModelForExcelDTO;
import mycom.orderapp.constants.CustomerRegistrationConstants;
import mycom.orderapp.controller.CustomerRegistrationController;
import mycom.orderapp.model.Customer;
import mycom.orderapp.model.CustomerAddress;
import mycom.orderapp.model.CustomerDeliveryAddress;
import mycom.orderapp.model.ItemsTO;
import mycom.orderapp.model.OrderDetails;
import mycom.orderapp.model.OrderHistoryTO;
import mycom.orderapp.model.OrderMaster;
import mycom.orderapp.model.OrderRevisionMappingTO;
import mycom.orderapp.service.CustomerHomeService;
import mycom.orderapp.utilities.CancelOrderMail;
import mycom.orderapp.utilities.CollateOrderMail;
import mycom.orderapp.utilities.FCM;
import mycom.orderapp.utilities.PlaceOrderMail;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



@Service
public class CustomerHomeServiceImpl implements CustomerHomeService{
	
	@Autowired
	MessageSource messageSource;
	
	@Autowired
	CustomerHomeDao customerHomeDao;
	
	@Autowired
	CustomerRegistrationDao customerRegistrationDao;
	
	private Locale locale;

	@Override
	public JSONObject login(CustomerLoginDTO customerLoginDto) {
		JSONObject object = new JSONObject();
		try {
			CustomerStatusDto status = new CustomerStatusDto();
			String password=customerLoginDto.getPassword();
			String emailId=customerLoginDto.getEmailId();
			Customer customer=customerHomeDao.verifyLogin(password,emailId);
			if(null!=customer) {
				customer.setDeviceId(customerLoginDto.getDeviceId());
				customer=customerHomeDao.updateDeviceId(customer);
				if(null!=customer) {
				status.setStatusCode(CustomerRegistrationConstants.CUSTOMER_LOGIN_VERIFIED);
				status.setStatusMessage(CustomerRegistrationConstants.CUSTOMER_EXISTS_PROCEED_TO_HOMEPAGE);
				object.put("CustomerRegistrationDetails", customer);
				object.put("Status", status);
				}else {
					status.setStatusCode("device update failed");
					status.setStatusMessage("Login failed due to device update failure");
					object.put("Status", status);
				}
			}else {
				status.setStatusCode(CustomerRegistrationConstants.CUSTOMER_NOT_FOUND);
				status.setStatusMessage(CustomerRegistrationConstants.CUSTOMER_SIGNUP_REQUIRED);
				object.put("Status", status);
			}
		}catch(Exception e) {
			object.put("Exception",e.getMessage());
		}
		
		return object;
	}

	@Override
	public JSONObject addItems(ItemsTO itemsTO) {
		JSONObject object = new JSONObject();
		try {
			boolean validateAddItems=customerHomeDao.validateAddItems(itemsTO.getItemNameEnglish(),itemsTO.getItemNameOdiya());
			if(validateAddItems==true) {
			itemsTO.setItemStatus(1);
			itemsTO=customerHomeDao.addItems(itemsTO);
			if(itemsTO!=null) {
				object.put("status", "item saved successfully");
			}
			}else {
				object.put("status", "item already exists");
			}
		}catch(Exception e){
			object.put("Exception", e);
		}
		return object;
	}

	@Override
	public JSONObject fetchAllCustomers() {
		JSONObject object = new JSONObject();
		try {
			List<Customer> customer=customerHomeDao.fetchAllCustomers();
			if(null!=customer) {
				object.put("customerList", customer);
			}
		
		}catch(Exception e){
		object.put("Exception", e);
		}
		
		return object;
	}

	@Override
	public JSONObject fetchCustomerItems(OrderMaster orderMaster) {
		JSONObject object = new JSONObject();
		try {
			List<Map<String, Object>> allItems = new ArrayList<Map<String, Object>>();
			Map<String, Object> appmap = new HashMap<String, Object>();
			Map<String, Object> itemsMap = new HashMap<String, Object>();
			List<ItemsTO> prefitemsTo=customerHomeDao.fetchprefeItems(orderMaster.getCustomerId());
			for(ItemsTO prefit:prefitemsTo) {
				prefit.setItemsFetchType("preferred");
			}
			
			
			List<ItemsTO> restitemsTo=customerHomeDao.fetchrestItems(orderMaster.getCustomerId());
			for(ItemsTO restit:restitemsTo) {
				restit.setItemsFetchType("rest");
			}
			prefitemsTo.addAll(restitemsTo);
			
			/*itemsMap.put("preferredItems", prefitemsTo);
			itemsMap.put("otherItems", restitemsTo);*/
			
			
			
			
			allItems.add(itemsMap);
			object.put("allItems",prefitemsTo);
		} catch (Exception e) {
			object.put("Exception", e);
		}
		return object;
	}

	@Override
	public JSONObject placeOrder(OrderMaster orderMaster) {
		JSONObject object = new JSONObject();
		try {
			List<String> adcon=new ArrayList<String>();
			String dline1="";
			String dline2="";
			String dline3="";
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Date dat_=new Date();
			String reportDate = df.format(dat_);
			CustomerDeliveryAddress deladdress=new CustomerDeliveryAddress();
			String serverId="androidserverid";
			String message="New order for XYZ !";
			String delivDate=orderMaster.getDeliveryDate();
			String delivTime=orderMaster.getDelivery_time();
			String delivTyp=orderMaster.getDeliveryType();
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			int noOfRevisions=0;
			int oldOrderId=0;
			int newOrderId=0;
			if(orderMaster.getOrderId()!=0) {
			 oldOrderId=orderMaster.getOrderId();
			 object.put("editOrNewOrderStatus", "edit");
			}
			List<Map<String, Object>> datalistOrder = new ArrayList<Map<String, Object>>();
			OrderDetails orderDetails=new OrderDetails();
			java.util.Date dt = new java.util.Date();
			String customerName=customerHomeDao.getCustomerName(orderMaster.getCustomerId());
			orderMaster.setCustomerId(orderMaster.getCustomerId());
			orderMaster.setOrderBy("System");
			orderMaster.setRevStatus("TBC");
			if(orderMaster.getDeliveryType().equalsIgnoreCase("SAMEDAY")) {
				orderMaster.setOrderEditStatus(2);
			}else {
			orderMaster.setOrderEditStatus(0);
			}
			orderMaster.setOrderId(0);
			deladdress.setLine1(orderMaster.getDeladdress().getLine1());
			deladdress.setLine2(orderMaster.getDeladdress().getLine2());
			deladdress.setLine3(orderMaster.getDeladdress().getLine3());
			deladdress.setCity(orderMaster.getDeladdress().getCity());
			deladdress.setCountry(orderMaster.getDeladdress().getCountry());
			deladdress.setDistrict(orderMaster.getDeladdress().getDistrict());
			deladdress.setPincode(orderMaster.getDeladdress().getPincode());
			deladdress.setState(orderMaster.getDeladdress().getState());
			deladdress.setCreatedBy(CustomerRegistrationConstants.CREATED_BY);
			deladdress.setCreatedDate(reportDate);
			deladdress.setAddressId(orderMaster.getDeladdress().getAddressId());
			deladdress.setUpdatedBy(CustomerRegistrationConstants.CREATED_BY);
			deladdress.setUpdatedDate(reportDate);
			deladdress=customerHomeDao.updateDeliveryAddress(deladdress);
			orderMaster.setDeladdress(deladdress);
			orderMaster = customerHomeDao.saveToOrder(orderMaster);
			newOrderId= orderMaster.getOrderId();
			if(orderMaster!=null) {
				for(ItemsTO items:orderMaster.getItemsTO()) {
					orderDetails.setOrder_id(orderMaster.getOrderId());
					orderDetails.setItemId(items.getItemId());
					orderDetails.setOrderBy("System");
					orderDetails.setOrderDate(orderMaster.getOrderDate());
					orderDetails.setOrderTime(orderMaster.getOrderTime());
					orderDetails.setCustomerId(orderMaster.getCustomerId());
					orderDetails.setQuantity(items.getQuantity());
					orderDetails.setUom(items.getUom());
					orderDetails=customerHomeDao.saveToOrderDetails(orderDetails);
					if(orderDetails!=null) {
						object.put("orderSaveStatus", "Your order is saved !!");
					}
				}
			}
			List<OrderDetails> odList=customerHomeDao.getOrderDetails(newOrderId);
			if(!odList.isEmpty()) {
				for(OrderDetails od:odList) {
					OrderHistoryTO orderHistoryTO=new OrderHistoryTO();
					Map<String, Object> orderMap = new HashMap<String, Object>();
					orderMap.put("orderId", orderMaster.getOrderId());
					
					//String orderDate = sdf.format(orderMaster.getOrderDate());
					orderMap.put("orderDate", orderMaster.getOrderDate());
					orderMap.put("orderDetailsId", od.getOrderDetailsId());
					ItemsTO itemsTO=customerHomeDao.getItemDetails(od.getItemId());
					orderMap.put("vegetableNameEnglish", itemsTO.getItemNameEnglish());
					orderMap.put("vegetableNameOdiya", itemsTO.getItemNameOdiya());
					orderMap.put("quantiyOrdered", od.getQuantity());
					orderMap.put("unitsOfMeasurement", od.getUom());
					orderMap.put("customerId", od.getCustomerId());
					orderMap.put("deliveryDate", orderMaster.getDeliveryDate());
					orderMap.put("deliveryTime", orderMaster.getDelivery_time());
					orderMap.put("freeText", orderMaster.getFreeText());
					orderMap.put("deliveryType", orderMaster.getDeliveryType());
					orderMap.put("orderEditStatus", orderMaster.getOrderEditStatus());
					orderMap.put("orderTime", orderMaster.getOrderTime());
					/*orderMap.put("line1", value);
					orderMap.put("line2", value);
					orderMap.put("line3", value);
					orderMap.put("city", value);
					orderMap.put("state", value);
					orderMap.put("country", value);
					orderMap.put("pin", value);
					orderMap.put("dist", value);*/
					orderHistoryTO.setOrderDate(orderMaster.getOrderDate());
					orderHistoryTO.setOrderTime(orderMaster.getOrderTime());
					orderHistoryTO.setCustomerId(od.getCustomerId());
					orderHistoryTO.setOrderDetailsID(od.getOrderDetailsId());
					orderHistoryTO.setOrderId(orderMaster.getOrderId());
					orderHistoryTO.setQuantity(od.getQuantity());
					orderHistoryTO.setUom(od.getUom());
					orderHistoryTO.setVegNameEng(itemsTO.getItemNameEnglish());
					orderHistoryTO.setVegNameOdi(itemsTO.getItemNameOdiya());
					Date delDate=new SimpleDateFormat("dd/MM/yyyy").parse(orderMaster.getDeliveryDate()); 
					orderHistoryTO.setDeliveryDate(orderMaster.getDeliveryDate());
					DateFormat formatter = new SimpleDateFormat("HH:mm");
					java.sql.Time timeValue = new java.sql.Time(formatter.parse(orderMaster.getDelivery_time()).getTime());
					orderHistoryTO.setDeliveryTime(timeValue);
					orderHistoryTO.setDeliveryType(orderMaster.getDeliveryType());
					orderHistoryTO.setOrderEditStatus(orderMaster.getOrderEditStatus());
					orderHistoryTO.setFreeText(orderMaster.getFreeText());
					orderHistoryTO.setRevStatus(orderMaster.getRevStatus());
					orderHistoryTO=customerHomeDao.saveHistory(orderHistoryTO);
					datalistOrder.add(orderMap);
				}
			}
			
			//Create old new mapping start
			
			if(oldOrderId!=0) {
				
				int sourceOrderId=customerHomeDao.getSourceOrderId(oldOrderId);
				if(sourceOrderId==0) {
					sourceOrderId=oldOrderId;
				}
				Date dat=new Date();
				OrderRevisionMappingTO orMT=new OrderRevisionMappingTO();
				orMT.setOrderIdOld(sourceOrderId);
				orMT.setOrderIdNew(newOrderId);
				orMT.setRevisedBy(String.valueOf(orderMaster.getCustomerId()));
				orMT.setRevisionDateandTime(dat);
				orMT=customerHomeDao.saveNewMap(orMT);
				noOfRevisions=customerHomeDao.getNoOfRevisions(sourceOrderId);
				if(noOfRevisions>=1) {
					boolean updateEditStatus=customerHomeDao.updateEditStatus(newOrderId);
				}
			}
			
			//Create old new mapping end
			
			
			
			/*ModelForExcelDTO model=new ModelForExcelDTO();
			model.setOrderList(datalistOrder);*/
			XSSFWorkbook workbook = new XSSFWorkbook(); 
	        XSSFSheet sheet = workbook.createSheet("Vegetable_Order");
	        
	        // Set Custom Header Font - headerFont
	        XSSFFont headerFont = workbook.createFont();
	        headerFont.setBold(true);
	        headerFont.setFontHeightInPoints((short) 14);
	        // Set Custom Header Font Style1 - headerStyle
	        CellStyle headerStyle = workbook.createCellStyle();
	        headerStyle.setFont(headerFont);
	        // Set Custom Header Font Style1 - nonHeader
	        CellStyle nonHeader = workbook.createCellStyle();
	        nonHeader.setWrapText(true);
	        
	        
	        
	        //Create Row Start
	        XSSFRow row0 = sheet.createRow(0);
	        //Create Row End
	        
	        //Create Cell
	        XSSFCell r1c1 = row0.createCell(0);
	        r1c1.setCellValue(customerName+"'s Order");
	        sheet.autoSizeColumn(0);
	        r1c1.setCellStyle(headerStyle);
	        //Create Cell
	        XSSFCell r1c3 = row0.createCell(2);
	        r1c3.setCellValue("Order Comments");
	        sheet.autoSizeColumn(2);
	        r1c3.setCellStyle(headerStyle);
	        //Create Cell
	        XSSFCell r1c4 = row0.createCell(3);
	        if(orderMaster.getFreeText()!=null) {
	        r1c4.setCellValue(orderMaster.getFreeText());
	        }else {
	        	r1c4.setCellValue("No Extra comments for this order");
	        }
	        sheet.autoSizeColumn(3);
	        r1c4.setCellStyle(nonHeader);
	      //Create Cell
	        XSSFCell r1c6 = row0.createCell(4);
	        r1c6.setCellValue("Order Date and Time");
	        sheet.autoSizeColumn(4);
	        r1c6.setCellStyle(headerStyle);
	        //Create Cell
	        XSSFCell r1c7 = row0.createCell(5);
	        r1c7.setCellValue(orderMaster.getOrderDate()+" "+orderMaster.getOrderTime()+" Hrs.");
	        sheet.autoSizeColumn(5);
	        r1c7.setCellStyle(nonHeader);
	        //Create Cell
	        XSSFCell r1c9 = row0.createCell(6);
	        r1c9.setCellValue("Delivery Date and Time");
	        sheet.autoSizeColumn(6);
	        r1c9.setCellStyle(headerStyle);
	        //Create Cell
	        XSSFCell r1c10 = row0.createCell(7);
	        if(delivTyp.equalsIgnoreCase("SAMEDAY")) {
	        	r1c10.setCellValue("Today "+delivTime+" Hrs.");
	        }else {
	        r1c10.setCellValue(delivDate+" "+delivTime+" Hrs.");
	        }
	        sheet.autoSizeColumn(7);
	        r1c10.setCellStyle(nonHeader);
	        
	        //Create Cell
	        XSSFCell r1c11 = row0.createCell(8);
	        r1c11.setCellValue("Order Code");
	        sheet.autoSizeColumn(8);
	        r1c11.setCellStyle(headerStyle);
	        //Create Cell
	        XSSFCell r1c12 = row0.createCell(9);
	        r1c12.setCellValue(orderMaster.getOrderId());
	        sheet.autoSizeColumn(9);
	        r1c12.setCellStyle(nonHeader);
	        
	        
	        //Create Cell
	        XSSFCell r1c13 = row0.createCell(10);
	        r1c13.setCellValue("Delivery Address");
	        sheet.autoSizeColumn(10);
	        r1c13.setCellStyle(headerStyle);
	        //Create Cell
	        XSSFCell r1c14 = row0.createCell(11);
	        if(!orderMaster.getDeladdress().getLine1().equals("")){
	        dline1=orderMaster.getDeladdress().getLine1();
	        adcon.add(dline1);
	        }
	        if(!orderMaster.getDeladdress().getLine2().equals("")) {
	        dline2=orderMaster.getDeladdress().getLine2();
	        adcon.add(dline2);
	        }
	        if(!orderMaster.getDeladdress().getLine2().equals("")) {
	        dline3=orderMaster.getDeladdress().getLine3();
	        adcon.add(dline3);
	        }
	        String daddress1=String.join(", ", adcon);
	        String daddress2=", "+orderMaster.getDeladdress().getCity()+", "+orderMaster.getDeladdress().getDistrict()+", "+orderMaster.getDeladdress().getState()+", "+
	        		orderMaster.getDeladdress().getPincode();
	        String finalvalueAddress=daddress1+daddress2;
	        r1c14.setCellValue(finalvalueAddress);
	        sheet.autoSizeColumn(11);
	        r1c14.setCellStyle(nonHeader);
	        
	        
	        
	        //Create Row Start
	        XSSFRow row1 = sheet.createRow(2);
	        //Create Row Start
	        
	        //Create Cell
			r1c1 = row1.createCell(0);
			r1c1.setCellValue("Sl.No.");
			sheet.autoSizeColumn(0);
			r1c1.setCellStyle(headerStyle);
			//Create Cell
			XSSFCell r1c2 = row1.createCell(1);
			r1c2.setCellValue("Vegetable Name English");
			sheet.setColumnWidth(1, 5000);
			r1c2.setCellStyle(headerStyle);
			//Create Cell
			r1c3 = row1.createCell(2);
			r1c3.setCellValue("Vegetable Name Oriya");
			sheet.autoSizeColumn(2);
			r1c3.setCellStyle(headerStyle);
			//Create Cell
			r1c4 = row1.createCell(3);
			r1c4.setCellValue("Quantity");
			sheet.autoSizeColumn(3);
			r1c4.setCellStyle(headerStyle);
			//Create Cell
			XSSFCell r1c5 = row1.createCell(4);
			r1c5.setCellValue("UOM");
			sheet.autoSizeColumn(4);
			r1c5.setCellStyle(headerStyle);
			/*//Create Cell
			XSSFCell r1c6 = row1.createCell(5);
			r1c6.setCellValue("UOM");
			sheet.autoSizeColumn(5);
			r1c6.setCellStyle(headerStyle);*/
			/*//Create Cell
			XSSFCell r1c7 = row1.createCell(6);
			r1c7.setCellValue("Delivery Date");
			sheet.autoSizeColumn(6);
			r1c7.setCellStyle(headerStyle);*/
			
			//Create Data List--Start
			int rownum = 3;
	        for(Map<String, Object> dlo:datalistOrder) {
	        	Row row = sheet.createRow(rownum++);
	        	createList(dlo, row, rownum);
	        }
	        //Create Data List--End
	        
	        
	        //Mail-Start
	        FileOutputStream out = new FileOutputStream(new File(messageSource.getMessage("awso", null, locale)+"_"+customerName+".xlsx")); // file name with path
            workbook.write(out);
            out.close();
			List<String> to = customerHomeDao.getAdminMailId();
			String cc1=customerHomeDao.getCustomerPrimaryMailId(orderMaster.getCustomerId());
			String cc2=customerHomeDao.getSecondaryOneMailId(orderMaster.getCustomerId());
			String cc3=customerHomeDao.getSecondaryTwoMailId(orderMaster.getCustomerId());
			Thread thread = new Thread();
			PlaceOrderMail maillist = new PlaceOrderMail();
			maillist.setParameters(to,cc1,cc2,cc3,noOfRevisions,customerName,delivTyp,delivDate,delivTime);
			thread = new Thread(maillist);
			thread.start();
			//Mail-End
			List<String> deviceIdListOfAdmins=customerHomeDao.deviceIdListOfAdmins();
			if(!deviceIdListOfAdmins.isEmpty()) {
				for(String devices:deviceIdListOfAdmins) {
					FCM.send_FCM_Notification(devices,serverId,message);
				}
			}
			
			
			object.put("list", datalistOrder);
			object.put("editOrNewOrderStatus", "new");
		}catch(Exception e) {
			object.put("Exception", e);
		}
		return object;
		
	}

	@Override
	public JSONObject getOrderHistory(int customerId) {
		JSONObject object = new JSONObject();
		try {
			List<Map<String, Object>> datalistOrder = new ArrayList<Map<String, Object>>();
			OrderMaster orderMaster=new OrderMaster();
			List<OrderMaster> orderMasterList=customerHomeDao.getOrderMasterList(customerId);
		    if(!orderMasterList.isEmpty()) {
		    	for(OrderMaster om:orderMasterList) {
		    		Map<String, Object> orderMasterMap = new HashMap<String, Object>();
		    		List<OrderDetails> orderDetailsList=customerHomeDao.getOrderDetailsList(om.getOrderId());
		    		List<Map<String, Object>> datalistOrderDetails = new ArrayList<Map<String, Object>>();
		    		if(!orderDetailsList.isEmpty()) {
		    			for(OrderDetails od:orderDetailsList) {
		    				Map<String, Object> orderDetailsMap = new HashMap<String, Object>();
		    				List<Map<String, Object>> datalistItemDetails = new ArrayList<Map<String, Object>>();
		    				List<ItemsTO> itemDetailsList=customerHomeDao.getItemsList(od.getItemId());
		    				if(!itemDetailsList.isEmpty()) {
		    					for(ItemsTO item:itemDetailsList) {
		    						Map<String, Object> itemDetailsMap = new HashMap<String, Object>();
		    						itemDetailsMap.put("itemNameEnglish", item.getItemNameEnglish());
		    						itemDetailsMap.put("itemNameOdiya", item.getItemNameOdiya());
		    						itemDetailsMap.put("itemStatus", item.getItemStatus());
		    						datalistItemDetails.add(itemDetailsMap);
		    					}
		    				}
		    				orderDetailsMap.put("itemDetails",datalistItemDetails);
		    				orderDetailsMap.put("itemId", od.getItemId());
							orderDetailsMap.put("quantity", od.getQuantity());
							orderDetailsMap.put("uom", od.getUom());
							datalistOrderDetails.add(orderDetailsMap);
		    				
		    			}
		    		}
		    		orderMasterMap.put("itemsTO", datalistOrderDetails);
		    		orderMasterMap.put("orderId", om.getOrderId());
		    		orderMasterMap.put("customerId", om.getCustomerId());
		    		orderMasterMap.put("freeText", om.getFreeText());
		    		orderMasterMap.put("deliveryDate", om.getDeliveryDate());
		    		orderMasterMap.put("deliveryType", om.getDeliveryType());
		    		orderMasterMap.put("delivery_time", om.getDelivery_time());
		    		orderMasterMap.put("orderEditStatus", om.getOrderEditStatus());
		    		datalistOrder.add(orderMasterMap);
		    		}
		    }
			object.put("orderHistory", datalistOrder);
		}catch(Exception e) {
			object.put("exception", e);
		}
		return object;
	}

	private static void createList(Map<String, Object> dlo, Row row, int rownum) // creating cells for each row
	{
	        Cell cell = row.createCell(0);
	        cell.setCellValue(rownum-3);
	     
	        /*cell = row.createCell(1);
	        cell.setCellValue(dlo.get("orderDate").toString());*/
	        
	        cell = row.createCell(1);
	        cell.setCellValue(dlo.get("vegetableNameEnglish").toString());
	        
	        cell = row.createCell(2);
	        cell.setCellValue(dlo.get("vegetableNameOdiya").toString());
	        
	        cell = row.createCell(3);
	        cell.setCellValue(dlo.get("quantiyOrdered").toString());
	        
	        cell = row.createCell(4);
	        cell.setCellValue(dlo.get("unitsOfMeasurement").toString()+".");
	        
	        /*cell = row.createCell(6);
	        cell.setCellValue(dlo.get("deliveryDate").toString());*/
	       
	    
	}

	@Override
	public JSONObject collateOrder() {
		JSONObject object = new JSONObject();
		try {
			
			List<OrderHistoryTO> orderHistoryList=customerHomeDao.collateAllOrders();
			
			XSSFWorkbook workbook = new XSSFWorkbook(); 
	        XSSFSheet sheet = workbook.createSheet("Order_For_Delivery");
	        
	        
	        XSSFFont headerFont = workbook.createFont();
	        headerFont.setBold(true);
	        headerFont.setFontHeightInPoints((short) 14);
	        // Set Header Font Style
	        CellStyle headerStyle = workbook.createCellStyle();
	        headerStyle.setFont(headerFont);
	        
	        
	        
	        XSSFRow row0 = sheet.createRow(0);
	        XSSFCell r1c1 = row0.createCell(0);
	        r1c1.setCellValue("Orders for delivery:");
	        sheet.autoSizeColumn(0);
	        r1c1.setCellStyle(headerStyle);
	        
	        
	        XSSFRow row1 = sheet.createRow(1);
			r1c1 = row1.createCell(0);
			r1c1.setCellValue("Sl.No.");
			sheet.autoSizeColumn(0);
			r1c1.setCellStyle(headerStyle);
			XSSFCell r1c2 = row1.createCell(1);
			r1c2.setCellValue("Customer Name");
			sheet.setColumnWidth(1, 5000);
			r1c2.setCellStyle(headerStyle);
			XSSFCell r1c3 = row1.createCell(2);
			r1c3.setCellValue("Vegetable Name English");
			sheet.autoSizeColumn(2);
			r1c3.setCellStyle(headerStyle);
			XSSFCell r1c4 = row1.createCell(3);
			r1c4.setCellValue("Vegetable Name Oriya");
			sheet.autoSizeColumn(3);
			r1c4.setCellStyle(headerStyle);
			XSSFCell r1c5 = row1.createCell(4);
			r1c5.setCellValue("Quantity");
			sheet.autoSizeColumn(4);
			r1c5.setCellStyle(headerStyle);
			XSSFCell r1c6 = row1.createCell(5);
			r1c6.setCellValue("UOM");
			sheet.autoSizeColumn(5);
			r1c6.setCellStyle(headerStyle);
			XSSFCell r1c7 = row1.createCell(6);
			r1c7.setCellValue("Order Date and Time");
			sheet.autoSizeColumn(6);
			r1c7.setCellStyle(headerStyle);
			XSSFCell r1c8 = row1.createCell(7);
			r1c8.setCellValue("Delivery Date");
			sheet.autoSizeColumn(7);
			r1c8.setCellStyle(headerStyle);
			XSSFCell r1c9 = row1.createCell(8);
			r1c9.setCellValue("Delivery Time");
			sheet.autoSizeColumn(8);
			r1c9.setCellStyle(headerStyle);
			XSSFCell r1c10 = row1.createCell(9);
			r1c10.setCellValue("Order code");
			sheet.autoSizeColumn(10);
			r1c10.setCellStyle(headerStyle);
			XSSFCell r1c11 = row1.createCell(10);
			r1c11.setCellValue("No of Revisions");
			sheet.autoSizeColumn(10);
			r1c11.setCellStyle(headerStyle);
			
			XSSFCell r1c12 = row1.createCell(11);
			r1c12.setCellValue("Special comments");
			sheet.autoSizeColumn(11);
			r1c12.setCellStyle(headerStyle);
			
			XSSFCell r1c13 = row1.createCell(12);
			r1c13.setCellValue("Delivery Address");
			sheet.autoSizeColumn(12);
			r1c13.setCellStyle(headerStyle);
			
			
			int rownum = 2;
	        for(OrderHistoryTO dlo:orderHistoryList) {
	        	Row row = sheet.createRow(rownum++);
	        	createListOne(dlo, row, rownum);
	        }
	        FileOutputStream out = new FileOutputStream(new File("/var/vegkart/CollatedOrder.xlsx")); // file name with path
            workbook.write(out);
            out.close();
            List<String> to = customerHomeDao.getAdminMailId();
            Thread thread = new Thread();
            CollateOrderMail maillist = new CollateOrderMail();
			maillist.setParameters(to);
			thread = new Thread(maillist);
			thread.start();
	        
	        
			object.put("orderHistory", orderHistoryList);
		}catch(Exception e) {
			object.put("object", e);
		}
		return object;
	}

	public void createListOne(OrderHistoryTO dlo, Row row, int rownum) // creating cells for each row
	{
		List<String> adcon=new ArrayList<String>();
		String dline1="";
		String dline2="";
		String dline3="";
		
			String customerName=customerHomeDao.getCustomerName(dlo.getCustomerId());
	        Cell cell = row.createCell(0);
	        cell.setCellValue(rownum-2);
	     
	        cell = row.createCell(1);
	        cell.setCellValue(customerName);
	        
	        cell = row.createCell(2);
	        cell.setCellValue(dlo.getVegNameEng());
	        
	        cell = row.createCell(3);
	        cell.setCellValue(dlo.getVegNameOdi());
	        
	        cell = row.createCell(4);
	        cell.setCellValue(dlo.getQuantity());
	        
	        cell = row.createCell(5);
	        cell.setCellValue(dlo.getUom()+".");
	        
	       
			String orderDate = dlo.getOrderDate().toString();
			String orderDateOnly[]= orderDate.split(" ");
	        cell = row.createCell(6);
	        cell.setCellValue(orderDate+" "+dlo.getOrderTime());
	        
	        /*java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
			String deliveryDate = sdf.format(dlo.getDeliveryDate());
			String deliveryDateOnly[]= deliveryDate.split(" ");*/
	        cell = row.createCell(7);
	        cell.setCellValue(dlo.getDeliveryDate());
	        
	        cell = row.createCell(8);
	        cell.setCellValue(String.valueOf(dlo.getDeliveryTime()));
	        
	        cell = row.createCell(9);
	        cell.setCellValue(String.valueOf(dlo.getOrderId()));
	        
	        List<OrderRevisionMappingTO> ormT=customerHomeDao.revisionMappingHistory(dlo.getOrderId());
	        
	        cell = row.createCell(10);
	        cell.setCellValue(String.valueOf(ormT.size()));
	    
	        cell = row.createCell(11);
	        cell.setCellValue(dlo.getFreeText());
	        
	        List<OrderMaster> orderMasterList=customerHomeDao.fetchOrderMasterWithId(dlo.getOrderId());
	        
	        cell = row.createCell(12);
	        if(!orderMasterList.get(0).getDeladdress().getLine1().equals("")){
		        dline1=orderMasterList.get(0).getDeladdress().getLine1();
		        adcon.add(dline1);
		        }
		        if(!orderMasterList.get(0).getDeladdress().getLine2().equals("")) {
		        dline2=orderMasterList.get(0).getDeladdress().getLine2();
		        adcon.add(dline2);
		        }
		        if(!orderMasterList.get(0).getDeladdress().getLine2().equals("")) {
		        dline3=orderMasterList.get(0).getDeladdress().getLine3();
		        adcon.add(dline3);
		        }
		        String daddress1=String.join(", ", adcon);
		        String daddress2=", "+orderMasterList.get(0).getDeladdress().getCity()+", "+orderMasterList.get(0).getDeladdress().getDistrict()+", "+orderMasterList.get(0).getDeladdress().getState()+", "+
		        		orderMasterList.get(0).getDeladdress().getPincode();
		        String finalvalueAddress=daddress1+daddress2;
	        cell.setCellValue(finalvalueAddress);
	       
	    
	}

	@Override
	public JSONObject fetchItemsForAdmin() {
		JSONObject object = new JSONObject();
		try {
			List<ItemsTO> adminItems=customerHomeDao.fetchadminitems();
			if(!adminItems.isEmpty()) {
				object.put("adminItems", adminItems);
			}
		}catch(Exception e) {
			object.put("error", e);
		}
		return object;
	}

	@Override
	public JSONObject deactivateItem(ItemsTO itemsTO) {
		JSONObject object = new JSONObject();
		try {
			int itemId=itemsTO.getItemId();
			boolean validateUpdate=customerHomeDao.updateItem(itemId);
			if(validateUpdate==true) {
				object.put("status", "Vegetable Deactivated");
			}else{
				object.put("status", "Update Failure");
			}
		}catch(Exception e) {
			object.put("error", e);
		}
		return object;
	}

	@Override
	public JSONObject deactivateCustomer(Customer customerTO) {
		JSONObject object = new JSONObject();
		try {
			int customerID=customerTO.getCustomerId();
			boolean validateUpdate=customerHomeDao.updateCustomer(customerID);
			if(validateUpdate==true) {
				object.put("status", "Customer Deactivated");
			}else{
				object.put("status", "Update Failure");
			}
		}catch(Exception e) {
			object.put("error", e);
		}
		return object;
	}

	@Override
	public JSONObject activateItem(ItemsTO itemsTO) {
		JSONObject object = new JSONObject();
		try {
			int itemId=itemsTO.getItemId();
			boolean validateUpdate=customerHomeDao.updateacItem(itemId);
			if(validateUpdate==true) {
				object.put("status", "Vegetable Activated");
			}else{
				object.put("status", "Update Failure");
			}
		}catch(Exception e) {
			object.put("error", e);
		}
		return object;
	}

	@Override
	public JSONObject activateCustomer(Customer customerTO) {
		JSONObject object = new JSONObject();
		try {
			int customerID=customerTO.getCustomerId();
			boolean validateUpdate=customerHomeDao.updateacCustomer(customerID);
			if(validateUpdate==true) {
				object.put("status", "Customer Activated");
			}else{
				object.put("status", "Update Failure");
			}
		}catch(Exception e) {
			object.put("error", e);
		}
		return object;
	}

	@Override
	public JSONObject addAdmin(CustomerRegistrationDTO customerTO) {
		JSONObject object = new JSONObject();
		try {
			CustomerAddress address=new CustomerAddress();
			CustomerDeliveryAddress deladdress=new CustomerDeliveryAddress();
			CustomerStatusDto status = new CustomerStatusDto();
			Customer cust=new Customer();
			boolean emailval = customerRegistrationDao.validateCustomer(customerTO.getPrimaryEmail());
			if(true==emailval) {
			cust.setCustomerActiveStatus(1);
			cust.setRole(0);
			cust.setName(customerTO.getName());
			cust.setMobileNumber(customerTO.getMobile());
			cust.setEmail(customerTO.getPrimaryEmail());
			cust.setSecEmailOne(customerTO.getSecondaryEmail1());
			cust.setSecEmailTwo(customerTO.getSecondaryEmail2());
			cust.setPassword(customerTO.getPassword());
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Date dt=new Date();
			String reportDate = df.format(dt);
			address.setLine1("");
			address.setLine2("");
			address.setLine3("");
			address.setCity("");
			address.setCountry("");
			address.setDistrict("");
			address.setPincode("");
			address.setState("");
			address.setCreatedBy(CustomerRegistrationConstants.CREATED_BY);
			address.setCreatedDate(reportDate);
			deladdress.setLine1("");
			deladdress.setLine2("");
			deladdress.setLine3("");
			deladdress.setCity("");
			deladdress.setCountry("");
			deladdress.setDistrict("");
			deladdress.setPincode("");
			deladdress.setState("");
			deladdress.setCreatedBy(CustomerRegistrationConstants.CREATED_BY);
			deladdress.setCreatedDate(reportDate);
			cust.setDeladdress(deladdress);
			cust.setAddress(address);
			cust=customerHomeDao.addAdmin(cust);
			if(cust.getCustomerId()!=0) {
				status.setStatusCode("Success");
				status.setStatusMessage("Admin added Successfully");
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
		}catch(Exception e){
			object.put("Exception", e);
		}
		return object;
	}

	@Override
	public JSONObject findAllAdmin() {
		JSONObject object = new JSONObject();
		try {
		List<Customer> admins=customerHomeDao.findAllAdmins();
		if(!admins.isEmpty()) {
			object.put("allAdmins", admins);
		}else {
			object.put("status", "No admin found. Please create a new admin");
		}
		}catch(Exception e) {
			object.put("error", e);
		}
		
		return object;
	}

	@Override
	public boolean updatePreviousOrderSetPreviousOrderNTBC(OrderMaster orderMaster) {
		JSONObject object = new JSONObject();
		try {
			boolean updateStatus=customerHomeDao.setUpdateRevStatus(orderMaster.getOrderId());
			if(updateStatus==true) {
				return true;
			}else {
				return false;
			}
		}catch(Exception e) {
			
		}
		return false;
	}

	@Override
	public void updateRevStatusForAll() {
		try {
			boolean updateStatus=customerHomeDao.updateRevStatusForAll();
			if(updateStatus==true)
			{
				System.out.println("Batch finished successfully");
			}
			
		}catch(Exception e) {
			
		}
		
	}

	@Override
	public JSONObject cancelOrder(OrderMaster orderMaster) {
		JSONObject object = new JSONObject();
		try {
			String serverId="android server id";
			String message="Cancelled Order for XYZ !";
			Date dt=new Date();
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			List<Map<String, Object>> datalistOrder = new ArrayList<Map<String, Object>>();
			int orderId=orderMaster.getOrderId();
			boolean cancelOrder=customerHomeDao.cancelOrder(orderId);
			
			orderMaster=customerHomeDao.getOrderMasterForCancellation(orderId);
			String delivDate=orderMaster.getDeliveryDate();
			String delivTime=orderMaster.getDelivery_time();
			String delivTyp=orderMaster.getDeliveryType();
			int noOfRevisions=0;
			int oldOrderId=0;
			int newOrderId=0;
			String customerName=customerHomeDao.getCustomerName(orderMaster.getCustomerId());
			List<OrderDetails> odList=customerHomeDao.getOrderDetails(orderMaster.getOrderId());
			if(!odList.isEmpty()) {
				for(OrderDetails od:odList) {
					OrderHistoryTO orderHistoryTO=new OrderHistoryTO();
					Map<String, Object> orderMap = new HashMap<String, Object>();
					orderMap.put("orderId", orderMaster.getOrderId());
					//String orderDate = sdf.format(orderMaster.getOrderDate());
					orderMap.put("orderDate", orderMaster.getOrderDate());
					orderMap.put("orderTime", orderMaster.getOrderTime());
					orderMap.put("orderDetailsId", od.getOrderDetailsId());
					ItemsTO itemsTO=customerHomeDao.getItemDetails(od.getItemId());
					orderMap.put("vegetableNameEnglish", itemsTO.getItemNameEnglish());
					orderMap.put("vegetableNameOdiya", itemsTO.getItemNameOdiya());
					orderMap.put("quantiyOrdered", od.getQuantity());
					orderMap.put("unitsOfMeasurement", od.getUom());
					orderMap.put("customerId", od.getCustomerId());
					orderMap.put("deliveryDate", orderMaster.getDeliveryDate());
					orderMap.put("deliveryTime", orderMaster.getDelivery_time());
					orderMap.put("freeText", orderMaster.getFreeText());
					orderMap.put("deliveryType", orderMaster.getDeliveryType());
					orderMap.put("orderEditStatus", orderMaster.getOrderEditStatus());
					datalistOrder.add(orderMap);
				}
			}
			String cancelDt = sdf.format(dt);
//Create old new mapping end
			
			
			
			/*ModelForExcelDTO model=new ModelForExcelDTO();
			model.setOrderList(datalistOrder);*/
			XSSFWorkbook workbook = new XSSFWorkbook(); 
	        XSSFSheet sheet = workbook.createSheet("Cancelled_Vegetable_Order");
	        
	        // Set Custom Header Font - headerFont
	        XSSFFont headerFont = workbook.createFont();
	        headerFont.setBold(true);
	        headerFont.setFontHeightInPoints((short) 14);
	        // Set Custom Header Font Style1 - headerStyle
	        CellStyle headerStyle = workbook.createCellStyle();
	        headerStyle.setFont(headerFont);
	        // Set Custom Header Font Style1 - nonHeader
	        CellStyle nonHeader = workbook.createCellStyle();
	        nonHeader.setWrapText(true);
	        
	        
	        
	        //Create Row Start
	        XSSFRow row0 = sheet.createRow(0);
	        //Create Row End
	        
	        //Create Cell
	        XSSFCell r1c1 = row0.createCell(0);
	        r1c1.setCellValue(customerName+"'s cancelled Order");
	        sheet.autoSizeColumn(0);
	        r1c1.setCellStyle(headerStyle);
	        //Create Cell
	        XSSFCell r1c3 = row0.createCell(2);
	        r1c3.setCellValue("Order Comments");
	        sheet.autoSizeColumn(2);
	        r1c3.setCellStyle(headerStyle);
	        //Create Cell
	        XSSFCell r1c4 = row0.createCell(3);
	        if(orderMaster.getFreeText()!=null) {
	        r1c4.setCellValue(orderMaster.getFreeText());
	        }else {
	        	r1c4.setCellValue("No Extra comments for this order");
	        }
	        sheet.autoSizeColumn(3);
	        r1c4.setCellStyle(nonHeader);
	      //Create Cell
	        XSSFCell r1c6 = row0.createCell(5);
	        r1c6.setCellValue("Order Date and Time");
	        sheet.autoSizeColumn(5);
	        r1c6.setCellStyle(headerStyle);
	        //Create Cell
	        XSSFCell r1c7 = row0.createCell(6);
	       
	        r1c7.setCellValue(orderMaster.getOrderDate()+" "+orderMaster.getOrderTime()+" Hrs.");
	        sheet.autoSizeColumn(7);
	        r1c7.setCellStyle(nonHeader);
	        //Create Cell
	        XSSFCell r1c9 = row0.createCell(8);
	        r1c9.setCellValue("Delivery Date and Time");
	        sheet.autoSizeColumn(8);
	        r1c9.setCellStyle(headerStyle);
	        //Create Cell
	        XSSFCell r1c10 = row0.createCell(9);
	        r1c10.setCellValue(delivDate+" "+delivTime+" Hrs.");
	        sheet.autoSizeColumn(9);
	        r1c10.setCellStyle(nonHeader);
	        //Create Cell
	        XSSFCell r1c12 = row0.createCell(11);
	        r1c12.setCellValue("Cancellation Date and Time");
	        sheet.autoSizeColumn(11);
	        r1c12.setCellStyle(headerStyle);
	        //Create Cell
	        XSSFCell r1c13 = row0.createCell(12);
	        r1c13.setCellValue(cancelDt+" Hrs.");
	        sheet.autoSizeColumn(12);
	        r1c13.setCellStyle(nonHeader);
	        
	        
	        
	        
	        //Create Row Start
	        XSSFRow row1 = sheet.createRow(2);
	        //Create Row Start
	        
	        //Create Cell
			r1c1 = row1.createCell(0);
			r1c1.setCellValue("Sl.No.");
			sheet.autoSizeColumn(0);
			r1c1.setCellStyle(headerStyle);
			//Create Cell
			XSSFCell r1c2 = row1.createCell(1);
			r1c2.setCellValue("Vegetable Name English");
			sheet.setColumnWidth(1, 5000);
			r1c2.setCellStyle(headerStyle);
			//Create Cell
			r1c3 = row1.createCell(2);
			r1c3.setCellValue("Vegetable Name Oriya");
			sheet.autoSizeColumn(2);
			r1c3.setCellStyle(headerStyle);
			//Create Cell
			r1c4 = row1.createCell(3);
			r1c4.setCellValue("Quantity");
			sheet.autoSizeColumn(3);
			r1c4.setCellStyle(headerStyle);
			//Create Cell
			XSSFCell r1c5 = row1.createCell(4);
			r1c5.setCellValue("UOM");
			sheet.autoSizeColumn(4);
			r1c5.setCellStyle(headerStyle);
			/*//Create Cell
			XSSFCell r1c6 = row1.createCell(5);
			r1c6.setCellValue("UOM");
			sheet.autoSizeColumn(5);
			r1c6.setCellStyle(headerStyle);*/
			/*//Create Cell
			XSSFCell r1c7 = row1.createCell(6);
			r1c7.setCellValue("Delivery Date");
			sheet.autoSizeColumn(6);
			r1c7.setCellStyle(headerStyle);*/
			
			//Create Data List--Start
			int rownum = 3;
	        for(Map<String, Object> dlo:datalistOrder) {
	        	Row row = sheet.createRow(rownum++);
	        	createList(dlo, row, rownum);
	        }
	        //Create Data List--End
	        
	        
	        //Cancellation Mail-Start
	        FileOutputStream out = new FileOutputStream(new File("/var/vegkart/OrderToXYZ.xlsx")); // file name with path
            workbook.write(out);
            out.close();
			List<String> to = customerHomeDao.getAdminMailId();
			String cc1=customerHomeDao.getCustomerPrimaryMailId(orderMaster.getCustomerId());
			String cc2=customerHomeDao.getSecondaryOneMailId(orderMaster.getCustomerId());
			String cc3=customerHomeDao.getSecondaryTwoMailId(orderMaster.getCustomerId());
			Thread thread = new Thread();
			CancelOrderMail maillist = new CancelOrderMail();
			maillist.setParameters(to,cc1,cc2,cc3,noOfRevisions,customerName,delivTyp,delivDate,delivTime);
			thread = new Thread(maillist);
			thread.start();
			//Cancellation Mail-End
	        
			List<String> deviceIdListOfAdmins=customerHomeDao.deviceIdListOfAdmins();
			if(!deviceIdListOfAdmins.isEmpty()) {
				for(String devices:deviceIdListOfAdmins) {
					FCM.send_FCM_Notification(devices,serverId,message);
				}
			}
	        
			if(cancelOrder==true) {
			
				object.put("status", "Order Cancelled and Mail Sent");
			}else {
				object.put("status", "Cancel Failure Order Persists");
			}
		}catch(Exception e) {
			object.put("error", e);
		}
		return object;
	}

	@Override
	public boolean doesItemExistInOrder(int itemId) {
		ItemsTO item=customerHomeDao.getItemName(itemId);
		boolean validateItemExist=customerHomeDao.validateItemExist(item.getItemNameEnglish());
		return validateItemExist;
	}

	@Override
	public boolean doesCustomerExistInOrder(int customerId) {
		boolean validateCustExist=customerHomeDao.custExist(customerId);
		return validateCustExist;
	}
	
	//@Override
	public JSONObject newcollateOrder() {
		JSONObject object = new JSONObject();
		try {
			
			List<Map<String, Object>> datalistOrder = new ArrayList<Map<String, Object>>();
			OrderMaster orderMaster=new OrderMaster();
			List<OrderMaster> orderMasterList=customerHomeDao.getOMList();
		    if(!orderMasterList.isEmpty()) {
		    	for(OrderMaster om:orderMasterList) {
		    		Map<String, Object> orderMasterMap = new HashMap<String, Object>();
		    		List<OrderDetails> orderDetailsList=customerHomeDao.getOrderDetailsList(om.getOrderId());
		    		List<Map<String, Object>> datalistOrderDetails = new ArrayList<Map<String, Object>>();
		    		if(!orderDetailsList.isEmpty()) {
		    			for(OrderDetails od:orderDetailsList) {
		    				Map<String, Object> orderDetailsMap = new HashMap<String, Object>();
		    				List<Map<String, Object>> datalistItemDetails = new ArrayList<Map<String, Object>>();
		    				List<ItemsTO> itemDetailsList=customerHomeDao.getItemsList(od.getItemId());
		    				if(!itemDetailsList.isEmpty()) {
		    					for(ItemsTO item:itemDetailsList) {
		    						Map<String, Object> itemDetailsMap = new HashMap<String, Object>();
		    						itemDetailsMap.put("itemNameEnglish", item.getItemNameEnglish());
		    						itemDetailsMap.put("itemNameOdiya", item.getItemNameOdiya());
		    						itemDetailsMap.put("itemStatus", item.getItemStatus());
		    						datalistItemDetails.add(itemDetailsMap);
		    					}
		    				}
		    				orderDetailsMap.put("itemDetails",datalistItemDetails);
		    				orderDetailsMap.put("itemId", od.getItemId());
							orderDetailsMap.put("quantity", od.getQuantity());
							orderDetailsMap.put("uom", od.getUom());
							datalistOrderDetails.add(orderDetailsMap);
		    				
		    			}
		    		}
		    		orderMasterMap.put("itemsTO", datalistOrderDetails);
		    		orderMasterMap.put("orderId", om.getOrderId());
		    		orderMasterMap.put("customerId", om.getCustomerId());
		    		orderMasterMap.put("freeText", om.getFreeText());
		    		orderMasterMap.put("deliveryDate", om.getDeliveryDate());
		    		orderMasterMap.put("deliveryType", om.getDeliveryType());
		    		orderMasterMap.put("delivery_time", om.getDelivery_time());
		    		orderMasterMap.put("orderEditStatus", om.getOrderEditStatus());
		    		orderMasterMap.put("orderDate", om.getOrderDate());
		    		orderMasterMap.put("orderTime", om.getOrderTime());
		    		datalistOrder.add(orderMasterMap);
		    		}
		    }
			object.put("orderHistory", datalistOrder);
			
			XSSFWorkbook workbook = new XSSFWorkbook(); 
	        XSSFSheet sheet = workbook.createSheet("Order_For_Delivery");
	        
	        
	        XSSFFont headerFont = workbook.createFont();
	        headerFont.setBold(true);
	        headerFont.setFontHeightInPoints((short) 14);
	        // Set Header Font Style
	        CellStyle headerStyle = workbook.createCellStyle();
	        headerStyle.setFont(headerFont);
	        
	        
	        
	        XSSFRow row0 = sheet.createRow(0);
	        XSSFCell r1c1 = row0.createCell(0);
	        r1c1.setCellValue("Orders for delivery:");
	        sheet.autoSizeColumn(0);
	        r1c1.setCellStyle(headerStyle);
	        
	        
	        XSSFRow row1 = sheet.createRow(1);
			r1c1 = row1.createCell(0);
			r1c1.setCellValue("Sl.No.");
			sheet.autoSizeColumn(0);
			r1c1.setCellStyle(headerStyle);
			XSSFCell r1c2 = row1.createCell(1);
			r1c2.setCellValue("Customer Name");
			sheet.setColumnWidth(1, 5000);
			r1c2.setCellStyle(headerStyle);
			XSSFCell r1c3 = row1.createCell(2);
			r1c3.setCellValue("Vegetable Name English");
			sheet.autoSizeColumn(2);
			r1c3.setCellStyle(headerStyle);
			XSSFCell r1c4 = row1.createCell(3);
			r1c4.setCellValue("Vegetable Name Oriya");
			sheet.autoSizeColumn(3);
			r1c4.setCellStyle(headerStyle);
			XSSFCell r1c5 = row1.createCell(4);
			r1c5.setCellValue("Quantity");
			sheet.autoSizeColumn(4);
			r1c5.setCellStyle(headerStyle);
			XSSFCell r1c6 = row1.createCell(5);
			r1c6.setCellValue("UOM");
			sheet.autoSizeColumn(5);
			r1c6.setCellStyle(headerStyle);
			XSSFCell r1c7 = row1.createCell(6);
			r1c7.setCellValue("Order Date and Time");
			sheet.autoSizeColumn(6);
			r1c7.setCellStyle(headerStyle);
			XSSFCell r1c8 = row1.createCell(7);
			r1c8.setCellValue("Delivery Date");
			sheet.autoSizeColumn(7);
			r1c8.setCellStyle(headerStyle);
			XSSFCell r1c9 = row1.createCell(8);
			r1c9.setCellValue("Delivery Time");
			sheet.autoSizeColumn(8);
			r1c9.setCellStyle(headerStyle);
			XSSFCell r1c10 = row1.createCell(9);
			r1c10.setCellValue("Order code");
			sheet.autoSizeColumn(10);
			r1c10.setCellStyle(headerStyle);
			XSSFCell r1c11 = row1.createCell(10);
			r1c11.setCellValue("No of Revisions");
			sheet.autoSizeColumn(10);
			r1c11.setCellStyle(headerStyle);
			
			XSSFCell r1c12 = row1.createCell(11);
			r1c12.setCellValue("Special comments");
			sheet.autoSizeColumn(11);
			r1c12.setCellStyle(headerStyle);
			
			
			int rownum = 2;
			int rowstart=0;
			int tillrow=0;
			
			
	        for(Map<String, Object> dlo:datalistOrder) {
	        	Row row = sheet.createRow(rownum++);
	        	createListTwo(dlo, row, rownum, sheet);
	        	System.out.println(rownum);
	        }
	        
	       /* int rowcounter=2;
			for (Map<String, Object> dlo : datalistOrder) {
				// Merging cells for All Questions
				//6 7
				//8 9
				
				// 6 7
				//9 10
				List<Map<String, Object>> itemto = (List<Map<String, Object>>) dlo.get("itemsTO");
				if (itemto.size() > 1) {
					if(rowstart>rowcounter) {
						sheet.addMergedRegion(new CellRangeAddress(rowstart, (rowstart + (itemto.size() - 1)), 9, 9));
						tillrow=(rowstart + (itemto.size() - 1));
					}else {
					sheet.addMergedRegion(new CellRangeAddress(rowcounter, (rowcounter + (itemto.size() - 1)), 9, 9));
					tillrow=(rowcounter + (itemto.size() - 1));
					}
					rowstart=tillrow+1;//8
				}
				  rowcounter++;//9
			}*/
					
					
	        FileOutputStream out = new FileOutputStream(new File("/var/vegkart/CollatedOrder.xlsx")); // file name with path
            workbook.write(out);
            out.close();
            List<String> to = customerHomeDao.getAdminMailId();
            Thread thread = new Thread();
            CollateOrderMail maillist = new CollateOrderMail();
			maillist.setParameters(to);
			thread = new Thread(maillist);
			thread.start();
	        
	        
			object.put("orderHistory", "");
		}catch(Exception e) {
			object.put("object", e);
		}
		return object;
	}
	
	
	public void createListTwo(Map<String, Object> dlo, Row row, int rownum, XSSFSheet sheet) // creating cells for each row
	{
		
		int rowsinitial=rownum;
			String customerName=customerHomeDao.getCustomerName(Integer.parseInt(dlo.get("customerId").toString()));
			List<Map<String, Object>> itemto=(List<Map<String, Object>>) dlo.get("itemsTO");
	        Cell cell = row.createCell(0);
	        cell.setCellValue(rownum-2);
	       
	     
		cell = row.createCell(1);
		cell.setCellValue(customerName);
		
		if (!itemto.isEmpty()) {
			for (Map<String, Object> item : itemto) {
				List<Map<String, Object>> itemdetailsto = (List<Map<String, Object>>) item.get("itemDetails");
				for (Map<String, Object> itemd : itemdetailsto) {
					cell = row.createCell(2);
					cell.setCellValue(itemd.get("itemNameEnglish").toString());

					cell = row.createCell(3);
					cell.setCellValue(itemd.get("itemNameOdiya").toString());
				}
				
				cell = row.createCell(4);
				cell.setCellValue(item.get("quantity").toString());
				
				cell = row.createCell(5);
				cell.setCellValue(item.get("uom").toString()+".");
			}
		}
		
		
	    	        
	        /*cell = row.createCell(3);
	        cell.setCellValue(dlo.getVegNameOdi());
	        
	        cell = row.createCell(4);
	        cell.setCellValue(dlo.getQuantity());
	        
	        cell = row.createCell(5);
	        cell.setCellValue(dlo.getUom()+".");*/
	        
	       
	        cell = row.createCell(6);
	        cell.setCellValue(dlo.get("orderDate").toString()+"  "+dlo.get("orderTime").toString());
	        
	        cell = row.createCell(7);
	        cell.setCellValue(dlo.get("deliveryDate").toString());
	        
	        cell = row.createCell(8);
	        cell.setCellValue(dlo.get("delivery_time").toString());
	        
	        cell = row.createCell(9);
	        cell.setCellValue(Integer.parseInt(dlo.get("orderId").toString()));
	        
	        List<OrderRevisionMappingTO> ormT=customerHomeDao.revisionMappingHistory(Integer.parseInt(dlo.get("orderId").toString()));
	        
	        cell = row.createCell(10);
	        cell.setCellValue(String.valueOf(ormT.size()));
	    
	        cell = row.createCell(11);
	        cell.setCellValue(dlo.get("freeText").toString());
	        
	       
	        	

	}

	@Override
	public JSONObject getCustomerAddress(Customer customer) {
		JSONObject obj=new JSONObject();
		try {
			int customerId=customer.getCustomerId();
			customer=customerHomeDao.getCustomerAddress(customerId);
			if(customer!=null) {
			obj.put("customerDetails",customer);
			obj.put("status","Customer fetched successfully");
			}
			else {
				obj.put("status","Customer does not exist");
			}
		}catch(Exception e) {
			obj.put("status", "error");
		}
		return obj;
	}

	@Override
	public JSONObject editCustomerDetails(CustomerRegistrationDTO customerRegistration) {
		
		JSONObject object=new JSONObject();
		try {
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
		address.setAddressId(customerRegistration.getAddressId());
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
		deladdress.setAddressId(customerRegistration.getDeliveryaddressid());
		customer.setCustomerId(customerRegistration.getCustomerId());
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
		customer=customerHomeDao.updateCustInfo(customer);
		if(customer!=null) {
			object.put("customer", customer);
			return object;
		}
		}catch(Exception e) {
			object.put("error", e);
		}
		return object;
	}

	

}
