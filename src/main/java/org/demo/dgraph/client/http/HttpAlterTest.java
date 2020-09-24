package org.demo.dgraph.client.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class HttpAlterTest {

    public static void main(String[] args) {
        String alterUrl = "http://localhost:8080/alter";
        String alterStr = "revenue: float .\n"
                + "  running_time: int .";

        alter(alterUrl,alterStr);
    }

    /**
     * alter.
     *
     * @param url
     * @param alterStr
     */
    private static void alter(String url, String alterStr) {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()
        ) {
            HttpPost post = new HttpPost(url);

            StringEntity entity = new StringEntity(alterStr);
            entity.setContentType("application/json");
            post.setEntity(entity);
            HttpResponse response = httpClient.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response.getEntity());
                System.out.println("-- http client response:\n" + result);
            } else {
                System.out.println("-- http client call error");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
