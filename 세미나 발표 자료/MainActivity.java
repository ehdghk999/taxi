package kr.ac.kumoh.s20130903.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;


public class MainActivity extends AppCompatActivity {
    private MapView.MapViewEventListener mapViewEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MapView mapView = new MapView(this);
        MyMapViewEventListener myMapViewEventListener = new MyMapViewEventListener();
        mapView.setMapViewEventListener(myMapViewEventListener);
        LinearLayout container = findViewById(R.id.map_view);
        container.addView(mapView);

    }
}
