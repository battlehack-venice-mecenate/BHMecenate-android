package com.battlehack_venice.lib.api;

import android.util.Log;

import com.battlehack_venice.lib.utils.MD5Util;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;


public class ApiClient
{
    private static final String LOG_TAG = "ApiClient";

    private final OkHttpClient _client;
    private final String _baseUrl;

    public ApiClient(OkHttpClient client, String baseUrl)
    {
        this._client = client;
        this._baseUrl = baseUrl;
    }

    public <T> Observable<T> get(String url, Map<String, String> urlParameters, ApiResponseParser<T> parser)
    {
        return this.request("GET", url, urlParameters, null, null, parser);
    }

    public <T> Observable<T> post(String url, Map<String, String> urlParameters, Map<String, String> bodyParameters, ApiResponseParser<T> parser)
    {
        return this.request("POST", url, urlParameters, bodyParameters, null, parser);
    }

    public <T> Observable<T> put(String url, Map<String, String> urlParameters, Map<String, String> bodyParameters, ApiResponseParser<T> parser)
    {
        return this.request("PUT", url, urlParameters, bodyParameters, null, parser);
    }

    public <T> Observable<T> delete(String url, Map<String, String> urlParameters, ApiResponseParser<T> parser)
    {
        return this.request("DELETE", url, urlParameters, null, null, parser);
    }

    public <T> Observable<T> request(String method, String url, Map<String, String> urlParameters, Map<String, String> bodyParameters, Map<String, String> headers, ApiResponseParser<T> parser)
    {
        // Compose request body (if supported)
        RequestBody body = null;

        // Compose url
        if (url == null) {
            return Observable.error(new Throwable("Unable to build url for route: " + url));
        }

        // Compose request body
        if ((method.equals("POST") || method.equals("PUT")) && bodyParameters != null) {

            FormEncodingBuilder formBuilder = new FormEncodingBuilder();
            for (Map.Entry<String, String> entry : bodyParameters.entrySet()) {
                formBuilder.add(entry.getKey(), entry.getValue());
            }

            body = formBuilder.build();
        }

        // Build request
        Request.Builder requestBuilder = new Request.Builder().method(method, body).url(this._baseUrl + url);

        // Add headers
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        // Enqueue
        return this._enqueueRequest(requestBuilder, parser, ApiError.PARSER);
    }

    private <T> Observable<T> _enqueueRequest(final Request.Builder requestBuilder, final ApiResponseParser<T> responseParser, final ApiErrorParser errorParser)
    {
        return Observable.create(new Observable.OnSubscribe<T>()
        {
            @Override
            public void call(Subscriber<? super T> subscriber)
            {
                String tag = MD5Util.generateHash((Math.random() * 10000) + "_" + System.currentTimeMillis()).substring(0, 7);
                Request request = requestBuilder.tag(tag).build();
                String body = null;
                int status = 0;

                // Perform request
                try {

                    // Execute request
                    Response response = _client.newCall(request).execute();

                    // Read response and release resources
                    status = response.code();
                    body = response.body().string();

                    // Check status
                    if (status >= 200 && status < 400) {

                        // Parse response
                        T resource = responseParser != null ? responseParser.parse(response.headers(), body) : null;
                        subscriber.onNext(resource);
                        subscriber.onCompleted();

                    } else {

                        // Parse error
                        ApiError error = errorParser.parse(status, body);
                        subscriber.onError(error);
                    }

                } catch (IOException e) {
                    Log.e(LOG_TAG, "API[" + request.tag() + "] I/O exception. Message: " + e.getMessage());
                    subscriber.onError(new ApiError(0, "Network error"));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "API[" + request.tag() + "] JSON exception. Message: " + e.getMessage() + ", body: " + body);
                    subscriber.onError(new ApiError(500, "Unable to parse API response"));
                } catch (Exception e) {
                    Log.e(LOG_TAG, "API[" + request.tag() + "] Unknown exception. Message: " + e.getMessage(), e);
                    subscriber.onError(new ApiError(400, "An unexpected error occured while handling your request"));
                }
            }
        });
    }
}
