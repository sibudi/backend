package com.yqg.service.third.digSign;

import com.itextpdf.html2pdf.HtmlConverter;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.Map;

@Service
@Slf4j
public class PDFService {



    /***
     * html模板数据填充
     * @param fillData
     * @return
     * @throws Exception
     */
    public static String fillHtmlTemplate(Map<String, String> fillData) throws Exception {
        Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        ClassTemplateLoader loader = new ClassTemplateLoader(
                PDFService.class, "/templates");
        // cfg.setClassForTemplateLoading(PdfFillService.class, "/templates");
        cfg.setTemplateLoader(loader);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);//.RETHROW
        cfg.setClassicCompatible(true);
        Template temp = cfg.getTemplate("agreement.ftl");

        StringWriter stringWriter = new StringWriter();
        temp.process(fillData, stringWriter);
        return stringWriter.toString();
    }

    /***
     * html转为outputStream
     * @param htmlData
     * @param destFileName
     * @throws Exception
     */
    public static File html2Pdf(String htmlData,String destFileName) throws Exception {
        File destFile = new File(destFileName);
        BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(destFile));
        HtmlConverter.convertToPdf(htmlData, outStream);
        return destFile;
    }




}

