package com.battlehack_venice.lib.api;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ApiResponseListParser<T extends Object> extends ApiResponseJsonParser<List<T>>
{
    private final ApiResponseJsonParser<T> _parser;
    private final String _property;


    public ApiResponseListParser(ApiResponseJsonParser<T> parser, String property)
    {
        this._parser = parser;
        this._property = property;
    }

    @Override
    public List<T> parse(JSONObject object) throws JSONException
    {
        try {
            JSONArray values = object.getJSONArray(this._property);
            List<T> result = new ArrayList<T>(values.length());

            for (int i = 0, length = values.length(); i < length; i++) {
                T value = this._parser.parse(values.getJSONObject(i));
                if (value != null) {
                    result.add(value);
                }
            }

            return result;
        } catch (JSONException exception) {
            throw new JSONException("Unable to parse API JSON list response: " + exception.getMessage());
        }
    }
}