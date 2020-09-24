package org.demo.dgraph.client.http;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * dgraph client http mutate
 */
public class HttpMutateTest {

    private static void mutateTest(){

        //commitNow=true 指mutate后直接提交事务
        String mutateUrl = "http://localhost:8080/mutate?commitNow=true";

        //TODO 两种实现方式，http ContentType {application/rdf,application/json}
        String mutateStr = "{\n"
                + "  set {\n"
                + "\n"
                + "    # -- Facets on scalar predicates\n"
                + "    _:alice <name> \"Alice\" .\n"
                + "    _:alice <mobile> \"040123456\" (since=2006-01-02T15:04:05) .\n"
                + "    _:alice <car> \"MA0123\" (since=2006-02-02T13:01:09, first=true) .\n"
                + "  }\n"
                + "}";
        mutateRdf(mutateUrl,mutateStr);

        mutateStr = "{\n"
                + "  \"delete\": [\n"
                + "    {\n"
                + "      \"uid\": \"0x1e\",\n"
                + "      \"name\": \"*\" "
                + "    }\n"
                + "  ]\n"
                + "}";
        mutateJson(mutateUrl,mutateStr);

    }

    private static void mutateRdf(String url,String mutateStr){
        try(CloseableHttpClient httpClient = HttpClientBuilder.create().build()
        ){
            HttpPost post = new HttpPost(url);

            StringEntity entity = new StringEntity(mutateStr);
            entity.setContentType("application/rdf");
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

    private static void mutateJson(String url,String mutateStr){
        try(CloseableHttpClient httpClient = HttpClientBuilder.create().build()
        ){
            HttpPost post = new HttpPost(url);

            StringEntity entity = new StringEntity(mutateStr);
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
