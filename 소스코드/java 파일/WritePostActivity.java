package "패키지 네임";

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WritePostActivity extends AppCompatActivity {
    private Button btn_write_post_start_location;
    private Button btn_write_post_end_location;
    private Button btn_write_post_do;
    private Button btn_write_post_cancel;
    private TimePicker timepic_write_post_start_time;
    private String start_time = null;
    private int btn_sta_loc_chk = 0;
    private int btn_end_loc_chk = 0;
    private final static int REQUEST_CODE = 100;
    private final static int RESULT_OK1 = 10;
    private final static int RESULT_OK2 = 20;
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
        setContentView(R.layout.activity_writepost);
        btn_write_post_start_location = findViewById(R.id.btn_write_post_start_location);
        btn_write_post_end_location = findViewById(R.id.btn_write_post_end_location);
        timepic_write_post_start_time = findViewById(R.id.tipic_write_post_start_time);
        btn_write_post_do = findViewById(R.id.btn_write_post_do);
        btn_write_post_cancel = findViewById(R.id.btn_write_post_cancel);
        btn_write_post_start_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WritePostActivity.this, WritePostMapActivity.class);
                intent.putExtra("check", "1");
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
        btn_write_post_end_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WritePostActivity.this, WritePostMapActivity.class);
                intent.putExtra("check", "2");
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        BTNWritePostDoListener btnWritePostDoListener = new BTNWritePostDoListener();
        btn_write_post_do.setOnClickListener(btnWritePostDoListener);
        timepic_write_post_start_time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    start_time = hourOfDay+"시 "+minute+"분";
            }
        });

        btn_write_post_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            if(resultCode == RESULT_OK1){
                btn_sta_loc_chk = 1;
                btn_write_post_start_location.setText(data.getStringExtra("check"));
            }else if(resultCode == RESULT_OK2){
                btn_end_loc_chk = 1;
                btn_write_post_end_location.setText(data.getStringExtra("check"));
            }else{
                Toast.makeText(getApplicationContext(), "취소 되었습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class BTNWritePostDoListener implements Button.OnClickListener{
        @Override
        public void onClick(View v) {
            if(btn_sta_loc_chk == 0){
                Toast.makeText(getApplicationContext(),"출발지를 입력하세요.", Toast.LENGTH_LONG).show();
                btn_write_post_start_location.requestFocus();
                return;
            }else if(btn_end_loc_chk == 0){
                Toast.makeText(getApplicationContext(), "도착지를 입력하세요.", Toast.LENGTH_LONG).show();
                btn_write_post_end_location.requestFocus();
                return;
            }
            String start = btn_write_post_start_location.getText().toString();
            String end = btn_write_post_end_location.getText().toString();
            if(start_time == null){
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("h시 m분");
                String gettime = sdf.format(date);
                start_time = gettime;
            }
            putPostData(start, end, start_time);

        }
    }
    public void putPostData(String start, String end, String time){
        class PutPostDataAsync extends AsyncTask<String, Void, String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(WritePostActivity.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                finish();
            }
            @Override
            protected String doInBackground(String... strings) {
                try{
                    String start_location = strings[0];
                    String end_location = strings[1];
                    String start_time = strings[2];

                    String link = "http://"+MainActivity.getMyip()+"/carpool_writepost.php";
                    String data1 = URLEncoder.encode("start_loc", "UTF-8") + "=" + URLEncoder.encode(start_location, "UTF-8");
                    data1 += "&" + URLEncoder.encode("end_loc", "UTF-8") + "=" + URLEncoder.encode(end_location, "UTF-8");
                    data1 += "&" + URLEncoder.encode("start_tim", "UTF-8") + "=" + URLEncoder.encode(start_time, "UTF-8");
                    data1 += "&" + URLEncoder.encode("studentID", "UTF-8") + "=" + URLEncoder.encode(MainActivity.getUser_id(), "UTF-8");
                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data1);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    // Read Server Response
                    for(line = reader.readLine();line != null;){
                        sb.append(line);
                        break;
                    }
                    return sb.toString().trim();
                }catch (Exception e){
                    return new String(e.getMessage());
                }
            }
        }
        PutPostDataAsync putPostDataAsync = new PutPostDataAsync();
        putPostDataAsync.execute(start, end, time);
        if(putPostDataAsync.loading!=null){
            putPostDataAsync.loading.dismiss();
        }
    }
    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
