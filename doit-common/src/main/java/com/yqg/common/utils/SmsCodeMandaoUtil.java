package com.yqg.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ??????
 * Created by Jacob on 2017/7/20.
 */
public class SmsCodeMandaoUtil {

    private static Logger logger= LoggerFactory.getLogger(SmsCodeMandaoUtil.class);


    private String serviceURL = "http://sdk.entinfo.cn:8061/webservice.asmx";
    private String sn = "";// ???
    private String password = "";
    private String pwd = "";// ??


    public static void main(String[] args) {

        String content= null;
        //String mobiletemp="13522144014,13641013069";
        String mobiletemp="17610156636";
//        content = "?????,???????????? http://t.cn/RSN5Ttv ??";
        content="?????????????1?????????????????????APP??????";
        sendSmsCode(mobiletemp,content);

    }


    /**
     * ??????
     * @param mobile
     * @param content
     */
    public static void sendSmsCode(String mobile,String content){
        String contentTemp="";
        try {
            contentTemp = URLEncoder.encode(content,"utf8");
            String result_mt = new SmsCodeMandaoUtil().mdsmssend(mobile, contentTemp, "", "", "", "");
            logger.info("---->??????????????:{},????:{}",mobile,content);
            logger.info("---->??????????{}",result_mt);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    /*
     * ????
     */
    public SmsCodeMandaoUtil() throws UnsupportedEncodingException {
        String sn="SDK-WSS-010-10732";
        String password = "3@0d22@4bbf";
        this.sn = sn;
        this.password = password;
        //???md5(sn+password)
        this.pwd = this.getMD5(sn + password);
    }


    /*
     * ?????getMD5
     * ?    ?????MD5??
     * ?    ????????
     * ? ? ?????????
     */
    public String getMD5(String sourceStr) throws UnsupportedEncodingException {
        String resultStr = "";
        try {
            byte[] temp = sourceStr.getBytes();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(temp);
            // resultStr = new String(md5.digest());
            byte[] b = md5.digest();
            for (int i = 0; i < b.length; i++) {
                char[] digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                        '9', 'A', 'B', 'C', 'D', 'E', 'F' };
                char[] ob = new char[2];
                ob[0] = digit[(b[i] >>> 4) & 0X0F];
                ob[1] = digit[b[i] & 0X0F];
                resultStr += new String(ob);
            }
            return resultStr;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


    /*
     * ?????mdgetSninfo
     * ?    ??????
     * ?    ??sn,pwd(??????????md5(sn+password))
     *
     */
    public String mdgetSninfo() {
        String result = "";
        String soapAction = "http://entinfo.cn/mdgetSninfo";
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
        xml += "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">";
        xml += "<soap:Body>";
        xml += "<mdgetSninfo xmlns=\"http://entinfo.cn/\">";
        xml += "<sn>" + sn + "</sn>";
        xml += "<pwd>" + pwd + "</pwd>";
        xml += "<mobile>" + "" + "</mobile>";
        xml += "<content>" + "" + "</content>";
        xml += "<ext>" + "" + "</ext>";
        xml += "<stime>" + "" + "</stime>";
        xml += "<rrid>" + "" + "</rrid>";
        xml += "<msgfmt>" + "" + "</msgfmt>";
        xml += "</mdgetSninfo>";
        xml += "</soap:Body>";
        xml += "</soap:Envelope>";

        URL url;
        try {
            url = new URL(serviceURL);

            URLConnection connection = url.openConnection();
            HttpURLConnection httpconn = (HttpURLConnection) connection;
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            bout.write(xml.getBytes());
            byte[] b = bout.toByteArray();
            httpconn.setRequestProperty("Content-Length", String
                    .valueOf(b.length));
            httpconn.setRequestProperty("Content-Type",
                    "text/xml; charset=gb2312");
            httpconn.setRequestProperty("SOAPAction", soapAction);
            httpconn.setRequestMethod("POST");
            httpconn.setDoInput(true);
            httpconn.setDoOutput(true);

            OutputStream out = httpconn.getOutputStream();
            out.write(b);
            out.close();

            InputStreamReader isr = new InputStreamReader(httpconn
                    .getInputStream());
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            while (null != (inputLine = in.readLine())) {
                Pattern pattern = Pattern.compile("<mdgetSninfoResult>(.*)</mdgetSninfoResult>");
                Matcher matcher = pattern.matcher(inputLine);
                while (matcher.find()) {
                    result = matcher.group(1);
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /*
     * ?????mdgxsend
     * ?    ????????
     * ?    ??mobile,content,ext,stime,rrid,msgfmt(?????????????????????????)
     * ? ? ????????????rrid????????
     */
    public String mdgxsend(String mobile, String content, String ext, String stime,
                           String rrid, String msgfmt) {
        String result = "";
        String soapAction = "http://entinfo.cn/mdgxsend";
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
        xml += "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">";
        xml += "<soap:Body>";
        xml += "<mdgxsend xmlns=\"http://entinfo.cn/\">";
        xml += "<sn>" + sn + "</sn>";
        xml += "<pwd>" + pwd + "</pwd>";
        xml += "<mobile>" + mobile + "</mobile>";
        xml += "<content>" + content + "</content>";
        xml += "<ext>" + ext + "</ext>";
        xml += "<stime>" + stime + "</stime>";
        xml += "<rrid>" + rrid + "</rrid>";
        xml += "<msgfmt>" + msgfmt + "</msgfmt>";
        xml += "</mdgxsend>";
        xml += "</soap:Body>";
        xml += "</soap:Envelope>";

        URL url;
        try {
            url = new URL(serviceURL);

            URLConnection connection = url.openConnection();
            HttpURLConnection httpconn = (HttpURLConnection) connection;
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            bout.write(xml.getBytes());
            byte[] b = bout.toByteArray();
            httpconn.setRequestProperty("Content-Length", String
                    .valueOf(b.length));
            httpconn.setRequestProperty("Content-Type",
                    "text/xml; charset=gb2312");
            httpconn.setRequestProperty("SOAPAction", soapAction);
            httpconn.setRequestMethod("POST");
            httpconn.setDoInput(true);
            httpconn.setDoOutput(true);

            OutputStream out = httpconn.getOutputStream();
            out.write(b);
            out.close();

            InputStreamReader isr = new InputStreamReader(httpconn
                    .getInputStream());
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            while (null != (inputLine = in.readLine())) {
                Pattern pattern = Pattern.compile("<mdgxsendResult>(.*)</mdgxsendResult>");
                Matcher matcher = pattern.matcher(inputLine);
                while (matcher.find()) {
                    result = matcher.group(1);
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /*
     * ?????mdsmssend
     * ?    ??????
     * ?    ??mobile,content,ext,stime,rrid,msgfmt(?????????????????????????)
     * ? ? ????????????rrid????????
     */
    public String mdsmssend(String mobile, String content, String ext, String stime,
                            String rrid,String msgfmt) {
        if(!content.contains("%e3%80%90%e6%91%87%e9%92%b1%e7%bd%90%e3%80%91") && !content.contains("%E9%80%80%E8%AE%A2%E5%9B%9ETD")){
            content = content+"+%e3%80%90%e6%91%87%e9%92%b1%e7%bd%90%e3%80%91";
        }
        String result = "";
        String soapAction = "http://entinfo.cn/mdsmssend";
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
        xml += "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">";
        xml += "<soap:Body>";
        xml += "<mdsmssend  xmlns=\"http://entinfo.cn/\">";
        xml += "<sn>" + sn + "</sn>";
        xml += "<pwd>" + pwd + "</pwd>";
        xml += "<mobile>" + mobile + "</mobile>";
        xml += "<content>" + content + "</content>";
        xml += "<ext>" + ext + "</ext>";
        xml += "<stime>" + stime + "</stime>";
        xml += "<rrid>" + rrid + "</rrid>";
        xml += "<msgfmt>" + msgfmt + "</msgfmt>";
        xml += "</mdsmssend>";
        xml += "</soap:Body>";
        xml += "</soap:Envelope>";

        URL url;
        try {
            url = new URL(serviceURL);

            URLConnection connection = url.openConnection();
            HttpURLConnection httpconn = (HttpURLConnection) connection;
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            bout.write(xml.getBytes());
            byte[] b = bout.toByteArray();
            httpconn.setRequestProperty("Content-Length", String
                    .valueOf(b.length));
            httpconn.setRequestProperty("Content-Type",
                    "text/xml; charset=gb2312");
            httpconn.setRequestProperty("SOAPAction", soapAction);
            httpconn.setRequestMethod("POST");
            httpconn.setDoInput(true);
            httpconn.setDoOutput(true);

            OutputStream out = httpconn.getOutputStream();
            out.write(b);
            out.close();

            InputStreamReader isr = new InputStreamReader(httpconn
                    .getInputStream());
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            while (null != (inputLine = in.readLine())) {
                Pattern pattern = Pattern.compile("<mdsmssendResult>(.*)</mdsmssendResult>");
                Matcher matcher = pattern.matcher(inputLine);
                while (matcher.find()) {
                    result = matcher.group(1);
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
