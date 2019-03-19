package com.postcard.threadcollector;

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
        while(index < 2) { //replace "index < 2" with true
            Request request = new Request.Builder()
                    .url(findThreads + "&page=" + index)
                    .build();
            Response response = client.newCall(request).execute();
            String rawJSON = response.body().string();
            if(rawJSON.contains("error")) {
                break;
            }
            /*if(rawJSON.contains("error")) {
                break;
            }*/
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
                op.getJSONObject("media").remove("media"); //wtf is this?
                op.getJSONObject("media").remove("media_orig");
                op.getJSONObject("media").remove("media_status");
                op.getJSONObject("media").remove("media_size");
                op.getJSONObject("media").remove("safe_media_hash");
                op.getJSONObject("media").remove("media_filename_processed");
                op.getJSONObject("media").remove("preview_h");
                op.getJSONObject("media").remove("media_w");
                op.getJSONObject("media").remove("preview_reply");
                op.getJSONObject("media").remove("total"); //wtf is this?
                op.getJSONObject("media").remove("media_hash");
                op.getJSONObject("media").remove("media_id");
                op.getJSONObject("media").remove("remote_media_link");
                op.getJSONObject("media").remove("preview_op");
                op.getJSONObject("media").remove("preview_orig");
                op.getJSONObject("media").remove("media_orig");
                op.getJSONObject("media").remove("media_h");
                op.getJSONObject("media").remove("preview_w");
                op.getJSONObject("media").remove("exif");
                op.remove("board");
                op.remove("capcode");
                op.remove("comment_processed");
                op.remove("comment_sanitized");
                op.remove("deleted");
                op.remove("doc_id");
                op.remove("email_processed");
                op.remove("exif");
                op.remove("extra_data");
                op.remove("formatted");
                op.remove("fourchan_date");
                op.remove("locked");
                op.remove("name_processed");
                op.remove("nimages");
                op.remove("nreplies");
                op.remove("poster_country");
                op.remove("poster_country_name");
                op.remove("poster_country_name_processed");
                op.remove("poster_hash");
                op.remove("poster_hash_processed");
                op.remove("sticky");
                op.remove("subnum");
                op.remove("timestamp_expired");
                op.remove("title"); //maybe?
                op.remove("title_processed");
                op.remove("trip_processed");

                //System.out.println(op.toString(4));
                collection.insert((DBObject) JSON.parse(parsedResponse.getJSONObject(thread).getJSONObject("op").toString()));
                if(parsedResponse.getJSONObject(thread).has("posts")) {
                    Iterator<String> keys = parsedResponse.getJSONObject(thread).getJSONObject("posts").keys();
                    keys.forEachRemaining(key -> {
                        JSONObject reply = parsedResponse.getJSONObject(thread).getJSONObject("posts").getJSONObject(key);
                        if(reply.get("media") instanceof JSONObject) {
                            reply.getJSONObject("media").remove("media");
                            reply.getJSONObject("media").remove("media_orig");
                            reply.getJSONObject("media").remove("media_status");
                            reply.getJSONObject("media").remove("media_size");
                            reply.getJSONObject("media").remove("safe_media_hash");
                            reply.getJSONObject("media").remove("media_filename_processed");
                            reply.getJSONObject("media").remove("preview_h");
                            reply.getJSONObject("media").remove("media_w");
                            reply.getJSONObject("media").remove("preview_reply");
                            reply.getJSONObject("media").remove("total"); //wtf is this?
                            reply.getJSONObject("media").remove("media_hash");
                            reply.getJSONObject("media").remove("media_id");
                            reply.getJSONObject("media").remove("remote_media_link");
                            reply.getJSONObject("media").remove("preview_op");
                            reply.getJSONObject("media").remove("preview_orig");
                            reply.getJSONObject("media").remove("media_orig");
                            reply.getJSONObject("media").remove("media_h");
                            reply.getJSONObject("media").remove("preview_w");
                            reply.getJSONObject("media").remove("exif");
                        } else {
                            reply.remove("media");
                        }
                        reply.remove("board");
                        reply.remove("capcode");
                        reply.remove("comment_processed");
                        reply.remove("comment_sanitized");
                        reply.remove("deleted");
                        reply.remove("doc_id");
                        reply.remove("email_processed");
                        reply.remove("exif");
                        reply.remove("extra_data");
                        reply.remove("formatted");
                        reply.remove("fourchan_date");
                        reply.remove("locked");
                        reply.remove("name_processed");
                        reply.remove("nimages");
                        reply.remove("nreplies");
                        reply.remove("poster_country");
                        reply.remove("poster_country_name");
                        reply.remove("poster_country_name_processed");
                        reply.remove("poster_hash");
                        reply.remove("poster_hash_processed");
                        reply.remove("sticky");
                        reply.remove("subnum");
                        reply.remove("timestamp_expired");
                        reply.remove("title"); //maybe?
                        reply.remove("title_processed");
                        reply.remove("trip_processed");
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
