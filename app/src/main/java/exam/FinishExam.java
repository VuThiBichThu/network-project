package exam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.application.multiplechoicequestions.MainActivity;
import com.application.multiplechoicequestions.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.Socket;
import utils.SocketUtil;

public class FinishExam extends AppCompatActivity {

    TextView resultTxt;
    TextView scoreTv;
    Button finishExamBtn;
    Socket socket;
    String id_list_ques;
    String rsTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_exam);
        resultTxt = findViewById(R.id.result_txt);

        finishExamBtn = findViewById(R.id.finish_exam_btn);
        scoreTv = findViewById(R.id.score);
        float score = getIntent().getFloatExtra("score", 0);
        int num_correct = getIntent().getIntExtra("num_correct", 0);
        int num_ques = getIntent().getIntExtra("num_ques", 0);
        id_list_ques = getIntent().getStringExtra("id_list_ques");
        Log.d("So cau dung", String.valueOf(num_correct));
        rsTxt = num_correct + "/" + num_ques;
        resultTxt.setText(rsTxt);
        scoreTv.setText(String.valueOf(score) + "");

        try {
            socket = SocketUtil.getConnection();
            socket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        finishExamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

}