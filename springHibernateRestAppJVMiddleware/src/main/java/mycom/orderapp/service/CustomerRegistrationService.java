package mycom.orderapp.service;

import org.json.simple.JSONObject;

import mycom.orderapp.DTO.CustomerRegistrationDTO;


public interface CustomerRegistrationService {

	JSONObject saveRegistrationDetails(CustomerRegistrationDTO registrationDto);

}
