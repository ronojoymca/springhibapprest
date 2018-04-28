package mycom.orderapp.utilities;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.simple.JSONObject;

import mycom.orderapp.DTO.CustomerStatusDto;

public class TimeDifferenceCalculator {
	
	public boolean timeDiffCalculator(String orTime, String delTime) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

			DateFormat formatter = new SimpleDateFormat("HH:mm");
			DateFormat formatter2 = new SimpleDateFormat("HH:mm:ss");

			java.sql.Time valOrderTime = new java.sql.Time(formatter.parse(orTime).getTime());
			java.sql.Time valDelivTime = new java.sql.Time(formatter.parse(delTime).getTime());

			String strODate = formatter2.format(valOrderTime);
			String strDDate = formatter2.format(valDelivTime);
			Date date1 = format.parse(strODate);
			Date date2 = format.parse(strDDate);
			long difference = date2.getTime() - date1.getTime();
			long result = difference / 1000 /* take out milliseconds */
					/ 60 /* convert to minutes */;

			if (result >= 240) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return false;
	}


	public boolean validateDeliveryBasedOnBeforeTwelveAfternoon(String reqOrderTime) {
		try {

			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

			DateFormat formatter = new SimpleDateFormat("HH:mm");
			DateFormat formatter2 = new SimpleDateFormat("HH:mm:ss");

			java.sql.Time valOrderTime = new java.sql.Time(formatter.parse(reqOrderTime).getTime());
			java.sql.Time valTwelve = new java.sql.Time(formatter.parse("12:00").getTime());

			String strODate = formatter2.format(valOrderTime);
			String strDDate = formatter2.format(valTwelve);
			Date date1 = format.parse(strODate);
			Date date2 = format.parse(strDDate);
			long difference = date2.getTime() - date1.getTime();
			long result = difference / 1000 /* take out milliseconds */
					/ 60 /* convert to minutes */;

			if (result > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return false;
	}


	public JSONObject validateOrderPoss(String reqOrderDate, String reqOrderTime, String reqDelivType,
			String reqDelivTime, String reqDelivDate) {
		JSONObject object = new JSONObject();
		try {
			
			CustomerStatusDto status = new CustomerStatusDto();
			// request variables-Start
			String orderDate = reqOrderDate;
			String deliveryType = reqDelivType;
			
			
			String nextDay = orderDate;  // Start date
			//System.out.println("Old Date:"+nextDay);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Calendar c = Calendar.getInstance();
			c.setTime(sdf.parse(nextDay));
			c.add(Calendar.DATE, 1);  // number of days to add
			nextDay = sdf.format(c.getTime());  // dt is now the new date
			//System.out.println("New Date:"+nextDay);
			// request variables-End

			// DateTimeString Formatter-Start
			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
			DateFormat formatter = new SimpleDateFormat("HH:mm");
			DateFormat formatter2 = new SimpleDateFormat("HH:mm:ss");
			// DateTimeString Formatter-End

			// Time for DateTimeString Formatter-Start
			java.sql.Time valOrderTime = new java.sql.Time(formatter.parse(reqOrderTime).getTime());
			java.sql.Time valDelivTime = new java.sql.Time(formatter.parse(reqDelivTime).getTime());
			java.sql.Time twelvepm = new java.sql.Time(formatter.parse("12:00").getTime());
			java.sql.Time ninepm = new java.sql.Time(formatter.parse("21:01").getTime());
			java.sql.Time twelveam = new java.sql.Time(formatter.parse("00:00").getTime());
			java.sql.Time fourpm = new java.sql.Time(formatter.parse("15:59").getTime());
			java.sql.Time sixam = new java.sql.Time(formatter.parse("05:59").getTime());
			java.sql.Time sevenam = new java.sql.Time(formatter.parse("06:59").getTime());
			java.sql.Time eleven59pm = new java.sql.Time(formatter.parse("11:59").getTime());
			// Time for DateTimeString Formatter-End

			// String Formatter-Start
			String strODate = formatter2.format(valOrderTime);
			String strDDate = formatter2.format(valDelivTime);
			String twelvepmstr = formatter2.format(twelvepm);
			String ninepmstr = formatter2.format(ninepm);
			String twelveamstr = formatter2.format(twelveam);
			String fourpmstr = formatter2.format(fourpm);
			String sixamstr = formatter2.format(sixam);
			String sevenamstr = formatter2.format(sevenam);
			String eleven59pmstr = formatter2.format(eleven59pm);
			// String Formatter-End

			// Time Formatter-Start
			Date orderTime = format.parse(strODate);
			Date deliveryTime = format.parse(strDDate);
			Date twelvepmTime = format.parse(twelvepmstr);
			Date ninepmTime = format.parse(ninepmstr);
			Date twelveamTime = format.parse(twelveamstr);
			Date fourpmTime = format.parse(fourpmstr);
			Date sixamTime = format.parse(sixamstr);
			Date sevenamTime = format.parse(sevenamstr);
			Date eleven59pmTime = format.parse(eleven59pmstr);
			// Time Formatter-End

			// Logic for XYZ delivery engine - Start
			if (deliveryType.equalsIgnoreCase("sameday")) {
				// case1-if ordertime from 12:01 am to 6 am then delivery time after 12:00 pm to
				// 9:00 pm
				// case2-if ordertime from 6:01 am to 12 pm delivery time starts after 6 hours
				// to 9:00 pm
				// case3-if ordertime from 12:01 pm to 4 pm no delivery sameday-------------ELSE
				// Condition

				if (orderTime.after(twelveamTime) && orderTime.before(sixamTime)) {
					if (deliveryTime.after(eleven59pmTime) && deliveryTime.before(ninepmTime)) {
						status.setStC(true);
						status.setStatusCode("VDP");
						status.setStatusMessage("Delivery possible");
						//return true;
					}else {
						status.setStC(false);
						status.setStatusCode("VDNP");
						status.setStatusMessage("Please set today's delivery time after 12 pm. General delivery hours (7 am to 9 pm)");
						//System.out.println("Sameday delivery time is after 12 pm till 9 pm. General Delivery hours (7 am to 9 pm)");
						//return false;
					}
				} else if (orderTime.after(sixamTime) && orderTime.before(twelvepmTime)) {
					long case2TimeDiff = deliveryTime.getTime() - orderTime.getTime();
					long case2TimeDiffactual = case2TimeDiff / 1000 /* take out milliseconds */
							/ 60 /* convert to minutes */;
					if (deliveryTime.before(ninepmTime) && (case2TimeDiffactual > 359)) {
						status.setStC(true);
						status.setStatusCode("VDP");
						status.setStatusMessage("Delivery possible");
						//return true;
					}else {
						status.setStC(false);
						status.setStatusCode("VDNP");
						status.setStatusMessage("Please set delivery time after 6 hours from now. General delivery hours (7 am to 9 pm). Please contact XYZ Admin for urgent requirement.");
						//System.out.println("Put delivery time after 6 hours. General Delivery hours (7 am to 9 pm)");
						//return false;
					}

				} else {
					status.setStC(false);
					status.setStatusCode("VDNP");
					status.setStatusMessage("Delivery not possible for today. Please change delivery date to tommorrow :"+nextDay+" or later. General delivery hours (7 am to 9 pm). Please contact XYZ Admin for urgent requirement.");
					//System.out.println("Delivery not possible for today. Please change delivery date. General Delivery hours (7 am to 9 pm)");
					//return false;
				}

			} else {
				// if ordertime 12:01 pm to 4 pm delivery next day after 7:00 am onwards to
				// anyday max 9:00 pm
				// if ordertime 4 pm onwards  delivery next day after 12 pm onwards to anyday max 9:00 pm
				if(orderTime.after(twelvepmTime) && orderTime.before(fourpmTime) && reqDelivDate.equalsIgnoreCase(nextDay)) {
					if(deliveryTime.after(sevenamTime) && deliveryTime.before(ninepmTime)) {
						status.setStC(true);
						status.setStatusCode("VDP");
						status.setStatusMessage("Delivery possible");
						//return true;
						}else {
							status.setStC(false);
							status.setStatusCode("VDNP");
							status.setStatusMessage("Please set a valid delivery time. Valid delivery hours (7 am to 9 pm). Please contact XYZ Admin for urgent requirement.");
							//System.out.println("General Delivery hours (7 am to 9 pm)");
							//return false;
						}
				} else if(orderTime.after(fourpmTime) && reqDelivDate.equalsIgnoreCase(nextDay)) {
					if(deliveryTime.after(eleven59pmTime) && deliveryTime.before(ninepmTime)) {
						status.setStC(true);
						status.setStatusCode("VDP");
						status.setStatusMessage("Delivery possible");
					//return true;
					}else if(deliveryTime.after(ninepmTime)){
						status.setStC(false);
						status.setStatusCode("VDNP");
						status.setStatusMessage("General Delivery hours (7 am to 9 pm). Please contact XYZ Admin for urgent requirement.");
						//System.out.println("Next day delivery possible after 12 pm. Please change delivery time. General Delivery hours (7 am to 9 pm)");
						//return false;
					}else {
						status.setStC(false);
						status.setStatusCode("VDNP");
						status.setStatusMessage("Delivery possible tommorrow :"+nextDay+", only after 12 pm. Please change delivery time accordingly. General delivery hours (7 am to 9 pm). Please contact XYZ Admin for urgent requirement.");
					}
				}else if (deliveryTime.after(sevenamTime) && deliveryTime.before(ninepmTime)) {
					status.setStC(true);
					status.setStatusCode("VDP");
					status.setStatusMessage("Delivery possible");
						//return true;
					}else {
						status.setStC(false);
						status.setStatusCode("VDNP");
						status.setStatusMessage("General Delivery hours (7 am to 9 pm). Please contact XYZ Admin for urgent requirement.");
						//System.out.println("General Delivery hours (7 am to 9 pm)");
						//return false;
					}
				}

			object.put("status",status);

		} catch (Exception e) {
			System.out.println(e);
		}
		return object;
		// Logic for XYZ delivery engine - Ends
	}



}
