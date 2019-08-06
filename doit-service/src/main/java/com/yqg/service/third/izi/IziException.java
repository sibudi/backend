package com.yqg.service.third.izi;

public class IziException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3018541229000155386L;
	private String errorMsg;
    private int errorCode;

    public IziException(int code, String msg) {
        errorCode = code;
        errorMsg = msg;
    }

    public IziException(int code, Throwable e) {
        errorCode = code;
        errorMsg = e.getMessage();
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
