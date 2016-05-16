package mp3.mp3.info;

import android.content.Intent;
import android.os.Bundle;
import android.support.percent.PercentFrameLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import mp3.mp3.R;


public class InfoActivity extends AppCompatActivity {

    PercentFrameLayout back;
    TextView price, s_price, distance, name, explain;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_main);

        img = (ImageView) findViewById(R.id.info_img);
        Glide.with(this)
                .load(getIntent().getStringExtra("url"))
                .error(R.drawable.s_background)
                .centerCrop()
                //					.diskCacheStrategy(DiskCacheStrategy.ALL) // 지정안해주면 처음부터 지정한 사이즈로 로드하고, 지정하면 원본사이즈로 로드해서 리사이징함
                .into(img);


        price = (TextView) findViewById(R.id.info_price1);
        price.setText(getIntent().getStringExtra("price"));
        s_price = (TextView) findViewById(R.id.info_price2);
        s_price.setText(getIntent().getStringExtra("s_price"));
        distance = (TextView) findViewById(R.id.info_distance);
        distance.setText(getIntent().getStringExtra("거리거리"));
        name = (TextView) findViewById(R.id.name);
        name.setText(getIntent().getStringExtra("name"));
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

    public void GOMAP(View v) {
        startActivity(new Intent(this, InfoMap.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left2);
    }
}
