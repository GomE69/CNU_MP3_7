package mp3.mp3.info;

import android.os.Bundle;
import android.support.percent.PercentFrameLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import mp3.mp3.R;

public class InfoActivity extends AppCompatActivity {

    PercentFrameLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_main);

        back = (PercentFrameLayout) findViewById(R.id.info_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left2, R.anim.slide_out_right);
            }
        });
    }

}
