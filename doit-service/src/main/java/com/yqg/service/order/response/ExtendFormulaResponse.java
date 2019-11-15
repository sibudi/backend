package com.yqg.service.order.response;

import lombok.Data;

import java.util.List;
import java.util.Map;
import com.yqg.service.order.response.ChildFormulaResponse;
/**
 * // New Formula for Extension
 */
@Data
public class ExtendFormulaResponse {


     private String name;
     private String mainFormula;
     private Float granulaNum;
     private List<ChildFormulaResponse> childFormula;
     private List<Map<String,String>> config;
}
