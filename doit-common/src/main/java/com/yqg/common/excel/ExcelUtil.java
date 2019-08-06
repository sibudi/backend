package com.yqg.common.excel;


import com.yqg.common.utils.DateUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Jacob on 2017/4/19.
 */
@Component
public class ExcelUtil {

    @Value("${excel.file.targetPath}")
    private String targetPath = "/Users/NeverMore/Desktop/excel";
//    /LOG/excel/pay
    @Value("${excel.file.templatesPath}")
    private String templatesPath = "/Users/NeverMore/Desktop/templates";

    Map<ExecelFileType, Workbook> fileTemplateMap = new HashMap<>();

    public static final String templatePostfix = "_template.xls";
    public static final String excelPostfix = ".xls";

    /**
     * ???,????
     */
    public void loadTemplate(ExecelFileType fileType) {
        FileInputStream in = null;
        Workbook wb = null;

        //"/templates/test_template.xls"
        StringBuilder templatePath = new StringBuilder();
        templatePath.append(templatesPath);
        templatePath.append(File.separator);
        templatePath.append(fileType.getValue());
        templatePath.append(templatePostfix);
        try {
            in = new FileInputStream(templatePath.toString());
            POIFSFileSystem fs = new POIFSFileSystem(in);
            wb = new HSSFWorkbook(fs);
        } catch (IOException e) {
            System.out.println(e.toString());
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
        Sheet sheet = wb.getSheetAt(0);
        fileTemplateMap.put(fileType, wb);


    }

    /**
     * ??????Excel(??????sheet)
     *
     * @param fileType
     * @param excelDataList
     * @param fileNameDate
     */
    public String createExcelFile(ExecelFileType fileType, List<ExcelData> excelDataList, Date fileNameDate) {

        if(!fileTemplateMap.containsKey(fileType)|| fileType == ExecelFileType.dayCallNumber_template){
            this.loadTemplate(fileType);
        }
        Workbook wb = fileTemplateMap.get(fileType);
        Sheet sheet = wb.getSheetAt(0);
        for (ExcelData data : excelDataList) {
            System.out.println("Excel:" + data.getRow() + "," + data.getColumn() + "," + data.getValue());
            Row row = sheet.getRow(data.getRow() - 1);
            if (row == null) {
                row = sheet.createRow(data.getRow() - 1);
            }
            Cell cell = row.getCell(data.getColumn() - 1);
            if (cell == null) {
                cell = row.createCell(data.getColumn() - 1);
            }
            if (cell != null) {
                switch (data.getDataType()) {
                    case DOUBLE:
                        cell.setCellValue((Double) data.getValue());
                        break;
                    case STRING:
                        cell.setCellValue((String) data.getValue());
                        break;
                    case BOOLEAN:
                        cell.setCellValue((boolean) data.getValue());
                        break;
                    case DATE:
                        cell.setCellValue((Date) data.getValue());
                        break;
                    case INTEGER:
                        cell.setCellValue((Integer) data.getValue());
                        break;
                }
            } else {
                System.out.println("Excel:" + fileType + "," + data.getRow() + "?," + data.getColumn() + "?,???");
            }
        }
        //???Excel??
        FileOutputStream out = null;
        String fileName = this.getFileName(fileType, fileNameDate);
        try {
            out = new FileOutputStream(fileName);
            wb.write(out);
        } catch (IOException e) {
            fileName = null;
            System.out.println(e.toString());
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
        return fileName;
    }

    /**
     * ?????
     *
     * @param fileType     ????
     * @param fileNameDate ???????
     * @return ??????????
     */
    public String getFileName(ExecelFileType fileType, Date fileNameDate) {
        StringBuilder filePathName = new StringBuilder();
        String dirPath = getDirPath(targetPath, fileType);
        filePathName.append(dirPath);
        filePathName.append(fileType.getValue());
        filePathName.append("_");
        filePathName.append(DateUtils.DateToString(fileNameDate));
        filePathName.append(excelPostfix);
        return filePathName.toString();
    }

    /**
     * ???????????
     *
     * @param rootPath ??key
     * @param fileType ????
     * @return ????
     */
    private static String getDirPath(String rootPath, ExecelFileType fileType) {
        StringBuilder fileDirPath = new StringBuilder();
        String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        fileDirPath.append(rootPath);// ???
        if (!rootPath.endsWith(File.separator)) {
            fileDirPath.append(File.separator);
        }
        fileDirPath.append(date);// ????
        fileDirPath.append(File.separator);
        fileDirPath.append(fileType.getValue());// ????
        fileDirPath.append(File.separator);
        File destDir = new File(fileDirPath.toString());
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return fileDirPath.toString();
    }


    public static void main(String[] args) {
        ExcelUtil util = new ExcelUtil();
        List<ExcelData> excelDataList = new ArrayList<>();
        excelDataList.add(new ExcelData(23, 1, ExcelDataType.DATE, new Date()));
        excelDataList.add(new ExcelData(5, 1, ExcelDataType.STRING, "???????????????001?"));
        excelDataList.add(new ExcelData(7, 2, ExcelDataType.DOUBLE, 5000.00));
        excelDataList.add(new ExcelData(7, 3, ExcelDataType.STRING, NumberToCN.number2CNMontrayUnit(new BigDecimal(5000))));
        excelDataList.add(new ExcelData(14, 1, ExcelDataType.DATE, new Date()));
        try {
            util.createExcelFile(ExecelFileType.test_template, excelDataList, DateUtils.stringToDate4("2017-09-07"));
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
