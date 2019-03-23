package com.dpt.server.controller;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class SearchController {
    private static final String BASEURL = "http://localhost:9200/dpt.posts/_search?q=";
    @CrossOrigin(origins = "http://localhost:3000") //remove this in production!
    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json")
    public String greeting(@RequestParam(value="q") String query) throws IOException {
        System.out.println(query);
        String url = BASEURL + query;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        String rawJSON = response.body().string();
        System.out.println(rawJSON);
        //LMFAO so easy to break... I'll add error handling later.
        return rawJSON;
    }
}
