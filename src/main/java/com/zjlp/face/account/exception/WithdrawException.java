package com.zjlp.face.account.exception;

import com.zjlp.face.util.exception.BaseException;
import com.zjlp.face.util.exception.enums.ExceptionObject;

/**
 * 提现异常类
 * @ClassName: WithdrawException 
 * @Description: (这里用一句话描述这个类的作用) 
 * @author Administrator
 * @date 2014年9月4日 下午8:15:10
 */
public class WithdrawException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5535989366348640633L;

	public WithdrawException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public WithdrawException(ExceptionObject exObj) {
		super(exObj);
		// TODO Auto-generated constructor stub
	}

	public WithdrawException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public WithdrawException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public WithdrawException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
}
