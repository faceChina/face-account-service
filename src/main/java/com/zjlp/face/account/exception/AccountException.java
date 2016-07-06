package com.zjlp.face.account.exception;

import com.zjlp.face.util.exception.BaseException;
import com.zjlp.face.util.exception.enums.ExceptionObject;

public class AccountException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8384105704831892478L;

	public AccountException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AccountException(ExceptionObject exObj) {
		super(exObj);
		// TODO Auto-generated constructor stub
	}

	public AccountException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public AccountException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public AccountException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
