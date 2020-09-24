package org.demo.dgraph.client.http;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpQueryTest {

    public static void main(String[] args) {
        //graphql服务器地址
        String queryUrl = "http://localhost:8080/query";
        //build一个新的graphqlclient
        String queryStr = "{\n"
                + "  me(func:allofterms(name, \"Star Wars\")) @filter(ge(release_date, \"1980\")) {\n"
                + "    name\n"
                + "    release_date\n"
                + "    revenue\n"
                + "    running_time\n"
                + "    director {\n"
                + "     name\n"
                + "    }\n"
                + "    starring {\n"
                + "     name\n"
                + "    }\n"
                + "  }\n"
                + "}";
        //TODO 两种实现方式，http ContentType {application/graphql+-,application/json}
        queryGraphQL(queryUrl,queryStr);
        queryJson(queryUrl,queryStr);
    }

    /**
     *  query
     * @param url
     * @param queryStr
     */
    private static void queryJson(String url,String queryStr){
        try(CloseableHttpClient httpClient = HttpClientBuilder.create().build()
        ){
            HttpPost post = new HttpPost(url);
            Map<String, String> params = new HashMap<>();
            params.put("query", queryStr);

            StringEntity entity = new StringEntity(JSON.toJSONString(params));
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

    private static void queryGraphQL(String url,String queryStr){
        try(CloseableHttpClient httpClient = HttpClientBuilder.create().build()
        ){
            HttpPost post = new HttpPost(url);

            StringEntity entity = new StringEntity(queryStr);
            entity.setContentType("application/graphql+-");
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
