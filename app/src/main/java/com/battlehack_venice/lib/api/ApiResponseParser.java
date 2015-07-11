package com.battlehack_venice.lib.api;

import com.squareup.okhttp.Headers;


public interface ApiResponseParser<T extends Object>
{
    /**
     * Parse an API response. Returns null on error.
     *
     * @param headers   Response headers
     * @param body      Response body
     */
    public T parse(Headers headers, String body) throws Exception;
}
