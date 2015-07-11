package com.battlehack_venice.lib.api;

import org.json.JSONObject;


public class ApiError extends RuntimeException
{
    public static final ApiErrorParser PARSER = new ApiErrorParser() {
        @Override
        public ApiError parse(int statusCode, JSONObject object)
        {
            return ApiError.createFromJson(statusCode, object);
        }
    };

    private final int _statusCode;
    private final String _message;


    public ApiError(int statusCode)
    {
        this(statusCode, null);
    }

    public ApiError(int statusCode, String message)
    {
        super("ApiError statusCode: " + statusCode + ", message: " + message);

        this._statusCode = statusCode;
        this._message = message;
    }

    /**
     * Returns the HTTP response code or 0 on network failure.
     */
    public int getStatusCode()
    {
        return this._statusCode;
    }

    public String getMessage()
    {
        return this._message;
    }

    public boolean isNetworkError()
    {
        return this._statusCode <= 0;
    }

    public boolean isClientError()
    {
        return this._statusCode >= 400 && this._statusCode < 500;
    }

    public boolean isServerError()
    {
        return this._statusCode >= 500 && this._statusCode < 600;
    }

    public boolean isUnauthenticated()
    {
        return this._statusCode == 403;
    }

    public boolean isUnauthorized()
    {
        return this._statusCode == 401;
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof ApiError) {
            return this.equals((ApiError) object);
        } else {
            return false;
        }
    }

    public boolean equals(ApiError error)
    {
        return error != null && this._statusCode == error._statusCode
                && ((this._message == null && error._message == null) || (this._message != null && this._message.equals(error._message)));
    }

    public static ApiError createFromJson(int statusCode, JSONObject data)
    {
        // TODO:
        try {
            return new ApiError(statusCode, "Shit!");
        } catch (Exception exception) {
            return null;
        }
    }
}
