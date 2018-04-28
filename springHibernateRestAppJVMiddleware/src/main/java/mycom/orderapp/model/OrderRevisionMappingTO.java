package mycom.orderapp.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "order_revision_mapping")
public class OrderRevisionMappingTO {
	
	@Id
	@Column(name = "order_revision_mapping_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int customerId;

	@Column(name = "order_id_old")
	private int orderIdOld;

	@Column(name = "order_id_new")
	private int orderIdNew;
	

	@Column(name = "revised_by")
	private String revisedBy;

	@Column(name = "revision_date")
	private Date revisionDateandTime;

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public int getOrderIdOld() {
		return orderIdOld;
	}

	public void setOrderIdOld(int orderIdOld) {
		this.orderIdOld = orderIdOld;
	}

	public int getOrderIdNew() {
		return orderIdNew;
	}

	public void setOrderIdNew(int orderIdNew) {
		this.orderIdNew = orderIdNew;
	}

	public String getRevisedBy() {
		return revisedBy;
	}

	public void setRevisedBy(String revisedBy) {
		this.revisedBy = revisedBy;
	}

	public Date getRevisionDateandTime() {
		return revisionDateandTime;
	}

	public void setRevisionDateandTime(Date revisionDateandTime) {
		this.revisionDateandTime = revisionDateandTime;
	}


}
