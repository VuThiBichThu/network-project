package login;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.media.MediaCodec;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.application.multiplechoicequestions.MainActivity;
import com.application.multiplechoicequestions.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.Socket;
import Object.*;
import io.socket.emitter.Emitter;
import register.Register;
import utils.SocketUtil;

public class Login extends Fragment {
    MediaCodec.QueueRequest queueRequest;
    Button login_button;
    EditText id;
    EditText password;
    LoginAccount loginAccount;
    Student student;
    Socket socket;
    TextView notify, register;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.activity_login, container, false);
        id = v.findViewById(R.id.id);
        password = v.findViewById(R.id.password);
        login_button = v.findViewById(R.id.login_button);
        notify = v.findViewById(R.id.notify);
        register = v.findViewById(R.id.register);
        try {
            socket = SocketUtil.getConnection();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Kiểm tra kết nối", Toast.LENGTH_LONG).show();
        }
        socket.connect();

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail = id.getText().toString();
                String strPass = password.getText().toString();
                JSONObject account = new JSONObject();
                if (!strEmail.isEmpty() && !strPass.isEmpty()) {
                    try {
                        loginAccount = new LoginAccount(strEmail, strPass);
                        account.put("id", strEmail);
                        account.put("pass", strPass);
                        socket.emit("accLogin", account);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    final View view = v;
                    socket.on("check account", new Emitter.Listener() {
                        @Override
                        public void call(final Object... args) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    JSONObject status = (JSONObject) args[0];

                                    try {
                                        Boolean isValid = status.getBoolean("status");
                                        Gson gson = new Gson();
                                        student = gson.fromJson(status.getJSONObject("student").toString(), Student.class);
                                        if (isValid) {
                                            notify.setVisibility(View.INVISIBLE);
                                            Toast.makeText(getContext(), "Login successfully!", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(view.getContext(), MainActivity.class);
                                            intent.putExtra("student", student);
                                            view.getContext().startActivity(intent);
                                        } else {
                                            Toast.makeText(getContext(), "Login failed!", Toast.LENGTH_LONG).show();
                                            notify.setVisibility(View.VISIBLE);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                }
                else {
                    notify.setText("Please input your account information!");
                    notify.setVisibility(View.VISIBLE);
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socket.disconnect();
                socket.close();
                Register nextFrag = new Register();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        return v;

    }


}