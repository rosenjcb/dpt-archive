package com.dpt.threadcollector;

import com.mongodb.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import com.mongodb.util.JSON;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/*
This gets a list of all /dpt/ threads found on rbt.asia.
 */
public class App {
    public static void main( String[] args ) throws IOException {
        System.out.println("************Loading DPT Threads************");
        String findThreads = "http://rbt.asia/_/api/chan/search/?board=g&subject=dpt&page=";
        String getThreads = "http://rbt.asia/_/api/chan/thread/?board=g&num=";
        OkHttpClient client = new OkHttpClient();
        //Boolean isLastPage = false;
        ArrayList<String> threads = new ArrayList<>();
        int index = 1; //index is page number and the first page is 1
        while(true) { //replace "index < 2" with true
            Request request = new Request.Builder()
                    .url(findThreads + "&page=" + index)
                    .build();
            Response response = client.newCall(request).execute();
            String rawJSON = response.body().string();
            if(rawJSON.contains("error")) {
                break;
            }
            JSONObject parsedResponse = new JSONObject(rawJSON);
            JSONArray posts = parsedResponse.getJSONObject("0").getJSONArray("posts");
            for(int i = 0; i < posts.length(); i++) { //replace 10 with posts.length()
                JSONObject post  = posts.getJSONObject(i);
                String threadNum = post.getString("thread_num");
                System.out.println(threadNum);
                threads.add(threadNum);
            }
            index++;
        }

        System.out.println("************SUCCESSFULLY COLLECTED ALL ARCHIVED THREADS************");
        MongoClient mongoClient = new MongoClient();
        DB database = mongoClient.getDB("dpt");
        DBCollection collection = database.getCollection("posts");
        threads.forEach(thread -> {
            DBObject threadObject = new BasicDBObject();
            threadObject.put("threadNumber", Integer.parseInt(thread));
            Request request = new Request.Builder()
                    .url(getThreads + thread)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String rawJSON = response.body().string();
                JSONObject parsedResponse = new JSONObject(rawJSON);

                //Many useless or redundant fields that should be removed.
                JSONObject op = parsedResponse.getJSONObject(thread).getJSONObject("op");
                String[] mediaFields = {"media", "media_orig", "media_status", "media_size",
                        "safe_media_hash", "media_filename_processed", "preview_h", "media_w", "preview_reply",
                        "total", "media_hash", "media_id", "remote_media_link", "preview_op", "preview_orig", "media_h",
                        "preview_w", "exif"};

                String[] fields = {"board", "capcode", "comment_processed", "comment_sanitized", "deleted", "doc_id", "email_processed",
                        "exif", "extra_data", "formatted", "fourchan_date", "locked", "name_processed", "nimages", "nreplies", "poster_country",
                        "poster_country_name", "poster_country_name_processed", "poster_hash", "poster_hash_processed", "sticky", "subnum",
                        "timestamp_expired", "title", "title_processed", "trip_processed"};

                for(String mediaField : mediaFields){
                    op.getJSONObject("media").remove(mediaField);
                }
                for(String field : fields) {
                    op.remove(field);
                }
                //System.out.println(op.toString(4));
                collection.insert((DBObject) JSON.parse(parsedResponse.getJSONObject(thread).getJSONObject("op").toString()));
                if(parsedResponse.getJSONObject(thread).has("posts")) {
                    Iterator<String> keys = parsedResponse.getJSONObject(thread).getJSONObject("posts").keys();
                    keys.forEachRemaining(key -> {
                        JSONObject reply = parsedResponse.getJSONObject(thread).getJSONObject("posts").getJSONObject(key);
                        if(reply.get("media") instanceof JSONObject) {
                            for(String mediaField : mediaFields) {
                                reply.getJSONObject("media").remove(mediaField);
                            }
                        } else {
                            reply.remove("media");
                        }
                        for(String field : fields) {
                            reply.remove(field);
                        }
                        collection.insert((DBObject) JSON.parse(reply.toString()));
                        System.out.println("************SUCCESSFULLY ADDED POST************");
                        //System.out.println(reply.toString(4));
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
