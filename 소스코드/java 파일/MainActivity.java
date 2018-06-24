package "패키지 네임";

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    private EditText edt_num;
    private EditText edt_psw;
    private Button btn_login;
    private Button btn_signup;
    private static final String myip = "개인 서버 아이피";
    private long lastTimeBackPressed;
    private static String user_id;


    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed<1500){
            ActivityCompat.finishAffinity(this);
            return;
        }
        Toast.makeText(getApplicationContext(), "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_LONG).show();
        lastTimeBackPressed = System.currentTimeMillis();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        edt_num = findViewById(R.id.edt_num);
        edt_psw = findViewById(R.id.edt_psw);
        btn_login = findViewById(R.id.btn_login);
        btn_signup = findViewById(R.id.btn_signup);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLoginData(edt_num.getText().toString(), edt_psw.getText().toString());
            }
        });
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    public static String getMyip() {
        return myip;
    }
    public static String getUser_id() {
        return user_id;
    }

    public void userLoginCheck(String s){
        char che = s.substring(s.length()-1, s.length()).charAt(0);
        if(che == 'a'){
            Toast.makeText(getApplicationContext(), "이미 로그인 되어있는 아이디 입니다.", Toast.LENGTH_LONG).show();
        }
        else if(che == 'b'){
            Toast.makeText(getApplicationContext(), "아이디가 존재하지 않습니다.", Toast.LENGTH_LONG).show();
        }
        else if(che == 'c'){
            Toast.makeText(getApplicationContext(), "비밀번호가 틀립니다.", Toast.LENGTH_LONG).show();
        }
        else if(che == 'd'){
            Log.i("s1", s);
            Toast.makeText(getApplicationContext(),"로그인에 성공했습니다.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, BoardListActivity.class);
            startActivity(intent);
        }
        else{
            Log.i("오류", s);
        }
    }
    public void userLoginData(String studentID, String password){
        class UserLoginDataAsync extends AsyncTask<String, Void, String>{
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                userLoginCheck(s);
            }

            @Override
            protected String doInBackground(String... strings) {
                try{
                    user_id = strings[0];
                    String pw = strings[1];

                    String link = "http://"+MainActivity.getMyip()+"/carpool_login.php";
                    String data = URLEncoder.encode("studentID", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8");
                    data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(pw, "UTF-8");
                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(data);
                    wr.flush();

                    InputStream rd = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(rd), 8 * 1024);
                    StringBuffer buff = new StringBuffer();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        buff.append(line);
                        break;
                    }

                    return buff.toString().trim();
                }catch (Exception e){
                    return new String("PutSignUpData Exception: "+e.getMessage());
                }
            }
        }

        UserLoginDataAsync userLoginDataAsync = new UserLoginDataAsync();
        userLoginDataAsync.execute(studentID, password);
        if(userLoginDataAsync.loading != null){
            userLoginDataAsync.loading.dismiss();
        }
    }
    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}