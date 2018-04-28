package mycom.orderapp.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "customer")
public class Customer {

	@Id
	@Column(name = "customer_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int customerId;

	@Column(name = "name")
	private String name;

	@Column(name = "mobile_number")
	private String mobileNumber;

	@Column(name = "email")
	private String email;
	
	@Column(name = "sec_email_one")
	private String secEmailOne;
	
	@Column(name = "sec_email_two")
	private String secEmailTwo;
	
	@Column(name = "role")
	private int role;
	
	/*@Column(name = "address")
	private int address*/;
	
	@Column(name = "password")
	private String password;

	@Column(name = "customer_active_status")
	private int customerActiveStatus;

	@Column(name = "created_date_time")
	private String createdDate;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "updated_date_time")
	private String updatedDate;

	@Column(name = "updated_by")
	private String updatedBy;
	
	@Column(name = "device_id")
	private String deviceId;
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "address")
	private CustomerAddress address;
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "deliveryaddress")
	private CustomerDeliveryAddress deladdress;
	
		
/*	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}*/

	public CustomerDeliveryAddress getDeladdress() {
		return deladdress;
	}

	public void setDeladdress(CustomerDeliveryAddress deladdress) {
		this.deladdress = deladdress;
	}

	public CustomerAddress getAddress() {
		return address;
	}

	public void setAddress(CustomerAddress address) {
		this.address = address;
	}
	
	public String getSecEmailOne() {
		return secEmailOne;
	}

	public void setSecEmailOne(String secEmailOne) {
		this.secEmailOne = secEmailOne;
	}

	public String getSecEmailTwo() {
		return secEmailTwo;
	}

	public void setSecEmailTwo(String secEmailTwo) {
		this.secEmailTwo = secEmailTwo;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}



	public int getCustomerActiveStatus() {
		return customerActiveStatus;
	}

	public void setCustomerActiveStatus(int customerActiveStatus) {
		this.customerActiveStatus = customerActiveStatus;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	

	


}
