package com.yqg.service.check;

import com.yqg.common.utils.StringUtils;
import com.yqg.order.entity.OrdPayCheck;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: caomiaoke
 * Date: 08/02/2018
 * Time: 11:43 AM
 */
public class ExcelOperator {

    public static HSSFWorkbook insertDataToExcel(List<OrdPayCheck> ordPayCheckList){
        HSSFWorkbook workbook = createExcel();
        HSSFSheet sheet = workbook.getSheet("orderPayCheck");

        //设置单元格格式居中
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        HSSFRow hssfRow;
        HSSFCell hssfCell;
        OrdPayCheck ordPayCheck;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < ordPayCheckList.size(); i++){
            hssfRow = sheet.createRow(i + 1);
            ordPayCheck = ordPayCheckList.get(i);

            // 订单号
            HSSFCell headCell = hssfRow.createCell(0);
            headCell.setCellValue(ordPayCheck.getOrderNo());
            headCell.setCellStyle(cellStyle);

            //放款状态
            headCell = hssfRow.createCell(1);
            headCell.setCellValue(ordPayCheck.getStatus());
            headCell.setCellStyle(cellStyle);
            // 第三方放款状态
            headCell = hssfRow.createCell(2);
            headCell.setCellValue(ordPayCheck.getStatusThird());
            headCell.setCellStyle(cellStyle);

            //放款金额
            headCell = hssfRow.createCell(3);
            headCell.setCellValue(ordPayCheck.getAmountApply().toString());
            headCell.setCellStyle(cellStyle);

            //第三方放款金额
            headCell = hssfRow.createCell(4);
            headCell.setCellValue(ordPayCheck.getAmountApplyThird().toString());
            headCell.setCellStyle(cellStyle);

            //放款时间
            headCell = hssfRow.createCell(5);
            if(ordPayCheck.getLendingTime()!=null){
                headCell.setCellValue(simpleDateFormat.format(ordPayCheck.getLendingTime()));
            }
            headCell.setCellStyle(cellStyle);

            //第三方放款时间
            headCell = hssfRow.createCell(6);
            if(ordPayCheck.getLendingTimeThird()!=null){
                headCell.setCellValue(simpleDateFormat.format(ordPayCheck.getLendingTimeThird()));
            }
            headCell.setCellStyle(cellStyle);

            //流水号
            headCell = hssfRow.createCell(7);
            headCell.setCellValue(ordPayCheck.getDisbursementId());
            headCell.setCellStyle(cellStyle);

            //放款渠道
            headCell = hssfRow.createCell(8);
            headCell.setCellValue(ordPayCheck.getChannel());
            headCell.setCellStyle(cellStyle);

            //放款错误码
            headCell = hssfRow.createCell(9);
            headCell.setCellValue(ordPayCheck.getErrorCode());
            headCell.setCellStyle(cellStyle);

            //放款失败原因
            headCell = hssfRow.createCell(10);
            headCell.setCellValue(ordPayCheck.getErrorMsg());
            headCell.setCellStyle(cellStyle);

        }

        return workbook;
    }


    private static HSSFWorkbook createExcel(){

        //创建一个Excel文件
        HSSFWorkbook workbook = new HSSFWorkbook();
        //创建一个工作表
        HSSFSheet sheet = workbook.createSheet("orderPayCheck");
        HSSFRow hssfRow = sheet.createRow(0);
        //设置单元格格式居中
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        //添加表头信息
        // 订单号
        HSSFCell headCell = hssfRow.createCell(0);
        headCell.setCellValue("订单编号");
        headCell.setCellStyle(cellStyle);

        //放款状态
        headCell = hssfRow.createCell(1);
        headCell.setCellValue("订单放款状态");
        headCell.setCellStyle(cellStyle);
        // 第三方放款状态
        headCell = hssfRow.createCell(2);
        headCell.setCellValue("订单放款状态(三方)");
        headCell.setCellStyle(cellStyle);

        //放款金额
        headCell = hssfRow.createCell(3);
        headCell.setCellValue("放款额度");
        headCell.setCellStyle(cellStyle);

        //第三方放款金额
        headCell = hssfRow.createCell(4);
        headCell.setCellValue("放款额度(三方)");
        headCell.setCellStyle(cellStyle);

        //放款时间
        headCell = hssfRow.createCell(5);
        headCell.setCellValue("放款时间");
        headCell.setCellStyle(cellStyle);

        //第三方放款时间
        headCell = hssfRow.createCell(6);
        headCell.setCellValue("放款时间(三方)");
        headCell.setCellStyle(cellStyle);


        //流水号
        headCell = hssfRow.createCell(7);
        headCell.setCellValue("放款流水号");
        headCell.setCellStyle(cellStyle);

        //放款渠道
        headCell = hssfRow.createCell(8);
        headCell.setCellValue("放款渠道");
        headCell.setCellStyle(cellStyle);

        //放款错误码
        headCell = hssfRow.createCell(9);
        headCell.setCellValue("错误码");
        headCell.setCellStyle(cellStyle);

        //放款失败原因
        headCell = hssfRow.createCell(10);
        headCell.setCellValue("错误信息");
        headCell.setCellStyle(cellStyle);

        return workbook;
    }

}
