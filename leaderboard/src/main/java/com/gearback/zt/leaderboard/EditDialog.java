package com.gearback.zt.leaderboard;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gearback.methods.Methods;
import com.gearback.zt.leaderboard.R;

public class EditDialog extends CustomDialogFragment {

    Methods methods = new Methods();
    ImageView selectedImage;
    RecyclerView itemList;
    AvatarAdapter adapter;
    EditText nameValue;
    TextView acceptBtn, rejectBtn;
    OnSetClickListener listener;
    GameMethods gameMethods = new GameMethods();

    int imageId = 1;

    public static EditDialog newInstance(String userName, int userImage) {
        EditDialog f = new EditDialog();
        Bundle args = new Bundle();
        args.putString("userName", userName);
        args.putInt("userImage", userImage);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_dialog, container, false);
        selectedImage = view.findViewById(R.id.selectedImage);
        itemList = view.findViewById(R.id.itemList);
        nameValue = view.findViewById(R.id.nameValue);
        acceptBtn = view.findViewById(R.id.setDialogBtn);
        rejectBtn = view.findViewById(R.id.closeDialogBtn);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String[] images = preferences.getString("AVATARS", "").split(",");
        String[] myImages = preferences.getString("MY_AVATARS", "").split(",");
        int myCount = 0;
        if (!preferences.getString("MY_AVATARS", "").equals("")) {
            myCount = myImages.length;
        }

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        itemList.setLayoutManager(mLinearLayoutManager);
        adapter = new AvatarAdapter(getActivity(), images.length, myCount, images, myImages, new AvatarAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String id) {
                imageId = Integer.parseInt(id);
                gameMethods.PopulateImage(getActivity(), imageId, selectedImage);
            }
        });
        itemList.setAdapter(adapter);

        boolean gotImage = false;
        for (int i = 0; i < images.length; i++) {
            if (getArguments().getInt("userImage") == Integer.parseInt(images[i])) {
                adapter.mSelectedPosition = i;
                itemList.scrollToPosition(i);
                gotImage = true;
                break;
            }
        }
        if (myCount > 0 && !gotImage) {
            for (int i = 0; i < myImages.length; i++) {
                if (getArguments().getInt("userImage") == Integer.parseInt(myImages[i])) {
                    adapter.mSelectedPosition = i + images.length;
                    itemList.scrollToPosition(i + images.length);
                    break;
                }
            }
        }

        imageId = getArguments().getInt("userImage");
        gameMethods.PopulateImage(getActivity(), imageId, selectedImage);

        nameValue.setText(getArguments().getString("userName"));

        nameValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(nameValue.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = String.valueOf(nameValue.getText()).replace("\\|", " ").trim();
                if (name.equals("")) {
                    ObjectAnimator animY = ObjectAnimator.ofFloat(nameValue, "translationY", -20f, 0f);
                    animY.setDuration(1000);
                    animY.setInterpolator(new BounceInterpolator());
                    animY.start();
                }
                else {
                    listener.onAccept(String.valueOf(nameValue.getText()), imageId);
                    dismiss();
                }
            }
        });
        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onReject();
                dismiss();
            }
        });

        return view;
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
    public void SetListener(OnSetClickListener listener) {
        this.listener = listener;
    }
    public interface OnSetClickListener {
        void onAccept(String name, int image);
        void onReject();
    }
}
