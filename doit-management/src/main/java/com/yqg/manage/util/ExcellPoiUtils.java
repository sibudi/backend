package com.yqg.manage.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Author: tonggen
 * Date: 2018/6/19
 * time: 下午2:21
 */
public class ExcellPoiUtils {

    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";
    /**
     * 判断Excel的版本,获取Workbook
     * @param in
     * @return
     * @throws IOException
     */
    public static Workbook getWorkbok(InputStream in, MultipartFile file) throws IOException {
        Workbook wb = null;
        //Excel 2003
        if (file.getOriginalFilename().endsWith(EXCEL_XLS)) {
            wb = new HSSFWorkbook(in);
            // Excel 2007/2010
        } else if (file.getOriginalFilename().endsWith(EXCEL_XLSX)){
            wb = new XSSFWorkbook(in);
        }
        return wb;
    }

    /**
     * 判断文件是否是excel
     * @throws Exception
     */
    public static void checkExcelVaild(File file) throws Exception {
        if(!file.exists()){
            throw new Exception("文件不存在");
        }
        if(!(file.isFile() && (file.getName().endsWith(EXCEL_XLS) || file.getName().endsWith(EXCEL_XLSX)))){
            throw new Exception("文件不是Excel");
        }
    }
}
