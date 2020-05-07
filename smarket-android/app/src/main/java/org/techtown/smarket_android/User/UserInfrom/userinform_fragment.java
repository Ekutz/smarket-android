package org.techtown.smarket_android.User.UserInfrom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.User.UserLogin.user_login_success;
import org.techtown.smarket_android.User.UserLogin.user_validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class userinform_fragment extends Fragment {

    public static userinform_fragment newInstance() {
        return new userinform_fragment();
    }

    private ViewGroup viewGroup;
    private TextView userinform_id;
    private EditText userinform_nick_et;
    private Button userinform_validate_nick_btn;
    private EditText userinform_pw_et;
    private EditText userinform_name_et;
    private Spinner userinform_phoneNumber_spinner;
    private ArrayAdapter<String> phoneNumberAdapter;
    private EditText userinform_phoneNumber1_et;
    private EditText userinform_phoneNumber2_et;
    private Button userinform_modify_btn;
    private AlertDialog dialog;
    private boolean validate_nickname = false;


    // ** 로그인 및 토큰 정보 ** //
    private SharedPreferences userFile;
    private String user_id;
    private String access_token;
    private String refresh_token;
    static private String TAG = "TOKEN";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.userinform_main, container, false);

        // userFile에 저장된 user_id 와 access_token 값 가져오기
        get_userFile();

        // ViewGroup 설정
        set_viewGroup();

        // 현재 로그인된 user_id 표시
        userinform_id.setText(user_id);

        // nickname 중복검사 버튼
        userinform_validate_nick_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { validate_nick();
            }
        });

        // 회원정보 수정 버튼
        userinform_modify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modify();
            }
        });

        return viewGroup;
    }

    // ViewGroup 설정
    private void set_viewGroup(){
        userinform_id = viewGroup.findViewById(R.id.userinform_id);
        userinform_nick_et = viewGroup.findViewById(R.id.userinform_nick_et);
        userinform_validate_nick_btn = viewGroup.findViewById(R.id.userinform_validate_nick_btn);
        userinform_pw_et = viewGroup.findViewById(R.id.userinform_pw_et);
        userinform_name_et = viewGroup.findViewById(R.id.userinform_name_et);
        userinform_phoneNumber1_et = viewGroup.findViewById(R.id.userinform_phoneNumber1_et);
        userinform_phoneNumber2_et = viewGroup.findViewById(R.id.userinform_phoneNumber2_et);
        userinform_modify_btn = viewGroup.findViewById(R.id.userinform_modify_btn);

        List<String> phoneNumber_list = new ArrayList<>();
        phoneNumber_list.add("010");
        phoneNumber_list.add("011");
        phoneNumber_list.add("012");
        phoneNumber_list.add("013");
        phoneNumber_list.add("014");
        phoneNumber_list.add("015");

        phoneNumberAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, phoneNumber_list);

        userinform_phoneNumber_spinner = (Spinner) viewGroup.findViewById(R.id.userinform_phoneNumber_spinner);
        userinform_phoneNumber_spinner.setAdapter(phoneNumberAdapter);

        String url = "http://10.0.2.2:3000/api/users/"+user_id;
        Log.d(TAG, "set_viewGroup: " + url);
        StringRequest userinform_get_request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d(TAG, "onResponse: " + jsonObject.toString());
                    boolean success = jsonObject.getBoolean("success");
                    JSONObject data = jsonObject.getJSONObject("data");

                    if (success) {
                        Log.d(TAG, "success ");
                        userinform_nick_et.setText(data.getString("nickname"));
                        userinform_name_et.setText(data.getString("name"));
                        String phonenumber = data.getString("phonenum");
                        String phonenum1 = phonenumber.substring(0,3);
                        switch(phonenum1){
                            case "010" : userinform_phoneNumber_spinner.setSelection(0);break;
                            case "011" : userinform_phoneNumber_spinner.setSelection(1);break;
                            case "012" : userinform_phoneNumber_spinner.setSelection(2);break;
                            case "013" : userinform_phoneNumber_spinner.setSelection(3);break;
                            case "014" : userinform_phoneNumber_spinner.setSelection(4);break;
                            case "015" : userinform_phoneNumber_spinner.setSelection(5);break;
                        }
                        userinform_phoneNumber1_et.setText(phonenumber.substring(3,7));
                        userinform_phoneNumber2_et.setText(phonenumber.substring(7,11));
                    }else{
                        Log.d(TAG, "onResponse: " + jsonObject.toString());
                    }
                } catch (Exception e) {
                    Log.d(TAG, "onResponse: " + e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: "+ error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                Map<String,String>parameters = new HashMap<>();
                parameters.put("x-access-token", access_token);
                return parameters;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(userinform_get_request);
    }

    // nickname 중복검사
    private void validate_nick() {
        String url = "http://10.0.2.2:3000/api/auth/checknickname";
        String key = "nickname";
        String user_nickname = userinform_nick_et.getText().toString();

        if (validate_nickname) {
            return;
        }
        if (user_nickname.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            dialog = builder.setMessage("닉네임은 빈 칸 일 수 없습니다.")
                    .setPositiveButton("확인", null)
                    .create();
            dialog.show();
            return;
        }
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        dialog = builder.setMessage("사용할 수 있는 닉네임 입니다.")
                                .setPositiveButton("확인", null)
                                .create();
                        dialog.show();
                        userinform_nick_et.setEnabled(false);
                        validate_nickname = true;
                        userinform_nick_et.setTextColor(getResources().getColor(R.color.colorgray));

                        return;
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        dialog = builder.setMessage("사용할 수 없는 닉네임 입니다.")
                                .setNegativeButton("확인", null)
                                .create();
                        dialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                }
            }
        };

        user_validate validateRequest = new user_validate(url, key, user_nickname, responseListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "errorListener", Toast.LENGTH_LONG).show();

            }
        });
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(validateRequest);
    }


    private void modify() {
        String userID = userinform_id.getText().toString();
        String userPW = userinform_pw_et.getText().toString();
        String userName = userinform_name_et.getText().toString();
        String userNick = userinform_nick_et.getText().toString();
        String userPhoneNumber1 = userinform_phoneNumber1_et.getText().toString();
        String userPhoneNumber2 = userinform_phoneNumber2_et.getText().toString();
        String userPhoneNumber = userinform_phoneNumber_spinner.getSelectedItem().toString() + userPhoneNumber1 + userPhoneNumber2;

        if (!validate_nickname) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            dialog = builder.setMessage("먼저 중복 체크를 해주세요.")
                    .setPositiveButton("확인", null)
                    .create();
            dialog.show();
            return;
        }

        if (userID.equals("") || userPW.equals("") || userName.equals("") || userNick.equals("") || userPhoneNumber1.equals("") || userPhoneNumber2.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            dialog = builder.setMessage("사용자 정보를 입력해주세요.")
                    .setPositiveButton("확인", null)
                    .create();
            dialog.show();
            return;
        }

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        dialog = builder.setMessage("회원정보를 수정했습니다.")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.main_layout, user_login_success.newInstance()).commit();
                                    }
                                })
                                .create();
                        dialog.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        dialog = builder.setMessage("회원정보를 수정할 수 없습니다.")
                                .setNegativeButton("확인", null)
                                .create();
                        dialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        userinform_modify_request registerRequest = new userinform_modify_request(getActivity(), userID, userPW, userName, userNick, userPhoneNumber, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(registerRequest);
    }


    // userFile에 저장된 user_id 와 access_token 값 가져오기
    private void get_userFile() {
        userFile = getActivity().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        user_id = userFile.getString("user_id", null);
        access_token = userFile.getString("access_token", null);
        refresh_token = userFile.getString("refresh_token", null);
    }
}
