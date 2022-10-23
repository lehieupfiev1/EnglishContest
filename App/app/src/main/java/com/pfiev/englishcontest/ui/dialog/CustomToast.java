package com.pfiev.englishcontest.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.content.ContextCompat;

import com.pfiev.englishcontest.R;

public class CustomToast extends Toast {

    public static final int SUCCESS = 1;
    public static final int WARNING = 2;
    public static final int ERROR = 3;
    public static final int CONFUSING = 4;

    private static long SHORT = Toast.LENGTH_SHORT;
    private static long LONG = Toast.LENGTH_LONG;

    public CustomToast(Context context) {
        super(context);
    }

    public static Toast makeText(Context context, String message, int type, int duration) {
        Toast toast = new Toast(context);
        toast.setDuration(duration);
        View layout = LayoutInflater.from(context)
                .inflate(R.layout.toast_custom, null, false);
        TextView messageTextView = (TextView) layout.findViewById(R.id.toast_custom_text);
        LinearLayout linearLayout = (LinearLayout) layout.findViewById(R.id.toast_custom_type);
        ImageView frontIcon = (ImageView) layout.findViewById(R.id.toast_custom_icon);
        messageTextView.setText(message);

        switch (type) {
            case SUCCESS:
                frontIcon.setImageResource(R.drawable.ic_baseline_check_24);

//                linearLayout.setBackgroundColor((context.getColor(R.color.toast_custom_success)));
                break;
            case WARNING:
                frontIcon.setImageResource(R.drawable.ic_baseline_check_24);

                linearLayout.getBackground().setColorFilter(
                        ContextCompat.getColor(context, R.color.toast_custom_warning),
                        PorterDuff.Mode.SRC_ATOP);
                break;
//                linearLayout.setBackgroundColor((context.getColor(R.color.toast_custom_warning)));
            case ERROR:
                frontIcon.setImageResource(R.drawable.ic_baseline_check_24);
                linearLayout.getBackground().setColorFilter(
                        ContextCompat.getColor(context, R.color.toast_custom_error),
                        PorterDuff.Mode.SRC_ATOP);
                break;
            case CONFUSING:
                linearLayout.getBackground().setColorFilter(
                        ContextCompat.getColor(context, R.color.toast_custom_confusing),
                        PorterDuff.Mode.SRC_ATOP);
                break;
            default:
                break;
        }
        toast.setView(layout);
        return toast;
    }
}
