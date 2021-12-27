package cc.nufe.tools.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import cc.nufe.tools.R;
import cc.nufe.tools.View.SystemUtil;
import cc.nufe.tools.database.dbUtil;

public class settingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        SystemUtil.setStatusBarColor(this, Color.parseColor("#2C3E50"));

        ImageView iv_back= (ImageView)findViewById(R.id.iv_close);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Button btn_clean= (Button)findViewById(R.id.btn_clean);
        btn_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbUtil dbu= new dbUtil();

                if(dbu.deleteTable(settingActivity.this)){
                    Toast.makeText(settingActivity.this, "清理完毕", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}