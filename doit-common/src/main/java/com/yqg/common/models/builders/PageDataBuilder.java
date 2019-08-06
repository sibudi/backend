package com.yqg.common.models.builders;


import com.yqg.common.models.PageData;

/**
 * @author by alan on 17-5-9.
 */
public class PageDataBuilder<T> {
    private int pageNo;

    private int pageSize;

    private int recordsTotal;

    private T data;

    public PageDataBuilder() {

    }

    public PageDataBuilder<T> pageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public PageDataBuilder<T> pageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public PageDataBuilder<T> recordsTotal(int recordsTotal) {
        this.recordsTotal = recordsTotal;
        return this;
    }

    public PageDataBuilder<T> data(T data) {
        this.data = data;
        return this;
    }

    public PageData<T> build() {
        PageData<T> result = new PageData<>();
        result.setPageNo(this.pageNo);
        result.setPageSize(this.pageSize);
        result.setData(this.data);
        result.setRecordsTotal(this.recordsTotal);
        return result;
    }

    public <T> PageData<T> success(T data) {
        return new PageDataBuilder<T>().pageNo(pageNo).pageSize(pageSize).recordsTotal(recordsTotal).data(data).build();
    }

    public <T> PageData<T> success() {
        return new PageDataBuilder<T>().pageNo(pageNo).pageSize(pageSize).recordsTotal(recordsTotal).build();
    }

}
