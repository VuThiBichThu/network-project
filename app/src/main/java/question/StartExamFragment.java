package question;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.application.multiplechoicequestions.R;

import java.util.ArrayList;
import java.util.List;

import exam.Exam;
import Object.*;

public class StartExamFragment extends Fragment {
    Button start_exam_button;
    List<Question> questions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_start_exam, container, false);

        start_exam_button = v.findViewById(R.id.start_exam_button);
        start_exam_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Exam.class);

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("questions", (ArrayList<? extends Parcelable>) questions);

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return v;
    }
}