package com.shopSphere.shared.exception;

public class CartOperationException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8723423616651361691L;

	public CartOperationException(String message) {
        super(message);
    }

    public CartOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}