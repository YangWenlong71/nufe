package cc.nufe.tools.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import cc.nufe.tools.R;
import cc.nufe.tools.View.SystemUtil;
import cc.nufe.tools.adapter.collecta;
import cc.nufe.tools.database.dbSubject;
import cc.nufe.tools.database.dbUtil;
import cc.nufe.tools.model.collect;

import java.util.ArrayList;
import java.util.List;

public class collectActivity extends AppCompatActivity {

    private String subjectName;
    private List<collect> collects = new ArrayList<collect>();
    private ListView lv_collect;

    private collecta collectaa;
    private collect cll;
    private TextView tv_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        SystemUtil.setStatusBarColor(this, Color.parseColor("#2C3E50"));

        Intent intent=getIntent();
        subjectName=intent.getStringExtra("subjectName");
        tv_title= (TextView) findViewById(R.id.tv_title);
        tv_title.setText(subjectName);

        ImageView iv_back= (ImageView)findViewById(R.id.iv_close);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        ImageView iv_clean= (ImageView) findViewById(R.id.iv_clean);
        iv_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbUtil dbu= new dbUtil();

                if(dbu.deleteTable(collectActivity.this,subjectName)){
                    Toast.makeText(collectActivity.this, "清理完毕", Toast.LENGTH_SHORT).show();
                }
            }
        });



        dbSubject dbsub = new dbSubject();
        String sublist = dbsub.getJson(collectActivity.this,subjectName);

        System.out.println("sublist:::"+sublist);


        lv_collect = (ListView) findViewById(R.id.lv_collect);

        Message msg = new Message();
        msg.what = 16;
        Bundle bundle = new Bundle();
        bundle.putString("result", sublist);//往Bundle中存放数据
        msg.setData(bundle);
        Thandler.sendMessage(msg);


    }

    private Handler Thandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 16:

                    collects.clear();

                    String result = msg.getData().getString("result");//接受msg传递过来的参数
                    List<collect> subc = JSON.parseArray(result, collect.class);
                    for (collect objsd : subc) {
                        cll = new collect();

                        String idnum1 = objsd.getNc_id();
                        String nc_subject1 = objsd.getSubject();
                        String nc_type1 = objsd.getType();
                        String nc_question1 = objsd.getQuestion();
                        String nc_a1 = objsd.getStr_a();
                        String nc_b1 = objsd.getStr_b();
                        String nc_c1 = objsd.getStr_c();
                        String nc_d1 = objsd.getStr_d();
                        String nc_e1 = objsd.getStr_e();
                        String nc_correct1 = objsd.getCorrect();
                        String nc_analysis1 = objsd.getResponse();

                       // System.out.println("sublist:::"+nc_question1);

                        cll.setNc_id(idnum1);
                        cll.setSubject(nc_subject1);
                        cll.setType(nc_type1);
                        cll.setQuestion(nc_question1);
                        cll.setStr_a(nc_a1);
                        cll.setStr_b(nc_b1);
                        cll.setStr_c(nc_c1);
                        cll.setStr_d(nc_d1);
                        cll.setStr_e(nc_e1);
                        cll.setCorrect(nc_correct1);
                        cll.setResponse(nc_analysis1);
                        collects.add(cll);
                    }
                    clickData((ArrayList<collect>) collects);
                    break;

            }
        }
    };


    public void clickData(ArrayList<collect> collects) {
        collectaa = new collecta(R.layout.item_exercise, collectActivity.this, collects);
        collectaa.notifyDataSetChanged();
        lv_collect.setAdapter(collectaa);

    }


}