package com.battlehack_venice.lib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.battlehack_venice.mecenate.R;

public class FixedRatioImageView extends ImageView
{
    private float _widthRatio = 1f;
    private float _heightRatio = 1f;

    public FixedRatioImageView(Context context)
    {
        super(context);
    }

    public FixedRatioImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        this._initFromAttributes(attrs);
    }

    public FixedRatioImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        this._initFromAttributes(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = Math.round(width * this._heightRatio / this._widthRatio);

        this.setMeasuredDimension(width, height);
    }

    private void _initFromAttributes(AttributeSet attrs)
    {
        TypedArray a = this.getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.FixedRatioImageView, 0, 0);

        try {
            this._widthRatio = a.getFloat(R.styleable.FixedRatioImageView_widthRatio, 1f);
            this._heightRatio = a.getFloat(R.styleable.FixedRatioImageView_heightRatio, 1f);
        } finally {
            a.recycle();
        }
    }
}
