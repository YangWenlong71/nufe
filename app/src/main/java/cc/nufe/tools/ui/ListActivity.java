package cc.nufe.tools.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import cc.nufe.tools.Cache.ACache;
import cc.nufe.tools.R;
import cc.nufe.tools.View.SystemUtil;
import cc.nufe.tools.adapter.subjecta;
import cc.nufe.tools.model.subject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ListActivity extends AppCompatActivity {


    private ListView lv_sub;
    private List<subject> list = new ArrayList<subject>();
    private subject listc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        SystemUtil.setStatusBarColor(this, Color.parseColor("#2C3E50"));

        ImageView iv_setting = (ImageView) findViewById(R.id.iv_setting);
        TextView tv_qq = (TextView) findViewById(R.id.tv_qq);
        ImageView iv_back = (ImageView) findViewById(R.id.iv_close);
        lv_sub = (ListView) findViewById(R.id.lv_sub);

        //数据缓存
        ACache mCache = ACache.get(ListActivity.this);
        String subjectlist = mCache.getAsString("subjectlist");

        System.out.println("subjectlist:::" + subjectlist);

//        if (subjectlist != null) {
//            List<subject> cc = JSON.parseArray(subjectlist, subject.class);
//            for (subject normControl : cc) {
//                listc = new subject();
//                String name = normControl.getNc_subject();
//
//                System.out.println("name:::" + name);
//                listc.setNc_subject(name);
//
//                list.add(listc);
//            }
//            Message msg = new Message();
//            msg.what = 16;
//            Thandler.sendMessage(msg);
//        } else {
            requestSubJect("http://www.nufe.cc:8000/api/all_lessons_title");
  //      }




        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        iv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ListActivity.this, settingActivity.class);//从哪里跳到哪里
                ListActivity.this.startActivity(intent);
            }
        });
        tv_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinQQGroup("n7YgzJ4U9rc-CCYWZ722oRIXD4r4BYuy");
            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            return true;
        return super.onKeyDown(keyCode, event);
    }//屏蔽返回键

    private void requestSubJect(String moreUrl) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request
                .Builder()
                .url(moreUrl)//要访问的链接
                .build();

        Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String result = response.body().string();

                //存到缓存
             //   ACache mCache = ACache.get(ListActivity.this);
             //   mCache.put("subjectlist", result, 3600);//保存3分钟

                List<subject> cc = JSON.parseArray("["+result+"]", subject.class);
                for (subject normControl : cc) {
                    listc = new subject();
                    String name = normControl.getNc_subject();
                    System.out.println("name:::" + name);
                    listc.setNc_subject(name);
                    list.add(listc);
                }
                Message msg = new Message();
                msg.what = 16;
                Thandler.sendMessage(msg);
            }
        });
    }


    private Handler Thandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 16:

                    click((ArrayList<subject>) list);


                    break;
            }
        }
    };
    private subjecta sjta;

    private void click(ArrayList<subject> list) {

        sjta = new subjecta(R.layout.item_sub, ListActivity.this, list);
        lv_sub.setAdapter(sjta);


    }



    /****************
     *
     * 发起添加群流程。群号：南财考试资料群(917605352) 的 key 为： n7YgzJ4U9rc-CCYWZ722oRIXD4r4BYuy
     * 调用 joinQQGroup(n7YgzJ4U9rc-CCYWZ722oRIXD4r4BYuy) 即可发起手Q客户端申请加群 南财考试资料群(917605352)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回false表示呼起失败
     ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }
}