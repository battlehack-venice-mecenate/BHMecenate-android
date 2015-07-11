package com.battlehack_venice.lib.api;

import com.squareup.okhttp.Headers;

import org.json.JSONException;
import org.json.JSONObject;


abstract public class ApiResponseJsonParser<T extends Object> implements ApiResponseParser<T>
{
    @Override
    public T parse(Headers headers, String body) throws JSONException
    {
        return this.parse(body);
    }

    /**
     * Parse the input data. Returns null on error.
     */
    public T parse(String data) throws JSONException
    {
        return this.parse(new JSONObject(data));
    }

    /**
     * Parse the input object. Returns null on error.
     */
    public abstract T parse(JSONObject object) throws JSONException;
}