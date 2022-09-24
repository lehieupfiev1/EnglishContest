package com.pfiev.englishcontest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pfiev.englishcontest.model.QuestionItem;

import java.util.ArrayList;
import java.util.List;

public class ResultGameActivity extends AppCompatActivity {
    private static final String TAG = "ResultGameActivity";
    public ArrayList<QuestionItem> ListQuestion;
    public ArrayList<Integer> ListChoose;
    public int numberCorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_game);
        initData();
        RecyclerView listQuestionView = (RecyclerView) findViewById(R.id.list_answer);
        QuestionAdapter questionAdapter = new QuestionAdapter(ListQuestion,ListChoose);
        TextView headerTv = (TextView) findViewById(R.id.result_answer_tv);
        listQuestionView.setAdapter(questionAdapter);
        listQuestionView.setLayoutManager(new LinearLayoutManager(this));

        String headerStr = getString(R.string.result_detail_play, numberCorrect, ListQuestion.size());
        headerTv.setText(headerStr);

    }

    private void initData() {
        Intent intent = getIntent();
        ListQuestion = intent.getParcelableArrayListExtra("ListQuestion");
        ListChoose = intent.getIntegerArrayListExtra("ListChoose");
        numberCorrect = intent.getIntExtra("numberCorrect",0);
        Log.i(TAG, "initData ListQuestion "+ListQuestion.size());
    }



    public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
        ArrayList<QuestionItem> mListQuestion;
        ArrayList<Integer> mListChoose;

        public QuestionAdapter(ArrayList<QuestionItem> mListQuestion, ArrayList<Integer> mListChoose) {
            this.mListQuestion = mListQuestion;
            this.mListChoose = mListChoose;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View questionsView = inflater.inflate(R.layout.question_item_layout,parent,false);

            ViewHolder viewHolder = new ViewHolder(questionsView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            QuestionItem questionItem = mListQuestion.get(position);
            int choose = mListChoose.get(position);
            Log.i(TAG, "onBindViewHolder choose: "+choose+ " correct :"+questionItem.answer);
            TextView questionTv = holder.question_tv;
            int index = position+1;
            String questionStr = ""+index+"."+questionItem.getQuestionContent();
            questionTv.setText(questionStr);

            TextView chooser1 = holder.chooser1;
            String chooseA = "A."+questionItem.getListAnswer().get(0).content;
            Log.i(TAG, "onBindViewHolder "+chooseA);
            chooser1.setText(chooseA);
            ImageView check1 = holder.check1;

            TextView chooser2 = holder.chooser2;
            String chooseB = "B."+questionItem.getListAnswer().get(1).content;
            Log.i(TAG, "onBindViewHolder "+chooseB);
            chooser2.setText(chooseB);
            ImageView check2 = holder.check2;

            TextView chooser3 = holder.chooser3;
            String chooseC = "C."+questionItem.getListAnswer().get(2).content;
            Log.i(TAG, "onBindViewHolder "+chooseC);
            chooser3.setText(chooseC);
            ImageView check3 = holder.check3;

            TextView chooser4 = holder.chooser4;
            String chooseD = "D."+questionItem.getListAnswer().get(3).content;
            Log.i(TAG, "onBindViewHolder "+chooseD);
            chooser4.setText(chooseD);
            ImageView check4 = holder.check4;

            //Reset set color
            chooser1.setTextColor(Color.BLACK);
            chooser2.setTextColor(Color.BLACK);
            chooser3.setTextColor(Color.BLACK);
            chooser4.setTextColor(Color.BLACK);
            check1.setVisibility(View.INVISIBLE);
            check2.setVisibility(View.INVISIBLE);
            check3.setVisibility(View.INVISIBLE);
            check4.setVisibility(View.INVISIBLE);

            if ((int)questionItem.answer == choose) {
                //Choose correct
                if (choose == 1) {
                    check1.setVisibility(View.VISIBLE);
                    chooser1.setTextColor(Color.BLUE);
                } else if (choose == 2) {
                    check2.setVisibility(View.VISIBLE);
                    chooser2.setTextColor(Color.BLUE);
                } else if (choose == 3) {
                    check3.setVisibility(View.VISIBLE);
                    chooser3.setTextColor(Color.BLUE);
                } else if (choose == 4) {
                    check4.setVisibility(View.VISIBLE);
                    chooser4.setTextColor(Color.BLUE);
                }

            } else {
                if (choose == 1) {
                    check1.setVisibility(View.VISIBLE);
                    check1.setImageResource(R.drawable.wrong_image_layout);
                    chooser1.setTextColor(Color.RED);
                } else if (choose == 2) {
                    check2.setVisibility(View.VISIBLE);
                    check2.setImageResource(R.drawable.wrong_image_layout);
                    chooser2.setTextColor(Color.RED);
                } else if (choose == 3) {
                    check3.setVisibility(View.VISIBLE);
                    check3.setImageResource(R.drawable.wrong_image_layout);
                    chooser3.setTextColor(Color.RED);
                } else if (choose == 4) {
                    check4.setVisibility(View.VISIBLE);
                    check4.setImageResource(R.drawable.wrong_image_layout);
                    chooser4.setTextColor(Color.RED);
                }

                int answer = (int) questionItem.answer;
                if (answer == 1) {
                    check1.setVisibility(View.VISIBLE);
                    check1.setImageResource(R.drawable.check_image_layout);
                    chooser1.setTextColor(Color.GREEN);
                } else if (answer == 2) {
                    check2.setVisibility(View.VISIBLE);
                    check2.setImageResource(R.drawable.check_image_layout);
                    chooser2.setTextColor(Color.GREEN);
                } else if (answer == 3) {
                    check3.setVisibility(View.VISIBLE);
                    check3.setImageResource(R.drawable.check_image_layout);
                    chooser3.setTextColor(Color.GREEN);
                } else if (answer == 4) {
                    check4.setVisibility(View.VISIBLE);
                    check4.setImageResource(R.drawable.check_image_layout);
                    chooser4.setTextColor(Color.GREEN);
                }

            }
        }

        @Override
        public int getItemCount() {
            return mListQuestion.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView question_tv;
            TextView chooser1;
            ImageView check1;
            TextView chooser2;
            ImageView check2;
            TextView chooser3;
            ImageView check3;
            TextView chooser4;
            ImageView check4;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                question_tv = (TextView) itemView.findViewById(R.id.question_tv);
                chooser1 = (TextView) itemView.findViewById(R.id.chooser1);
                check1 = (ImageView) itemView.findViewById(R.id.check1);
                chooser2 = (TextView) itemView.findViewById(R.id.chooser2);
                check2 = (ImageView) itemView.findViewById(R.id.check2);
                chooser3 = (TextView) itemView.findViewById(R.id.chooser3);
                check3 = (ImageView) itemView.findViewById(R.id.check3);
                chooser4 = (TextView) itemView.findViewById(R.id.chooser4);
                check4 = (ImageView) itemView.findViewById(R.id.check4);
            }
        }
    }
}