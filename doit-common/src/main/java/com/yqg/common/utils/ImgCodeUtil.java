package com.yqg.common.utils;

import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.redis.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ImgCodeUtil {

    private static Logger logger = LoggerFactory.getLogger(ImgCodeUtil.class);

    // ???????
    private static final int CODE_NUM = 4;
    // ?????????????????
    private static Font myFont = new Font("Arial", Font.BOLD, 16);
    // ??????
    private static char[] charSequence = "0123456789"
            .toCharArray();

    private static Random random = new Random();

    // ?????????
    public static Map<String, Object> generateImgCode(OutputStream output) throws IOException {
        // ????????????
        int width = 80, height = 25;
        // ???????
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        // ????????
        Graphics g = image.getGraphics();
        g.setColor(getRandomColor(200, 250));
        g.fillRect(1, 1, width - 1, height - 1);
        g.setColor(new Color(102, 102, 102));
        g.drawRect(0, 0, width - 1, height - 1);
        g.setFont(myFont);
        // ?????????????????
        g.setColor(getRandomColor(160, 200));
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(width - 1);// ???x??
            int y = random.nextInt(height - 1);// ???y??
            int x1 = random.nextInt(6) + 1;// x????
            int y1 = random.nextInt(12) + 1;// y????
            g.drawLine(x, y, x + x1, y + y1);
        }
        // ?????????????????
        for (int i = 0; i < 70; i++) {
            int x = random.nextInt(width - 1);
            int y = random.nextInt(height - 1);
            int x1 = random.nextInt(12) + 1;
            int y1 = random.nextInt(6) + 1;
            g.drawLine(x, y, x - x1, y - y1);
        }

        // ?????????????????
        StringBuilder sRand = new StringBuilder(CODE_NUM);
        for (int i = 0; i < CODE_NUM; i++) {
            // ????????
            String tmp = getRandomChar();
            sRand.append(tmp);
            // ?????????????????????
            g.setColor(new Color(20 + random.nextInt(110), 20 + random
                    .nextInt(110), 20 + random.nextInt(110)));
            g.drawString(tmp, 15 * i + 10, 20);
        }
        g.dispose();
        // ?????????
        ImageIO.write(image, "JPEG", output);
        Map<String, Object> map = new HashMap<>();
        map.put("output", output);
        map.put("randomString", sRand);
        logger.info("code:{}", sRand);
        return map;
    }

    // ??????
    private static Color getRandomColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    // ????????
    private static String getRandomChar() {
        int index = random.nextInt(charSequence.length);
        return String.valueOf(charSequence[index]);
    }

    //???????
    public static void validationCode(String imgSessionId, String inCode, RedisClient redisClient)
            throws ServiceException {
        String code = (String) redisClient.get(imgSessionId);
        logger.info("?????" + code);
        if (code == null || !code.equalsIgnoreCase(inCode)) {
            logger.info("???????");
            throw new ServiceException(ExceptionEnum.USER_VALID_ERROR);
        }
        redisClient.del(imgSessionId);
    }

}
