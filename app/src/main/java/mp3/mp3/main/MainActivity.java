package mp3.mp3.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import mp3.mp3.R;
import mp3.mp3.info.InfoActivity;
import mp3.mp3.info.InfoMap;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    EditText input01;

    private GoogleMap mMap; // GoogleMap 전역변수
    ImageAdapter myImageAdapter;
    GridView gridview;

    JSONArray arr_json, s_jon;
    int json_size;

    // 내 위치 마커
    Marker mMarker = null;
    GoogleMap map;
    SupportMapFragment mapFragment;

    Bitmap b1, b2; // 마커 이미지지

    LatLng mLatLng; // 내위치 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getSharedPreferences("location", MODE_PRIVATE);
        mLatLng = new LatLng(Double.parseDouble(sharedPref.getString("lat", "")), Double.parseDouble(sharedPref.getString("lng", "")));


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map = mapFragment.getMap();
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                try {
                    JSONObject j = null;
                    for (int i = 0; i < s_jon.length(); i++)
                        if (marker.getTitle().equals(s_jon.getJSONObject(i).getString("name"))) {
                            j = s_jon.getJSONObject(i);
                            break;
                        }
                    startActivity(new Intent(MainActivity.this, InfoMap.class)
                            .putExtra("sname", j.getString("name"))
                            .putExtra("s_idx", j.getString("s_idx")));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });

        Bitmap bigPictureBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ico_heremap);
        b1 = Bitmap.createScaledBitmap(bigPictureBitmap, bigPictureBitmap.getWidth() / 3, bigPictureBitmap.getHeight() / 3, false);
        bigPictureBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ico_etcmap);
        b2 = Bitmap.createScaledBitmap(bigPictureBitmap, bigPictureBitmap.getWidth() / 3, bigPictureBitmap.getHeight() / 3, false);


        gridview = (GridView) findViewById(R.id.contents);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject j = arr_json.getJSONObject(position);
                    JSONObject s = null;
                    float[] result = new float[1];
                    for (int i = 0; i < s_jon.length(); i++) {
                        if (j.getString("s_idx").equals(s_jon.getJSONObject(i).getString("s_idx"))) {
                            s = s_jon.getJSONObject(i);
                            break;
                        }
                    }
                    Location.distanceBetween(mLatLng.latitude, mLatLng.longitude, s.getDouble("lat"), s.getDouble("lng"), result);

                    startActivity(new Intent(MainActivity.this, InfoActivity.class)
                            .putExtra("name", j.getString("name"))
                            .putExtra("sname", s.getString("name"))
                            .putExtra("price", j.getString("price"))
                            .putExtra("s_price", j.getString("s_price"))
                            .putExtra("explain", j.getString("explain"))
                            .putExtra("s_idx", j.getString("s_idx"))
                            .putExtra("distance", (int) result[0] + "")
                            .putExtra("url", j.getString("url")));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

//        gridview.setOnItemLongClickListener();

        myImageAdapter = new ImageAdapter(this);

        mapFragment.getMapAsync(MainActivity.this);
        new Acces().execute(mLatLng.latitude + "", mLatLng.longitude + "");
//        startLocationService();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.getUiSettings().setAllGesturesEnabled(false); // 모든옵션 x
//		googleMap.getUiSettings().setMyLocationButtonEnabled(false); // 내위치 버튼 안보이게
//		googleMap.getUiSettings().setCompassEnabled(false); // 나침반 안보이기
//		googleMap.getUiSettings().setRotateGesturesEnabled(false); // 화면 회전 안되게
        googleMap.getUiSettings().setScrollGesturesEnabled(true); // 스크롤
        googleMap.getUiSettings().setZoomGesturesEnabled(true); // 줌

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 16));

    }

    public void GOINFO(View view) {
        startActivity(new Intent(this, InfoActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left2);
    }


    public class ImageAdapter extends BaseAdapter {

        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return json_size;
        }

        public Object getItem(int position) {

            return position;
        }

        public long getItemId(int position) {
            return position;
        }


        public View getView(final int position, View convertView, ViewGroup parent) {
            ImageView imageView = null;


            if (convertView == null)
                imageView = new ImageView(mContext.getApplicationContext());
            else
                imageView = (ImageView) convertView;


//			imageView.setImageURI(Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, String.valueOf(id)));
//			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(getResources().getDisplayMetrics().widthPixels / 3, getResources().getDisplayMetrics().widthPixels / 3));

            try {
                Glide.with(mContext)
                        .load(arr_json.getJSONObject(position).getString("url"))
                        .error(R.drawable.s_background)
                        .centerCrop()
                        //					.diskCacheStrategy(DiskCacheStrategy.ALL) // 지정안해주면 처음부터 지정한 사이즈로 로드하고, 지정하면 원본사이즈로 로드해서 리사이징함
                        .into(imageView);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return imageView;
        }

    }


    /**
     * 소켓 연결할 스레드 정의
     */

    public class Acces extends AsyncTask<String, Void, Void> {


        String result;


        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL("http://112.166.55.35:9738/p_init?lat=" + params[0] + "&lng=" + params[1]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn != null) {

                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);


                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                        BufferedInputStream buff = new BufferedInputStream(conn.getInputStream());
                        BufferedReader buff2 = new BufferedReader(new InputStreamReader(buff, "UTF-8"));

                        String read;
                        result = "";
                        while ((read = buff2.readLine()) != null) {
                            result += read;
                        }

                        arr_json = new JSONObject("{\"result\":" + result + "}").getJSONArray("result");
                        json_size = arr_json.length();
                        conn.disconnect();

                    }
                }


                url = new URL("http://112.166.55.35:9738/s_init?lat=" + params[0] + "&lng=" + params[1]);
                conn = (HttpURLConnection) url.openConnection();
                if (conn != null) {

                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);


                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                        BufferedInputStream buff = new BufferedInputStream(conn.getInputStream());
                        BufferedReader buff2 = new BufferedReader(new InputStreamReader(buff, "UTF-8"));

                        String read;
                        result = "";
                        while ((read = buff2.readLine()) != null) {
                            result += read;
                        }

                        s_jon = new JSONObject("{\"result\":" + result + "}").getJSONArray("result");
                        conn.disconnect();


                    }
                }

            } catch (Exception ex) {
                Log.e("SampleHTTP", "Exception in processing response.", ex);
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

//            try {
//                Glide.with(MainActivity.this)
//                        .load(json.getString("url"))
//                        .error(R.drawable.ic_launcher)
//                        .into(img);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            txtMsg.setText(result);
            gridview.setAdapter(myImageAdapter);
            map.addMarker(new MarkerOptions()
                    .position(mLatLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(b1)));
            for (int i = 0; i < s_jon.length(); i++) {
                try {
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.valueOf(s_jon.getJSONObject(i).getString("lat")), Double.valueOf(s_jon.getJSONObject(i).getString("lng"))))
                            .icon(BitmapDescriptorFactory.fromBitmap(b2))
                            .title(s_jon.getJSONObject(i).getString("name")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


    }



}
