package com.gearback.zt.leaderboard;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
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
import android.widget.TextView;
import android.widget.Toast;

import com.gearback.methods.Methods;

import java.util.List;


public class AddFriendDialog extends CustomDialogFragment {
    Methods methods = new Methods();
    EditText mailValue;
    TextView acceptBtn, rejectBtn, emptyValue;
    GameMethods gameMethods = new GameMethods();
    OnSetClickListener listener;

    public static AddFriendDialog newInstance() {
        AddFriendDialog f = new AddFriendDialog();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_friend_dialog, container, false);
        mailValue = view.findViewById(R.id.mailValue);
        emptyValue = view.findViewById(R.id.emptyValue);
        acceptBtn = view.findViewById(R.id.setDialogBtn);
        rejectBtn = view.findViewById(R.id.closeDialogBtn);
        emptyValue.setVisibility(View.GONE);

        mailValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mailValue.getWindowToken(), 0);

                    String mail = String.valueOf(mailValue.getText());
                    if (!mail.equals("")) {
                        emptyValue.setVisibility(View.GONE);
                        AddRequest(mail);
                    }
                    else {
                        emptyValue.setVisibility(View.VISIBLE);
                    }

                    return true;
                }
                return false;
            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = String.valueOf(mailValue.getText());
                if (!mail.equals("")) {
                    emptyValue.setVisibility(View.GONE);
                    AddRequest(mail);
                }
                else {
                    emptyValue.setVisibility(View.VISIBLE);
                }
            }
        });
        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }

    void AddRequest(final String mail) {
        if (methods.isInternetAvailable(getActivity())) {
            gameMethods.AddFriend(getArguments().getString("userToken"), mail, new GameMethods.ResultListener() {
                @Override
                public void onResult(String result) {
                    gameMethods.ParseFriendsResult(result, 1, getArguments().getString("userToken"), new GameMethods.FriendListener() {
                        @Override
                        public void onResult(List<Classes.Friend> friends, List<Classes.Friend> followers, String friendsString, String followersString) {
                            listener.onFriendsClick();
                            dismiss();
                        }

                        @Override
                        public void onFail() {
                            ConsentDialog consentDialog = ConsentDialog.newInstance(getString(R.string.wrong_email), getString(R.string.wrong_email_desc), getString(R.string.share), getString(R.string.skip));
                            consentDialog.SetListener(new ConsentDialog.OnSetClickListener() {
                                @Override
                                public void onAccept() {
                                    Toast.makeText(getActivity(), R.string.share_friends, Toast.LENGTH_LONG).show();
                                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                    sharingIntent.setType("text/plain");
                                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.gearback.zt.leaderboard");
                                    startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
                                }

                                @Override
                                public void onReject() {

                                }
                            });
                            consentDialog.show(getActivity().getSupportFragmentManager(), "consentDialog");
                        }
                    });
                }
            });
        }
        else {
            ConsentDialog consentDialog = ConsentDialog.newInstance(getString(R.string.no_internet), getString(R.string.no_internet_desc), getString(R.string.try_again), "");
            consentDialog.SetListener(new ConsentDialog.OnSetClickListener() {
                @Override
                public void onAccept() {
                    AddRequest(mail);
                }

                @Override
                public void onReject() {

                }
            });
            consentDialog.show(getActivity().getSupportFragmentManager(), "consentDialog");
        }
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
    public void SetListener(OnSetClickListener listener) {
        this.listener = listener;
    }
    public interface OnSetClickListener {
        void onFriendsClick();
    }
}
