package com.gearback.zt.leaderboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gearback.methods.Methods;
import com.gearback.zt.leaderboard.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeaderBoardFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView leaderBoardList;
    LeaderBoardAdapter adapter;
    TextView noLeaderBoard, backBtn, leaderBoardTitle;
    Methods methods = new Methods();
    GameMethods gameMethods = new GameMethods();
    List<Classes.LeaderBoard> leaderBoards = new ArrayList<>();
    
    String userToken = "", appToken = "";
    GameMethods.BackListener backListener = null;
    GameMethods.LoginListener loginListener = null;

    TextView rank1Name, rank1Score, rank2Name, rank2Score, rank3Name, rank3Score;
    View rank1ImageBackground, rank2ImageBackground, rank3ImageBackground;
    ImageView rank1Image, rank2Image, rank3Image;
    LinearLayout rankHolder, rank1Holder, rank2Holder, rank3Holder;
    private List<Integer> colors = Arrays.asList(R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.color5, R.color.color6, R.color.color7, R.color.color8, R.color.color9, R.color.color10, R.color.color11, R.color.color12, R.color.color13, R.color.color14, R.color.color15, R.color.color16, R.color.color17, R.color.color18, R.color.color19, R.color.color20, R.color.color21, R.color.color22, R.color.color23, R.color.color24, R.color.color25);

    BroadcastReceiver actionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            UpdateTop();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.leaderboard_fragment, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        leaderBoardList = view.findViewById(R.id.leaderBoardList);
        noLeaderBoard = view.findViewById(R.id.noLeaderBoards);
        leaderBoardTitle = view.findViewById(R.id.leaderBoardTitle);
        backBtn = view.findViewById(R.id.backBtn);

        rank1Image = view.findViewById(R.id.rank1Image);
        rank2Image = view.findViewById(R.id.rank2Image);
        rank3Image = view.findViewById(R.id.rank3Image);
        rank1ImageBackground = view.findViewById(R.id.rank1ImageBackground);
        rank2ImageBackground = view.findViewById(R.id.rank2ImageBackground);
        rank3ImageBackground = view.findViewById(R.id.rank3ImageBackground);
        rank1Name = view.findViewById(R.id.rank1Name);
        rank2Name = view.findViewById(R.id.rank2Name);
        rank3Name = view.findViewById(R.id.rank3Name);
        rank1Score = view.findViewById(R.id.rank1Score);
        rank2Score = view.findViewById(R.id.rank2Score);
        rank3Score = view.findViewById(R.id.rank3Score);
        rankHolder = view.findViewById(R.id.rankHolder);
        rank1Holder = view.findViewById(R.id.rank1Holder);
        rank2Holder = view.findViewById(R.id.rank2Holder);
        rank3Holder = view.findViewById(R.id.rank3Holder);

        leaderBoardTitle.setText(getString(R.string.level_leader_board));

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (backListener != null) {
                    backListener.onBack();
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                gameMethods.GetLeaderBoard(getActivity(), appToken, userToken, new GameMethods.LeaderBoardListener() {
                    @Override
                    public void onResult(List<Classes.LeaderBoard> list, int rank) {
                        leaderBoards = list;
                        UpdateTop();
                    }
                });
            }
        });

        getActivity().registerReceiver(actionReceiver, new IntentFilter("actionIntent"));

        return view;
    }

    public void UpdateTop() {
        if (leaderBoards.size() > 3) {
            UpdateList();
            rankHolder.setVisibility(View.VISIBLE);
            if (leaderBoards.size() > 2) {
                PopulateFirstPlace(leaderBoards.get(0));
                PopulateSecondPlace(leaderBoards.get(1));
                PopulateThirdPlace(leaderBoards.get(2));
                rank1Holder.setVisibility(View.VISIBLE);
                rank2Holder.setVisibility(View.VISIBLE);
                rank3Holder.setVisibility(View.VISIBLE);
            }
            else if (leaderBoards.size() > 1) {
                PopulateFirstPlace(leaderBoards.get(0));
                PopulateSecondPlace(leaderBoards.get(1));
                rank1Holder.setVisibility(View.VISIBLE);
                rank2Holder.setVisibility(View.VISIBLE);
                rank3Holder.setVisibility(View.INVISIBLE);
            }
            else if (leaderBoards.size() > 0) {
                PopulateFirstPlace(leaderBoards.get(0));
                rank1Holder.setVisibility(View.VISIBLE);
                rank2Holder.setVisibility(View.INVISIBLE);
                rank3Holder.setVisibility(View.INVISIBLE);
            }
            else {
                noLeaderBoard.setVisibility(View.GONE);
                leaderBoardList.setVisibility(View.VISIBLE);
                rankHolder.setVisibility(View.GONE);
            }
        }
        else {
            noLeaderBoard.setVisibility(View.GONE);
            leaderBoardList.setVisibility(View.GONE);
            rankHolder.setVisibility(View.VISIBLE);
            if (leaderBoards.size() > 2) {
                PopulateFirstPlace(leaderBoards.get(0));
                PopulateSecondPlace(leaderBoards.get(1));
                PopulateThirdPlace(leaderBoards.get(2));
                rank1Holder.setVisibility(View.VISIBLE);
                rank2Holder.setVisibility(View.VISIBLE);
                rank3Holder.setVisibility(View.VISIBLE);
            }
            else if (leaderBoards.size() > 1) {
                PopulateFirstPlace(leaderBoards.get(0));
                PopulateSecondPlace(leaderBoards.get(1));
                rank1Holder.setVisibility(View.VISIBLE);
                rank2Holder.setVisibility(View.VISIBLE);
                rank3Holder.setVisibility(View.INVISIBLE);
            }
            else if (leaderBoards.size() > 0) {
                PopulateFirstPlace(leaderBoards.get(0));
                rank1Holder.setVisibility(View.VISIBLE);
                rank2Holder.setVisibility(View.INVISIBLE);
                rank3Holder.setVisibility(View.INVISIBLE);
            }
            else {
                noLeaderBoard.setVisibility(View.GONE);
                leaderBoardList.setVisibility(View.VISIBLE);
                rankHolder.setVisibility(View.GONE);
            }
        }
    }

    public void UpdateList() {
        swipeRefreshLayout.setRefreshing(false);
        if (leaderBoards.size() > 0) {
            noLeaderBoard.setVisibility(View.GONE);
            leaderBoardList.setVisibility(View.VISIBLE);
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
            leaderBoardList.setLayoutManager(mLinearLayoutManager);
            adapter = new LeaderBoardAdapter(getActivity(), userToken, leaderBoards, false, new LeaderBoardAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {

                }
            });
            leaderBoardList.setAdapter(adapter);
        }
        else {
            noLeaderBoard.setVisibility(View.VISIBLE);
            leaderBoardList.setVisibility(View.GONE);
        }
    }
    
    public void PopulateFirstPlace(Classes.LeaderBoard item) {
        rank1Score.setText(methods.ReplaceNumber(String.valueOf(item.getHigh())));
        if (item.getName().equals("")) {
            rank1Name.setText(getString(R.string.player));
            rank1Image.setImageResource(R.drawable.image_0);
        }
        else {
            String[] temp = item.getName().split("\\|");
            if (temp.length > 1) {
                rank1Name.setText(temp[1]);
                gameMethods.PopulateImage(getActivity(), Integer.parseInt(temp[0]), rank1Image);
            }
            else {
                if (!item.getName().startsWith("|")) {
                    rank1Name.setText(getString(R.string.player));
                    gameMethods.PopulateImage(getActivity(), Integer.parseInt(item.getName().replace("|", "")), rank1Image);
                }
                else {
                    rank1Name.setText(temp[1]);
                    rank1Image.setImageResource(R.drawable.image_0);
                }
            }
        }
        int colorIndex = String.valueOf(rank1Name.getText()).length();
        if (colorIndex >= 25) {
            colorIndex -= 25;
        }
        LayerDrawable layerDrawable = (LayerDrawable) rank1ImageBackground.getBackground();
        GradientDrawable backgroundColor = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.iconColor);
        backgroundColor.setColor(ContextCompat.getColor(getActivity(), colors.get(colorIndex)));

        if (item.getToken().equals(userToken)) {
            rank1Name.setTextColor(ContextCompat.getColor(getActivity(), R.color.purpleColor));
            rank1Name.setTypeface(rank1Name.getTypeface(), Typeface.BOLD);
        }
        else {
            rank1Name.setTextColor(ContextCompat.getColor(getActivity(), R.color.darkText));
            rank1Name.setTypeface(rank1Name.getTypeface(), Typeface.NORMAL);
        }
    }

    public void PopulateSecondPlace(Classes.LeaderBoard item) {
        rank2Score.setText(methods.ReplaceNumber(String.valueOf(item.getHigh())));
        if (item.getName().equals("")) {
            rank2Name.setText(getString(R.string.player));
            rank2Image.setImageResource(R.drawable.image_0);
        }
        else {
            String[] temp = item.getName().split("\\|");
            if (temp.length > 1) {
                rank2Name.setText(temp[1]);
                gameMethods.PopulateImage(getActivity(), Integer.parseInt(temp[0]), rank2Image);
            }
            else {
                if (!item.getName().startsWith("|")) {
                    rank2Name.setText(getString(R.string.player));
                    gameMethods.PopulateImage(getActivity(), Integer.parseInt(item.getName().replace("|", "")), rank2Image);
                }
                else {
                    rank2Name.setText(temp[1]);
                    rank2Image.setImageResource(R.drawable.image_0);
                }
            }
        }
        int colorIndex = String.valueOf(rank2Name.getText()).length();
        if (colorIndex >= 25) {
            colorIndex -= 25;
        }
        LayerDrawable layerDrawable = (LayerDrawable) rank2ImageBackground.getBackground();
        GradientDrawable backgroundColor = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.iconColor);
        backgroundColor.setColor(ContextCompat.getColor(getActivity(), colors.get(colorIndex)));

        if (item.getToken().equals(userToken)) {
            rank2Name.setTextColor(ContextCompat.getColor(getActivity(), R.color.purpleColor));
            rank2Name.setTypeface(rank2Name.getTypeface(), Typeface.BOLD);
        }
        else {
            rank2Name.setTextColor(ContextCompat.getColor(getActivity(), R.color.darkText));
            rank2Name.setTypeface(rank2Name.getTypeface(), Typeface.NORMAL);
        }
    }

    public void PopulateThirdPlace(Classes.LeaderBoard item) {
        rank3Score.setText(methods.ReplaceNumber(String.valueOf(item.getHigh())));
        if (item.getName().equals("")) {
            rank3Name.setText(getString(R.string.player));
            rank3Image.setImageResource(R.drawable.image_0);
        }
        else {
            String[] temp = item.getName().split("\\|");
            if (temp.length > 1) {
                rank3Name.setText(temp[1]);
                gameMethods.PopulateImage(getActivity(), Integer.parseInt(temp[0]), rank3Image);
            }
            else {
                if (!item.getName().startsWith("|")) {
                    rank3Name.setText(getString(R.string.player));
                    gameMethods.PopulateImage(getActivity(), Integer.parseInt(item.getName().replace("|", "")), rank3Image);
                }
                else {
                    rank3Name.setText(temp[1]);
                    rank3Image.setImageResource(R.drawable.image_0);
                }
            }
        }
        int colorIndex = String.valueOf(rank3Name.getText()).length();
        if (colorIndex >= 25) {
            colorIndex -= 25;
        }
        LayerDrawable layerDrawable = (LayerDrawable) rank3ImageBackground.getBackground();
        GradientDrawable backgroundColor = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.iconColor);
        backgroundColor.setColor(ContextCompat.getColor(getActivity(), colors.get(colorIndex)));

        if (item.getToken().equals(userToken)) {
            rank3Name.setTextColor(ContextCompat.getColor(getActivity(), R.color.purpleColor));
            rank3Name.setTypeface(rank3Name.getTypeface(), Typeface.BOLD);
        }
        else {
            rank3Name.setTextColor(ContextCompat.getColor(getActivity(), R.color.darkText));
            rank3Name.setTypeface(rank3Name.getTypeface(), Typeface.NORMAL);
        }
    }

    public void SetData(String appToken, String uToken, List<Classes.LeaderBoard> list, GameMethods.BackListener backListener, GameMethods.LoginListener lListener) {
        leaderBoards = list;
        this.backListener = backListener;
        this.loginListener = lListener;
        this.appToken = appToken;
        userToken = uToken;

        UpdateTop();

        if (userToken.equals("")) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ConsentDialog consentDialog = ConsentDialog.newInstance(getString(R.string.login_prompt), getString(R.string.login_prompt_desc), getString(R.string.login), getString(R.string.skip));
                    consentDialog.SetListener(new ConsentDialog.OnSetClickListener() {
                        @Override
                        public void onAccept() {
                            if (loginListener != null) {
                                loginListener.onClick();
                            }
                        }

                        @Override
                        public void onReject() {

                        }
                    });
                    consentDialog.show(getActivity().getSupportFragmentManager(), "consentDialog");
                }
            }, 1000);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(actionReceiver);
    }
}
