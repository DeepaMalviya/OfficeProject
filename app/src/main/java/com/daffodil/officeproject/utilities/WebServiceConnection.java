package com.daffodil.officeproject.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

public class WebServiceConnection {

    static String text = "";
    static InputStream is = null;
    static HttpResponse response;

    public static String getData(String url, String params) {
        System.out.println("URL is:-- " + url);

        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);

        try {
            StringEntity entity = new StringEntity(params.toString(), HTTP.UTF_8);
            httppost.setEntity(entity);
            // Execute HTTP Post Request
            response = httpclient.execute(httppost);
            System.out.println("status" + response.toString());
            //Log.d("entity length",response.getEntity().getContentLength()+"");
            HttpEntity entitys = response.getEntity();


            if (response != null && entitys != null) {
                is = entitys.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                //Log.d("reader:===** ", reader + "");
                // Log.d("reader.readLine():=== ", reader.readLine());
                // line = reader.readLine();
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);

                        System.out.println("sb ************   " + sb);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        is.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                text = sb.toString();
//                System.out.println("return from web service"+text); 
//                System.out.println(sb.toString());

            } else {
                text = "";
            }

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
        }
        System.out.println("Return text ----- " + text);
        return text;

    }

}
