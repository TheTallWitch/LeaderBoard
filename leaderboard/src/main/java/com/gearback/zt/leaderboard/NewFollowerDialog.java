package com.gearback.zt.leaderboard;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gearback.methods.Methods;
import com.gearback.zt.leaderboard.R;

import java.util.List;

public class NewFollowerDialog extends CustomDialogFragment {

    Methods methods = new Methods();
    GameMethods gameMethods = new GameMethods();
    LinearLayout followerHolder;
    TextView newFollowerText;
    TextView setDialogBtn, closeDialogBtn;
    OnSetClickListener listener;

    public static NewFollowerDialog newInstance(int oldCount, int newCount, List<Classes.LeaderBoard> followerLeaderBoard) {
        String[] array = new String[followerLeaderBoard.size()];
        for(int i = 0; i < followerLeaderBoard.size(); i++) array[i] = followerLeaderBoard.get(i).getName();
        NewFollowerDialog f = new NewFollowerDialog();
        Bundle args = new Bundle();
        args.putInt("oldCount", oldCount);
        args.putInt("newCount", newCount);
        args.putStringArray("names", array);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_follower_dialog, container, false);
        followerHolder = view.findViewById(R.id.followerHolder);
        newFollowerText = view.findViewById(R.id.newFollowerText);
        closeDialogBtn = view.findViewById(R.id.closeDialogBtn);
        setDialogBtn = view.findViewById(R.id.setDialogBtn);

        int diff = getArguments().getInt("newCount") - getArguments().getInt("oldCount");
        newFollowerText.setText(getString(R.string.new_follower_desc, methods.ReplaceNumber(String.valueOf(diff))));
        AddAvatars();

        closeDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAccept();
                dismiss();
            }
        });

        return view;
    }

    public void AddAvatars() {
        int start = getArguments().getInt("oldCount"), end = getArguments().getInt("newCount");
        String[] followers = getArguments().getStringArray("names");
        followerHolder.removeAllViews();
        for (int i = start; i < end; i++) {
            try {
                View avatar = View.inflate(getActivity(), R.layout.follower_item, null);
                ImageView avatarImage = avatar.findViewById(R.id.avatarImage);
                TextView avatarName = avatar.findViewById(R.id.avatarName);

                String[] temp = followers[i].split("\\|");
                if (temp.length > 1) {
                    avatarName.setText(temp[1]);
                    gameMethods.PopulateImage(getActivity(), Integer.parseInt(temp[0]), avatarImage);
                }
                else {
                    avatarName.setText(followers[i]);
                    avatarImage.setImageResource(R.drawable.image_0);
                }

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                avatar.setLayoutParams(params);
                followerHolder.addView(avatar);
            }
            catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    public void SetListener(OnSetClickListener listener) {
        this.listener = listener;
    }
    public interface OnSetClickListener {
        void onAccept();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.fade_animation;
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Window win = getDialog().getWindow();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) (displaymetrics.widthPixels * 0.98);
        if (methods.isTablet(getActivity())) {
            width = (int) (displaymetrics.widthPixels * 0.5);
        }
        win.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        win.setGravity(Gravity.CENTER);
    }
}
