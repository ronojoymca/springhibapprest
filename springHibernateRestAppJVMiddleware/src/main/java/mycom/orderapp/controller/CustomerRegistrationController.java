package mycom.orderapp.controller;

/*import org.apache.logging.log4j.Logger;*/
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import mycom.orderapp.DTO.CustomerRegistrationDTO;
import mycom.orderapp.DTO.CustomerStatusDto;
import mycom.orderapp.service.CustomerRegistrationService;


@RestController
public class CustomerRegistrationController {
	
	
	@Autowired
	CustomerRegistrationService registrationservice;
	
	@RequestMapping(value = "/signUp", headers = "Accept=application/json", method = RequestMethod.POST)
	public @ResponseBody JSONObject getQuestions(@RequestBody CustomerRegistrationDTO registrationDto) {
		JSONObject obj = new JSONObject();
		try {
			CustomerStatusDto status = new CustomerStatusDto();
		
			obj = registrationservice.saveRegistrationDetails(registrationDto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

}
