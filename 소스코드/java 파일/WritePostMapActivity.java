package "패키지 네임";

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.List;

public class WritePostMapActivity  extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private TextView txt_writepost_map_addr;
    private EditText edt_writepostmap_search;
    private Button btn_writepostmap_search_do;
    private Button btn_writepost_map_addr_do;
    private Button btn_writepost_map_cancel;
    private ImageButton btn_writepostmap_myloc;
    private SupportMapFragment frg_writepost_map;
    private String check;
    private long lastTimeBackPressed;
    private LocationManager locationManager;

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
        setContentView(R.layout.activity_writepostmap);
        txt_writepost_map_addr = findViewById(R.id.txt_writepost_map_addr);
        edt_writepostmap_search = findViewById(R.id.edt_writepostmap_search);
        frg_writepost_map = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.frg_writepost_map);
        frg_writepost_map.getMapAsync(this);
        btn_writepostmap_search_do = findViewById(R.id.btn_writepostmap_search_do);
        btn_writepost_map_addr_do = findViewById(R.id.btn_writepost_map_addr_do);
        btn_writepost_map_cancel = findViewById(R.id.btn_writepost_map_cancel);
        btn_writepostmap_myloc = findViewById(R.id.btn_writepostmap_myloc);

        final Intent intent = getIntent();
        check = intent.getStringExtra("check");

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        btn_writepostmap_myloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    //GPS 설정화면으로 이동
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                }
                try{
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                            100, // 통지사이의 최소 시간간격 (miliSecond)
                            1, // 통지사이의 최소 변경거리 (m)
                            mLocationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                            100, // 통지사이의 최소 시간간격 (miliSecond)
                            1, // 통지사이의 최소 변경거리 (m)
                            mLocationListener);
                }catch(SecurityException ex){

                }
            }
        });
        btn_writepost_map_addr_do.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check.equals("1")){
                    intent.putExtra("check", txt_writepost_map_addr.getText().toString());
                    setResult(10, intent);
                }else if(check.equals("2")){
                    intent.putExtra("check", txt_writepost_map_addr.getText().toString());
                    setResult(20, intent);
                }
                finish();
            }
        });
        btn_writepost_map_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.
            Log.d("test", "onLocationChanged, location:" + location);
            final Geocoder geocoder = new Geocoder(WritePostMapActivity.this);
            List<Address> list;
            LatLng point;
            String []splitStr;
            String loc_address;
            try{
                list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),10);
                if(list.size() == 0 ){
                    Toast.makeText(getApplicationContext(), "위치가 표시되지 않는 지역입니다.",
                            Toast.LENGTH_LONG).show();
                }else{
                    splitStr = list.get(0).toString().split(",");
                    loc_address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // 주소
                    MarkerOptions mOptions = new MarkerOptions();
                    point = new LatLng(location.getLatitude(), location.getLongitude());
                    mOptions.title(loc_address);
                    mOptions.snippet(location.getLatitude() + "\n" + location.getLongitude());
                    mOptions.position(point);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,13));
                    mMap.addMarker(mOptions);
                    txt_writepost_map_addr.setText(loc_address);
                }
            }catch (Exception e){
                Log.i("오류2", e.getMessage());
            }
        }
        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test1", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test2", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test3", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };
    public void getLLtoAddress(Double longitude, Double latitude){
        final Geocoder geocoder = new Geocoder(this);
        List<Address> list;
        LatLng point;
        String []splitStr;
        String loc_address;
        try{
            list = geocoder.getFromLocation(longitude, latitude,10);
            if(list.size() == 0){
                Toast.makeText(getApplicationContext(), "위치가 표시되지 않는 지역입니다.",
                        Toast.LENGTH_LONG).show();
            }else{
                splitStr = list.get(0).toString().split(",");
                loc_address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // 주소
                MarkerOptions mOptions = new MarkerOptions();
                point = new LatLng(latitude, longitude);
                mOptions.title(loc_address);
                mOptions.snippet(latitude.toString() + "\n" + longitude.toString());
                mOptions.position(point);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,13));
                mMap.addMarker(mOptions);
                txt_writepost_map_addr.setText(loc_address);
            }
        }catch (Exception e){
            Log.i("오류",e.getMessage());
        }
    }
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        final Geocoder geocoder = new Geocoder(this);
        LatLng base_loc = new LatLng(36.1444249,128.39326860000006);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(base_loc, 13));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                getLLtoAddress(latLng.longitude, latLng.latitude);
            }
        });
        btn_writepostmap_search_do.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = edt_writepostmap_search.getText().toString();
                List<Address> list = null;
                String []splitStr;
                String latitude;
                String longitude;
                try{
                    list = geocoder.getFromLocationName(str,10);
                    if(list.size()==0){
                        Toast.makeText(getApplicationContext(), "위치가 표시되지 않는 지역입니다.", Toast.LENGTH_LONG).show();
                    }else{
                        splitStr = list.get(0).toString().split(",");
                        latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1);
                        longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1);
                        LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                        MarkerOptions mOptions2 = new MarkerOptions();
                        mOptions2.title(str);
                        mOptions2.snippet(latitude.toString()+"\n"+longitude.toString());
                        mOptions2.position(point);
                        mMap.addMarker(mOptions2);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,13));
                        txt_writepost_map_addr.setText(str);
                    }
                }catch (Exception e){
                    Log.i("오류1", e.getMessage());
                }
            }
        });

    }
    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
