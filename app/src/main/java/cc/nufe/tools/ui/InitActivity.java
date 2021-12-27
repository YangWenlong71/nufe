package cc.nufe.tools.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import cc.nufe.tools.R;
import cc.nufe.tools.View.SystemUtil;

import java.util.Timer;
import java.util.TimerTask;

public class InitActivity extends AppCompatActivity {
    private TextView init_text_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        SystemUtil.setStatusBarColor(this, Color.parseColor("#ffffff"));
        init_text_number = (TextView)findViewById(R.id.init_text_number);
        timer_start();


//        dbUtil dbu= new dbUtil();
//        dbu.deleteDatabase(InitActivity.this);
    }

    private int timer_flag =3;
    private void timer_start()
    {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                Message msg =new Message();
                msg.what = 1;
                msg.obj = timer_flag;
                handler.sendMessage(msg);
                timer_flag--;
                if(timer_flag < 0)
                {
                    timer_flag = 3;
                    timer.cancel();
                    Intent intent = new Intent(InitActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 3, 1000);
    }


    private Handler handler =new Handler()
    {
        public void handleMessage(Message msg)
        {
            if(msg.what == 1)
            {
                init_text_number.setText(""+msg.obj);
            }
        }
    };

}
