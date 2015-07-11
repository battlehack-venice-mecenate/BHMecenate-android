package com.battlehack_venice.lib.api;


import org.json.JSONException;
import org.json.JSONObject;

public class ApiResponseEntityParser<T extends Object> extends ApiResponseJsonParser<T>
{
    private final ApiResponseJsonParser<T> _parser;
    private final String _property;

    public ApiResponseEntityParser(ApiResponseJsonParser<T> parser, String property)
    {
        this._parser = parser;
        this._property = property;
    }

    @Override
    public T parse(JSONObject object) throws JSONException
    {
        return this._parser.parse(object.getJSONObject(this._property));
    }
}
