package mycom.orderapp.DAOImpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mycom.orderapp.DAO.CustomerHomeDao;
import mycom.orderapp.controller.CustomerRegistrationController;
import mycom.orderapp.model.Customer;
import mycom.orderapp.model.CustomerAddress;
import mycom.orderapp.model.CustomerDeliveryAddress;
import mycom.orderapp.model.ItemsTO;
import mycom.orderapp.model.OrderDetails;
import mycom.orderapp.model.OrderHistoryTO;
import mycom.orderapp.model.OrderMaster;
import mycom.orderapp.model.OrderRevisionMappingTO;

@Repository
public class CustomerHomeDaoImpl implements CustomerHomeDao {
	@Autowired
	private SessionFactory sessionFactory;
	Session session = null;

	@Override
	@Transactional
	public Customer verifyLogin(String password, String emailID) {
		int check=1;
		session = this.sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Customer.class).add(Restrictions.eq("email", emailID))
				.add(Restrictions.eq("password", password)).add(Restrictions.eq("customerActiveStatus", check));
		/*List<Customer> cusgtList = criteria.list();*/
		/*for (Customer c : cusgtList) {
			System.out.println(c.getEmail() + " " + c.getPassword());
		}*/
		//criteria = 
		Customer customer = (Customer) criteria.uniqueResult();
		return customer;
	}

	@Override
	@Transactional
	public Customer updateDeviceId(Customer customer) {
		session = this.sessionFactory.getCurrentSession();
		/*Criteria criteria = session.createCriteria(Customer.class).add(Restrictions.eq("email", customer.getEmail()))
				.add(Restrictions.eq("password", customer.getPassword()));*/
		session.saveOrUpdate(customer);
		return customer;
	}

	@Override
	@Transactional
	public ItemsTO addItems(ItemsTO itemsTO) {
		session = this.sessionFactory.getCurrentSession();
		session.save(itemsTO);
		return itemsTO;
		
	}

	@Override
	@Transactional
	public List<Customer> fetchAllCustomers() {
		int customerrole=1;
		session = this.sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Customer.class).add(Restrictions.eq("role", customerrole));
		return criteria.list();
	}

	@Override
	@Transactional
	public List<OrderMaster> fetchCustomerItems(int customerId) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("SELECT * FROM order_master fds where customer_id=1 and fds.order_date BETWEEN  DATE_SUB(convert_tz(now(),'+00:00','+05:30'), INTERVAL 96 HOUR) AND convert_tz(now(),'+00:00','+05:30')");
		query.addEntity(OrderMaster.class);
		return query.list();
	}

	@Override
	@Transactional
	public List<ItemsTO> fetchOrderDetails(int customerId) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select it.* from order_details as od\r\n" + 
				"inner join items as it\r\n" + 
				"on od.item_id=it.item_id\r\n" + 
				"where customer_id="+customerId+" \r\n" + 
				"and od.order_date BETWEEN  DATE_SUB(convert_tz(now(),'+00:00','+05:30'), INTERVAL 96 HOUR) AND convert_tz(now(),'+00:00','+05:30')");
		query.addEntity(ItemsTO.class);
		return query.list();
		
	}

	@Override
	@Transactional
	public List<ItemsTO> fetchItemsOriginal() {
		session = this.sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(ItemsTO.class);
		return criteria.list();
	}

	@Override
	@Transactional
	public ItemsTO fetchPrefItemsById(Integer ps) {
		session = this.sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(ItemsTO.class).add(Restrictions.eq("itemId", ps));
		return (ItemsTO) criteria.uniqueResult();
	}

	@Override
	@Transactional
	public OrderMaster saveToOrder(OrderMaster orderMaster) {
		session = this.sessionFactory.getCurrentSession();
		session.saveOrUpdate(orderMaster);
		return orderMaster;
	}

	@Override
	@Transactional
	public OrderDetails saveToOrderDetails(OrderDetails orderDetails) {
		session = this.sessionFactory.getCurrentSession();
		session.save(orderDetails);
		return orderDetails;
	}

	@Override
	@Transactional
	public List<String> getAdminMailId() {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select email from customer where (role=0 or role=10) and customer_active_status=1");
		return query.list();
	}

	@Override
	@Transactional
	public String getCustomerPrimaryMailId(int customerId) {
		String customermail=null;
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select email from customer where customer_active_status=1 and role=1 and customer_id="+customerId);
		List list=query.list();
		if(!list.isEmpty() || list.size() > 0)
		{
			customermail= list.get(0).toString();
		}
		return customermail;
	}

	@Override
	@Transactional
	public List<OrderDetails> getOrderDetails(int orderId) {
		session = this.sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(OrderDetails.class).add(Restrictions.eq("order_id", orderId));
		return criteria.list();
	}

	@Override
	@Transactional
	public ItemsTO getItemDetails(int itemId) {
		session = this.sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(ItemsTO.class).add(Restrictions.eq("itemId", itemId));
		return (ItemsTO) criteria.uniqueResult();
	}

	@Override
	@Transactional
	public OrderHistoryTO saveHistory(OrderHistoryTO orderHistoryTO) {
		session = this.sessionFactory.getCurrentSession();
		session.save(orderHistoryTO);
		return orderHistoryTO;
	}

	
	@Override
	@Transactional
	public List<OrderHistoryTO> getHistory(int customerId) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("SELECT oh.* FROM orderapp.order_history as oh JOIN(\r\n" + 
				"select order_id from orderapp.order_master where customer_id= "+customerId+" and rev_status='TBC' \r\n" + 
				"order by order_id desc limit 4) om\r\n" + 
				"where oh.order_id in (om.order_id) order by oh.order_history_id desc");
		query.setCacheable(true);
		query.addEntity(OrderHistoryTO.class);
		return query.list();
	}

	@Override
	@Transactional
	public String getCustomerName(int customerId) {
		String customerName=null;
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select name from customer where role=1 and customer_id="+customerId);
		List list=query.list();
		if(!list.isEmpty() || list.size() > 0)
		{
			customerName= list.get(0).toString();
		}
		return customerName;
	}

	@Override
	@Transactional
	public List<OrderHistoryTO> collateAllOrders() {
		try {
		Date dt = new Date();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		DateFormat formatter2 = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm");
		dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String strUTCDate = dateFormatter.format(dt);
		dateFormatter.setTimeZone(TimeZone.getTimeZone("IST"));
		String strUTCDateist = dateFormatter.format(dt);
		DateFormat formatter = new SimpleDateFormat("HH:mm");
		java.sql.Time collateTimeIST = new java.sql.Time(formatter.parse(strUTCDateist).getTime());
		java.sql.Time twelveam = new java.sql.Time(formatter.parse("00:00").getTime());
		java.sql.Time five30am = new java.sql.Time(formatter.parse("00:00").getTime());
		String twelveamstr = formatter2.format(twelveam);
		String five30amstr = formatter2.format(five30am);
		String collateTimeISTstr = formatter2.format(collateTimeIST);
		Date twelveamTime = format.parse(twelveamstr);
		Date five30amTime = format.parse(five30amstr);
		Date coltime = format.parse(collateTimeISTstr);
		//if collate time is after 6:30 utc then ist is 00:00 
		
		if(coltime.after(twelveamTime) && coltime.before(five30amTime)) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("Select * from order_history where rev_status='TBC' and STR_TO_DATE(orderapp.order_history.delivery_date, '%d/%m/%Y') >= date_format(curdate(), '%Y/%m/%d') + interval 1 day order by delivery_date asc, delivery_time asc");
		query.addEntity(OrderHistoryTO.class);
		return query.list();
		}else {
			SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("Select * from order_history where rev_status='TBC' and STR_TO_DATE(orderapp.order_history.delivery_date, '%d/%m/%Y') >= date_format(curdate(), '%Y/%m/%d') order by delivery_date asc, delivery_time asc");
		
		query.addEntity(OrderHistoryTO.class);
		return query.list();
		}
		}catch(Exception e){
			
		}
		return null;
	}

	@Override
	@Transactional
	public String getSecondaryOneMailId(int customerId) {
		String secondaryone="";
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select sec_email_one from customer where role=1 and customer_id="+customerId);
		List list=query.list();
		if(!list.isEmpty() || list.size() > 0)
		{
			secondaryone= list.get(0).toString();
		}
		return secondaryone;
	}

	@Override
	@Transactional
	public String getSecondaryTwoMailId(int customerId) {
		String secondarytwo=null;
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select sec_email_two from customer where role=1 and customer_id="+customerId);
		List list=query.list();
		if(!list.isEmpty() || list.size() > 0)
		{
			secondarytwo= list.get(0).toString();
		}
		return secondarytwo;
	}

	@Override
	@Transactional
	public List<ItemsTO> fetchprefeItems(int customerId) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("SELECT vi.* \r\n" + 
				"FROM orderapp.items as vi\r\n" + 
				"inner join \r\n" + 
				"orderapp.order_history as vo\r\n" + 
				"on vi.item_name_english=vo.veg_name_english\r\n" + 
				"where vo.customer_id="+customerId+" \r\n" + 
				"and\r\n" + 
				"STR_TO_DATE(vo.order_date, '%d/%m/%Y')\r\n" + 
				"BETWEEN  DATE_SUB(convert_tz(now(),'+00:00','+05:30'), INTERVAL 96 HOUR) AND convert_tz(now(),'+00:00','+05:30') Group by vi.item_name_english");
		query.setCacheable(true);
		query.addEntity(ItemsTO.class);
		return query.list();
	}

	@Override
	@Transactional
	public List<ItemsTO> fetchrestItems(int customerId) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select DISTINCT X.item_id,X.item_name_english,X.item_name_oriya,X.item_image,X.item_status from \r\n" + 
				"(SELECT vi.*,vo.veg_name_english \r\n" + 
				"FROM orderapp.items as vi\r\n" + 
				"left join \r\n" + 
				"orderapp.order_history as vo\r\n" + 
				"on vi.item_name_english=vo.veg_name_english and vo.customer_id="+customerId+" \r\n" + 
				"and \r\n" + 
				"STR_TO_DATE(vo.order_date, '%d/%m/%Y')\r\n" + 
				"BETWEEN  DATE_SUB(convert_tz(now(),'+00:00','+05:30'), INTERVAL 96 HOUR) \r\n" + 
				"AND convert_tz(now(),'+00:00','+05:30')) X WHERE X.veg_name_english IS NULL");
		query.setCacheable(true);
		query.addEntity(ItemsTO.class);
		return query.list();
	}

	@Override
	@Transactional
	public List<ItemsTO> fetchadminitems() {
		session = this.sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(ItemsTO.class);
		return criteria.list();
	}

	@Override
	@Transactional
	public boolean updateItem(int itemId) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("update orderapp.items set item_status=0 where item_id =" + itemId);
		query.executeUpdate();
		return true;
		
	}

	@Override
	@Transactional
	public boolean updateCustomer(int customerID) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("update orderapp.customer set customer_active_status=0 where customer_id =" + customerID);
		query.executeUpdate();
		return true;
	}

	@Override
	@Transactional
	public boolean updateacItem(int itemId) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("update orderapp.items set item_status=1 where item_id =" + itemId);
		query.executeUpdate();
		return true;
	}

	@Override
	@Transactional
	public boolean updateacCustomer(int customerID) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("update orderapp.customer set customer_active_status=1 where customer_id =" + customerID);
		query.executeUpdate();
		return true;
	}

	@Override
	@Transactional
	public Customer addAdmin(Customer customerTO) {
		session = this.sessionFactory.getCurrentSession();
		session.save(customerTO);
		return customerTO;
	}

	@Override
	@Transactional
	public List<Customer> findAllAdmins() {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("SELECT * FROM orderapp.customer where role=0");
		query.setCacheable(true);
		query.addEntity(Customer.class);
		return query.list();
	}

	@Override
	@Transactional
	public List<OrderMaster> fetchOrderMaster(int customerId) {
		session = this.sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(OrderMaster.class);
		return criteria.list();
	}

	@Override
	@Transactional
	public boolean setUpdateRevStatus(int orderId) {
		SQLQuery query = sessionFactory.getCurrentSession()
				.createSQLQuery("update order_master set rev_status='NTBC' where order_id="+orderId);
		SQLQuery query1 = sessionFactory.getCurrentSession()
				.createSQLQuery("update order_history set rev_status='NTBC' where order_id="+orderId);
		if (query.executeUpdate() != 0) {
			if (query1.executeUpdate() != 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	@Transactional
	public OrderRevisionMappingTO saveNewMap(OrderRevisionMappingTO orMT) {
		session = this.sessionFactory.getCurrentSession();
		session.save(orMT);
		return orMT;
	}

	@Override
	@Transactional
	public int getNoOfRevisions(int sourceOrderId) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select * from order_revision_mapping where order_id_old="+sourceOrderId);
		return query.list().size();
	}

	@Override
	@Transactional
	public boolean updateEditStatus(int newOrderId) {
		SQLQuery query = sessionFactory.getCurrentSession()
				.createSQLQuery("update order_master set order_edit_status=1 where order_id="+newOrderId);
		SQLQuery query1 = sessionFactory.getCurrentSession()
				.createSQLQuery("update order_history set order_edit_status=1 where order_id="+newOrderId);
		if (query.executeUpdate() != 0) {
			if (query1.executeUpdate() != 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	@Transactional
	public int getSourceOrderId(int oldOrderId) {
		int sourceId=0;
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select order_id_old from order_revision_mapping where order_id_new="+oldOrderId);
		if(query.list()==null || query.list().isEmpty() || query.list().size()==0) {
			return sourceId;
		}
		List list=query.list();
		if(!list.isEmpty() || list.size() > 0)
		{
			sourceId= (int) list.get(0);
		}
		return sourceId;
	}

	@Override
	@Transactional
	public boolean updateRevStatusForAll() {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("update orderapp.order_master set order_edit_status=1 where STR_TO_DATE(order_master.delivery_date, '%d/%m/%Y') = date_format(curdate(), '%Y/%m/%d') + interval 1 day and order_edit_status!=2");
		
		SQLQuery query1 = sessionFactory.getCurrentSession().createSQLQuery("update orderapp.order_history set order_edit_status=1\r\n" + 
				"where STR_TO_DATE(orderapp.order_history.delivery_date, '%d/%m/%Y') = date_format(curdate(), '%Y/%m/%d') + interval 1 day and order_edit_status!=2");
		
		query.executeUpdate();
		query1.executeUpdate();
		return true;
	}

	@Override
	@Transactional
	public boolean cancelOrder(int orderId) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("update orderapp.order_master\r\n" + 
				"set\r\n" + 
				"delivery_type='cancelled',\r\n" + 
				"order_edit_status=3\r\n" + 
				"where \r\n" + 
				"order_id="+orderId);
		
		SQLQuery query1 = sessionFactory.getCurrentSession().createSQLQuery("update orderapp.order_history\r\n" + 
				"set\r\n" + 
				"delivery_type='cancelled',\r\n" + 
				"order_edit_status=3\r\n" + 
				"where \r\n" + 
				"order_id="+orderId);
		query.executeUpdate();
		query1.executeUpdate();
		return true;
	}

	@Override
	@Transactional
	public OrderMaster getOrderMasterForCancellation(int orderId) {
		int ordEditStatus=0;
		session = this.sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(OrderMaster.class).add(Restrictions.eq("orderId", orderId));
		return (OrderMaster) criteria.uniqueResult();
	}

	@Override
	@Transactional
	public List<String> deviceIdListOfAdmins() {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select device_id from customer where (role=0 or role=10) and customer_active_status=1");
		return query.list();
	}

	@Override
	@Transactional
	public List<OrderMaster> getOrderMasterList(int customerId) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select * from orderapp.order_master where customer_id= "+customerId+" and rev_status='TBC' \r\n" + 
				"order by order_id desc limit 4");
		query.setCacheable(true);
		query.addEntity(OrderMaster.class);
		return query.list();
	}

	@Override
	@Transactional
	public List<OrderDetails> getOrderDetailsList(int orderId) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("SELECT * FROM orderapp.order_details where order_id="+orderId);
		query.setCacheable(true);
		query.addEntity(OrderDetails.class);
		return query.list();
	}

	@Override
	@Transactional
	public List<ItemsTO> getItemsList(int itemId) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("SELECT * FROM items where item_id="+itemId);
		query.setCacheable(true);
		query.addEntity(ItemsTO.class);
		return query.list();
	}

	@Override
	@Transactional
	public boolean validateItemExist(String itemName) {
		int sourceId=0;
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select oh.* \r\n" + 
				"from \r\n" + 
				"order_history as oh\r\n" + 
				"inner join customer as c\r\n" + 
				"on c.customer_id=oh.customer_id\r\n" + 
				"inner join items as it\r\n" + 
				"on it.item_name_english=oh.veg_name_english\r\n" + 
				"where\r\n" + 
				"oh.rev_status='TBC' and oh.veg_name_english='"+itemName+"'");
		if(query.list().size()>0) {
		return true;
		}
		else {
			return false;
		}
	}

	@Override
	@Transactional
	public ItemsTO getItemName(int itemId) {
		session = this.sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(ItemsTO.class).add(Restrictions.eq("itemId", itemId));
		return (ItemsTO) criteria.uniqueResult();
	}

	@Override
	@Transactional
	public boolean custExist(int customerId) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select om.* from customer as c\r\n" + 
				"inner join order_master as om\r\n" + 
				"on c.customer_id=om.customer_id\r\n" + 
				"where\r\n" + 
				"om.rev_status='TBC' and om.customer_id="+customerId);
		if(query.list().size()>0) {
			return true;
			}
			else {
				return false;
			}
	}

	@Override
	@Transactional
	public boolean validateAddItems(String itemNameEnglish, String itemNameOdiya) {
		session = this.sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(ItemsTO.class).add(Restrictions.eq("itemNameEnglish", itemNameEnglish)).add(Restrictions.eq("itemNameOdiya", itemNameOdiya));
		if(criteria.list().size()>0) {
			return false;
		}else {
			return true;
		}
		
	}

	@Override
	@Transactional
	public List<OrderRevisionMappingTO> revisionMappingHistory(int orderId) {
		session = this.sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(OrderRevisionMappingTO.class).add(Restrictions.eq("orderIdOld", orderId));
		return criteria.list();
	}

	@Override
	@Transactional
	public List<OrderMaster> getOMList() {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("Select * from order_master where STR_TO_DATE(delivery_date, '%d/%m/%Y') >= date_format(curdate(), '%Y/%m/%d') order by delivery_date asc");
		query.setCacheable(true);
		query.addEntity(OrderMaster.class);
		return query.list();
	}

	@Override
	@Transactional
	public Customer getCustomerAddress(int customerId) {
		session = this.sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Customer.class).add(Restrictions.eq("customerId", customerId));
		return (Customer) criteria.uniqueResult();
	}

	@Override
	@Transactional
	public CustomerDeliveryAddress updateDeliveryAddress(CustomerDeliveryAddress custDelAddress) {
		String line1=custDelAddress.getLine1();
		String line2=custDelAddress.getLine2();
		String line3=custDelAddress.getLine3();
		String city=custDelAddress.getCity();
		 String dist=custDelAddress.getDistrict();
		 String state=custDelAddress.getState();
		 String country=custDelAddress.getCountry();
		 String pin=custDelAddress.getPincode();
		 int addressid=custDelAddress.getAddressId();
		 
		 SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("update customer_delivery_address\r\n" + 
		 		"set \r\n" + 
		 		" line1='"+line1+"',\r\n" + 
		 		" line2='"+line2+"',\r\n" + 
		 		" line3='"+line3+"',\r\n" + 
		 		" city='"+city+"',\r\n" + 
		 		" district='"+dist+"',\r\n" + 
		 		" state='"+state+"',\r\n" + 
		 		" country='"+country+"',\r\n" + 
		 		" pincode='"+pin+"'\r\n" + 
		 		" where address_id="+addressid);
		 int updateStatus=query.executeUpdate();
		 SQLQuery queryaddress = sessionFactory.getCurrentSession().createSQLQuery("select * from customer_delivery_address where address_id="+addressid);
		 queryaddress.setCacheable(true);
		 queryaddress.addEntity(CustomerDeliveryAddress.class);
		return (CustomerDeliveryAddress) queryaddress.uniqueResult();
	}

	@Override
	@Transactional
	public List<OrderMaster> fetchOrderMasterWithId(int orderId) {
		session = this.sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(OrderMaster.class).add(Restrictions.eq("orderId", orderId));
		return criteria.list();
	}

	@Override
	@Transactional
	public Customer updateCustInfo(Customer customer) {
		session = this.sessionFactory.getCurrentSession();
		session.saveOrUpdate(customer);
		return customer;
	}
	
	
	/*@Override
	@Transactional
	public List<ItemsTO> fetchprefeItems(int customerId) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("CALL pref_items("+customerId+")");
		query.addEntity(ItemsTO.class);
		return query.list();
	}

	@Override
	@Transactional
	public List<ItemsTO> fetchrestItems(int customerId) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("CALL rest_items("+customerId+")");
		query.addEntity(ItemsTO.class);
		return query.list();
	}*/
	
	

}
