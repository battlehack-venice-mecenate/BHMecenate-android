package com.battlehack_venice.lib.model;

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
                        .setImageUrl(object.getString("image_url"))
                        .setLat(object.getDouble("lat"))
                        .setLon(object.getDouble("lon"))
                        .setTotalDonations(object.getInt("total_donations_in_cents"));

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
    private int _totalDonations; // in cents
    private double _lat;
    private double _lon;

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

    public int getTotalDonations()
    {
        return this._totalDonations;
    }

    public Monument setTotalDonations(int totalDonations)
    {
        this._totalDonations = totalDonations;
        return this;
    }

    public Monument addDonation(int donation)
    {
        this._totalDonations += donation;
        return this;
    }

    public double getLat()
    {
        return this._lat;
    }

    public Monument setLat(double lat)
    {
        this._lat = lat;
        return this;
    }

    public double getLon()
    {
        return this._lon;
    }

    public Monument setLon(double lon)
    {
        this._lon = lon;
        return this;
    }
}
