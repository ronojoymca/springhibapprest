package mycom.orderapp.model;



import java.util.Date;
import java.util.List;

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
@Table(name = "order_master")
public class OrderMaster {
	
	@Id
	@Column(name = "order_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int orderId;

	@Column(name = "customer_id")
	private int customerId;

	@Column(name = "order_date")
	private String orderDate;
	
	@Column(name = "order_time")
	private String orderTime;

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	@Column(name = "order_by")
	private String orderBy;
	
	@Column(name = "delivery_date")
	private String deliveryDate;
	
	@Column(name = "delivery_time")
	private String delivery_time;
	
	@Column(name = "delivery_type")
	private String deliveryType;
	
	@Column(name = "order_edit_status")
	private int orderEditStatus;
	
	@Column(name = "rev_status")
	private String revStatus;
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "deliveryaddress")
	private CustomerDeliveryAddress deladdress;
	
	public CustomerDeliveryAddress getDeladdress() {
		return deladdress;
	}

	public void setDeladdress(CustomerDeliveryAddress deladdress) {
		this.deladdress = deladdress;
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

	public String getDelivery_time() {
		return delivery_time;
	}

	public void setDelivery_time(String delivery_time) {
		this.delivery_time = delivery_time;
	}

	@Column(name = "free_text")
	private String freeText;
	
	
	public String getFreeText() {
		return freeText;
	}

	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}

	public String getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	@Transient
	private List<ItemsTO> itemsTO;

	public List<ItemsTO> getItemsTO() {
		return itemsTO;
	}

	public void setItemsTO(List<ItemsTO> itemsTO) {
		this.itemsTO = itemsTO;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	
	

}
