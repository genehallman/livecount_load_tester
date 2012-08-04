package com.livefyre.livecount;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
import org.perf4j.StopWatch;

import com.livefyre.livecount.PerformanceMetric.RequestType;

public class Client implements Runnable {

    private String host;
    private int convId;
    private int userId;
    private String routedHost = null;
    private boolean timeout = false;

    public Client(String host, int convId, int userId) {
        this.host = host;
        this.convId = convId;
        this.userId = userId;
    }

    private JSONObject makeRequest() throws Exception {
        HttpURLConnection connection = null;
        try {
            String url = String.format("%s/livecountping/%d/%d/", this.routedHost == null ? this.host : this.routedHost, this.convId,
                    this.userId);
            String params = "?";
            if (timeout) {
                params += "timeout=1";
            } else if (routedHost != null) {
                params += "routed=1";
            }
            if (params.length() > 1) {
                params += "&";
            }
            params += "callback=asdf";

            URL serverAddress = new URL(url + params);
            connection = (HttpURLConnection) serverAddress.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setReadTimeout(3000);
            connection.connect();

            BufferedReader response = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = response.readLine()) != null) {
                sb.append(line + '\n');
            }

            String rawResp = sb.toString().trim();

            if (rawResp.startsWith("asdf(")) {
                rawResp = rawResp.replaceFirst("asdf\\(", "");
            }

            if (rawResp.endsWith(");")) {
                System.out.print("0");
                rawResp = rawResp.replaceFirst("\\);$", "");
            } else {
                System.out.print("1");
            }

            JSONObject resp = new JSONObject(rawResp);

            // p(resp);
            return resp;

        } catch (SocketTimeoutException e) {
            timeout = true;
            routedHost = null;
            throw e;
        } catch (Exception e) {
            throw e;
        } finally {
            // close the connection, set all objects to null
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void processResponse(JSONObject resp, PerformanceMetric metric) throws JSONException {
        timeout = false;
        int code = resp.getInt("code");
        switch (code) {
        case 200:
            break;
        case 302:
            routedHost = resp.getString("data");
            break;
        case 500:
            routedHost = null;
            break;
        }
    }

    public void run() {
        StopWatch timer = null;
        PerformanceMetric metric = null;
        try {
            timer = new StopWatch();
            metric = new PerformanceMetric();
            metric.requestType = RequestType.Count;
            if (timeout) {
                metric.requestType = RequestType.Timeout;
            } else if (routedHost == null) {
                metric.requestType = RequestType.Route;
            }
            timer.start();

            JSONObject resp = makeRequest();
            metric.roundTripTime = timer.getElapsedTime();
            timer.stop();

            metric.returnCode = resp.getInt("code");
            processResponse(resp, metric);

        } catch (Exception e) {
            if (metric != null) {
                metric.exception = e;
                metric.roundTripTime = metric.roundTripTime > 0 ? metric.roundTripTime : timer.getElapsedTime();
                timer.stop();
            }
        } finally {
            LoadTester.metrics.add(metric);
        }
    }
}
