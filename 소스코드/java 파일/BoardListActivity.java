package "패키지 네임";

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BoardListActivity extends AppCompatActivity {
    private EditText edt_search_word;
    private Button btn_write_post;
    private Button btn_board_list_logout;
    private ListView list_carpool_board_view;
    private JSONArray postDatajson;
    private PostListAdapter postListAdapter = new PostListAdapter();
    private List<PostList> list;
    private final static String POST_LST = "board_list";
    private final static String POST_NUM = "id";
    private final static String POST_STA = "start_location";
    private final static String POST_END = "end_location";
    private final static String POST_TIM = "start_time";
    private final static String POST_USE = "studentID";
    private final static String POST_DATE = "write_date";
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
        setContentView(R.layout.activity_boardlist);
        edt_search_word = findViewById(R.id.edt_search_word);
        btn_write_post = findViewById(R.id.btn_write_post);
        btn_board_list_logout = findViewById(R.id.btn_board_list_logout);
        list_carpool_board_view = findViewById(R.id.list_carpool_board_view);
        list = new ArrayList<PostList>();
        edt_search_word.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String search_word = edt_search_word.getText().toString();
                postListAdapter.postListArrayList.clear();
                if(search_word.length()==0){
                    postListAdapter.postListArrayList.addAll(list);
                }else{
                    for(int i=0;i<list.size();i++){
                        if(list.get(i).getStart_loc().contains(search_word)||list.get(i).getEnd_loc().contains(search_word)){
                            postListAdapter.postListArrayList.add(list.get(i));
                        }
                    }
                }
                postListAdapter.notifyDataSetChanged();
            }
        });
        btn_write_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoardListActivity.this, WritePostActivity.class);
                startActivity(intent);
            }
        });
        btn_board_list_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        postListAdapter.postListArrayList.clear();
        getPostData("http://"+MainActivity.getMyip()+"/carpool_getboardlist.php");
        list_carpool_board_view.setAdapter(postListAdapter);

    }

    public class PostListAdapter extends BaseAdapter{
        private ArrayList<PostList> postListArrayList = new ArrayList<PostList>();
        @Override
        public int getCount() {
            return postListArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return postListArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Context context = parent.getContext();
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.activity_postlist_view, null);
            }
            TextView textView = convertView.findViewById(R.id.txt_postlist_view_num);
            Button button = convertView.findViewById(R.id.btn_postlist_view);
            PostList postList = postListArrayList.get(position);

            BTNPostListLisetener btnPostListLisetener = new BTNPostListLisetener(postList);
            button.setOnClickListener(btnPostListLisetener);
            String sta_tim = postList.getStart_time();
            String sta_loc = postList.getStart_loc();
            String ed_loc = postList.getEnd_loc();
            String us_id = postList.getUser_id();
            String post_dat = postList.getPost_date();


            String total = "출발 시간: "+sta_tim+"\n경로: "+sta_loc+"→"+ed_loc+"\n작성자: "+us_id+"\n"+post_dat;

            SpannableStringBuilder sp1 = new SpannableStringBuilder(total);

            sp1.setSpan(new ForegroundColorSpan(Color.rgb(231,230,230)),
                    0,6,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
            sp1.setSpan(new ForegroundColorSpan(Color.rgb(248,203,173)),
                    7,7+sta_tim.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sp1.setSpan(new ForegroundColorSpan(Color.rgb(231,230,230)),
                    7+sta_tim.length(),
                    12+sta_tim.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sp1.setSpan(new ForegroundColorSpan(Color.rgb(187,235,219)),
                    12+sta_tim.length(),
                    12+sta_tim.length()+sta_loc.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sp1.setSpan(new ForegroundColorSpan(Color.rgb(231,230,230)),
                    12+sta_tim.length()+sta_loc.length(),
                    13+sta_tim.length()+sta_loc.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sp1.setSpan(new ForegroundColorSpan(Color.rgb(187,235,219)),
                    13+sta_tim.length()+sta_loc.length(),
                    13+sta_tim.length()+sta_loc.length()+ed_loc.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sp1.setSpan(new ForegroundColorSpan(Color.rgb(231,230,230)),
                    13+sta_tim.length()+sta_loc.length()+ed_loc.length(),
                    19+sta_tim.length()+sta_loc.length()+ed_loc.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sp1.setSpan(new ForegroundColorSpan(Color.rgb(209,179,243)),
                    19+sta_tim.length()+sta_loc.length()+ed_loc.length(),
                    19+sta_tim.length()+sta_loc.length()+ed_loc.length()+us_id.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sp1.setSpan(new ForegroundColorSpan(Color.rgb(219,219,219)),
                    20+sta_tim.length()+sta_loc.length()+ed_loc.length()+us_id.length(),
                    20+sta_tim.length()+sta_loc.length()+ed_loc.length()+us_id.length()+post_dat.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            String position_ = String.valueOf(position+1);
            textView.setText(position_);
            button.setText(sp1);

            return convertView;
        }
        public void addItem(String post_num, String start_loc, String end_loc,
                            String start_time, String user, String post_date){
            PostList postList = new PostList();
            postList.setPost_num(post_num);
            postList.setStart_loc(start_loc);
            postList.setEnd_loc(end_loc);
            postList.setStart_time(start_time);
            postList.setUser_id(user);
            postList.setPost_date(post_date);
            postListArrayList.add(postList);
        }
    }
    public class BTNPostListLisetener implements Button.OnClickListener{
        private PostList postList;

        public BTNPostListLisetener(PostList postList) {
            this.postList = postList;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(BoardListActivity.this, CarpoolPostActivity.class);
            intent.putExtra("postlist", postList);
            startActivity(intent);
        }
    }

    public void setPostList(String s){
        try{
            JSONObject jsonObject = new JSONObject(s);
            postDatajson = jsonObject.getJSONArray(POST_LST);
            for(int i = 0; i< postDatajson.length(); i++){
                JSONObject c = postDatajson.getJSONObject(i);
                String post_num = c.getString(POST_NUM);
                String start_loc = c.getString(POST_STA);
                String end_loc = c.getString(POST_END);
                String start_time = c.getString(POST_TIM);
                String user = c.getString(POST_USE);
                String post_date = c.getString(POST_DATE);
                postListAdapter.addItem(post_num, start_loc, end_loc,
                        start_time, user, post_date);
            }
            list.addAll(postListAdapter.postListArrayList);
            postListAdapter.notifyDataSetChanged();
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), e.toString(),Toast.LENGTH_LONG).show();
        }
    }
    public void getPostData(String url){
        class GetPostDataAsync extends AsyncTask<String, Void, String>{
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                setPostList(s);
            }

            @Override
            protected String doInBackground(String... strings) {
                String uri = strings[0];
                BufferedReader bufferedReader = null;
                try{
                    URL url_ = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url_.openConnection();
                    StringBuilder sb = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                }catch (Exception e){
                    return new String("GetPostDataAsync Exception"+e.getMessage());
                }
            }
        }
        GetPostDataAsync getPostDataAsync = new GetPostDataAsync();
        getPostDataAsync.execute(url);

    }
    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
