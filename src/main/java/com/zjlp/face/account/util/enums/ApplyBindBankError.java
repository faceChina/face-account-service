package com.zjlp.face.account.util.enums;


public enum ApplyBindBankError {
	/** 1100  本平台暂不支持,该银行卡或卡号输入有误，请更换银行卡或重新输入正确卡号.*/
	APPLY_1100("1100","本平台暂不支持,该银行卡或卡号输入有误，请更换银行卡或重新输入正确卡号"),
	/** 1101  该卡已挂失，请更换银行卡签约*/
	APPLY_1101("1101","该卡已挂失，请更换银行卡签约"),
	/** 1102  */
	APPLY_1102("1102",""),
	/** 1103  您的卡已经过期或您输入的有效期不正确，请核实您的卡或填写正确的有效期*/
	APPLY_1103("1103","您的卡已经过期或您输入的有效期不正确，请核实您的卡或填写正确的有效期"),
	/** 1105  */
	APPLY_1105("1105",""),
	/** 1107  您的银行卡暂未开通在线支付业务，请您先开通在线支付业务或更换银行卡*/
	APPLY_1107("1107","您的银行卡暂未开通在线支付业务，请您先开通在线支付业务或更换银行卡"),
	/** 1108 您输入的证件号、姓名或手机号码错误，请您核对信息，输入正确信息，谢谢  */
	APPLY_1108("1108","您输入的证件号、姓名或手机号码错误，请您核对信息，输入正确信息，谢谢"),
	/** 1109 您的卡号和您的身份证号信息不符，请您核对正确信息 */
	APPLY_1109("1109","您的卡已经过期或您输入的有效期不正确，请核实您的卡或填写正确的有效期"),
	/** 1110  您的卡存在异常情况无法签约，请您核实正确信息*/
	APPLY_1110("1110","您的卡存在异常情况无法签约，请您核实正确信息"),
	/** 1112  您输入的证件号有误，请您核对正确信息 */
	APPLY_1112("1112","您输入的证件号有误，请您核对正确信息"),
	/** 1113  您的卡和持卡人姓名不符，请核实正确信息  */
	APPLY_1113("1113","您的卡和持卡人姓名不符，请核实正确信息"),
	/** 1114  您的手机号输入错误，请重新输入正确手机号  */
	APPLY_1114("1114","您的手机号输入错误，请重新输入正确手机号"),
	/** 1115  您的卡开户时未预留手机号码，无法收取验证码，请重新更换银行卡  */
	APPLY_1115("1115","您的卡开户时未预留手机号码，无法收取验证码，请重新更换银行卡"),
	
	/** 1200 您的银行卡暂不支持此平台，请您重新选择其他银行卡进行支付/签约  */
	APPLY_1200("1200","您的银行卡暂不支持此平台，请您重新选择其他银行卡进行支付/签约"),
	
	/** 1900 验证码输入错误，校验不一致无法签约  */
	APPLY_1900("1900","验证码输入错误，校验不一致无法签约"),
	/** 1901 短信验证码已失效，请重新获取 */
	APPLY_1901("1901","短信验证码已失效，请重新获取"),
	
	/** 2004  */
	APPLY_2004("2004",""),
	
	
	/** 3003  */
	APPLY_3003("3003",""),
	/** 3004  */
	APPLY_3004("3004",""),
	/** 3005 平台暂时不支持该银行支付，请更换银行卡，谢谢  */
	APPLY_3005("3005","平台暂时不支持该银行支付，请更换银行卡，谢谢"),
	/** 3006  您的银行卡号输入错误，请重新输入正确的信息*/
	APPLY_3006("3006","您的银行卡号输入错误，请重新输入正确的信息"),
	
	/** 4000 */
	APPLY_4000("4000",""),
	/**卡bin查询失败*/
	APPLY_5001("5001","您的银行卡号输入错误，请重新输入正确的信息"),
	
	/** 9700  您输入的短信验证码错误*/
	APPLY_9700("9700","您输入的短信验证码错误"),
	/** 9701 */
	APPLY_9701("9701",""),
	/** 9702 */
	APPLY_9702("9702",""),
	/** 9703 */
	APPLY_9703("9703",""),
	/** 9704 */
	APPLY_9704("9704",""),
	
	/** 9903 */
	APPLY_9903("9903",""),
	/** 9910 同一银行卡绑定次数过多，无法签约*/
	APPLY_9910("9910","同一银行卡绑定次数过多，无法签约"),
	/** 9912 该卡暂时不支持，请更换银行卡*/
	APPLY_9912("9912","该卡暂时不支持，请更换银行卡"),
	/** 9913 您的银行卡暂不支持此平台，请您重新选择其他银行卡进行支付/签约*/
	APPLY_9913("9913","该卡已经签约成功，不能重复签约"),
	;
	
	private ApplyBindBankError(String errorCode, String errorMsg) {
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	private String errorCode;
	
	private String errorMsg;
	
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public static String getMsgByCode(String errorCode){
		if (null == errorCode || "".equals(errorCode)) {
			return null;
		}
		String message = null;
		for (ApplyBindBankError applyBindBankError : ApplyBindBankError.values()) {
			if (errorCode.equals(applyBindBankError.getErrorCode())) {
				message = applyBindBankError.getErrorMsg();
				break;
			}
		}
		return message;
	}
	
	
	public static void main(String[] args) {
		
		System.out.println(getMsgByCode("3005"));
	}
	
}
