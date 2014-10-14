package com.sopinka.refuge.utilities;

import com.google.gson.Gson;
import com.sopinka.refuge.objects.Restroom;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Mike on 10/12/2014.
 */
public class HttpHelper {

    private static String SEARCH_URL = "http://www.refugerestrooms.org/restrooms?search=Current+Location&lat=%s&long=%s&page=%s";
    private static String SUBMIT_URL = "http://www.refugerestrooms.org/restrooms";

    // Get restrooms for given lat/lng
    public static Restroom[] getMapResults(double lat, double lng) {
        ArrayList<Restroom> finalResults = new ArrayList<Restroom>();

        // iterate 5 times to get multiple pages
        for (int i = 1; i <= 5; i++)
        {
            String json = getHtmlString(String.format(SEARCH_URL, lat, lng, i));
            Restroom[] results = new Gson().fromJson(json, Restroom[].class);
            if (results != null && results.length > 0)
            {
                finalResults.addAll(Arrays.asList(results));
            }
            else
            {
                break;
            }
        }

        return finalResults.toArray(new Restroom[0]);
    }

    // Submit restroom
    public static boolean submitRestroom(Restroom submission) {
        boolean success = false;

        try {

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(SUBMIT_URL);

            post.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.124 Safari/537.36");
            post.setHeader("Cookie", "_saferstalls_rails_session=MnFITEdkbFg5eWJjVGhHcVBRZGJMY0lIT3NaczVZRCtxbUFTazRJcUNrYlg4eUFDKzJ4ZHk3U1ZjTk5RSWxzdXErWnpyV2wrVVQ1UVVadUxmWmxENit4TStCbjB6UmhuRVhLNlc2VFlWTXBNR0pkYjFlZk9sd1FtckQ1S0dKaG9COUV4azNjckNQYkpYMmtockhRMEpsQ2IrMndGTlBVdzFLOHhQWVNXaGlYbEdhbWptTFkzU2t3bWYvNjI5ZzJ3LS14REJvd2cwU3V6VU5YRkZxeDFpazNnPT0%3D--98a1d9a381aea21320ff50007d8c8045e3b02da3");

            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("utf8", "%E2%9C%93"));
            urlParameters.add(new BasicNameValuePair("authenticity_token", "D8Ukr0sP4Sn8/8KS0By1c6eBIMJUIVmf+nKWZjY1X8g"));
            urlParameters.add(new BasicNameValuePair("restroom[name]", submission.name));
            urlParameters.add(new BasicNameValuePair("restroom[street]", submission.street));
            urlParameters.add(new BasicNameValuePair("restroom[city]", submission.city));
            urlParameters.add(new BasicNameValuePair("restroom[state]", submission.state));
            urlParameters.add(new BasicNameValuePair("restroom[country]", submission.country));
            urlParameters.add(new BasicNameValuePair("restroom[latitude]", ""));
            urlParameters.add(new BasicNameValuePair("restroom[longitude]", ""));
            urlParameters.add(new BasicNameValuePair("restroom[unisex]", String.valueOf(submission.unisex)));
            urlParameters.add(new BasicNameValuePair("restroom[accessible]", String.valueOf(submission.accessible)));
            urlParameters.add(new BasicNameValuePair("restroom[directions]", submission.directions));
            urlParameters.add(new BasicNameValuePair("restroom[comment]", submission.comment));
            urlParameters.add(new BasicNameValuePair("commit", "Save Restroom"));

            post.setEntity(new UrlEncodedFormEntity(urlParameters));

            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == 302) {

                success = true;
            }
            String feed = parseHttpInputStream(response.getEntity().getContent());
            if (feed != null) {

            }
        }
        catch(Exception ex) {
            if (ex != null) {

            }
        }

        return success;
    }

    // Get HTML call as string
    private static String getHtmlString(String url) {
        InputStream is = getHtmlInputStream(url);
        return parseHttpInputStream(is);
    }

    // Parse Http response input stream to string
    private static String parseHttpInputStream(InputStream is) {
        String feed = null;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(is));

            StringBuffer sb = new StringBuffer("");
            String line = "";

            while((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();

            feed = sb.toString();
        }
        catch (Exception ex) {
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ex) {
                }
            }
        }

        return feed;
    }

    // Get HTML call input stream
    private static InputStream getHtmlInputStream(String url) {

        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            request.addHeader("Accept", "application/json");
            HttpResponse response = client.execute(request);

            return response.getEntity().getContent();
        }
        catch (Exception ex) {
        }

        return null;
    }
}
