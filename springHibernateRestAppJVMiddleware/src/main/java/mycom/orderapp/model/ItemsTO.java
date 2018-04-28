package mycom.orderapp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "items")
public class ItemsTO {
	
	@Id
	@Column(name = "item_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int itemId;

	@Column(name = "item_name_english")
	private String itemNameEnglish;

	@Column(name = "item_name_oriya")
	private String itemNameOdiya;
	
	@Column(name = "item_image")
	private byte[] itemImg;
	
	@Column(name = "item_status")
	private int itemStatus;
	
	public int getItemStatus() {
		return itemStatus;
	}

	public void setItemStatus(int itemStatus) {
		this.itemStatus = itemStatus;
	}

	@Transient
	private String quantity;
	
	@Transient
	private String uom;
	
	@Transient
	private String itemsFetchType;
	
	/*@Transient
	private String comboItemName;

	public String getComboItemName() {
		return comboItemName;
	}

	public void setComboItemName(String comboItemName) {
		this.comboItemName = comboItemName;
	}
*/
	public String getItemsFetchType() {
		return itemsFetchType;
	}

	public void setItemsFetchType(String itemsFetchType) {
		this.itemsFetchType = itemsFetchType;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public byte[] getItemImg() {
		return itemImg;
	}

	public void setItemImg(byte[] itemImg) {
		this.itemImg = itemImg;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public String getItemNameEnglish() {
		return itemNameEnglish;
	}

	public void setItemNameEnglish(String itemNameEnglish) {
		this.itemNameEnglish = itemNameEnglish;
	}

	public String getItemNameOdiya() {
		return itemNameOdiya;
	}

	public void setItemNameOdiya(String itemNameOdiya) {
		this.itemNameOdiya = itemNameOdiya;
	}


}
