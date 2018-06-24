package "패키지 네임";

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ReplyActivity extends AppCompatActivity {
    private ListView lst_reply;
    private EditText edt_reply;
    private Button btn_reply;
    private Button btn_reply_check;
    private JSONArray replyDatajson;
    private PostList postList;
    private ReplyListAdapter replyListAdapter = new ReplyListAdapter();
    private static final String REPLY_LST ="reply_list";
    private static final String REPLY_ID ="id";
    private static final String REPLY_PID ="post_id";
    private static final String REPLY_USER = "user_id";
    private static final String REPLY_POST = "reply";
    private static final String REPLY_DATE = "reply_date";
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
        setContentView(R.layout.activity_reply);
        lst_reply = findViewById(R.id.lst_reply);
        edt_reply = findViewById(R.id.edt_reply);
        btn_reply = findViewById(R.id.btn_reply);
        btn_reply_check = findViewById(R.id.btn_reply_check);
        Intent intent = getIntent();
        postList = (PostList)intent.getSerializableExtra("Reply");

        btn_reply_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getReplyData("http://"+MainActivity.getMyip()+"/carpool_getreplylist.php",
                postList.getPost_num());
        btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edt_reply.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext(), "댓글을 입력해주세요.", Toast.LENGTH_LONG).show();
                    edt_reply.requestFocus();
                    return;
                }else{
                    putReplyData(postList.getPost_num(),
                            MainActivity.getUser_id(), edt_reply.getText().toString());
                    edt_reply.setText(null);
                }
            }
        });

    }

    public class ReplyListAdapter extends BaseAdapter{
        private ArrayList<ReplyList> replyListArrayList = new ArrayList<ReplyList>();

        @Override
        public int getCount() {
            return replyListArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return replyListArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Context context = parent.getContext();

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_reply_listview, null);

            TextView textView1 = convertView.findViewById(R.id.txt_lst_reply_user_id);
            TextView textView2 = convertView.findViewById(R.id.txt_lst_reply_post);
            TextView textView3 = convertView.findViewById(R.id.txt_lst_reply_date);
            ReplyList replyList = replyListArrayList.get(position);

            textView1.setText(replyList.getUser_id());
            textView2.setText(replyList.getReply());
            textView3.setText(replyList.getReply_date());

            return convertView;
        }
        public void addItem(String id, String post_id, String user_id, String reply, String reply_date){
            ReplyList replyList = new ReplyList();
            replyList.setId(id);
            replyList.setPost_id(post_id);
            replyList.setUser_id(user_id);
            replyList.setReply(reply);
            replyList.setReply_date(reply_date);
            replyListArrayList.add(replyList);
        }
    }
    public void setReplyData(String s){
        try{
            JSONObject jsonObject = new JSONObject(s);
            replyDatajson = jsonObject.getJSONArray(REPLY_LST);
            for(int i = 0; i< replyDatajson.length(); i++){
                JSONObject c = replyDatajson.getJSONObject(i);
                String reply_id = c.getString(REPLY_ID);
                String post_id = c.getString(REPLY_PID);
                String user_id = c.getString(REPLY_USER);
                String reply_post = c.getString(REPLY_POST);
                String reply_date = c.getString(REPLY_DATE);
                replyListAdapter.addItem(reply_id, post_id, user_id, reply_post, reply_date);
            }
            lst_reply.setAdapter(replyListAdapter);
            replyListAdapter.notifyDataSetChanged();
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), e.toString(),Toast.LENGTH_LONG).show();
        }
    }
    public void getReplyData(String url, String post_id){
        class GetReplyDataAsync extends AsyncTask<String, Void, String>{
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                setReplyData(s);
            }

            @Override
            protected String doInBackground(String... strings) {
                try{
                    String uri = strings[0];
                    String post_num = strings[1];
                    String data =  URLEncoder.encode("post_id", "UTF-8") + "=" + URLEncoder.encode(post_num, "UTF-8");
                    URL url_ = new URL(uri);
                    URLConnection con = url_.openConnection();
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                    wr.write(data);
                    wr.flush();

                    InputStream rd = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(rd), 8 * 1024);
                    StringBuffer buff = new StringBuffer();
                    String line = null;

                    while ((line = reader.readLine()) != null) {
                        buff.append(line+"\n");
                    }
                    return buff.toString().trim();
                }catch (Exception e){
                    return new String("GetReplyData Exception"+e.getMessage());
                }
            }
        }
        GetReplyDataAsync getReplyDataAsync = new GetReplyDataAsync();
        getReplyDataAsync.execute(url, post_id);

    }
    public void putReplyData(String post_id, final String user_id, String reply_memo){
        class PutReplyDataAsync extends AsyncTask<String, Void, String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ReplyActivity.this, "Please Wait", null, true, true);

            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                replyListAdapter.replyListArrayList.clear();
                getReplyData("http://"+MainActivity.getMyip()+"/carpool_getreplylist.php",
                        postList.getPost_num());
                loading.dismiss();
            }
            @Override
            protected String doInBackground(String... strings) {
                try{
                    String post = strings[0];
                    String user_num = strings[1];
                    String reply_post = strings[2];
                    String link = "http://"+MainActivity.getMyip()+"/carpool_putreply.php";
                    String data = URLEncoder.encode("post_num", "UTF-8") + "=" + URLEncoder.encode(post, "UTF-8");
                    data += "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_num, "UTF-8");
                    data += "&" + URLEncoder.encode("reply", "UTF-8") + "=" + URLEncoder.encode(reply_post, "UTF-8");

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

                    while ((line = reader.readLine()) != null) {
                        buff.append(line);
                        break;
                    }
                    return buff.toString().trim();
                }catch (Exception e){
                    return new String("PutReplyData Exception: "+e.getMessage());
                }
            }
        }
        PutReplyDataAsync putReplyDataAsync = new PutReplyDataAsync();
        putReplyDataAsync.execute(post_id, user_id, reply_memo);
        if(putReplyDataAsync.loading != null){
            putReplyDataAsync.loading.dismiss();
        }
    }
    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
