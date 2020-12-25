package register;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.method.HideReturnsTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.application.multiplechoicequestions.R;

import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.Socket;

import  Object.Student;
import io.socket.emitter.Emitter;
import login.Login;
import utils.SocketUtil;

public class Register extends Fragment {
    TextView id, name, grade, pass, confirm_pass, notifi, loginLink;
    Button register_button;
    ImageButton show_pass;
    Socket socket;

    public Register() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register1, container, false);
        id = v.findViewById(R.id.id);
        name = v.findViewById(R.id.name);
        grade = v.findViewById(R.id.grade);
        pass = v.findViewById(R.id.password);
        confirm_pass = v.findViewById(R.id.password_confirm);
        notifi = v.findViewById(R.id.notifi);
        loginLink = v.findViewById(R.id.login_link);
        register_button = v.findViewById(R.id.register_button);

        try {
            socket = SocketUtil.getConnection();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        socket.connect();
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String idString = id.getText().toString();
                final String nameString = name.getText().toString();
                if (nameString.isEmpty()) Log.d("aaaaaaaaa", "yyyy");
                final String gradeString = grade.getText().toString();
                final String passString = pass.getText().toString();
                String confirmPassString = confirm_pass.getText().toString();
                if (!idString.isEmpty() && !nameString.isEmpty() && !gradeString.isEmpty() && !passString.isEmpty() && !confirmPassString.isEmpty()) {
                    if (confirmPassString.equals(passString)) {
                        final Student student = new Student(idString, nameString, gradeString, 0f, passString);
                        try {
                            JSONObject idStdJSon = new JSONObject();
                            idStdJSon.put("idStudent", idString);
                            socket.emit("registerAccount", idStdJSon);
                        } catch (Exception e) {
                        }
                        socket.on("check existed account", new Emitter.Listener() {
                            @Override
                            public void call(final Object... args) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        JSONObject json = (JSONObject) args[0];
                                        try {
                                            Boolean isExistedAcc = json.getBoolean("isExisted");
                                            if (isExistedAcc) {
                                                notifi.setText("Id tài khoản đã tồn tại!");
                                                notifi.setVisibility(View.VISIBLE);
                                            } else {
                                                //  Student student = new Student(idString, nameString, gradeString, 0f, passString);
                                                JSONObject jsonStd = new JSONObject();
                                                jsonStd.put("idStudent", idString);
                                                jsonStd.put("name", nameString);
                                                jsonStd.put("grade", gradeString);
                                                jsonStd.put("score", 0f);
                                                jsonStd.put("pass", passString);
                                                socket.emit("accountReg", jsonStd);
                                                notifi.setText("Đăng ký thành công!");
                                                notifi.setVisibility(View.VISIBLE);
                                                register_button.setEnabled(false);
                                                socket.disconnect();
                                                socket.close();
                                            }
                                        } catch (Exception e) {
                                        }
                                    }
                                });
                            }
                        });
                    } else {
                        notifi.setText("Xác nhận mật khẩu không đúng!");
                        notifi.setVisibility(View.VISIBLE);
                    }
                } else {
                    notifi.setText("Hãy điền đủ thông tin!");
                    notifi.setVisibility(View.VISIBLE);
                }
            }
        });
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login nextFrag = new Login();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        return v;
    }
}















