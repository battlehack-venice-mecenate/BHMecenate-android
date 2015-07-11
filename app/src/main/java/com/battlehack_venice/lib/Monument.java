package com.battlehack_venice.lib;

import com.battlehack_venice.lib.api.ApiResponseJsonParser;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by alex on 11/07/15.
 */
public class Monument implements Serializable
{
    public static final ApiResponseJsonParser<Monument> PARSER = new ApiResponseJsonParser<Monument>()
    {
        @Override
        public Monument parse(JSONObject object)
        {
            try {
                return new Monument(object.getLong("id"))
                        .setName(object.getString("name"))
                        .setDescription(object.getString("description"))
                        .setImageUrl(object.getString("image_url"));

            } catch (Exception e) {
                return null;
            }
        }
    };
    private static final long serialVersionUID = 1L;

    private final long _id;
    private String _name;
    private String _imageUrl;
    private String _description;

    public Monument(long id)
    {
        this._id = id;
    }

    public Monument setName(String name)
    {
        this._name = name;
        return this;
    }

    public long getId()
    {
        return this._id;
    }

    public String getName()
    {
        return this._name;
    }

    public String getImageUrl()
    {
        return this._imageUrl;
    }

    public Monument setImageUrl(String imageUrl)
    {
        this._imageUrl = imageUrl;
        return this;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Monument)) {
            return false;
        }

        return this.equals((Monument) o);
    }

    public boolean equals(Monument o)
    {
        return this.getId() == o.getId();
    }

    public String getDescription()
    {
        return this._description;
    }

    public Monument setDescription(String description)
    {
        this._description = description;
        return this;
    }
}
