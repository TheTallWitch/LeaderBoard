package com.gearback.zt.leaderboard;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gearback.zt.leaderboard.Classes;
import com.gearback.methods.Methods;
import com.gearback.zt.leaderboard.R;

import java.util.Arrays;
import java.util.List;

public class LeaderBoardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Classes.LeaderBoard> mainItems;
    private Activity activity;
    private Classes classes = new Classes();
    private OnItemClickListener listener;
    private Methods methods = new Methods();
    private GameMethods gameMethods = new GameMethods();
    private boolean reverse;
    private String userToken;

    private List<Integer> colors = Arrays.asList(R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.color5, R.color.color6, R.color.color7, R.color.color8, R.color.color9, R.color.color10, R.color.color11, R.color.color12, R.color.color13, R.color.color14, R.color.color15, R.color.color16, R.color.color17, R.color.color18, R.color.color19, R.color.color20, R.color.color21, R.color.color22, R.color.color23, R.color.color24, R.color.color25);

    public LeaderBoardAdapter(Activity a, String userToken, List<Classes.LeaderBoard> list, boolean reverse, OnItemClickListener listener) {
        mainItems = list;
        activity = a;
        this.listener = listener;
        this.reverse = reverse;
        this.userToken = userToken;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        if (mainItems.size() > 3) {
            return mainItems.size() - 3;
        }
        else {
            return 0;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_row, parent, false);
        return classes.new LeaderBoardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Classes.LeaderBoardViewHolder viewHolder = (Classes.LeaderBoardViewHolder) holder;
        final Classes.LeaderBoard item = mainItems.get(position + 3);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(position + 3);
            }
        });

        if (item.getName().equals("")) {
            viewHolder.name.setText(activity.getString(R.string.player));
            viewHolder.image.setImageResource(R.drawable.image_0);
        }
        else {
            String[] temp = item.getName().split("\\|");
            if (temp.length > 1) {
                viewHolder.name.setText(temp[1]);
                gameMethods.PopulateImage(activity, Integer.parseInt(temp[0]), viewHolder.image);
            }
            else {
                if (!item.getName().startsWith("|")) {
                    viewHolder.name.setText(activity.getString(R.string.player));
                    gameMethods.PopulateImage(activity, Integer.parseInt(item.getName().replace("|", "")), viewHolder.image);
                }
                else {
                    viewHolder.name.setText(temp[1]);
                    viewHolder.image.setImageResource(R.drawable.image_0);
                }
            }
        }

        viewHolder.rank.setText(methods.ReplaceNumber(position + 4));
        if (reverse) {
            int score = 1000000 - item.getHigh();
            viewHolder.score.setText(methods.ReplaceNumber(score + " ثانیه"));
        }
        else {
            viewHolder.score.setText(methods.ReplaceNumber(String.valueOf(item.getHigh())));
        }

        if (item.getToken().equals(userToken)) {
            viewHolder.itemView.setBackgroundColor(Color.parseColor("#1A000000"));

            int colorIndex = String.valueOf(viewHolder.name.getText()).length();
            if (colorIndex >= 25) {
                colorIndex -= 25;
            }

            LayerDrawable layerDrawable = (LayerDrawable) viewHolder.background.getBackground();
            GradientDrawable backgroundColor = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.iconColor);
            backgroundColor.setColor(ContextCompat.getColor(activity, colors.get(colorIndex)));
        }
        else {
            viewHolder.itemView.setBackgroundColor(Color.parseColor("#00FFFFFF"));

            int colorIndex = String.valueOf(viewHolder.name.getText()).length();
            if (colorIndex >= 25) {
                colorIndex -= 25;
            }

            LayerDrawable layerDrawable = (LayerDrawable) viewHolder.background.getBackground();
            GradientDrawable backgroundColor = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.iconColor);
            backgroundColor.setColor(ContextCompat.getColor(activity, colors.get(colorIndex)));
        }

    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
