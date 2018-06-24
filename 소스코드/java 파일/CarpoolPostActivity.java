package "패키지 네임";

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

public class CarpoolPostActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private TextView txt_carpool_post_user_id;
    private TextView txt_carpool_post_start_time;
    private TextView txt_carpool_post_start_loc;
    private TextView txt_carpool_post_end_loc;
    private Button btn_carpool_post_check;
    private Button btn_carpool_post_load;
    private Button btn_carpool_post_delete;
    private Button btn_carpool_post_reply;
    private PostList postList;
    private SupportMapFragment mapFragment;
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
        setContentView(R.layout.activity_carpoolpost);
        txt_carpool_post_user_id = findViewById(R.id.txt_carpool_post_user_id);
        txt_carpool_post_start_time = findViewById(R.id.txt_carpool_post_start_time);
        txt_carpool_post_start_loc = findViewById(R.id.txt_carpool_post_start_loc);
        txt_carpool_post_end_loc = findViewById(R.id.txt_carpool_post_end_loc);
        btn_carpool_post_check = findViewById(R.id.btn_carpool_post_check);
        btn_carpool_post_load = findViewById(R.id.btn_carpool_post_load);
        btn_carpool_post_delete = findViewById(R.id.btn_carpool_post_delete);
        btn_carpool_post_reply = findViewById(R.id.btn_carpool_post_reply);

        Intent intent = getIntent();
        postList = (PostList)intent.getSerializableExtra("postlist");
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googlemapfrg);
        mapFragment.getMapAsync(this);
        txt_carpool_post_user_id.setText("작성자\n"+postList.getUser_id());
        txt_carpool_post_start_time.setText("출발 시간\n"+postList.getStart_time());
        txt_carpool_post_start_loc.setText("출발지\n"+postList.getStart_loc());
        txt_carpool_post_end_loc.setText("도착지\n"+postList.getEnd_loc());
        btn_carpool_post_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri intentUri = Uri.parse("http://www.google.com/maps/dir/?api=1&f=d&origin="
                        +postList.getStart_loc()+"&destination="
                        +postList.getEnd_loc()+"&hl=ko&travelmode=transit");
                Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });
        btn_carpool_post_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarpoolPostActivity.this, ReplyActivity.class);
                intent.putExtra("Reply", postList);
                startActivity(intent);
            }
        });
        btn_carpool_post_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(postList.getUser_id().equals(MainActivity.getUser_id())){
                    deletePost(postList.getPost_num(), MainActivity.getUser_id());
                }else{
                    Toast.makeText(getApplicationContext(), "다른 사용자의 게시물입니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
        btn_carpool_post_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        Geocoder geocoder = new Geocoder(this);
        List<Address> list = null;
        List<Address> list2 = null;
        try{
            list = geocoder.getFromLocationName(
                    postList.getStart_loc(), // 주소
                    10);
            list2 = geocoder.getFromLocationName(
                    postList.getEnd_loc(),
                    10);
        }catch (IOException e) {
            e.printStackTrace();
            Log.e("test","입출력 오류 - 서버에서 주소변환시 에러발생");
        }

        if(list != null){
            if(list.size() == 0){
                LatLng base_location = new LatLng(37.52487, 126.92723);
                MarkerOptions makerOptions = new MarkerOptions();
                makerOptions
                        .position(base_location)
                        .title("검색된 위치가 없습니다.");
                mMap.addMarker(makerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(base_location,13));
            }else{
                String []splitStr = list.get(0).toString().split(",");
                String start_loc_address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // 주소
                String start_loc_latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
                String start_loc_longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도

                String []splitStr2 = list2.get(0).toString().split(",");
                String end_loc_address = splitStr2[0].substring(splitStr2[0].indexOf("\"") + 1,splitStr2[0].length() - 2); // 주소
                String end_loc_latitude = splitStr2[10].substring(splitStr2[10].indexOf("=") + 1); // 위도
                String end_loc_longitude = splitStr2[12].substring(splitStr2[12].indexOf("=") + 1); // 경도

                LatLng start_loc = new LatLng(Double.parseDouble(start_loc_latitude), Double.parseDouble(start_loc_longitude));
                LatLng end_loc = new LatLng(Double.parseDouble(end_loc_latitude), Double.parseDouble(end_loc_longitude));

                MarkerOptions makerOptions = new MarkerOptions();
                MarkerOptions makerOptions2 = new MarkerOptions();

                makerOptions.title("출발지");
                makerOptions.snippet(start_loc_address);
                makerOptions.position(start_loc);
                makerOptions2.title("도착지");
                makerOptions2.snippet(end_loc_address);
                makerOptions2.position(end_loc);

                mMap.addMarker(makerOptions);
                mMap.addMarker(makerOptions2);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(start_loc).include(end_loc);
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0));
            }
        }


    }
    public void deletePost(String post_num, String id){
        class DeletePostAsync extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(CarpoolPostActivity.this, "Please Wait", null, true, true);

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
                    String post_num = strings[0];
                    String user_id = strings[1];
                    String link = "http://"+MainActivity.getMyip()+"/carpool_deletepost.php";
                    String data = URLEncoder.encode("post_num", "UTF-8") + "=" + URLEncoder.encode(post_num, "UTF-8");
                    data += "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8");
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
        DeletePostAsync deletePostAsync = new DeletePostAsync();
        deletePostAsync.execute(post_num, id);
        if(deletePostAsync.loading != null){
            deletePostAsync.loading.dismiss();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
