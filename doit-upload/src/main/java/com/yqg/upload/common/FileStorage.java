package com.yqg.upload.common;


/**
 * FileStorage??
 * 
 * @author ????
 *
 */
public class FileStorage {
	private static final long serialVersionUID = 1L;
	/**??*/
	public static final String tableName = "FILE_STORAGE";
	/**???*/
	private String  fileName = "";
	/**??? ????????*/
	public static final String FIELD_FILE_NAME = "fileName";
	/**????*/
	private String  fileType = "";
	/**???? ????????*/
	public static final String FIELD_FILE_TYPE = "fileType";
	/**????*/
	private String  fileSize = "";
	/**???? ????????*/
	public static final String FIELD_FILE_SIZE = "fileSize";
	/**??????*/
	private String  fileUrl = "";
	/**?????? ????????*/
	public static final String FIELD_FILE_URL = "fileUrl";
	/**0:???,1:???,2:???*/
	private int  status =0;
	/**0:???,1:???,2:??? ????????*/
	public static final String FIELD_STATUS = "status";
	/**????json?*/
	private String  params = "";
	/**????json? ????????*/
	public static final String FIELD_PARAMS = "params";
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
}
