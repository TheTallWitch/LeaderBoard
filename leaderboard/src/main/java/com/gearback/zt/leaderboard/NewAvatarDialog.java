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

public class NewAvatarDialog extends CustomDialogFragment {

    Methods methods = new Methods();
    GameMethods gameMethods = new GameMethods();
    LinearLayout avatarHolder;
    TextView newAvatarText;
    TextView setDialogBtn, closeDialogBtn;
    OnSetClickListener listener;

    public static NewAvatarDialog newInstance(int oldCount, int newCount) {
        NewAvatarDialog f = new NewAvatarDialog();
        Bundle args = new Bundle();
        args.putInt("oldCount", oldCount);
        args.putInt("newCount", newCount);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_avatar_dialog, container, false);
        avatarHolder = view.findViewById(R.id.avatarHolder);
        newAvatarText = view.findViewById(R.id.newAvatarText);
        closeDialogBtn = view.findViewById(R.id.closeDialogBtn);
        setDialogBtn = view.findViewById(R.id.setDialogBtn);

        int diff = getArguments().getInt("newCount") - getArguments().getInt("oldCount");
        newAvatarText.setText(getString(R.string.new_avatar_desc, methods.ReplaceNumber(String.valueOf(diff))));
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
        avatarHolder.removeAllViews();
        for (int i = start + 1; i <= end; i++) {
            View avatar = View.inflate(getActivity(), R.layout.avatar_row, null);
            ImageView avatarImage = avatar.findViewById(R.id.avatarImage);
            gameMethods.PopulateImage(getActivity(), i, avatarImage);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            avatar.setLayoutParams(params);
            avatarHolder.addView(avatar);
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
        int width = (int) (displaymetrics.widthPixels * 0.9);
        if (methods.isTablet(getActivity())) {
            width = (int) (displaymetrics.widthPixels * 0.5);
        }
        win.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        win.setGravity(Gravity.CENTER);
    }
}
