package mycom.orderapp.utilities;

import static mycom.orderapp.constants.MailConstant.*;

import java.util.List;
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


public class CollateOrderMail implements Runnable{
	
	List<String> email = null;
	String cc="";
	/*String businessName = "";
	String getToken = "";
	String roleBean = "";
	String productName="";
	double Quantity=0.0f;
	double price=0.0f;*/

	public CollateOrderMail() {
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

	public void setParameters(List<String> to) {
		this.email = to;
		this.cc=cc;
		/*this.businessName=name;
		this.productName=productName;
		this.Quantity=Quantity;
		this.price=price;*/

	}
    public static String status;
	private static String subject = "XYZ Collated Order for Delivery"; 
	private static String note = "Note : Please do not share the service availed details outside XYZ, under confidentiality compliance.";
	private static String text = "";
	public Properties properties = System.getProperties();
	public Session session = Session.getDefaultInstance(properties);
	Transport transport = null;
	public MimeMessage message = new MimeMessage(session);

	public String publishSendingToUser() {
		text = "Dear Admin (XYZ)" + email + ",\n\n This order list is a collated order list for delivery. \n\n Please open the attachment to view the order list.";
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
			message.setSubject(subject);
			message.setText(text);
			
			//3) create MimeBodyPart object and set your message text     
		    BodyPart messageBodyPart1 = new MimeBodyPart();  
		    messageBodyPart1.setText(text);  
		    
		    //4) create new MimeBodyPart object and set DataHandler object to this object      
		    MimeBodyPart messageBodyPart2 = new MimeBodyPart();  
		  
		    String filename = "/var/vegkart/CollatedOrder.xlsx";//change accordingly  
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
