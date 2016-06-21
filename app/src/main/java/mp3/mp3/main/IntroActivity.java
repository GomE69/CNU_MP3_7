package mp3.mp3.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import mp3.mp3.R;

/**
 * Created by KimYeongJun on 2016-06-21.
 */
public class IntroActivity extends AppCompatActivity {

    LocationManager manager;
    GPSListener gpsListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_main);


        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gpsListener = new GPSListener();

//        startLocationService();

    }

    /**
     * 위치 정보 확인을 위해 정의한 메소드
     */
    private void startLocationService() {

        long minTime = 5000;
        float minDistance = 0;

        // GPS를 이용한 위치 요청
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);

        // 네트워크를 이용한 위치 요청
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);


    }

    /**
     * 리스너 클래스 정의
     */
    private class GPSListener implements LocationListener {
        /**
         * 위치 정보가 확인될 때 자동 호출되는 메소드
         */
        public void onLocationChanged(Location location) {
            SharedPreferences sp = getSharedPreferences("location", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();

            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

            editor.putString("lat", latitude.toString());
            editor.putString("lng", longitude.toString());
            editor.commit();

            Log.i("GPS수신 : ", "lat : " + latitude + ", lng : " + longitude);

            if (ActivityCompat.checkSelfPermission(IntroActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(IntroActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            manager.removeUpdates(gpsListener);
            finish();
            startActivity(new Intent(IntroActivity.this, MainActivity.class));
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    }


}
