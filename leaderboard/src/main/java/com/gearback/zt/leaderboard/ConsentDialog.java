package com.gearback.zt.leaderboard;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.gearback.methods.Methods;
import com.gearback.zt.leaderboard.CustomDialogFragment;
import com.gearback.zt.leaderboard.R;

public class ConsentDialog extends CustomDialogFragment {

    Methods methods = new Methods();
    TextView consentTitle, consentText;
    TextView acceptBtn, rejectBtn;
    OnSetClickListener listener;

    public static ConsentDialog newInstance(String title, String text, String accept, String reject) {
        ConsentDialog f = new ConsentDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("text", text);
        args.putString("accept", accept);
        args.putString("reject", reject);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.consent_dialog, container, false);
        consentTitle = view.findViewById(R.id.consentTitle);
        consentText = view.findViewById(R.id.consentText);
        acceptBtn = view.findViewById(R.id.setDialogBtn);
        rejectBtn = view.findViewById(R.id.closeDialogBtn);

        consentTitle.setText(getArguments().getString("title"));
        consentText.setText(getArguments().getString("text"));
        acceptBtn.setText(getArguments().getString("accept"));
        rejectBtn.setText(getArguments().getString("reject"));

        if (getArguments().getString("reject").equals("")) {
            rejectBtn.setVisibility(View.GONE);
        }

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onAccept();
                dismiss();
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
        void onAccept();
        void onReject();
    }
}
