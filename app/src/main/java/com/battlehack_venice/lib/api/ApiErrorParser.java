package com.battlehack_venice.lib.api;

import org.json.JSONObject;

public abstract class ApiErrorParser
{
    /**
     * Parse the input data. Never returns null.
     */
    public ApiError parse(int statusCode, String data)
    {
        return new ApiError(statusCode, data);
    }

    /**
     * Parse the input object. Returns null on error.
     */
    public abstract ApiError parse(int statusCode, JSONObject object);
}
