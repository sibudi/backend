package com.yqg.base.multiDataSource.dbconfig;

public enum DataSourceType {

	read("read", "??"),
	write("write", "??"),
	risk_backup("riskBackup","risk_backup_db");
	
    private String type;
    
    private String name;

    DataSourceType(String type, String name) {
        this.type = type;
        this.name = name;
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
}
