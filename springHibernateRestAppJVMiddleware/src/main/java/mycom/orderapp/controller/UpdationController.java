package mycom.orderapp.controller;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import mycom.orderapp.service.CustomerHomeService;


@Controller
@EnableScheduling
@EnableAsync
@RequestMapping("/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UpdationController {

	@Autowired
	CustomerHomeService customerHomeService;

@Autowired
HttpSession httpSession;

@Autowired
MessageSource messageSource;

private Locale locale;



	@Scheduled(cron = "0 59 23 * * *", zone = "IST")
	public void run() throws InterruptedException {
		System.out.println("Cron scheduler is running at " + new Date());

		

		customerHomeService.updateRevStatusForAll();

		Thread.sleep(3000);
	}	

	

}
