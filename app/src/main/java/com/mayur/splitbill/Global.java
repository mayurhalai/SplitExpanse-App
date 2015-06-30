package com.mayur.splitbill;

import android.app.Application;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mayur on 6/19/2015.
 */
public class Global extends Application {
    public static final String baseurl = "http://www.splitbill.byethost18.com/";
    public static final String url = baseurl + "mobile/";
    public static final String urlhome = baseurl + "mobilehome/";

    public static String downloadUrl(String urls, String token) throws IOException {
        String content = null;
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(urls);
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("token", token));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = client.execute(httpPost);
            InputStream is = response.getEntity().getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            content = br.readLine();
            /*String str = null;
            StringBuilder sb = new StringBuilder();
            while ((str=br.readLine()) != null) {
                sb.append(str);
                sb.append("\n");
            }
            content = sb.toString();*/
        } catch (Exception e) {

        }
        return content;
    }

    public static String downloadUrl(String urls, String token, String data) throws IOException {
        String content = null;
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpClient client = new DefaultHttpClient(params);
        try {
            HttpPost httpPost = new HttpPost(urls);
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            entity.addPart("token", new StringBody(token));
            entity.addPart("data", new StringBody(data));
            httpPost.setEntity(entity);

            HttpResponse response = client.execute(httpPost);
            InputStream is = response.getEntity().getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            content = br.readLine();
            /*String str = null;
            StringBuilder sb = new StringBuilder();
            while ((str=br.readLine()) != null) {
                sb.append(str);
                sb.append("\n");
            }
            content = sb.toString();*/
        } catch (Exception e) {

        }
        return content;
    }

    public static String downloadUrl(String urls, String token, String data, File image) throws IOException {
        String content = null;
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpClient client = new DefaultHttpClient(params);
        try {
            HttpPost httpPost = new HttpPost(urls);
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            entity.addPart("token", new StringBody(token));
            entity.addPart("data", new StringBody(data));
            entity.addPart("image", new FileBody(image));
            httpPost.setEntity(entity);

            HttpResponse response = client.execute(httpPost);
            InputStream is = response.getEntity().getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            content = br.readLine();
            /*String str = null;
            StringBuilder sb = new StringBuilder();
            while ((str=br.readLine()) != null) {
                sb.append(str);
                sb.append("\n");
            }
            content = sb.toString();*/
        } catch (Exception e) {

        }
        return content;
    }
}

