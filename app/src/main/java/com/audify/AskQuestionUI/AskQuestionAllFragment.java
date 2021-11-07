package com.audify.AskQuestionUI;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.audify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AskQuestionAllFragment extends Fragment {


    TextInputLayout questionBox;

    Button sendQuestion;

    String creatorName, creatorId;

    EditText question;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_ask_question_all, container, false);

        questionBox = view.findViewById(R.id.questionBox);
        question = view.findViewById(R.id.question_edit);

        sendQuestion = view.findViewById(R.id.submit);
        sendQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String questionAsked = question.getText().toString().trim();

                if(TextUtils.isEmpty(questionAsked)){
                    Toast.makeText(getContext(), "Please enter a question to continue", Toast.LENGTH_SHORT).show();
                }
                else{
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    HashMap<String, Object> data = new HashMap<>();
                    data.put("userName", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    data.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    data.put("question", questionAsked);

                    db.collection("Questions").add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            Toast.makeText(getContext(), "Question submitted successfully, we will notify you when we receive an answer", Toast.LENGTH_LONG).show();

                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    });
                }
            }
        });

        return view;
    }
}