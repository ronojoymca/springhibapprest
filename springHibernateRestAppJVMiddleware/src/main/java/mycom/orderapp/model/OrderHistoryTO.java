package mycom.orderapp.model;

import java.sql.Time;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "order_history")
public class OrderHistoryTO {
	
	@Id
	@Column(name = "order_history_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int orderHistoryId;

	@Column(name = "order_id")
	private int orderId;

	@Column(name = "order_details_id")
	private int orderDetailsID;
	
	@Column(name = "order_date")
	private String orderDate;
	
	@Column(name = "veg_name_english")
	private String vegNameEng;
	
	@Column(name = "veg_name_odiya")
	private String vegNameOdi;
	
	@Column(name = "uom")
	private String uom;
	
	
	@Column(name = "quantity_ordered")
	private String quantity;
	
	@Column(name = "customer_id")
	private int customerId;
	
	@Column(name = "delivery_date")
	private String deliveryDate;
	
	@Column(name = "delivery_time")
	private Time deliveryTime;
	
	@Column(name = "delivery_type")
	private String deliveryType;
	
	@Column(name = "order_edit_status")
	private int orderEditStatus;
	
	@Column(name = "rev_status")
	private String revStatus;
	
	@Column(name = "order_time")
	private String orderTime;
	
	@Column(name = "free_text")
	private String freeText;
	
	public String getFreeText() {
		return freeText;
	}

	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public String getRevStatus() {
		return revStatus;
	}

	public void setRevStatus(String revStatus) {
		this.revStatus = revStatus;
	}
	
	public int getOrderEditStatus() {
		return orderEditStatus;
	}

	public void setOrderEditStatus(int orderEditStatus) {
		this.orderEditStatus = orderEditStatus;
	}

	public String getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}

	public Time getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(Time deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public String getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public int getOrderHistoryId() {
		return orderHistoryId;
	}

	public void setOrderHistoryId(int orderHistoryId) {
		this.orderHistoryId = orderHistoryId;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getOrderDetailsID() {
		return orderDetailsID;
	}

	public void setOrderDetailsID(int orderDetailsID) {
		this.orderDetailsID = orderDetailsID;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getVegNameEng() {
		return vegNameEng;
	}

	public void setVegNameEng(String vegNameEng) {
		this.vegNameEng = vegNameEng;
	}

	public String getVegNameOdi() {
		return vegNameOdi;
	}

	public void setVegNameOdi(String vegNameOdi) {
		this.vegNameOdi = vegNameOdi;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	
	
	

}
