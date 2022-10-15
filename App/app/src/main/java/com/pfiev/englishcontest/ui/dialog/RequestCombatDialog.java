package com.pfiev.englishcontest.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pfiev.englishcontest.ExperimentalActivity;
import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.model.NotificationItem;
import com.pfiev.englishcontest.ui.experimental.FindingMatchFragment;
import com.pfiev.englishcontest.ui.wiget.RoundedAvatarImageView;

public class RequestCombatDialog extends Dialog implements
        View.OnClickListener {

    private Bundle mBundle;
    public Button acceptBtn, rejectBtn;
    private RoundedAvatarImageView avatar;
    private TextView username;
    private String matchId;

    public RequestCombatDialog(@NonNull Context context) {
        super(context);
    }

    public void setData(Bundle bundle) {
        this.mBundle = bundle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_request_combat);

        avatar = (RoundedAvatarImageView) findViewById(R.id.dialog_request_combat_avatar);
        avatar.load(getContext(), mBundle.getString(NotificationItem.FIELD_NAME.USER_PHOTO_URL));

        username = (TextView) findViewById(R.id.dialog_request_combat_username);
        username.setText(mBundle.getString(NotificationItem.FIELD_NAME.USER_NAME));

        acceptBtn = (Button) findViewById(R.id.dialog_request_combat_accept_btn);
        rejectBtn = (Button) findViewById(R.id.dialog_request_combat_reject_btn);
        acceptBtn.setOnClickListener(this);
        rejectBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_request_combat_accept_btn:
                Intent intent = new Intent(getContext(), ExperimentalActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(
                        ExperimentalActivity.START_FRAGMENT_KEY,
                        ExperimentalActivity.FRAGMENT.FINDING_MATCH);
                bundle.putString(
                        FindingMatchFragment.MATCH_ID_FIELD,
                        mBundle.getString(NotificationItem.FIELD_NAME.MATCH_ID)
                );
                bundle.putBoolean(FindingMatchFragment.IS_OWNER_FIELD, false);
                intent.putExtras(bundle);
                getContext().startActivity(intent, bundle);
                break;
            case R.id.dialog_request_combat_reject_btn:

                break;
            default:
                break;
        }
        dismiss();
    }
}
