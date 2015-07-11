package com.battlehack_venice.lib.api;

import com.squareup.okhttp.Headers;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alex on 11/07/15.
 */
public class ApiResponseTextParser extends ApiResponseJsonParser<String>
{
    @Override
    public String parse(Headers headers, String body)
    {
        return body;
    }

    public String parse(String data) throws JSONException
    {
        return this.parse(new JSONObject(data));
    }

    @Override
    public String parse(JSONObject object) throws JSONException
    {
        return object.toString();
    }
}
