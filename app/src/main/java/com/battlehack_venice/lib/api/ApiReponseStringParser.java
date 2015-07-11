package com.battlehack_venice.lib.api;

import com.squareup.okhttp.Headers;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alex on 11/07/15.
 */
public class ApiReponseStringParser extends ApiResponseEntityParser<String>
{
    private final String _property;

    public ApiReponseStringParser(String property)
    {
        super(null, property);

        this._property = property;
    }

    @Override
    public String parse(JSONObject object) throws JSONException
    {
        return object.getString(this._property);
    }
}
