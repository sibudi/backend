package com.yqg.common.excel;

/**
 * excel ???
 * Created by Jacob on 2017/4/24.
 */
public class ExcelData {
    private int row;//?
    private int column;//?
    private ExcelDataType dataType;//???
    private Object value;//?

    public ExcelData(int row, int column, ExcelDataType dataType, Object value) {
        this.row = row;
        this.column = column;
        this.dataType = dataType;
        this.value = value;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public ExcelDataType getDataType() {
        return dataType;
    }

    public void setDataType(ExcelDataType dataType) {
        this.dataType = dataType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
