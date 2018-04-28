package mycom.orderapp.DTO;

import java.io.Serializable;

public class CustomerStatusDto implements Serializable {
	private static final long serialVersionUID = 1L;
	private String statusCode;
	private String statusMessage;
	private boolean stC;

	public boolean isStC() {
		return stC;
	}

	public void setStC(boolean stC) {
		this.stC = stC;
	}

	public String getStatusCode() {
		return this.statusCode;
	}

	public void setStatusCode(String registerationResponseOtpVerificationStarted) {
		this.statusCode = registerationResponseOtpVerificationStarted;
	}

	public String getStatusMessage() {
		return this.statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
}
