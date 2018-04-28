package mycom.orderapp.DTO;

import java.util.List;
import java.util.Map;

public class ModelForExcelDTO {
	
	List<Map<String, Object>> orderList;

	public List<Map<String, Object>> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<Map<String, Object>> orderList) {
		this.orderList = orderList;
	}

}
