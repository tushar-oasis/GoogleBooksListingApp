package com.oasis.googlebookslistingapp.utils;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.oasis.googlebookslistingapp.AppConstants;
import com.oasis.googlebookslistingapp.model.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.oasis.googlebookslistingapp.AppConstants.publishedDate;
import static com.oasis.googlebookslistingapp.AppConstants.title;

/**
 * Created by tushar on 11-01-2017.
 */

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Null private constructor.
     * Intentionally so, because no one shall create an object of QueryUtils class
     */
    private QueryUtils(){

    }

    private static URL createUrl(String urlString){
        URL url =  null;
        try{
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "Problem building the url.", e);
        }
        return url;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static String makeHttpRequest(URL url) throws IOException{
        String jsonResponse = "";
        if(url == null){
            return jsonResponse; // if the url is null, return null string as jsonResponse
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if(urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else{
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        }catch (IOException e){
            Log.e(LOG_TAG, "Problem retrieving the Google Books JSON results.", e);
        }finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static List<Book> extractFeaturesFromJson(String jsonString){
        if(TextUtils.isEmpty(jsonString)){
            return null;
        }
        List<Book> bookList = new ArrayList<>();
        try{
            JSONObject baseJsonResponse = new JSONObject(jsonString);
            JSONArray booksArray = baseJsonResponse.getJSONArray(AppConstants.items);
            Log.d(LOG_TAG, "item count in json: " + booksArray.length());
            for(int i=0; i<booksArray.length(); i++){
                JSONObject currentBookJsonOnject = booksArray.getJSONObject(i);
                JSONObject volumeInfoJsonObject = currentBookJsonOnject.getJSONObject(AppConstants.volumeInfo);
                String title;
                if(volumeInfoJsonObject.has(AppConstants.title)) {
                    title = volumeInfoJsonObject.getString(AppConstants.title); // title of the book
                }else{
                    title = "Title not specified";
                }
                List<String> authorsList = new ArrayList<>();
                if(volumeInfoJsonObject.has(AppConstants.authors)) {
                    JSONArray authorsJsonArray = volumeInfoJsonObject.getJSONArray(AppConstants.authors);
                    if (authorsJsonArray.length() != 0) {
                        for (int j = 0; j < authorsJsonArray.length(); j++) {
                            authorsList.add(authorsJsonArray.getString(j));
                        }
                    } else {
                        authorsList.add("Authors not specified");
                    }
                } else {
                    authorsList.add("Authors not specified");
                }
                String publishedDate;
                if (volumeInfoJsonObject.has(AppConstants.publishedDate)) {
                    publishedDate = volumeInfoJsonObject.getString(AppConstants.publishedDate);
                    if (TextUtils.isEmpty(publishedDate)) {
                        publishedDate = "Not specified";
                    }
                }else{
                    publishedDate = "Not specified";
                }
                String thumbnailUrl;
                if(volumeInfoJsonObject.has(AppConstants.imageLinks)) {
                    JSONObject imageLinksJsonObject = volumeInfoJsonObject.getJSONObject(AppConstants.imageLinks);
                    thumbnailUrl = imageLinksJsonObject.getString(AppConstants.thumbnail);
                    if (TextUtils.isEmpty(thumbnailUrl)) {
                        thumbnailUrl = "https://unsplash.it/200"; // Placeholder image url if thumbnail url is
                        // not provided.
                    }
                }else{
                    thumbnailUrl = "https://unsplash.it/200"; // Placeholder image url if thumbnail url is
                }
                String infoLinkUrl;
                if(volumeInfoJsonObject.has(AppConstants.infoLink)) {
                    infoLinkUrl = volumeInfoJsonObject.getString(AppConstants.infoLink);
                }else{
                    infoLinkUrl = "https://www.google.com";
                }
                Book newBook = new Book(title, authorsList, thumbnailUrl, infoLinkUrl, publishedDate);
                bookList.add(newBook);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the Google Books JSON results", e);
        }
        return bookList;
    }

    public static List<Book> fetchBookData(String urlString){
        URL url = createUrl(urlString);
        String jsonResponse = null;
        try{
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        List<Book> bookList = extractFeaturesFromJson(jsonResponse);
        return bookList;
    }
}
