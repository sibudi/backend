package com.yqg.service.util;

import com.yqg.common.utils.DESUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class ImageUtil {

    public static BufferedImage rotateImage(final BufferedImage bufferedimage,
                                            final int degree) {
        int w = bufferedimage.getWidth();
        int h = bufferedimage.getHeight();
        int type = bufferedimage.getColorModel().getTransparency();
        BufferedImage img;
        Graphics2D graphics2d;
        (graphics2d = (img = new BufferedImage(w, h, type))
                .createGraphics()).setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2d.rotate(Math.toRadians(degree), w / 2, h / 2);
        graphics2d.drawImage(bufferedimage, 0, 0, null);
        graphics2d.dispose();
        return img;
    }


    public static String encryptUrl(String path) {
        path = DESUtils.encrypt(path);

        try {
            return URLEncoder.encode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String decryptUrl(String path) {

//        try {
//            path = URLDecoder.decode(path, "UTF-8");
//
//            return DESUtils.decrypt(path);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        return DESUtils.decrypt(path);
    }
}
