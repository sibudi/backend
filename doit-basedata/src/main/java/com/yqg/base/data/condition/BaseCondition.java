/*
 * Copyright (c) 2017-2018 , Inc. All Rights Reserved.
 */
package com.yqg.base.data.condition;

import com.yqg.base.data.annotations.Common;
import com.yqg.base.data.annotations.Condition;

/**
* @author Jacob
*
*/
public class BaseCondition {

    @Common
    private Integer _start; //e.g. limit start, pageSize

    @Common
    private Integer _pageSize;

    @Common
    private String _orderBy;//e.g. order by id,name

    @Common
    private String _condition;

    @Condition
    private String _inField;

    @Condition
    private Object[] _inValues;

    @Condition
    private String _likeField;

    @Condition
    private String _likeKeyword;

    public String get_inField() {
        return _inField;
    }

    public void set_inField(String _inField) {
        this._inField = _inField;
    }

    public Object[] get_inValues() {
        return _inValues;
    }

    public void set_inValues(Object[] _inValues) {
        this._inValues = _inValues;
    }

    public String get_likeField() {
        return _likeField;
    }

    public void set_likeField(String _likeField) {
        this._likeField = _likeField;
    }

    public String get_likeKeyword() {
        return _likeKeyword;
    }

    public void set_likeKeyword(String _likeKeyword) {
        this._likeKeyword = _likeKeyword;
    }

    public Integer get_start() {
        return this._start;
    }

    public void set_start(Integer _start) {
        this._start = _start;
    }

    public Integer get_pageSize() {
        return this._pageSize;
    }

    public void set_pageSize(Integer _pageSize) {
        this._pageSize = _pageSize;
    }

    public String get_orderBy() {
        return this._orderBy;
    }

    public void set_orderBy(String _orderBy) {
        this._orderBy = _orderBy;
    }

    public String get_condition() {
        return this._condition;
    }

    public void set_condition(String _condition) {
        this._condition = _condition;
    }
}