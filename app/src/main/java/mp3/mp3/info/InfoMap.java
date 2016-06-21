package mp3.mp3.info;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.percent.PercentFrameLayout;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import mp3.mp3.R;


public class InfoMap extends FragmentActivity implements OnMapReadyCallback {

    StringBuilder polyline;

    ImageAdapter myImageAdapter;
    GridView gridview;
    JSONArray p_json;

    private GoogleMap map;

    PercentFrameLayout back;

    NodeList pathList; // xml 파싱 저장할 노드리스트


    SupportMapFragment mapFragment; // 구글 맵 띄울대 씀
    LatLng sLatLng; // info에서 받아온 가게 위도,경도
    LatLng mLatLng; // mApp에서 내 위치 가져올거

    String s_idx;

    Bitmap b1, b2; // 마커 이미지지

    TextView sname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_map);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 화면회전 고정

        s_idx = getIntent().getStringExtra("s_idx");
        Bitmap bigPictureBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ico_heremap);
        b1 = Bitmap.createScaledBitmap(bigPictureBitmap, bigPictureBitmap.getWidth() / 3, bigPictureBitmap.getHeight() / 3, false);
        bigPictureBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ico_etcmap);
        b2 = Bitmap.createScaledBitmap(bigPictureBitmap, bigPictureBitmap.getWidth() / 3, bigPictureBitmap.getHeight() / 3, false);

        sname = (TextView) findViewById(R.id.info_map_storename);
        sname.setText(getIntent().getStringExtra("sname"));

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map = mapFragment.getMap();


        gridview = (GridView) findViewById(R.id.map_contents);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject j = p_json.getJSONObject(position);

                    startActivity(new Intent(InfoMap.this, InfoActivity2.class)
                            .putExtra("name", j.getString("name"))
                            .putExtra("sname", getIntent().getStringExtra("sname"))
                            .putExtra("price", j.getString("price"))
                            .putExtra("s_price", j.getString("s_price"))
                            .putExtra("explain", j.getString("explain"))
                            .putExtra("s_idx", j.getString("s_idx"))
                            .putExtra("distance", getIntent().getStringExtra("distance"))
                            .putExtra("url", j.getString("url")));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });



        myImageAdapter = new ImageAdapter(this);


        back = (PercentFrameLayout) findViewById(R.id.info_map_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left2, R.anim.slide_out_right);
            }
        });


        new InfoMapAsync().execute();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


        googleMap.getUiSettings().setAllGesturesEnabled(false); // 모든옵션 x
//		googleMap.getUiSettings().setMyLocationButtonEnabled(false); // 내위치 버튼 안보이게
//		googleMap.getUiSettings().setCompassEnabled(false); // 나침반 안보이기
//		googleMap.getUiSettings().setRotateGesturesEnabled(false); // 화면 회전 안되게
        googleMap.getUiSettings().setScrollGesturesEnabled(true); // 스크롤
        googleMap.getUiSettings().setZoomGesturesEnabled(true); // 줌


        // mLatLng 내 위치, sLatLng 상점 위치

        // 내 마커, 상점 마커
        map.addMarker(new MarkerOptions().position(mLatLng).icon(BitmapDescriptorFactory.fromBitmap(b1)));
        map.addMarker(new MarkerOptions().position(sLatLng).icon(BitmapDescriptorFactory.fromBitmap(b2)));

        PolylineOptions po = new PolylineOptions().geodesic(true);
        po.color(Color.rgb(54, 72, 124));
        StringTokenizer poly = new StringTokenizer(polyline.toString(), " ");
        StringTokenizer poly2;
        double Lat, Lng;
        double minLat = (sLatLng.latitude > mLatLng.latitude ? mLatLng.latitude : sLatLng.latitude);
        double maxLat = (sLatLng.latitude > mLatLng.latitude ? sLatLng.latitude : mLatLng.latitude);
        double minLng = (sLatLng.longitude > mLatLng.longitude ? mLatLng.longitude : sLatLng.longitude);
        double maxLng = (sLatLng.longitude > mLatLng.longitude ? sLatLng.longitude : mLatLng.longitude);


        po.add(mLatLng);
        while (poly.hasMoreTokens()) {
            poly2 = new StringTokenizer(poly.nextToken(), ",");
            Lng = Double.valueOf(poly2.nextToken());
            if (Lng > maxLng)
                maxLng = Lng;
            if (Lng < minLng)
                minLng = Lng;
            Lat = Double.valueOf(poly2.nextToken());
            if (Lat > maxLat)
                maxLat = Lat;
            if (Lat < minLat)
                minLat = Lat;
            po.add(new LatLng(Lat, Lng));
        }
        po.add(sLatLng);

        map.addPolyline(po);

        // 폴리라인 추가
//        map.addPolyline(new PolylineOptions().geodesic(true)
//                .add(mLatLng)
//                .add(sLatLng));

        //      Toast.makeText(InfoMap.this, pathList.item(0).getChildNodes().item(12).getFirstChild().getNodeValue() + "", Toast.LENGTH_SHORT).show();

        //map.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
        //map.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 16));

        LatLngBounds mBounds = new LatLngBounds(new LatLng(minLat, minLng), new LatLng(maxLat, maxLng));

        map.animateCamera(CameraUpdateFactory.newLatLngBounds(mBounds, getResources().getDisplayMetrics().widthPixels / 5));

    }


    public class InfoMapAsync extends AsyncTask<String, Void, Void> {


        // conn, url은 여러곳에서 계속 재사용된다.
        HttpURLConnection conn;
        URL url;

        Bitmap[] btm; // 동적할당할 이미지를 담을 비트맵
        ImageView[] imgarr; // 동적할당할 비트맵을 담을 이미지뷰
        LinearLayout[] Viewarr; // 동적할당할 이미지뷰를 담을 행

        HttpsURLConnection https;

        JSONArray json;
        String result;
        SharedPreferences sharedPref = getSharedPreferences("location", MODE_PRIVATE);

        @Override
        protected Void doInBackground(String... params) {


            try {


                url = new URL("http://112.166.55.35:9738/list?s_idx=" + s_idx);
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

                        p_json = new JSONObject("{\"result\":" + result + "}").getJSONArray("result");
                        conn.disconnect();

                    }
                }



                url = new URL("http://112.166.55.35:9738/search?s_idx=" + s_idx);
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

                        json = new JSONObject("{\"result\":" + result + "}").getJSONArray("result");
                        conn.disconnect();


                    }
                }


                mLatLng = new LatLng(Double.parseDouble(sharedPref.getString("lat", "")), Double.parseDouble(sharedPref.getString("lng", "")));
                sLatLng = new LatLng(Double.parseDouble(json.getJSONObject(0).getString("lat")), Double.parseDouble(json.getJSONObject(0).getString("lng")));


                url = new URL("https://apis.skplanetx.com/tmap/routes/pedestrian?version=1&startX=" + mLatLng.longitude + "&startY=" + mLatLng.latitude + "&endX=" + sLatLng.longitude + "&endY=" + sLatLng.latitude + "&reqCoordType=WGS84GEO&resCoordType=WGS84GEO&startName=z&endName=z&appKey=bc59db7a-3522-3a36-8ca7-fe02a7be39e1");

                https = (HttpsURLConnection) url.openConnection();
                https.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                });


                // polyline 생성할 경로 T-map에서 받아와서 pathList에 저장

                conn = https;
                conn.setRequestProperty("Accept", "application/xml"); // 안해주면 json으로 받아옴
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document doc = db.parse(conn.getInputStream());
                    doc.getDocumentElement().normalize();
                    pathList = doc.getElementsByTagName("coordinates"); // 지정한 태그 이름으로 노드리스트를 만듬
                    conn.disconnect();
                }

                polyline = new StringBuilder();
                for (int i = 0; i < pathList.getLength(); i++)
                    polyline.append(pathList.item(i).getFirstChild().getNodeValue());


            } catch (Exception ex) {
                Log.e("SampleHTTP", "Exception in processing response.", ex);
                ex.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mapFragment.getMapAsync(InfoMap.this);
            gridview.setAdapter(myImageAdapter);
        }
    }


    public class ImageAdapter extends BaseAdapter {

        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return p_json.length();
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
                        .load(p_json.getJSONObject(position).getString("url"))
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

    @Override
    public void onBackPressed() {
            finish();
            overridePendingTransition(R.anim.slide_in_left2, R.anim.slide_out_right);
    }

}
