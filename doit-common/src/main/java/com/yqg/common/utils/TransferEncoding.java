package com.yqg.common.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class TransferEncoding {
	private static String encoding = "UTF-8";
	
	public static void main(String[] args) {
        try {  
            Socket socket = new Socket(InetAddress.getByName("https://api.51datakey.com/carrier/v3/mobiles/13641013069/mxreport"), 80);  
            socket.setSoTimeout(10000); // 10????? 
            OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());  
            StringBuffer sb = new StringBuffer();  
            sb.append("GET / HTTP/1.1\r\n");  
//            sb.append("Accept: image/png, i, image/*;q=0.8, */*;q=0.5\r\n");  
//            sb.append("X-HttpWatch-RID: 18613-10702\r\n");  
//            sb.append("Referer: http://www.cnblogs.com/haitao-fan/archive/2013/01/18/2866994.html\r\n");  
//            sb.append("Accept-Language: zh-CN\r\n");  
//            sb.append("User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko\r\n");  
//            sb.append("Accept-Encoding: gzip, deflate\r\n"); 
            sb.append("Authorization: token b67a5c05d45b4cb7a316b0fe83a5d371");
//            sb.append("Host: www.iteye.com\r\n");  
//            sb.append("Connection: Keep-Alive\r\n");
//            sb.append("DNT: 1\r\n");
            //??????????????????????????????????????????????????  
            sb.append("\r\n");  
            osw.write(sb.toString());  
            osw.flush();  

            //--??????????????  
            InputStream is = socket.getInputStream();
            boolean isGzip = false;
            // ????????????????????  
            String header = readHeader(is);
            System.out.println(header);
            // ???????????gzip?????
            String acceptEncoding = getHeaderValue(header, "Content-Encoding");
            if (acceptEncoding.startsWith("gzip")) {
        		isGzip = true;
        	}
            String sContLength = getHeaderValue(header, "Content-Length");
            int coentLength = isBlank(sContLength) ? 0 : Integer.parseInt(sContLength);
        	
            String body;
            if (isGzip) {
            	body = readGzipBodyToString(is);
            } else {
            	body = readBody(is, coentLength);
            }
            
            //--?????  
            System.out.print(body);  
  
            //???  
            is.close();
            socket.close();
            
        } catch (UnknownHostException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    } 
	
	/**
	 * ?????????????????
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String readGzipBodyToString(InputStream is) throws IOException {
		// ??????
        List<Byte> bodyByteList = readGzipBody(is);
        // ??????Byte List?????
		byte[] tmpByteArr = new byte[bodyByteList.size()];  
        for (int i = 0; i < bodyByteList.size(); i++) {  
            tmpByteArr[i] = ((Byte) bodyByteList.get(i)).byteValue();  
        }  
        bodyByteList.clear();  // ????
        // ??GZIPInputStream????????????
        ByteArrayInputStream bais = new ByteArrayInputStream(tmpByteArr);
		GZIPInputStream gzis = new GZIPInputStream(bais);

        StringBuffer body = new StringBuffer();
    	InputStreamReader reader = new InputStreamReader(gzis, encoding);
    	BufferedReader bin = new BufferedReader(reader);
        String str = null;
        // ????
        while((str = bin.readLine()) != null) {
        	body.append(str).append("\r\n");
        }
        // ????????
        bin.close();
		return body.toString();
	}

	/**
	 * ??????????
	 * 
	 * @param is
	 * @param contentLe
	 * @return
	 */
	private static String readBody(InputStream is, int contentLe) {
		List<Byte> lineByteList = new ArrayList<Byte>();  
        byte readByte;  
        int total = 0; 
        try {
			do {  
				readByte = (byte) is.read();
	            lineByteList.add(Byte.valueOf(readByte));  
	            total++;  
	        } while (total < contentLe);
		} catch (IOException e) {
			e.printStackTrace();
		}  
        
        byte[] tmpByteArr = new byte[lineByteList.size()];  
        for (int i = 0; i < lineByteList.size(); i++) {  
            tmpByteArr[i] = ((Byte) lineByteList.get(i)).byteValue();  
        }  
        lineByteList.clear();  
  
        String line = "";
		try {
			line = new String(tmpByteArr, encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return line;
	}

	/**
	 * ??????
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	private static String readLine(InputStream is) throws IOException {  
        List<Byte> lineByteList = new ArrayList<Byte>();  
        byte readByte;
        
        do { 
            readByte = (byte) is.read();  
            lineByteList.add(Byte.valueOf(readByte));  
            
        } while (readByte != 10);// ???????"\n"?????
        
        byte[] tmpByteArr = new byte[lineByteList.size()];  
        for (int i = 0; i < lineByteList.size(); i++) {  
            tmpByteArr[i] = ((Byte) lineByteList.get(i)).byteValue();  
        }  
        lineByteList.clear();  
        String line = new String(tmpByteArr, encoding);
        return line;
    } 
	
	/**
	 * ??????
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	private static String readHeader(InputStream is) throws IOException {
		List<Byte> lineByteList = new ArrayList<Byte>();
        byte readByte;
        int n = 0;
        int doMax = 1000000;   // do-while???????
        int count = 0;
        
        do { 
            readByte = (byte) is.read();  
            lineByteList.add(Byte.valueOf(readByte));  
            if (readByte == 13 || readByte == 10) {
            	// ???"\r"??"\n"
            	n++;
            } else if (n > 0) {
            	// ???????"\r\n"???????????
            	n = 0;
            }
            count ++;
            if (count > doMax) {  
            	// ????????????doMax??????????????????????????????"\r\n\r\n"?????????????????
            	// doMax???html?????????????
            	break;
            }
        } while (n != 4);// ?????\r\n????????????????
        
        byte[] tmpByteArr = new byte[lineByteList.size()];  
        for (int i = 0; i < lineByteList.size(); i++) {  
            tmpByteArr[i] = ((Byte) lineByteList.get(i)).byteValue();  
        }  
        lineByteList.clear();  // ????
        String header = new String(tmpByteArr, encoding);
        return header;
	}
	
	/**
	 * ??http???????
	 * 
	 * @param content
	 * @param key
	 * @return
	 */
	private static String getHeaderValue(String content, String key) {
		Map<String, String> headerMap = new HashMap<String, String>();
		if (isNotBlank(content)) {
			String[] array = content.split("\r\n");
			for (String item : array) {
				int i = item.indexOf(":");
				if (i < 0) {
					continue;
				}
				String k = item.substring(0, i);
				String v = item.substring(i + 1);
				headerMap.put(k, v);
			}
		}
		String s = headerMap.get(key);
		
		return s != null ? s.trim() : "";
	}
	
	private static boolean isNotBlank(String content) {
		if (content != null && content.trim().length() > 0)
			return true;
		return false;
	}

	/**
	 * ??gzip??????
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
    private static List<Byte> readGzipBody(InputStream is) throws IOException {
    	// ?????????chunked?????????????????16?????????????????????
    	int chunk = getChunkSize(is);
    	List<Byte> bodyByteList = new ArrayList<Byte>();
        byte readByte = 0;
        int count = 0;
        
        while (count < chunk) {  // ??????????chunk?byte
            readByte = (byte) is.read();  
            bodyByteList.add(Byte.valueOf(readByte));
            count ++;
        }
        if (chunk > 0) { // chunk??????????????????????????
        	List<Byte> tmpList = readGzipBody(is);
        	bodyByteList.addAll(tmpList);
        }
        return bodyByteList;
    }
	
    /**
     * ?????????
     * 
     * @param is
     * @return
     * @throws IOException
     */
	private static int getChunkSize(InputStream is) throws IOException {
		String sLength = readLine(is).trim();
		if (isBlank(sLength)) {  // ?????????????????
			// ????????????????
			sLength = readLine(is).trim();
		}
        if (sLength.length() < 4) {
        	sLength = 0 + sLength;
        }
        // ?16????????Int??
        int length = Integer.valueOf(sLength, 16);
        return length;
	}

	private static boolean isBlank(String sLength) {
		if (sLength == null)
			return true;
		if (sLength.trim().length() == 0)
			return true;
		return false;
	}
}
