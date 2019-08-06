package com.yqg.manage.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @program: microservice
 * @description: 压缩文件工具
 * @author: 许金泉
 * @create: 2019-04-03 17:29
 **/
@Slf4j
public class ZipUtils {

    private static final int BUFFER_SIZE = 2 * 1024;

    /**
     * 压缩成ZIP 方法2
     *
     * @param srcFiles 需要压缩的文件列表
     * @param out      压缩文件输出流
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(Map<String, InputStream> srcMap, OutputStream out) throws RuntimeException {
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            for (Map.Entry<String, InputStream> kv : srcMap.entrySet()) {
                zos.putNextEntry(new ZipEntry(kv.getKey()));
                int len;
                InputStream in = kv.getValue();
                byte[] buf = new byte[ in.available()];
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
            long end = System.currentTimeMillis();
            log.info("压缩完成，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            log.error("压缩失败", e);
            throw new RuntimeException("zip error from ZipUtils", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    log.error("压缩流关闭失败", e);
                }
            }
        }
    }
}
