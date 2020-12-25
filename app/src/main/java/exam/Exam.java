package exam;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.application.multiplechoicequestions.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import question.QuestionAdapter;
import Object.*;
import utils.SocketUtil;

public class Exam extends AppCompatActivity implements QuestionAdapter.OnItemClickListener {
    Socket socket;

    RecyclerView recyclerView;

    List<Question> questions;
    QuestionAdapter adapter;
    Question question;
    Map<Integer, Integer> selectedAnswers;

    TextView examName;
    ImageButton submit_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        recyclerView = findViewById(R.id.recyclerview_exam);
        examName = findViewById(R.id.name_exam);
        submit_button = findViewById(R.id.submit_button);

        try {
            socket = SocketUtil.getConnection();
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        questions = new ArrayList<Question>();
        socket.emit("get questions");
        socket.on("get questions", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray questionJSON = (JSONArray) args[0];
                        try {
                            JSONObject questionJSONOb = (JSONObject) questionJSON.get(0);
                            String nameExam = questionJSONOb.getString("name");
                            examName.setText(nameExam);
                            String id = questionJSONOb.getString("id");
                            JSONArray ques_set = questionJSONOb.getJSONArray("question_set");

                            for (int j = 0; j < ques_set.length() - 5; j++) {
                                JSONObject quesJSON = (JSONObject) ques_set.get(j);
                                JSONArray arrayAns = (JSONArray) quesJSON.getJSONArray("answers");
                                int correct = (int) quesJSON.get("correct");
                                String question_content = quesJSON.getString("question");
                                Map<Integer, String> answers = new LinkedHashMap<>();
                                for (int k = 0; k < arrayAns.length(); k++) {
                                    JSONObject answer = (JSONObject) arrayAns.get(k);
                                    Integer idAns = answer.getInt("id");
                                    String ans_content = answer.getString("text");
                                    answers.put(idAns, ans_content);
                                }
                                question = new Question(question_content, answers, correct);

                                questions.add(question);
                            }

                            adapter = new QuestionAdapter(getApplicationContext(), questions, selectedAnswers);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        selectedAnswers = new LinkedHashMap<>();
        for (int i = 0; i < questions.size(); i++) {
            Log.d("Cau", "i: " + questions.get(i));
            selectedAnswers.put(i, 0);
        }
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
    }

    public void confirm() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notification: ");
        builder.setMessage("Are you sure you want to finish this quiz?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                viewScore();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void viewScore() {
        Log.d("Score", "View score");
        Log.d("Question quantity", String.valueOf(questions.size()));

        int correct_ques = 0;
        List<Map.Entry<Integer, Integer>> entries = new ArrayList<>(selectedAnswers.entrySet());
        for (int i = 0; i < questions.size(); i++) {
            Log.d("Cau", i + "=> Da chon dap an:" + entries.get(i).getValue());
            Log.d("Cau", i + "=> Dap an dung la:" + questions.get(i).getCorrectAnswer());
            if (questions.get(i).getCorrectAnswer() == entries.get(i).getValue()) {
                correct_ques++;
            }
        }

        Log.d("So cau tra loi dung", String.valueOf(correct_ques));

        float score = (float) correct_ques * 10.0f;

        Intent intent = new Intent(getApplicationContext(), FinishExam.class);
        intent.putExtra("score", score);
        intent.putExtra("num_correct", correct_ques);
        intent.putExtra("num_ques", questions.size());
        startActivity(intent);
    }
}