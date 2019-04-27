package com.gearback.zt.leaderboard;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class AvatarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private int count, myCount;
    private String[] items, myItems;
    private Classes classes = new Classes();
    private OnItemClickListener listener;
    private GameMethods gameMethods = new GameMethods();
    public int mSelectedPosition = -1;

    public AvatarAdapter(Activity a, int count, int myCount, String[] items, String[] myItems, OnItemClickListener listener) {
        activity = a;
        this.listener = listener;
        this.items = items;
        this.myItems = myItems;
        this.count = count;
        this.myCount = myCount;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return count + myCount;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return classes.new AvatarViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Classes.AvatarViewHolder viewHolder = (Classes.AvatarViewHolder) holder;
        if (position < items.length) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mSelectedPosition != position) {
                        mSelectedPosition = position;
                        listener.onItemClick(items[position]);
                        notifyDataSetChanged();
                    }
                }
            });

            if (!items[position].equals("")) {
                gameMethods.PopulateImage(activity, Integer.parseInt(items[position]), viewHolder.image);
            }
        }
        else {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mSelectedPosition != position) {
                        mSelectedPosition = position;
                        listener.onItemClick(myItems[position - items.length]);
                        notifyDataSetChanged();
                    }
                }
            });

            if (!myItems[position - items.length].equals("")) {
                gameMethods.PopulateImage(activity, Integer.parseInt(myItems[position - items.length]), viewHolder.image);
            }
        }

        if (position == mSelectedPosition) {
            viewHolder.indicator.setVisibility(View.VISIBLE);
            viewHolder.image.setAlpha(1.0f);
            viewHolder.mouth.setAlpha(1.0f);
            viewHolder.mouth.setImageResource(R.drawable.m0);
        }
        else {
            if (position == 0 && mSelectedPosition == -1) {
                viewHolder.indicator.setVisibility(View.VISIBLE);
                viewHolder.image.setAlpha(1.0f);
                viewHolder.mouth.setAlpha(1.0f);
                viewHolder.mouth.setImageResource(R.drawable.m0);
            }
            else {
                viewHolder.indicator.setVisibility(View.INVISIBLE);
                viewHolder.image.setAlpha(0.5f);
                viewHolder.mouth.setAlpha(0.5f);
                viewHolder.mouth.setImageResource(R.drawable.m2);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String id);
    }
}
