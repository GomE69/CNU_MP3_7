package mp3.mp3.info;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.percent.PercentFrameLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import mp3.mp3.R;


public class InfoActivity extends AppCompatActivity {

    PercentFrameLayout back, imgmap;
    TextView price, s_price, distance, name, sname,explain;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_main);


        img = (ImageView) findViewById(R.id.info_img);
        img.setLayoutParams(new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().widthPixels));

        Glide.with(this)
                .load(getIntent().getStringExtra("url"))
                .error(R.drawable.s_background)
                .centerCrop()
                //					.diskCacheStrategy(DiskCacheStrategy.ALL) // 지정안해주면 처음부터 지정한 사이즈로 로드하고, 지정하면 원본사이즈로 로드해서 리사이징함
                .into(img);


        imgmap = (PercentFrameLayout) findViewById(R.id.info_map);
        imgmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InfoActivity.this, InfoMap.class)
                .putExtra("s_idx",getIntent().getStringExtra("s_idx"))
                .putExtra("sname",getIntent().getStringExtra("sname"))
                .putExtra("distance", getIntent().getStringExtra("distance")));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left2);
            }
        });

        price = (TextView) findViewById(R.id.info_price1);
        price.setText(getIntent().getStringExtra("price"));
        price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        s_price = (TextView) findViewById(R.id.info_price2);
        s_price.setText(getIntent().getStringExtra("s_price"));
        distance = (TextView) findViewById(R.id.info_distance);
        distance.setText(getIntent().getStringExtra("distance"));
        name = (TextView) findViewById(R.id.name);
        name.setText(getIntent().getStringExtra("name"));
        sname = (TextView) findViewById(R.id.sname);
        sname.setText(getIntent().getStringExtra("sname"));
        explain = (TextView) findViewById(R.id.info_content);
        explain.setText(getIntent().getStringExtra("explain"));


        back = (PercentFrameLayout) findViewById(R.id.info_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left2, R.anim.slide_out_right);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left2, R.anim.slide_out_right);
    }


}
