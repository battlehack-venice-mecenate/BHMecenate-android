package com.battlehack_venice.lib;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;

abstract public class ItemAdapter<T, VH extends RecyclerView.ViewHolder> extends RxAdapter<T, VH>
{
    private ArrayList<T> _items;

    public ItemAdapter()
    {
        this._items = new ArrayList<T>();
    }

    public T get(int position)
    {
        return this._items.get(position);
    }

    @Override
    public int getItemCount()
    {
        return this._items.size();
    }

    @Override
    public void onNext(T item)
    {
        // Handle refresh
        if (this._refreshing) {
            this._refreshing = false;

            this._items.clear();
            this.notifyDataSetChanged();
        }

        // Add the item
        this.add(item);
    }

    @Override
    public void onCompleted()
    {
        if (!this._refreshing) {
            return;
        }

        this._refreshing = false;

        this._items.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public void onError(Throwable e)
    {
        this._refreshing = false;
    }

    public void add(T item)
    {
        if (this._items.contains(item)) {
            this.update(item);
            return;
        }

        this._items.add(item);
        this.notifyItemInserted(this._items.size() - 1);
    }

    public void add(int position, T item)
    {
        int safePosition = position < 0 ? 0 : position > this._items.size() ? this._items.size() : position;
        int index = this._items.indexOf(item);

        if (index >= 0) {
            this._items.remove(index);
            this._items.add(safePosition, item);
            this.notifyItemMoved(index, safePosition);
            return;
        }

        this._items.add(safePosition, item);
        this.notifyItemInserted(safePosition);
    }

    public void update(T item)
    {
        int index = this._items.indexOf(item);
        if (index < 0) {
            return;
        }

        this._items.set(index, item);
        this.notifyItemChanged(index);
    }

    public void remove(T item)
    {
        int index = this._items.indexOf(item);
        if (index < 0) {
            return;
        }

        this._items.remove(index);
        this.notifyItemRemoved(index);
    }

    public void clear()
    {
        this._items.clear();
        this.notifyDataSetChanged();
    }

    public void add(Collection<T> items)
    {
        for (T item : items) {
            this.add(item);
        }
    }
}
