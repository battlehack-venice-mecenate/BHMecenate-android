package com.battlehack_venice.lib;

/**
 * Created by alex on 11/07/15.
 */
public class Monument
{
    private final long _id;
    private String _name;
    private String _imageUrl;

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
}
