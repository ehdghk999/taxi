package kr.ac.kumoh.s20130903.myapplication;

import android.graphics.Color;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

public class MyMapViewEventListener implements MapView.MapViewEventListener{
    @Override
    public void onMapViewInitialized(MapView mapView) {
        MapPOIItem marker = new MapPOIItem();//마커 객체 선언
        marker.setItemName("Default Marker");//마커 이름 선언
        marker.setTag(0);

        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(36.119485,128.34457339999994));
        //마커 중심점 설정
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        // 기본 BluePin 마커 모양으로 설정
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, RedPin 마커 모양으로 설정
        mapView.addPOIItem(marker);//MapView에 마커 추가
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(36.119485,128.34457339999994),true);
        //마커 중심점과 지도 중심점이 다르므로 지도 중심점 설정

    }
    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }
    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        //중심점 변경, 구미시에서 금오공대로
        mapView.setMapCenterPoint(MapPoint.	mapPointWithGeoCoord(36.1444249,
                128.39326860000006), true);
        //줌 레벨 변경, 기본 레벨에서 4 레벨로
        mapView.setZoomLevel(4, true);
    }
    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
        MapPolyline polyline = new MapPolyline();//선 객체 생성
        polyline.setTag(1000);
        polyline.setLineColor(Color.argb(128, 255, 51, 0)); //선 색

        // 선 좌표
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.1444249, 128.39326860000006));
        // 첫 번째 좌표
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.136234,128.398668));
        // 두 번째 좌표

        mapView.addPolyline(polyline); // 선 지도에 그리기.

        MapPointBounds mapPointBounds = new MapPointBounds(polyline.getMapPoints());
        int padding = 100; // px
        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));
        // 지도뷰의 중심좌표와 줌레벨을 선이 모두 나오도록 조정.

    }
    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }
    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }
    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }
    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {


    }
    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
        MapCircle circle = new MapCircle( // 원 객체 생성
                MapPoint.mapPointWithGeoCoord(36.1444249, 128.39326860000006), // 원 중심점 좌표 설정
                500, // 원 반지름 길이
                Color.argb(50, 10, 10, 0), // 원 윤곽선 색
                Color.argb(100, 100, 200, 0));//원 내부 색
        circle.setTag(1234);
        mapView.addCircle(circle);//지도에 원 그리기

        // 지도뷰의 중심좌표와 줌레벨을 Circle이 모두 나오도록 조정.
        MapPointBounds[] mapPointBoundsArray = { circle.getBound()};
        MapPointBounds mapPointBounds = new 	MapPointBounds(mapPointBoundsArray);
        int padding = 50; // px
        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));

    }

}

