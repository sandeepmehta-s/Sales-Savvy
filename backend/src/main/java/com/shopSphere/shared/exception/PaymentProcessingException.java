package com.shopSphere.shared.exception;

public class PaymentProcessingException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4465881656180865043L;

	public PaymentProcessingException(String message) {
        super(message);
    }

    public PaymentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}