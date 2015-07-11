package com.battlehack_venice.mecenate.monument;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.battlehack_venice.lib.Monument;
import com.battlehack_venice.lib.utils.ImageLoader;
import com.battlehack_venice.mecenate.Application;
import com.battlehack_venice.mecenate.BaseActivity;
import com.battlehack_venice.mecenate.R;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MonumentActivity extends BaseActivity
{
    public static final String EXTRA_MONUMENT = "monument";

    @InjectView(R.id.monument_image)
    ImageView _coverImage;
    @InjectView(R.id.monument_title)
    TextView _name;
    @InjectView(R.id.monument_description)
    TextView _description;
    @InjectView(R.id.monument_button)
    Button _button;

    @Inject
    ImageLoader _imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.monument_activity);

        ButterKnife.inject(this);
        Application.injector().inject(this);

        if (getIntent() != null) {
            this._hydrate((Monument) getIntent().getSerializableExtra(EXTRA_MONUMENT));
        }
    }

    private void _hydrate(Monument monument)
    {
        if (monument == null) {
            return;
        }

        this._name.setText(monument.getName());
        this._description.setText(monument.getDescription());
        this._imageLoader.loadImage(monument.getImageUrl(), this._coverImage);

        this._initPaypal();
    }

    private void _initPaypal()
    {
        // TODO:
    }
}
