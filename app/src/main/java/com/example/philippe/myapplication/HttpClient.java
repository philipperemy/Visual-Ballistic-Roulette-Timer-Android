package com.example.philippe.myapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HttpClient {

    private ExecutorService executor = Executors.newFixedThreadPool(2);
    private String apiTargetUrl;
    private String phoneIdentifier;

    public HttpClient(String phoneIdentifier, String apiTargetUrl) {
        this.apiTargetUrl = apiTargetUrl;
        this.phoneIdentifier = phoneIdentifier;
    }

    public class SendTimestampTask implements Callable<String> {

        private long timestamp;

        public SendTimestampTask(long timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public String call() throws Exception {
            long startMs = System.currentTimeMillis();
            String targetUrl = apiTargetUrl + "?ts=" + timestamp + "&type=" + phoneIdentifier;
            lowLevelCall(targetUrl);
            return "Call took " + (System.currentTimeMillis() - startMs) + "ms. Target is " + targetUrl;
        }
    }

    public String sendTimeStamp(long timestamp) throws Exception {
        Future<String> callRet = executor.submit(new SendTimestampTask(timestamp));
        return callRet.get();
    }

    private String lowLevelCall(String targetUrl) throws IOException {
        String fullString = "";
        BufferedReader reader = null;
        try {
            URL url = new URL(targetUrl);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                fullString += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fullString;
    }
}