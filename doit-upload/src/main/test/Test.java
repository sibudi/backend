import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by Jacob on 2017/11/24.
 */
public class Test {

    public static void main(String[] args) {
        Test test = new Test();
        test.upload("/Users/gao/Desktop/id_card.jpg");
    }

    public void upload(String localFile) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.createDefault();

            // ??????????????????? ???servlet
            HttpPost httpPost = new HttpPost("http://localhost:8080/fileUpload");
            httpPost = new HttpPost("http://192.168.206.226:8080/uploadFile");

            // ?????????FileBody
            FileBody bin = new FileBody(new File(localFile));

            StringBody userName = new StringBody("Scott", ContentType.create(
                    "text/plain", Consts.UTF_8));
            StringBody password = new StringBody("123456", ContentType.create(
                    "text/plain", Consts.UTF_8));
            StringBody fileType = new StringBody("IDCARD", ContentType.create(
                    "text/plain", Consts.UTF_8));
            StringBody sessionId = new StringBody("eb3069a8f28543cdb078f1bfc0dab3f9", ContentType.create(
                    "text/plain", Consts.UTF_8));

            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    // ???<input type="file" name="file"/>
                    .addPart("file", bin)

                    // ???<input type="text" name="userName" value=userName>
                    .addPart("userName", userName)
                    .addPart("pass", password)
                    .addPart("fileType", fileType)
                    .addPart("sessionId", sessionId)
                    .build();

            httpPost.setEntity(reqEntity);

            // ???? ????????
            response = httpClient.execute(httpPost);

            System.out.println("The response value of token:" + response.getFirstHeader("token"));

            // ??????
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                // ??????
                System.out.println("Response content length: " + resEntity.getContentLength());
                // ??????
                System.out.println(EntityUtils.toString(resEntity, Charset.forName("UTF-8")));
            }

            // ??
            EntityUtils.consume(resEntity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
