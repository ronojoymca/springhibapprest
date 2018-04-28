package mycom.orderapp.utilities;

import static mycom.orderapp.constants.MailConstant.*;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;


public class PlaceOrderMail implements Runnable{
	
	
	private Locale locale;
	
	List<String> email = null;
	String cc1="";
	String cc2="";
	String cc3="";
	int revNo=0;
	String custName="";
	String delivType="";
	String delivDate="";
	String delivTime="";
	//String cc4="";
	/*String businessName = "";
	String getToken = "";
	String roleBean = "";
	String productName="";
	double Quantity=0.0f;
	double price=0.0f;*/

	public PlaceOrderMail() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		try {
			System.out.println("inside thread");
			String status=this.publishSendingToUser();
		
		} catch (Exception e) {
			System.out.println(status);
			e.printStackTrace();
		}
	}

	public void setParameters(List<String> to, String cc1,String cc2,String cc3,int revNo,String custName,String delivType,String delivDate,String delivTime) {
		this.email = to;
		this.cc1=cc1;
		this.cc2=cc2;
		this.cc3=cc3;
		this.revNo=revNo;
		this.custName=custName;
		this.delivType=delivType;
		this.delivDate=delivDate;
		this.delivTime=delivTime;
		//this.cc4=cc4;
		/*this.businessName=name;
		this.productName=productName;
		this.Quantity=Quantity;
		this.price=price;*/

	}
    public static String status;
    
	private static String subject = "For XYZ by "; 
	private static String note = "Note : Please do not share the service availed details outside XYZ, under confidentiality compliance.";
	private static String text = "";
	public Properties properties = System.getProperties();
	public Session session = Session.getDefaultInstance(properties);
	Transport transport = null;
	public MimeMessage message = new MimeMessage(session);

	public String publishSendingToUser() {
		text = "Dear Admin (XYZ) ,\n\n New Order Placed by "+custName+" ! \n\n Please open the attachment to view the order "
				+ "\n\n Delivery Date: "+delivDate+" \n\n Delivery Time: "+delivTime+" Hrs.";
		properties.put("mail.smtp.host", emailServiceProvider);
		properties.put("mail.smtp.user", emailServiceUser);
		properties.put("mail.smtp.password", emailServicePassword);
		properties.put("mail.smtp.port", emailServicePort);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");

		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.socketFactory.fallback", "false");
		properties.put("mail.smtp.socketFactory.port", emailServicePort);
		properties.put("mail.debug", "true");
		properties.put("mail.store.protocol", "pop3");
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.debug.auth", "true");
		properties.put("mail.pop3.socketFactory.fallback", "false");
		
		
		try {
			message.setFrom(new InternetAddress(emailServiceUser));
			if(!email.isEmpty()) {
				for(String to:email) {
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
				}
			}
			//message.addRecipient(Message.RecipientType.TO, new InternetAddress(cc));
			message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc1));
			
			if(cc2!=null && !cc2.isEmpty()) {
			message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc2));
			}
			if(cc3!=null && !cc3.isEmpty()) {
			message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc3));
			}
			if(revNo>0) {
			message.setSubject("[ REVISED ORDER "+revNo+" ] [ DELIVERY DATE AND TIME : "+delivDate+" "+delivTime+" Hrs.] - "+subject+custName);
			}else if(delivType.equalsIgnoreCase("SAMEDAY")) {
				message.setSubject("[ TODAY'S DELIVERY ORDER ] [ DELIVERY TIME : "+delivTime+" Hrs.] - "+subject+custName);

			}else {
				message.setSubject("[ GENERAL ORDER ] [ DELIVERY DATE AND TIME : "+delivDate+" "+delivTime+" Hrs.] - "+subject+custName);
			}
			message.setText(text);
			
			//3) create MimeBodyPart object and set your message text     
		    BodyPart messageBodyPart1 = new MimeBodyPart();  
		    messageBodyPart1.setText(text);  
		    
		    //4) create new MimeBodyPart object and set DataHandler object to this object      
		    MimeBodyPart messageBodyPart2 = new MimeBodyPart();  
		  
		    String filename = "/var/vegkart/OrderToXYZ_"+custName+".xlsx";//change accordingly  
		    DataSource source = new FileDataSource(filename);  
		    messageBodyPart2.setDataHandler(new DataHandler(source));  
		    messageBodyPart2.setFileName(filename);  
		     
		     
		    //5) create Multipart object and add MimeBodyPart objects to this object      
		    Multipart multipart = new MimeMultipart();  
		    multipart.addBodyPart(messageBodyPart1);  
		    multipart.addBodyPart(messageBodyPart2);  
		  
		    //6) set the multiplart object to the message object  
		    message.setContent(multipart );  
			
			transport = session.getTransport("smtp");
			transport.connect(emailServiceProvider, emailServiceUser, emailServicePassword);
			transport.sendMessage(message, message.getAllRecipients());
			status = "success";
			File file = new File(filename);

            if(file.delete()){
                System.out.println("Deleted file: " + file.getName());
            }else{
                System.out.println("Delete failed on file :" + file.getName());
            }

		} catch (AddressException ae) {
			status = "failure";
			ae.printStackTrace();
		} catch (MessagingException me) {
			status = "failure";
			me.printStackTrace();
		} catch (Exception e) {
			status = "failure";
			e.printStackTrace();
		} finally {
			try {
				transport.close();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return status;
	}

}
