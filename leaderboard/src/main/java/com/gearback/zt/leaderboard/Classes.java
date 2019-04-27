package com.gearback.zt.leaderboard;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.nio.file.Path;
import java.util.Date;
import java.util.List;

public class Classes {

    public class Score {
        private String user;
        private int level;
        private int score;
        private int goal;
        private int high;

        public Score(String user, int level, int score, int goal, int high) {
            this.user = user;
            this.level = level;
            this.score = score;
            this.goal = goal;
            this.high = high;
        }

        public String getUser() {return user;}
        public int getLevel() {return level;}
        public int getScore() {return score;}
        public int getGoal() {return goal;}
        public int getHigh() {return high;}
    }

    public class LeaderBoard implements Comparable<LeaderBoard> {
        private String token;
        private String name;
        private int level;
        private int score;
        private int high;
        private boolean isAdded;

        public LeaderBoard(String token, String name, int level, int score, int high, boolean isAdded) {
            this.token = token;
            this.name = name;
            this.level = level;
            this.score = score;
            this.high = high;
            this.isAdded = isAdded;
        }

        public String getToken() {return token;}
        public String getName() {return name;}
        public int getLevel() {return level;}
        public int getScore() {return score;}
        public int getHigh() {return high;}
        public boolean isAdded() {return isAdded;}
        public void setAdded(boolean isAdded) {this.isAdded = isAdded;}

        @Override
        public int compareTo(@NonNull LeaderBoard o) {
            return Integer.compare(o.getHigh(), getHigh());
        }
    }

    public class LeaderBoardViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView score;
        public TextView rank;
        public ImageView image;
        public View background;

        public LeaderBoardViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.leaderBoardName);
            score = itemView.findViewById(R.id.leaderBoardScore);
            rank = itemView.findViewById(R.id.leaderBoardRank);
            image = itemView.findViewById(R.id.leaderBoardImage);
            background = itemView.findViewById(R.id.leaderBoardImageBackground);
        }
    }

    public class AddViewHolder extends RecyclerView.ViewHolder {
        public AddViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class AvatarViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public ImageView mouth;
        public View indicator;

        public AvatarViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.avatarImage);
            mouth = itemView.findViewById(R.id.avatarMouth);
            indicator = itemView.findViewById(R.id.avatarIndicator);
        }
    }

    public class CharacterViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public ImageView mouth;
        public TextView name;

        public CharacterViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.playerImage);
            mouth = itemView.findViewById(R.id.playerImageMouth);
            name = itemView.findViewById(R.id.playerName);
        }
    }

    public class Friend {
        private String token;
        private String user;

        public Friend(String token, String user) {
            this.token = token;
            this.user = user;
        }

        public String getToken() {return token;}
        public String getUser() {return user;}
    }
}
