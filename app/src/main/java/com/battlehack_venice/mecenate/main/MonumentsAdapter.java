package com.battlehack_venice.mecenate.main;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.battlehack_venice.lib.ItemAdapter;
import com.battlehack_venice.lib.Monument;
import com.battlehack_venice.lib.utils.ImageLoader;
import com.battlehack_venice.mecenate.Application;
import com.battlehack_venice.mecenate.BaseActivity;
import com.battlehack_venice.mecenate.R;
import com.battlehack_venice.mecenate.monument.MonumentActivity;
import com.bumptech.glide.Glide;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MonumentsAdapter extends ItemAdapter<Monument, MonumentViewHolder>
{
    @Inject
    ImageLoader _imageLoader;

    @Override
    public void onCreate(BaseActivity activity)
    {
        super.onCreate(activity);

        Application.injector().inject(this);
    }

    @Override
    public MonumentViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.monument_card, parent, false);

        return new MonumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MonumentViewHolder holder, int position)
    {
        Monument item = get(position);

        // Hydrate
        holder.text.setText(item.getName());
        this._imageLoader.loadImage(item.getImageUrl(), holder.icon);

        holder.itemView.setOnClickListener(new OpenMonumentOnClickListener(item));
    }

    private class OpenMonumentOnClickListener implements View.OnClickListener
    {
        private final Monument _item;

        public OpenMonumentOnClickListener(Monument item)
        {
            this._item = item;
        }

        @Override
        public void onClick(View view)
        {
            Intent intent = new Intent(getActivity(), MonumentActivity.class);
            intent.putExtra(MonumentActivity.EXTRA_MONUMENT, this._item);

            getActivity().startActivity(intent);
        }
    }
}

class MonumentViewHolder extends RecyclerView.ViewHolder
{
    @InjectView(R.id.monument_card_item_image)
    public ImageView icon;
    @InjectView(R.id.monument_card_item_title)
    public TextView text;

    public MonumentViewHolder(View itemView)
    {
        super(itemView);

        ButterKnife.inject(this, itemView);
    }
}