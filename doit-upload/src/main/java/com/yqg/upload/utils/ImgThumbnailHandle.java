package com.yqg.upload.utils;

import com.yqg.upload.common.Constants;
import com.yqg.upload.config.UploadConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * img?????
 */
public class ImgThumbnailHandle implements ImgHandle {

    Logger logger = LoggerFactory.getLogger(ImgThumbnailHandle.class);

    private ImgHandle handle;

    public ImgThumbnailHandle(ImgHandle handle){
        this.handle = handle;
    }

    @Override
    public void handle(UploadConfig uploadConfig) {
        try{
            handle.handle(uploadConfig);
        }catch (ImgHandleResultException img){

            logger.info("?????============");
            try {
                logger.debug("byte[] ??ByteArrayInputStream ?========");
                ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(img.getStream());

                logger.debug("ByteArrayInputStream??image ========");
                Image srcImg  = ImageIO.read(tInputStringStream);

                logger.debug("ByteArrayInputStream??image ?========");
                int height = srcImg.getHeight(null)*Constants.IMG_THUMBNAIL_WIDTH/srcImg.getWidth(null);
                BufferedImage image = new BufferedImage(Constants.IMG_THUMBNAIL_WIDTH
                    ,height,BufferedImage.TYPE_INT_RGB);

                Graphics2D graphics = image.createGraphics();

                logger.debug("image ??bufferImg========");
                boolean drawImage = graphics.drawImage(srcImg, 0, 0, Constants.IMG_THUMBNAIL_WIDTH
                        , height, null);
                logger.debug("drawImage===={}",drawImage);
                String savePath = img.getInfo().getFileUrl();

                savePath = savePath.replace(".","_thumbnail.");

                logger.info("???url:[{}]======??:[{}],??:[{}],??:[{}],??:[{}]",savePath
                        ,srcImg.getHeight(null),image.getHeight(),srcImg.getWidth(null),image.getWidth());

//                FileOutputStream fos = new FileOutputStream(new File(savePath));
//                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fos);
//                encoder.encode(image);
                String formatName = savePath.substring(savePath.lastIndexOf(".") + 1);
                ImageIO.write(image, /*"GIF"*/ formatName /* format desired */ , new File(savePath) /* target */ );

                image.flush();
//                fos.flush();
//                fos.close();

                logger.info("?????url:[{}]======",savePath);

                img.setHasThumbnail(true);

                throw img;

            } catch (IOException e) {
                logger.error(e.getMessage());
            }

        }
    }

}
