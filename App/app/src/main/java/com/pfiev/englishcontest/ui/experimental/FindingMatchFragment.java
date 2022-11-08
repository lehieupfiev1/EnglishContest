package com.pfiev.englishcontest.ui.experimental;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.pfiev.englishcontest.EnglishApplication;
import com.pfiev.englishcontest.GlobalConstant;
import com.pfiev.englishcontest.PlayGameActivity;
import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.databinding.FragmentExperimentalFindingmatchBinding;
import com.pfiev.englishcontest.firestore.FireStoreClass;
import com.pfiev.englishcontest.firestore.MatchCollection;
import com.pfiev.englishcontest.model.ChoiceItem;
import com.pfiev.englishcontest.model.FriendItem;
import com.pfiev.englishcontest.model.QuestionItem;
import com.pfiev.englishcontest.realtimedb.FriendList;
import com.pfiev.englishcontest.realtimedb.Status;
import com.pfiev.englishcontest.ui.dialog.CustomToast;
import com.pfiev.englishcontest.ui.wiget.MenuBubble;
import com.pfiev.englishcontest.utils.MatchJoinable;
import com.pfiev.englishcontest.utils.SharePreferenceUtils;
import com.pfiev.englishcontest.utils.TextUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FindingMatchFragment extends Fragment {

    private final String TAG = "FindingMatchFragment";
    private FragmentExperimentalFindingmatchBinding mBinding;
    private static boolean isFirstChangePlay = false;
    ArrayList<QuestionItem> mListQuestion;
    public String mOtherUserId = " ";
    public String mMatchId = " ";
    public boolean mIsOwner;
    private boolean mIsAccepted = false;
    ListenerRegistration listenerRegistration;
    private UpdateUI updateUI;

    public final static String MATCH_ID_FIELD = "match_id";
    public final static String IS_OWNER_FIELD = "is_owner";
    // Min time allow to show by milliseconds
    private final long MIN_TIME_SHOW_AGREE = 1000;

    public static FindingMatchFragment newInstance() {
        return new FindingMatchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentExperimentalFindingmatchBinding.inflate(inflater, container, false);
        if (updateUI == null) updateUI = new UpdateUI();
        mBinding.findingBtn.setOnClickListener(view -> {
            String timeBlockRemain = MatchJoinable.getBlockTimeRemainString(getContext());
            if (!timeBlockRemain.isEmpty()) {
                updateUI.showBlockTimeRemainToast(timeBlockRemain);
                return;
            }
            mBinding.findingBtn.setEnabled(false);
            mBinding.findingMenuBubble.fadeOut(
                    updateUI::showLooking
            );
            sendRequestFindMatch();
        });

        // Check if navigate from request combat
        if (getArguments() != null) {
            String matchId = getArguments().getString(MATCH_ID_FIELD);
            mIsOwner = getArguments().getBoolean(IS_OWNER_FIELD);

            if (matchId != null && !matchId.isEmpty()) {
                mMatchId = matchId;
                mIsAccepted = true;
                listenerStateMatchHistoryDoc(matchId);
                // hide button and image necessary
                mBinding.findingBtn.setVisibility(View.GONE);
                // Send request join match or show waiting screen
                if (!mIsOwner) sendRequestAcceptJoinMatch(matchId);
                updateUI.addConfirmationLayout();
                updateUI.showWaitingCompetitor();
                return mBinding.getRoot();
            }
        }

        return mBinding.getRoot();
    }

    private void sendRequestFindMatch() {
        Log.i(TAG, "sendRequestFindMatch ");
        Toast.makeText(getContext(), "Finding match", Toast.LENGTH_SHORT).show();
        String uid = SharePreferenceUtils.getString(getContext(), GlobalConstant.USER_ID);
        FireStoreClass.findMatchRequest(uid).addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Exception e = task.getException();
                    if (e.getMessage() != null)
                        Log.i(TAG, " task is not success :" + e.getMessage());
                } else {
                    Map data = TextUtils.convertHashMap(task.getResult());
                    String matchId = (String) data.get(GlobalConstant.MATCH_ID);
                    String ownerId = (String) data.get(GlobalConstant.OWNER_ID);
                    mMatchId = matchId;
                    mIsOwner = uid.equalsIgnoreCase(ownerId);
                    listenerStateMatchHistoryDoc(matchId);
                }
            }
        });
    }

    public void listenerStateMatchHistoryDoc(String matchId) {
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(GlobalConstant.MATCH_HISTORY).document(matchId);
        listenerRegistration = docRef.addSnapshotListener(MetadataChanges.EXCLUDE,
                new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {


                        String state = (String) value.get(GlobalConstant.STATE);
                        if (state == null) {
                            waitingEnableFind(mIsAccepted);
                            return;
                        }
                        switch (state) {
                            case GlobalConstant.FINDING_STATE:
                                isFirstChangePlay = false;
                                // If user accept last time then looking match again
                                if (mIsAccepted) {
                                    updateUI.hideMask();
                                    updateUI.clearConfirmationLayout();
                                    updateUI.showLooking();
                                    updateUI.showCompetitorNotJoinToast();
                                }
                                break;
                            case GlobalConstant.WAITING_ACCEPT_STATE:
                                isFirstChangePlay = false;
                                mIsAccepted = false;
                                long createdTime = (Long) value.get(MatchCollection.FIELD_NAME.CREATED_AT);
                                showAgreeDialog(matchId, createdTime);
                                break;
                            case GlobalConstant.PLAYING_STATE:
                                if (isFirstChangePlay) {
                                    getMatchHistoryData(value);
                                } else {
                                    isFirstChangePlay = true;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                });

    }

    public void getMatchHistoryData(@Nullable DocumentSnapshot value) {
        String uid = SharePreferenceUtils.getString(getContext(), GlobalConstant.USER_ID);
        List<String> participants = (ArrayList) value.get(GlobalConstant.PARTICIPANTS);
        for (int i = 0; i < participants.size(); i++) {
            String id = participants.get(i);
            if (!uid.equalsIgnoreCase(id)) {
                mOtherUserId = id;
                break;
            }
        }


        List<HashMap> question_list = (ArrayList) value.get(GlobalConstant.LIST_QUESTION);
        mListQuestion = new ArrayList<>();
        for (int i = 0; i < question_list.size(); i++) {
            HashMap hashMap = question_list.get(i);
            String question = (String) hashMap.get(GlobalConstant.QUESTION);
            long question_id = (Long) hashMap.get(GlobalConstant.QUESTION_ID);
            long answer = (Long) hashMap.get(GlobalConstant.ANSWER);
            List<HashMap> list_choices = (ArrayList) hashMap.get(GlobalConstant.CHOICES);
            ArrayList<ChoiceItem> list_choice_item = new ArrayList<>();
            for (int j = 0; j < list_choices.size(); j++) {
                long content_id = (Long) list_choices.get(j).get(GlobalConstant.CONTENT_ID);
                String content = (String) list_choices.get(j).get(GlobalConstant.CONTENT);
                ChoiceItem choiceItem = new ChoiceItem(content_id, content);
                list_choice_item.add(choiceItem);
            }
            QuestionItem questionItem = new QuestionItem(question_id, question, list_choice_item, answer, 0);
            mListQuestion.add(questionItem);
        }
        waitLoadingQuestion();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerRegistration != null)
            listenerRegistration.remove();
    }

    public void showAgreeDialog(String match_id, long createdTime) {
        mIsAccepted = false;
        // Check if has time remain from match created time
        long timePass = Instant.now().getEpochSecond() - createdTime;
        // Time remain by milliseconds
        long timeRemain = (GlobalConstant.DEFAULT_WAITING_JOIN_TIME - timePass) * 1000;
        if (timeRemain > MIN_TIME_SHOW_AGREE) {
            updateUI.showMask();
            updateUI.hideLooking(() -> {
                // Show join confirm dialog and set callback
                // when time to confirmation is end
                updateUI.showJoinConfirm(timeRemain, () -> {
                    updateUI.hideMask();
                    if (!mIsAccepted) {
                        if (listenerRegistration != null)
                            listenerRegistration.remove();
                        // Set block time and show toast
                        MatchJoinable.setBlockFromNow(getContext());
                        MatchJoinable.increaseNotJoinMatchNumber(getContext());
                        String timeBlockRemain = MatchJoinable.getBlockTimeRemainString(getContext());
                        updateUI.showBlockTimeRemainToast(timeBlockRemain);
                        // Change ui to default
                        updateUI.clearConfirmationLayout();
                        updateUI.showFindingButton();
                        listenerRegistration.remove();
                    }
                });
                // Send join match request when click button
                updateUI.getAcceptActionElem().setOnClickListener(
                        view -> {
                            mIsAccepted = true;
                            updateUI.hideMask();
                            updateUI.showWaitingCompetitor();
                            MatchJoinable.resetNotJoinMatchNumber(getContext());
                            sendRequestAcceptJoinMatch(match_id);
                        });

            });
        }
    }

    public void sendRequestAcceptJoinMatch(String match_id) {
        String uid = SharePreferenceUtils.getString(getContext(), GlobalConstant.USER_ID);
        FireStoreClass.joinMatchRequest(uid, match_id).addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Exception e = task.getException();
                    if (e.getMessage() != null)
                        Log.i(TAG, " task is not success :" + e.getMessage());
                }
            }
        });

    }

    private void waitLoadingQuestion() {
        new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                navigatePlayActivity();
            }
        }.start();
    }

    /**
     * Finding again after fail matching.
     * User will be waiting if not accept last time
     *
     * @param isAccepted is user join last time
     */
    private void waitingEnableFind(boolean isAccepted) {
        if (isAccepted) {
            updateUI.hideMask();
            updateUI.clearConfirmationLayout();
            updateUI.showFindingButton();
            mBinding.findingBtn.performClick();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // update playing status to friends
        String ownerUid = SharePreferenceUtils.getUserData(getActivity()).getUserId();
        FriendList friendList = FriendList.getInstance();
        friendList.setUid(ownerUid);
        friendList.updateStatusToFriends(FriendItem.STATUS.PLAYING);
        // Set current status
        Status.getInstance().setState(Status.STATE_PLAYING);
        EnglishApplication.setCurrentUserStatus(Status.STATE_PLAYING);

        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private void navigatePlayActivity() {
        //Add data info player
        Intent intent = new Intent(getActivity(), PlayGameActivity.class);
        Bundle bundle = new Bundle();
        Log.i(TAG, "navigatePlayActivity mListQuestion :" + mListQuestion.size());
        bundle.putParcelableArrayList("ListQuestion", mListQuestion);
        intent.putExtras(bundle);
        intent.putExtra(GlobalConstant.MATCH_ID, mMatchId);
        intent.putExtra("isOwner", mIsOwner);
        intent.putExtra("CompetitorId", mOtherUserId);
        startActivity(intent);
    }

    private class UpdateUI {
        private final ImageView maskLayer;
        private final LottieAnimationView lookingLottie;
        private final TextView findingBtn;
        private final TextView introMess;
        private final MenuBubble menuBubble;
        private TextView acceptBtn;
        private CountDownTimer countDownAccept;
        private View confirmationLayout;

        public UpdateUI() {
            maskLayer = mBinding.findingMatchMaskOverlay;
            lookingLottie = mBinding.findingProgress;
            findingBtn = mBinding.findingBtn;
            introMess = mBinding.findingMatchBubbleIntroMess;
            menuBubble = mBinding.findingMenuBubble;
        }

        /**
         * Show mask overlay
         */
        public void showMask() {
            maskLayer.setAlpha(0f);
            maskLayer.setVisibility(View.VISIBLE);
            maskLayer.animate().alpha(1f).setDuration(500);
        }

        /**
         * Hide mask overlay
         */
        public void hideMask() {
            maskLayer.animate().alpha(0f).setDuration(500);
            maskLayer.setVisibility(View.GONE);
        }

        /**
         * Show looking element
         */
        public void showLooking() {
            introMess.setText(
                    R.string.finding_match_bubble_intro_mess_looking
            );
            findingBtn.setVisibility(View.INVISIBLE);
            lookingLottie.setVisibility(View.VISIBLE);
            lookingLottie.playAnimation();
            menuBubble.fadeIn();
        }

        /**
         * Show finding button
         */
        public void showFindingButton() {
            findingBtn.setVisibility(View.VISIBLE);
            findingBtn.setEnabled(true);
        }

        /**
         * Hide looking
         */
        public void hideLooking(MenuBubble.FadeOutCallback callback) {
            lookingLottie.setVisibility(View.GONE);
            menuBubble.fadeOut(callback);
        }

        /**
         * Show join match confirmation element
         *
         * @param timeRemain time remain to confirm
         */
        public void showJoinConfirm(long timeRemain, Runnable confirmationTimeout) {
            // Change intro mess, add confirmation layout
            introMess.setText(R.string.finding_match_bubble_intro_mess_found);
            addConfirmationLayout();
            // Set time remain to dismiss
            countDownAccept = new CountDownTimer(timeRemain, 1000) {

                @Override
                public void onTick(long l) {
                    // Set time remain show on accept btn
                    acceptBtn.setText(
                            getString(
                                    R.string.fight_agree_layout_accept_btn_text,
                                    "" + l / 1000)
                    );
                }

                @Override
                public void onFinish() {
                    // if not click to join match
                    confirmationTimeout.run();
                }
            }.start();
            menuBubble.fadeIn();
        }

        /**
         * Show waiting competitor to accept elem
         */
        public void showWaitingCompetitor() {
            if (countDownAccept != null) countDownAccept.cancel();
            if (acceptBtn != null) acceptBtn.setVisibility(View.GONE);
            introMess.setText(R.string.finding_match_bubble_intro_mess_waiting_competitor);

        }

        /**
         * Clear confirmation layout
         */
        public void clearConfirmationLayout() {
            if (confirmationLayout != null) {
                menuBubble.getMainContainer().removeView(confirmationLayout);
                confirmationLayout = null;
            }
        }

        /**
         * Get accept action element
         *
         * @return get action elem
         */
        public TextView getAcceptActionElem() {
            return acceptBtn;
        }

        /**
         * Show block time remaining toast
         *
         * @param timeBlockRemain time block string
         */
        public void showBlockTimeRemainToast(String timeBlockRemain) {
            String notification = getString(
                    R.string.finding_match_not_join_match_warning,
                    timeBlockRemain);
            CustomToast.makeText(
                    getContext(), notification, Toast.LENGTH_LONG, CustomToast.ERROR
            ).show();
        }

        /**
         * Show toast notify that competitor not join
         */
        public void showCompetitorNotJoinToast() {
            String notification = getString(
                    R.string.finding_match_competitor_not_join_warning);
            CustomToast.makeText(
                    getContext(), notification, Toast.LENGTH_LONG, CustomToast.ERROR
            ).show();
        }

        /**
         * Add confirmation layout
         */
        public void addConfirmationLayout() {
            // Insert join match layout
            confirmationLayout = LayoutInflater.from(getContext())
                    .inflate(R.layout.fight_layout_dialog, menuBubble, false);
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.topToBottom = R.id.finding_match_bubble_intro_mess;
            layoutParams.startToStart = menuBubble.getMainContainer().getId();
            confirmationLayout.setLayoutParams(layoutParams);
            menuBubble.getMainContainer().addView(confirmationLayout);
            acceptBtn = mBinding.getRoot().findViewById(R.id.fight_agree_layout_accept_btn);
        }
    }
}
