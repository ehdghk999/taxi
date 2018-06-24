package "패키지 네임";

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class SignUpActivity extends AppCompatActivity {
    private TextView txt_pswconfirm;
    private EditText edt_signup_num;
    private EditText edt_signup_psw;
    private EditText edt_signup_pswconfirm;
    private EditText edt_email_input;
    private Button btn_signup;
    private Button btn_signup_cancel;
    private Button btn_email_cert_do;
    private int email_check = 0;
    private long lastTimeBackPressed;
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        txt_pswconfirm = findViewById(R.id.txt_pswconfirm);
        edt_signup_num = findViewById(R.id.edt_signup_num);
        edt_signup_psw = findViewById(R.id.edt_signup_psw);
        edt_signup_pswconfirm = findViewById(R.id.edt_signup_pswconfirm);
        edt_email_input = findViewById(R.id.edt_email_input);
        btn_signup = findViewById(R.id.btn_signup_do);
        btn_signup_cancel = findViewById(R.id.btn_signup_cancel);
        btn_email_cert_do = findViewById(R.id.btn_email_cert_do);

        SignUpTextWatcher signUpTextWatcher = new SignUpTextWatcher();
        edt_signup_pswconfirm.addTextChangedListener(signUpTextWatcher);
        BTNSignUpListener btnSignUpListener = new BTNSignUpListener();

        btn_signup.setOnClickListener(btnSignUpListener);

        BTNEmailCertListener btnEmailCertListener = new BTNEmailCertListener();
        btn_email_cert_do.setOnClickListener(btnEmailCertListener);
        btn_signup_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    public class SignUpTextWatcher implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String password = edt_signup_psw.getText().toString();
            String confirm = edt_signup_pswconfirm.getText().toString();

            if( password.equals(confirm) ) {
                txt_pswconfirm.setText("비밀번호 같음");
                txt_pswconfirm.setTextColor(Color.argb(255,0,112,192));
            } else {
                txt_pswconfirm.setText("비밀번호 다름");
                txt_pswconfirm.setTextColor(Color.argb(255,255,55,55));
            }
        }
    }
    public class BTNEmailCertListener implements Button.OnClickListener{
        private String certifi_number;
        @Override
        public void onClick(View v) {
            String address = edt_email_input.getText().toString();
            if(address.length() == 0){
                Toast.makeText(v.getContext(), "이메일을 입력해주십시오.", Toast.LENGTH_LONG).show();
                edt_email_input.requestFocus();
                return;
            }
            int index = address.indexOf("@");
            if(!address.contains("@")){
                Toast.makeText(v.getContext(), "이메일을 정확히 입력해 주십시오.", Toast.LENGTH_LONG).show();
                edt_email_input.requestFocus();
                return;
            }
            String email_kumoh = address.substring(index+1);
            if(!email_kumoh.equals("kumoh.ac.kr")){
                Toast.makeText(v.getContext(), "금오공대 이메일만 사용 가능 합니다.", Toast.LENGTH_LONG).show();
                edt_email_input.requestFocus();
                return;
            }
            double randomValue = Math.random();
            int intRandomValue = (int)(randomValue*100)+1;
            long time = System.currentTimeMillis();
            SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            String currentTime = dayTime.format(new Date(time));

            String hash = sha256Make(edt_signup_psw.getText().toString()+intRandomValue+currentTime);
            certifi_number = makeRandomNumber(hash);
            try{
                GMailSender gMailSender = new GMailSender("ldonghwa99@gmail.com", "donghwa7");
                gMailSender.sendMail("이메일 인증 번호입니다.",
                        "인증 번호: "+ certifi_number,
                        address
                );
                Toast.makeText(v.getContext(), "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
            }catch (SendFailedException e) {
                Toast.makeText(v.getContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
            } catch (MessagingException e) {
                Toast.makeText(v.getContext(), "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
                Log.i("MessagingException: ", e.toString());
            } catch (Exception e) {
                Log.i("GMailSender Exception: ", e.toString());
            }

            final EmailCertifiDialog emailCertifiDialog = new EmailCertifiDialog(SignUpActivity.this);
            emailCertifiDialog.setCertifi_number(certifi_number);
            DisplayMetrics dm = SignUpActivity.this.getResources().getDisplayMetrics();
            int width = dm.widthPixels; //디바이스 화면 너비
            int height = dm.heightPixels; //디바이스 화면 높이

            WindowManager.LayoutParams wm = emailCertifiDialog.getWindow().getAttributes();
            wm.copyFrom(emailCertifiDialog.getWindow().getAttributes());  //여기서 설정한값을 그대로 다이얼로그에 넣겠다는의미
            wm.width = width-100 ;
            wm.height = height-200 ;

            emailCertifiDialog.show();
            emailCertifiDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    email_check = emailCertifiDialog.getResult_number();
                }
            });
        }
        private String sha256Make(String base){
            try{
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                byte[] hash = messageDigest.digest(base.getBytes("UTF-8"));
                StringBuffer hexString = new StringBuffer();
                for (int i = 0; i < hash.length; i++) {
                    String hex = Integer.toHexString(0xff & hash[i]);
                    if(hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString();
            }catch (Exception e){
                Log.i("sha256Make Exception: ", e.getMessage());
                return e.getMessage();
            }
        }
        private String makeRandomNumber(String hash){
            char str1[] = hash.substring(0,10).toCharArray();
            char str2[] = hash.substring(10,20).toCharArray();
            char str3[] = hash.substring(20,30).toCharArray();
            char str4[] = hash.substring(30,40).toCharArray();
            char str5[] = hash.substring(40,50).toCharArray();
            char str6[] = hash.substring(50,60).toCharArray();
            char str7[] = hash.substring(60).toCharArray();

            int st1 = str1[0];
            int st2 = str2[0];
            int st3 = str3[0];
            int st4 = str4[0];
            int st5 = str5[0];
            int st6 = str6[0];

            for(int i=1;i<10;i++){
                st1 = st1 ^ str1[i];
                st2 = st2 ^ str2[i];
                st3 = st3 ^ str3[i];
                st4 = st4 ^ str4[i];
                st5 = st5 ^ str5[i];
                st6 = st6 ^ str6[i];
            }
            st1 = st1 ^ str7[0];
            st2 = st2 ^ str7[1];
            st4 = st4 ^ str7[2];
            st5 = st5 ^ str7[3];

            st1 = st1%10;
            st2 = st2%10;
            st3 = st3%10;
            st4 = st4%10;
            st5 = st5%10;
            st6 = st6%10;

            String result = st1+""+st2+""+st3+""+st4+""+st5+""+st6;
            return result;
        }
    }
    public class BTNSignUpListener implements Button.OnClickListener{
        @Override
        public void onClick(View v) {
            String studentid = edt_signup_num.getText().toString();
            String pass = edt_signup_psw.getText().toString();
            if(edt_signup_num.getText().length() == 0){
                Toast.makeText(getApplicationContext(),"학번을 입력하세요.", Toast.LENGTH_LONG).show();
                edt_signup_num.requestFocus();
                return;
            }
            if(edt_signup_num.getText().length() != 8){
                Toast.makeText(getApplicationContext(),"8자리 학번을 입력하세요.", Toast.LENGTH_LONG).show();
                edt_signup_num.requestFocus();
                return;
            }
            if(edt_signup_psw.getText().length() == 0){
                Toast.makeText(getApplicationContext(),"비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                edt_signup_psw.requestFocus();
                return;
            }
            if(edt_signup_psw.getText().length() <8){
                Toast.makeText(getApplicationContext(),"비밀번호를 8자리 이상 입력해주세요.", Toast.LENGTH_LONG).show();
                edt_signup_psw.requestFocus();
                return;
            }
            if(edt_signup_pswconfirm.getText().length()==0){
                Toast.makeText(getApplicationContext(),"비밀번호 확인을 입력하세요.", Toast.LENGTH_LONG).show();
                edt_signup_pswconfirm.requestFocus();
                return;
            }
            if(!edt_signup_psw.getText().toString().equals(edt_signup_pswconfirm.getText().toString())){
                Toast.makeText(getApplicationContext(),"비밀번호가 일치하지 않습니다.",Toast.LENGTH_LONG).show();
                edt_signup_psw.requestFocus();
                return;
            }
            if(email_check == 0||edt_email_input.getText().toString().length()==0){
                Toast.makeText(getApplicationContext(), "이메일 인증이 완료되지 않았습니다.", Toast.LENGTH_LONG).show();
                edt_email_input.requestFocus();
                return;
            }
            putSignUpData(studentid, pass, edt_email_input.getText().toString());
            finish();
        }
    }
    private void putSignUpData(String num, String pw, String email){
        class PutSignUpDataAsync extends AsyncTask<String, Void, String>{
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SignUpActivity.this, "Please Wait", null, true, true);

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
            }

            @Override
            protected String doInBackground(String... strings) {
                try{
                    String studentID = (String)strings[0];
                    String password = (String)strings[1];
                    String email = (String)strings[2];
                    String link = "http://"+MainActivity.getMyip()+"/carpool_signup.php";
                    String data = URLEncoder.encode("studentID", "UTF-8") + "=" + URLEncoder.encode(studentID, "UTF-8");
                    data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                    data += "&" + URLEncoder.encode("studentEmail", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString().trim();
                }catch (Exception e){
                    return new String(e.getMessage());
                }
            }
        }
        PutSignUpDataAsync putSignUpDataAsync = new PutSignUpDataAsync();
        putSignUpDataAsync.execute(num, pw, email);
        if(putSignUpDataAsync.loading!=null){
            putSignUpDataAsync.loading.dismiss();
        }
    }
    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
