package com.pfiev.englishcontest.ui.experimental;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.pfiev.englishcontest.GlobalConstant;
import com.pfiev.englishcontest.PlayGameActivity;
import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.databinding.FragmentExperimentalFindingmatchBinding;
import com.pfiev.englishcontest.firestore.FireStoreClass;
import com.pfiev.englishcontest.model.ChoiceItem;
import com.pfiev.englishcontest.model.QuestionItem;
import com.pfiev.englishcontest.utils.SharePreferenceUtils;
import com.pfiev.englishcontest.utils.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FindingMatchFragment extends Fragment {

    private final String TAG = "FindingMatchFragment";
    private FragmentExperimentalFindingmatchBinding mBinding;
    private LottieAnimationView finding_lottie;
    private FirebaseFunctions mFunctions;
    private static boolean isFirstChangePlay = false;
    ArrayList<QuestionItem> mListQuestion;
    public String mOtherUserId = " ";
    public String mMatchId = " ";
    public boolean mIsOwner;
    private Dialog mAcceptDialog;
    private boolean mIsAccepted = false;
    ListenerRegistration listenerRegistration;

    public final static String MATCH_ID_FIELD = "match_id";
    public final static String IS_OWNER_FIELD = "is_owner";

    public static FindingMatchFragment newInstance() {
        return new FindingMatchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mBinding = FragmentExperimentalFindingmatchBinding.inflate(inflater, container, false);
        finding_lottie = mBinding.findingProgress;
        mFunctions = FirebaseFunctions.getInstance();
        if (getArguments() != null) {
            String matchId = getArguments().getString(MATCH_ID_FIELD);
            boolean isOwner = getArguments().getBoolean(IS_OWNER_FIELD);

            if (matchId != null && !matchId.isEmpty()) {
                mMatchId = matchId;
                listenerStateMatchHistoryDoc(matchId, isOwner);
                if (!isOwner) sendRequestAcceptJoinMatch(matchId);
                if (isOwner)
                    mBinding.statusFinding.setText(R.string.experimental_request_combat_loading_txt);
                // hide button and image necessary
                mBinding.findingBtn.setVisibility(View.GONE);
                mBinding.findPeople.setVisibility(View.GONE);
                return mBinding.getRoot();
            }
        }

        mBinding.findingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.findingBtn.setEnabled(false);
                finding_lottie.setRepeatCount(LottieDrawable.INFINITE);
                finding_lottie.playAnimation();
                sendRequestFindMatch();
            }
        });
        mAcceptDialog = new Dialog(getContext());
        mIsAccepted = false;

        return mBinding.getRoot();
    }

    private void sendRequestFindMatch() {
        Log.i(TAG, "sendRequestFindMatch ");
        Toast.makeText(getContext(), "Finding match", Toast.LENGTH_SHORT).show();
        String uid = SharePreferenceUtils.getString(getContext(), GlobalConstant.USER_ID);
        FireStoreClass.findMatchRequest(uid).addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                Log.i(TAG, " Resul find match onComplete:");
                if (!task.isSuccessful()) {
                    Exception e = task.getException();
                    if (e instanceof FirebaseFunctionsException) {
                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                        FirebaseFunctionsException.Code code = ffe.getCode();
                        Object details = ffe.getDetails();
                    }
                    Log.i(TAG, " task is not success :" + e.getMessage());
                } else {
                    Log.i(TAG, " Result find match :" + task.getResult());
                    Map data = TextUtils.convertHashMap(task.getResult());
                    String matchId = (String) data.get(GlobalConstant.MATCH_ID);
                    String ownerId = (String) data.get(GlobalConstant.OWNER_ID);
                    mMatchId = matchId;
                    if (uid.equalsIgnoreCase(ownerId)) {
                        mIsOwner = true;
                        listenerStateMatchHistoryDoc(matchId, true);
                    } else {
                        mIsOwner = false;
                        listenerStateMatchHistoryDoc(matchId, false);
                    }
                    Log.i(TAG, " Result find match ID:" + matchId);
                    Toast.makeText(getActivity(), " Result: " + task.getResult(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void listenerStateMatchHistoryDoc(String matchId, boolean isOwner) {
        Log.i(TAG, " listenerStateMatchHistoryDoc matchId :" + matchId + " isOwner :" + isOwner);
        DocumentReference docRef = FirebaseFirestore.getInstance().collection(GlobalConstant.MATCH_HISTORY).document(matchId);
        listenerRegistration = docRef.addSnapshotListener(MetadataChanges.EXCLUDE, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                String state = (String) value.get(GlobalConstant.STATE);
                Log.i(TAG, "listenerStateMatchHistoryDoc " + state + " -" + value);
                if (state == null) {
                    Toast.makeText(getActivity(), " Match is removed ", Toast.LENGTH_SHORT).show();
                    waitingEnableFind(mIsAccepted);
                    return;
                }
                switch (state) {
                    case GlobalConstant.FINDING_STATE:
                        isFirstChangePlay = false;
                        break;
                    case GlobalConstant.WAITING_ACCEPT_STATE:
                        isFirstChangePlay = false;
                        mIsAccepted = false;
                        mBinding.statusFinding.setText("Wait player accept........");
                        showAgreeDialog(getContext(), matchId);
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

        Log.i(TAG, "getMatchHistoryData " + mListQuestion.size());
        Log.i(TAG, "getMatchHistoryData " + mListQuestion.get(0).getQuestionContent());
        waitLoadingQuestion();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listenerRegistration.remove();
    }

    public void showAgreeDialog(Context context, String match_id) {
        Log.i(TAG, " showAgreeDialog ");
        mAcceptDialog.setContentView(R.layout.fight_layout_dialog);
        mAcceptDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mAcceptDialog.setCancelable(false);
        ImageView close_btn = mAcceptDialog.findViewById(R.id.close_fight_layout);
        Button accept_btn = mAcceptDialog.findViewById(R.id.accept_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAcceptDialog.dismiss();
                Log.i(TAG, " showAgreeDialog : close dialog");
            }
        });
        accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsAccepted = true;
                sendRequestAcceptJoinMatch(match_id);
                Log.i(TAG, " showAgreeDialog : accept fight");
                mAcceptDialog.dismiss();
            }
        });

        mAcceptDialog.show();

    }

    public void sendRequestAcceptJoinMatch(String match_id) {
        String uid = SharePreferenceUtils.getString(getContext(), GlobalConstant.USER_ID);
        FireStoreClass.joinMatchRequest(uid, match_id).addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                Log.i(TAG, " Result find match onComplete:");
                if (!task.isSuccessful()) {
                    Exception e = task.getException();
                    if (e instanceof FirebaseFunctionsException) {
                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                        FirebaseFunctionsException.Code code = ffe.getCode();
                        Object details = ffe.getDetails();
                    }
                    Log.i(TAG, " task is not success :" + e.getMessage());
                } else {
                    Log.i(TAG, " Resul AcceptJoinMatch :" + task.getResult());
                    Map data = TextUtils.convertHashMap(task.getResult());

                }
            }
        });

    }

    private void waitLoadingQuestion() {
        new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long l) {
                mBinding.statusFinding.setText("Wait loading........");
            }

            @Override
            public void onFinish() {
                navigatePlayActivity();
            }
        }.start();
    }

    private void waitingEnableFind(boolean isAccepted) {
        if (isAccepted) {
            // show button and image necessary in the case request combat fail
            mBinding.findingBtn.setVisibility(View.VISIBLE);
            mBinding.findPeople.setVisibility(View.VISIBLE);
            mBinding.findingBtn.setEnabled(true);
            mBinding.statusFinding.setText("Finding.");
            return;
        }
        new CountDownTimer(15000, 1000) {

            @Override
            public void onTick(long l) {
                mBinding.statusFinding.setText("Wait 15s to continue find match.");
            }

            @Override
            public void onFinish() {
                mBinding.findingBtn.setEnabled(true);
                mBinding.statusFinding.setText("Finding.");
            }
        }.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBinding.findingBtn.setEnabled(true);
        finding_lottie.pauseAnimation();
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
}
