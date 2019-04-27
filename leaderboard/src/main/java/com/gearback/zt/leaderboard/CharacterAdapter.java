package com.gearback.zt.leaderboard;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class CharacterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<Classes.LeaderBoard> items;
    private Classes classes = new Classes();
    private OnItemClickListener listener;
    private GameMethods gameMethods = new GameMethods();
    private String userName;
    private int userImage;

    private static final int HEADER_TYPE = 0, ITEM_TYPE = 1, ADD_TYPE = 2;

    public CharacterAdapter(Activity a, String userName, int userImage, List<Classes.LeaderBoard> items, OnItemClickListener listener) {
        activity = a;
        this.listener = listener;
        this.items = items;
        this.userName = userName;
        this.userImage = userImage;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return items.size() + 2;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER_TYPE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.character_header_row, parent, false);
            return classes.new CharacterViewHolder(v);
        }
        else if (viewType == ITEM_TYPE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.character_row, parent, false);
            return classes.new CharacterViewHolder(v);
        }
        else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_row, parent, false);
            return classes.new AddViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        if (viewType == HEADER_TYPE) {
            final Classes.CharacterViewHolder viewHolder = (Classes.CharacterViewHolder) holder;
            viewHolder.name.setText(userName);
            gameMethods.PopulateImage(activity, userImage, viewHolder.image);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onMeClick();
                }
            });
        }
        else if (viewType == ITEM_TYPE) {
            final Classes.CharacterViewHolder viewHolder = (Classes.CharacterViewHolder) holder;
            Classes.LeaderBoard item = items.get(position - 1);
            String[] temp = item.getName().split("\\|");
            if (temp.length > 1) {
                viewHolder.name.setText(temp[1]);
                gameMethods.PopulateImage(activity, Integer.parseInt(temp[0]), viewHolder.image);
            }
            else {
                viewHolder.name.setText(item.getName());
                viewHolder.image.setImageResource(R.drawable.image_0);
            }
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position);
                }
            });
        }
        else {
            Classes.AddViewHolder viewHolder = (Classes.AddViewHolder) holder;
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAddClick();
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onMeClick();
        void onItemClick(int position);
        void onAddClick();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER_TYPE;
        }
        else if (position == items.size() + 1) {
            return ADD_TYPE;
        }
        else {
            return ITEM_TYPE;
        }
    }
}
