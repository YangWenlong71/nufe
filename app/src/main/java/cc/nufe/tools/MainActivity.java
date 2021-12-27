package cc.nufe.tools;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.alibaba.fastjson.JSONObject;

import cc.nufe.tools.View.SystemUtil;
import cc.nufe.tools.database.DatabaseHelper;
import cc.nufe.tools.database.dbUtil;

import java.io.IOException;
import java.math.BigDecimal;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;


public class MainActivity extends AppCompatActivity {

    //http://103.45.145.67/nufe/question.php?nc_id=1
    public TextView nc_up, nc_next, bt_ok,tv_title;
    private RadioGroup nc_choice;
    public RadioButton nc_a, nc_b, nc_c, nc_d;
    public CheckBox rb_a, rb_b, rb_c, rb_d, rb_e;
    public TextView nc_response, nc_correct, nc_question, nc_type, idnum;
    public LinearLayout ll_correct, ic_danxuan, ic_duoxuan;
    public int qid = 0;
    public int maxid;
    public Vibrator vibrator;
    private AlertDialog.Builder builder;
    public String subjectName="国际商务谈判(00186)";
    public android.widget.Toolbar Toolbar;
    public ProgressBar pb_progressbar;
    private LinearLayout ll_updown,ll_collect;

    private int errornum;
    private int alreadynum;
    private String temphint="南财考试资料群";

    private ImageView iv_collect;
    private TextView tv_errornum,tv_alreadynum,tv_accuracy;
    private int nc_id;

    private SQLiteDatabase db;
    private DatabaseHelper helper;
    boolean state = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent=getIntent();
        subjectName=intent.getStringExtra("subjectName");


        //初始化数据库
        initsqlite();

        gerfindview();

        SystemUtil.setStatusBarColor(this, Color.parseColor("#2C3E50"));
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        ImageView iv_back= (ImageView)findViewById(R.id.iv_close);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //获取总数
        // http://103.45.145.67/nufe/maxnum.php
        HttpResponse2("http://www.nufe.cc:8000/api/count_item_lesson/"+subjectName);
        //http://103.45.145.67/nufe/subjectlist.php?nc_sub=国际商务谈判(00186)&nc_id=2

        //请求
        HttpResponse("http://www.nufe.cc:8000/api/get_item_from_lesson/"+subjectName+"/" + 1);
        tv_title.setText(subjectName);



        dbUtil dbu = new dbUtil();
        System.out.println("json:::"+dbu.getJson(MainActivity.this));
    }
    public void initsqlite() {
        //5.实例化SQLiteOpenHelper的子类对象-MyDataBaseHelper
        helper = new DatabaseHelper(this, "db_owlsmart", null, R.string.db_version);//dbName数据库名
        db = helper.getWritableDatabase();//获取到了 SQLiteDatabase 对象
    }


    private void gerfindview() {
        ll_updown = (LinearLayout)findViewById(R.id.ll_updown);
        pb_progressbar = (ProgressBar)findViewById(R.id.pb_progressbar);
        nc_choice = (RadioGroup) findViewById(R.id.nc_choice);
        nc_a = (RadioButton) findViewById(R.id.nc_a);
        nc_b = (RadioButton) findViewById(R.id.nc_b);
        nc_c = (RadioButton) findViewById(R.id.nc_c);
        nc_d = (RadioButton) findViewById(R.id.nc_d);

        rb_a = (CheckBox) findViewById(R.id.rb_a);
        rb_b = (CheckBox) findViewById(R.id.rb_b);
        rb_c = (CheckBox) findViewById(R.id.rb_c);
        rb_d = (CheckBox) findViewById(R.id.rb_d);
        rb_e = (CheckBox) findViewById(R.id.rb_e);


        nc_response = (TextView) findViewById(R.id.nc_response);
        nc_correct = (TextView) findViewById(R.id.nc_correct);
        nc_question = (TextView) findViewById(R.id.nc_question);
        nc_type = (TextView) findViewById(R.id.nc_type);
        idnum = (TextView) findViewById(R.id.idnum);
        idnum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInput();
            }
        });
        nc_up = (TextView) findViewById(R.id.nc_up);
        bt_ok = (TextView) findViewById(R.id.bt_ok);
        nc_next = (TextView) findViewById(R.id.nc_next);
        tv_alreadynum = (TextView) findViewById(R.id.tv_alreadynum);
        tv_errornum = (TextView) findViewById(R.id.tv_errornum);
        tv_accuracy = (TextView) findViewById(R.id.tv_accuracy);

        ll_correct = (LinearLayout) findViewById(R.id.ll_correct);
        ic_danxuan = (LinearLayout) findViewById(R.id.ic_danxuan);
        ic_duoxuan = (LinearLayout) findViewById(R.id.ic_duoxuan);

        tv_title= (TextView) findViewById(R.id.tv_title);

        ll_collect = (LinearLayout) findViewById(R.id.ll_collect);
        iv_collect = (ImageView) findViewById(R.id.iv_collect);

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //先判断答案有几个
                String tvcorrect = nc_correct.getText().toString();

                if (correctNum() == getSubCount(tvcorrect, ",")) {
                    Message msg = new Message();
                    msg.what = 6;
                    handler.sendMessage(msg);//用activity中的handler发送消息

                    nc_next.setEnabled(true);
                    nc_up.setEnabled(true);

                    rb_a.setChecked(false);
                    rb_b.setChecked(false);
                    rb_c.setChecked(false);
                    rb_d.setChecked(false);
                    rb_e.setChecked(false);
                    rb_a.setTextColor(Color.parseColor("#2E2E2E"));
                    rb_b.setTextColor(Color.parseColor("#2E2E2E"));
                    rb_c.setTextColor(Color.parseColor("#2E2E2E"));
                    rb_d.setTextColor(Color.parseColor("#2E2E2E"));
                    rb_e.setTextColor(Color.parseColor("#2E2E2E"));

                    ll_correct.setVisibility(View.GONE);

                    state = false;
                    iv_collect.setImageDrawable(getDrawable(R.drawable.uncollect));

                    ll_updown.setBackground(getResources().getDrawable(R.drawable.view_border));
                    alreadynum++;
                    System.out.println("alreadynum:::"+alreadynum);
                    System.out.println("errornum:::"+errornum);
                    tv_errornum.setText("错误："+errornum);
                    tv_alreadynum.setText("已做："+alreadynum);

                    tv_accuracy.setText("正确率："+errornum/alreadynum+"%");
                }else if(correctNum()<= getSubCount(tvcorrect, ",")){
                    Toast.makeText(MainActivity.this, "缺少选项" , Toast.LENGTH_SHORT).show();


                    ll_updown.setBackground(getResources().getDrawable(R.drawable.view_border_uncheck));
                }else if(correctNum() ==7){
                    //Toast.makeText(MainActivity.this, "错误" , Toast.LENGTH_SHORT).show();
                }
            }
        });

//        tv_qq.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                joinQQGroup("n7YgzJ4U9rc-CCYWZ722oRIXD4r4BYuy");
//            }
//        });
        nc_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nc_choice.clearCheck();
                if (qid == maxid) {
                    Toast.makeText(MainActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
                } else {
                    abcdStata();
                    qid = qid+1;
                    HttpResponse("http://www.nufe.cc:8000/api/get_item_from_lesson/"+subjectName+"/" + qid);
                }
            }
        });

        nc_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nc_choice.clearCheck();
                if (qid == 1) {
                    Toast.makeText(MainActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
                } else {
                    abcdStata();
                    qid = qid-1;
                    HttpResponse("http://www.nufe.cc:8000/api/get_item_from_lesson/"+subjectName+"/" + qid);
                }
            }
        });

        nc_choice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {

                    case R.id.nc_a:
                        if (nc_correct.getText().toString().equals(nc_a.getText().toString())) {

                            Message msg = new Message();
                            msg.what = 6;
                            handler.sendMessage(msg);//用activity中的handler发送消息
                            abcdStata();
                        } else {
                            ll_correct.setVisibility(View.VISIBLE);
                            nc_a.setChecked(true);
                            nc_a.setTextColor(Color.parseColor("#ff3300"));
                            nc_next.setEnabled(false);
                            nc_up.setEnabled(false);
                            vibrator.vibrate(30);//vibrate有多个重载方法，可以实现多种效果，这个括号中是持续时间
                            ll_updown.setBackground(getResources().getDrawable(R.drawable.view_border_uncheck));
                            if(!temphint.contains(nc_question.getText().toString())){
                                errornum++;
                                temphint = nc_question.getText().toString();
                            }
                        }
                        break;

                    case R.id.nc_b:
                        if (nc_correct.getText().toString().equals(nc_b.getText().toString())) {
                            Message msg = new Message();
                            msg.what = 6;
                            handler.sendMessage(msg);//用activity中的handler发送消息
                            abcdStata();
                        } else {
                            ll_correct.setVisibility(View.VISIBLE);
                            nc_b.setChecked(true);
                            nc_b.setTextColor(Color.parseColor("#ff3300"));
                            nc_next.setEnabled(false);
                            nc_up.setEnabled(false);
                            vibrator.vibrate(30);
                            ll_updown.setBackground(getResources().getDrawable(R.drawable.view_border_uncheck));
                            if(!temphint.contains(nc_question.getText().toString())){
                                errornum++;
                                temphint = nc_question.getText().toString();
                            }
                        }
                        break;
                    case R.id.nc_c:
                        if (nc_correct.getText().toString().equals(nc_c.getText().toString())) {

                            Message msg = new Message();
                            msg.what = 6;
                            handler.sendMessage(msg);//用activity中的handler发送消息
                            abcdStata();
                        } else {
                            ll_correct.setVisibility(View.VISIBLE);
                            nc_c.setChecked(true);
                            nc_c.setTextColor(Color.parseColor("#ff3300"));
                            nc_next.setEnabled(false);
                            nc_up.setEnabled(false);
                            vibrator.vibrate(30);
                            ll_updown.setBackground(getResources().getDrawable(R.drawable.view_border_uncheck));
                            if(!temphint.contains(nc_question.getText().toString())){
                                errornum++;
                                temphint = nc_question.getText().toString();
                            }
                        }
                        break;
                    case R.id.nc_d:
                        if (nc_correct.getText().toString().equals(nc_d.getText().toString())) {

                            Message msg = new Message();
                            msg.what = 6;
                            handler.sendMessage(msg);//用activity中的handler发送消息
                            abcdStata();
                        } else {
                            ll_correct.setVisibility(View.VISIBLE);
                            nc_d.setChecked(true);
                            nc_d.setTextColor(Color.parseColor("#ff3300"));
                            nc_next.setEnabled(false);
                            nc_up.setEnabled(false);
                            vibrator.vibrate(30);
                            ll_updown.setBackground(getResources().getDrawable(R.drawable.view_border_uncheck));
                            if(!temphint.contains(nc_question.getText().toString())){
                                errornum++;
                                temphint = nc_question.getText().toString();
                            }
                        }
                        break;
                }

            }
        });



        ll_collect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //nc_id text ,subject text ,question text,correct text,response text,type text,str_a text,str_b text,str_c text,str_d text,str_e text
                //收藏
                if(state==false){
                    state = true;
                    iv_collect.setImageDrawable(getDrawable(R.drawable.collect));
                    //写入内置数据库
                    //拿到科目
                    String subject =  tv_title.getText().toString();
                    String question =  nc_question.getText().toString();
                    String  correct = nc_correct.getText().toString();
                    String  response = nc_response.getText().toString();
                    //拿到类型
                    String type= nc_type.getText().toString();
                    //判断
                    String str_a= null,str_b = null,str_c= null,str_d= null,str_e= null;
                    if("单选".equals(type)){
                       str_a =  nc_a.getText().toString();
                       str_b =  nc_b.getText().toString();
                       str_c =  nc_c.getText().toString();
                       str_d =  nc_d.getText().toString();
                       str_e = "";
                    }else if("多选".equals(type)){
                        str_a =  rb_a.getText().toString();
                        str_b =  rb_b.getText().toString();
                        str_c =  rb_c.getText().toString();
                        str_d =  rb_d.getText().toString();
                        str_e = rb_e.getText().toString();
                    }

                    dbUtil dbu= new dbUtil();
                    if(dbu.judgeDate(MainActivity.this,nc_id)){
                        System.out.println("数据存在");
                    }else {

                    //System.out.println("str_b:::"+str_b);
                    db = helper.getWritableDatabase();//获取到了 SQLiteDatabase 对象
                    ContentValues valuedown = new ContentValues();
                    // 开始组装第一条数据
                    valuedown.put("nc_id", nc_id);
                    valuedown.put("subject", subject);
                    valuedown.put("question", question);
                    valuedown.put("correct", correct);
                    valuedown.put("response", response);
                    valuedown.put("str_a", str_a);
                    valuedown.put("str_b", str_b);
                    valuedown.put("str_c", str_c);
                    valuedown.put("str_d", str_d);
                    valuedown.put("str_e", str_e);
                    valuedown.put("type", type);
                    // 插入第一条数据
                    db.insert("collectTable", null, valuedown);
                    valuedown.clear();
                    db.close();
                    }
                }else if(state==true){
                    state = false;
                    iv_collect.setImageDrawable(getDrawable(R.drawable.uncollect));
                    //删除指定ID数据
                    dbUtil dbu = new dbUtil();
                    dbu.deleteAppoint(MainActivity.this,nc_id);

                    System.out.println("json:::"+dbu.getJson(MainActivity.this));

                }
            }
        });


    }

    /**
     * 一个输入框的 dialog
     */
    private void showInput() {
        final EditText editText = new EditText(MainActivity.this);
        builder = new AlertDialog.Builder(MainActivity.this).setTitle("从第多少题开始").setView(editText)
                .setPositiveButton("跳转", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(MainActivity.this, "输入内容为：" + editText.getText().toString()
//                                , Toast.LENGTH_LONG).show();

                        if(Integer.parseInt(editText.getText().toString())<maxid) {
                            qid = Integer.parseInt(editText.getText().toString());
                            HttpResponse("http://www.nufe.cc:8000/api/get_item_from_lesson/"+subjectName+"/" + editText.getText().toString());
                        }else {
                            Toast.makeText(MainActivity.this, "请输入小于总数的阿拉伯数字"
                                , Toast.LENGTH_LONG).show();
                        }
                    }
                });
        builder.create().show();
    }



    public int correctNum(){
        int correctnum=0;
        int falsenum = 0;
        if (rb_a.isChecked()) {
            String tx_a = rb_a.getText().toString();
            String tx_correct = nc_correct.getText().toString();
            if (!tx_correct.contains(tx_a)) {
                ll_correct.setVisibility(View.VISIBLE);
                rb_a.setChecked(true);
                rb_a.setTextColor(Color.parseColor("#ff3300"));
                nc_next.setEnabled(false);
                nc_up.setEnabled(false);
                falsenum=falsenum+1;
                vibrator.vibrate(30);//vibrate有多个重载方法，可以实现多种效果，这个括号中是持续时间
                ll_updown.setBackground(getResources().getDrawable(R.drawable.view_border_uncheck));
            }else if(tx_correct.contains(tx_a)){
                correctnum=correctnum+1;
            }
        }
        if (rb_b.isChecked()) {
            String tx_a = rb_b.getText().toString();
            String tx_correct = nc_correct.getText().toString();
            if (!tx_correct.contains(tx_a)) {
                ll_correct.setVisibility(View.VISIBLE);
                rb_b.setChecked(true);
                rb_b.setTextColor(Color.parseColor("#ff3300"));
                nc_next.setEnabled(false);
                nc_up.setEnabled(false);
                falsenum=falsenum+1;
                vibrator.vibrate(30);//vibrate有多个重载方法，可以实现多种效果，这个括号中是持续时间
                ll_updown.setBackground(getResources().getDrawable(R.drawable.view_border_uncheck));
            }else if(tx_correct.contains(tx_a)){
                correctnum=correctnum+1;
            }
        }
        if (rb_c.isChecked()) {
            String tx_a = rb_c.getText().toString();
            String tx_correct = nc_correct.getText().toString();
            if (!tx_correct.contains(tx_a)) {
                ll_correct.setVisibility(View.VISIBLE);
                rb_c.setChecked(true);
                rb_c.setTextColor(Color.parseColor("#ff3300"));
                nc_next.setEnabled(false);
                nc_up.setEnabled(false);
                falsenum=falsenum+1;
                vibrator.vibrate(30);//vibrate有多个重载方法，可以实现多种效果，这个括号中是持续时间
                ll_updown.setBackground(getResources().getDrawable(R.drawable.view_border_uncheck));
            }else if(tx_correct.contains(tx_a)){
                correctnum=correctnum+1;
            }
        }
        if (rb_d.isChecked()) {
            String tx_a = rb_d.getText().toString();
            String tx_correct = nc_correct.getText().toString();
            if (!tx_correct.contains(tx_a)) {
                ll_correct.setVisibility(View.VISIBLE);
                rb_d.setChecked(true);
                rb_d.setTextColor(Color.parseColor("#ff3300"));
                nc_next.setEnabled(false);
                nc_up.setEnabled(false);
                falsenum=falsenum+1;
                vibrator.vibrate(30);//vibrate有多个重载方法，可以实现多种效果，这个括号中是持续时间
                ll_updown.setBackground(getResources().getDrawable(R.drawable.view_border_uncheck));
            }else if(tx_correct.contains(tx_a)){
                correctnum=correctnum+1;
            }
        }
        if (rb_e.isChecked()) {
            String tx_a = rb_e.getText().toString();
            String tx_correct = nc_correct.getText().toString();
            if (!tx_correct.contains(tx_a)) {
                ll_correct.setVisibility(View.VISIBLE);
                rb_e.setChecked(true);
                rb_e.setTextColor(Color.parseColor("#ff3300"));
                nc_next.setEnabled(false);
                nc_up.setEnabled(false);
                falsenum=falsenum+1;
                vibrator.vibrate(30);//vibrate有多个重载方法，可以实现多种效果，这个括号中是持续时间
                ll_updown.setBackground(getResources().getDrawable(R.drawable.view_border_uncheck));
            }else if(tx_correct.contains(tx_a)){
                correctnum=correctnum+1;
            }
        }

        if(falsenum==0){
            return correctnum;
        }else
            return 7;
    }

    public static int getSubCount(String str, String key) {
        int count = 0;
        int index = 0;
        while ((index = str.indexOf(key, index)) != -1) {
            index = index + key.length();

            count++;
        }
        return count+1;
    }



    public void abcdStata() {

        nc_next.setEnabled(true);
        nc_up.setEnabled(true);


        if (nc_a.isChecked())
            nc_a.setChecked(false);
        if (nc_b.isChecked())
            nc_b.setChecked(false);
        if (nc_c.isChecked())
            nc_c.setChecked(false);
        if (nc_d.isChecked())
            nc_d.setChecked(false);


        nc_a.setTextColor(Color.parseColor("#2E2E2E"));
        nc_b.setTextColor(Color.parseColor("#2E2E2E"));
        nc_c.setTextColor(Color.parseColor("#2E2E2E"));
        nc_d.setTextColor(Color.parseColor("#2E2E2E"));

        ll_correct.setVisibility(View.GONE);

        state = false;
        iv_collect.setImageDrawable(getDrawable(R.drawable.uncollect));

        alreadynum++;
        System.out.println("alreadynum:::"+alreadynum);
        System.out.println("errornum:::"+errornum);

       double num=  (((double)alreadynum-(double)errornum)/(double)alreadynum)*100;

        BigDecimal bg = new BigDecimal(num);
        double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
           tv_accuracy.setText("正确率："+f1+"%");

        tv_errornum.setText("错误："+errornum);
        tv_alreadynum.setText("已做："+alreadynum);

        ll_updown.setBackground(getResources().getDrawable(R.drawable.view_border));

        temphint = nc_question.getText().toString();
        System.out.println("temphint:::"+temphint);

        //ll_updown.setBackground(getResources().getDrawable(R.drawable.view_border_uncheck));
    }

    private void HttpResponse2(String url) {


        OkHttpClient client = new OkHttpClient();
        Request request = new Request
                .Builder()
                .url(url)//要访问的链接
                .build();

        Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String res = response.body().string();
                //Log.i("response===========>",res);

                Message msg = new Message();
                msg.what = 22;
                Bundle bundle = new Bundle();
                bundle.putString("maxnum", res);  //往Bundle中存放数据
                msg.setData(bundle);//mes利用Bundle传递数据
                handler.sendMessage(msg);//用activity中的handler发送消息
            }
        });

    }


    private void HttpResponse(String url) {


        OkHttpClient client = new OkHttpClient();
        Request request = new Request
                .Builder()
                .url(url)//要访问的链接
                .build();

        Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String res = response.body().string();
                //Log.i("response===========>",res);

                Message msg = new Message();
                msg.what = 12;
                Bundle bundle = new Bundle();
                bundle.putString("nc_data", res);  //往Bundle中存放数据
                msg.setData(bundle);//mes利用Bundle传递数据
                handler.sendMessage(msg);//用activity中的handler发送消息
            }
        });

    }



    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 6:
                    if (qid == maxid) {
                        Toast.makeText(MainActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
                    } else {
                        pb_progressbar.setProgress(qid);

                        qid = qid+1;
                        HttpResponse("http://www.nufe.cc:8000/api/get_item_from_lesson/"+subjectName+"/" + qid);
                    }
                    break;

                case 12:
                    String nc_data = msg.getData().getString("nc_data");
                    //[{"nc_subject":"国际金融(301624301)","nc_type":"单选","nc_question":"标志着布雷顿森林体系开始动摇的是","nc_a":"侵朝战争 ","nc_b":"美国国际收支顺差","nc_c":"美国国际收支逆差","nc_d":"美元危机","nc_e":"","nc_correct":"美元危机","nc_analysis":"练习一第一题","id":"1"}]
                    //删掉[]
                    String nc_data1 = nc_data.replace("[", "");
                    String nc_data2 = nc_data1.replace("]", "");
                    JSONObject objsd = JSONObject.parseObject(nc_data2);
                    String idnum1 = objsd.getString("id");
                    String nc_subject1 = objsd.getString("nc_subject");
                    String nc_type1 = objsd.getString("nc_type");
                    String nc_question1 = objsd.getString("nc_question");
                    String nc_a1 = objsd.getString("nc_a");
                    String nc_b1 = objsd.getString("nc_b");
                    String nc_c1 = objsd.getString("nc_c");
                    String nc_d1 = objsd.getString("nc_d");
                    String nc_e1 = objsd.getString("nc_e");
                    String nc_correct1 = objsd.getString("nc_correct");
                    String nc_analysis1 = objsd.getString("nc_analysis");

                    try {
                        nc_id = Integer.parseInt(idnum1);
                        dbUtil dbu= new dbUtil();
                        if(dbu.judgeDate(MainActivity.this,nc_id)){
                            iv_collect.setImageDrawable(getDrawable(R.drawable.collect));
                        }else {
                            iv_collect.setImageDrawable(getDrawable(R.drawable.uncollect));
                        }
                    } catch(NumberFormatException nfe) {
                        System.out.println("idnum1:::"+idnum1);
                    }


                    idnum.setText(qid + "/" + maxid);
                    nc_response.setText(nc_subject1+"/"+nc_analysis1);
                    nc_correct.setText(nc_correct1);
                    nc_question.setText(nc_question1);
                    nc_type.setText(nc_type1);

                    if (nc_type1.equals("单选")) {
                        ic_danxuan.setVisibility(View.VISIBLE);
                        ic_duoxuan.setVisibility(View.GONE);

                        nc_a.setText(nc_a1);
                        nc_b.setText(nc_b1);
                        nc_c.setText(nc_c1);
                        nc_d.setText(nc_d1);
                    }
                    if (nc_type1.equals("多选")) {
                        ic_duoxuan.setVisibility(View.VISIBLE);
                        ic_danxuan.setVisibility(View.GONE);
                        rb_a.setText(nc_a1);
                        rb_b.setText(nc_b1);
                        rb_c.setText(nc_c1);
                        rb_d.setText(nc_d1);
                        rb_e.setText(nc_e1);
                    }
                    break;
                case 22:
                    String maxnum = msg.getData().getString("maxnum");
                    maxid = Integer.parseInt(maxnum);
                    pb_progressbar.setMax(maxid);
                    break;
            }
        }
    };

}
