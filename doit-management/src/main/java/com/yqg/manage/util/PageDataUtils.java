package com.yqg.manage.util;

import com.github.pagehelper.PageInfo;
import com.yqg.common.models.PageData;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 *
 ****/
public class PageDataUtils {
   public static PageData mapPageInfoToPageData(PageInfo pageInfo){
       PageData pageData = new PageData();
       pageData.setData(pageInfo.getList());
       pageData.setRecordsTotal((int)pageInfo.getTotal());
       pageData.setPageNo(pageInfo.getPageNum());
       pageData.setPageSize(pageInfo.getPageSize());
       return pageData;
   }
}
