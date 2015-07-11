package com.battlehack_venice.lib;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MonumentsAdapter extends ItemAdapter<Monument, MonumentViewHolder>
{
    @Override
    public MonumentViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.activity_list_item, parent, false);

        return new MonumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MonumentViewHolder holder, int position)
    {
        Monument item = get(position);

        // Hydrate
        holder.text.setText(item.getName());

        Glide.with(getActivity())
                .load(item.getImageUrl())
                .into(holder.icon);
    }
}

class MonumentViewHolder extends RecyclerView.ViewHolder
{
    @InjectView(android.R.id.icon)
    public ImageView icon;
    @InjectView(android.R.id.text1)
    public TextView text;

    public MonumentViewHolder(View itemView)
    {
        super(itemView);

        ButterKnife.inject(this, itemView);
    }
}