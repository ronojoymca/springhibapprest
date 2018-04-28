package mycom.orderapp.DAOImpl;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mycom.orderapp.DAO.CustomerRegistrationDao;
import mycom.orderapp.controller.CustomerRegistrationController;
import mycom.orderapp.model.Customer;


@Repository
public class CustomerRegistrationDaoImpl implements CustomerRegistrationDao{

	@Autowired
	private SessionFactory sessionFactory;
	Session session = null;
	
	
	@Override
	@Transactional
	public Customer insertNewCustomer(Customer customer) {
		session = this.sessionFactory.getCurrentSession();
		session.saveOrUpdate(customer);
		return customer;
	}


	@Override
	@Transactional
	public boolean validateCustomer(String email) {
		SQLQuery querynew = sessionFactory.getCurrentSession().createSQLQuery("SELECT * FROM orderapp.customer where email='"+email+"'");
		if(querynew.list().isEmpty()) {
			return true;
		}else {
			return false;
		}
		
	}

}
