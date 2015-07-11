package com.battlehack_venice.lib;

import com.battlehack_venice.lib.api.ApiResponseJsonParser;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by alex on 11/07/15.
 */
public class Donation implements Serializable
{
    public static final ApiResponseJsonParser<Donation> PARSER = new ApiResponseJsonParser<Donation>()
    {
        @Override
        public Donation parse(JSONObject object)
        {
            try {
                return new Donation(object.getLong("id"))
                        .setAmount(object.getInt("amount_in_cents"));

            } catch (Exception e) {
                return null;
            }
        }
    };
    private static final long serialVersionUID = 1L;

    private final long _id;
    private int _amount; // in cents

    public Donation(long id)
    {
        this._id = id;
    }

    public int getAmount()
    {
        return this._amount;
    }

    public Donation setAmount(int amount)
    {
        this._amount = amount;
        return this;
    }
}
