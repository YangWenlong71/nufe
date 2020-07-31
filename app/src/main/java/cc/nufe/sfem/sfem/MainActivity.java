package cc.nufe.sfem.sfem;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.timmy.tdialog.TDialog;
import com.timmy.tdialog.base.BindViewHolder;
import com.timmy.tdialog.base.TBaseAdapter;
import com.timmy.tdialog.list.TListDialog;

import java.io.IOException;
import java.util.List;

import cc.nufe.sfem.sfem.uiView.SystemUtil;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity {

    //http://103.45.145.67/nufe/question.php?nc_id=1
    public TextView nc_up, nc_next, bt_ok;
    private RadioGroup nc_choice;
    public RadioButton nc_a, nc_b, nc_c, nc_d;
    public CheckBox rb_a, rb_b, rb_c, rb_d, rb_e;
    public TextView nc_response, nc_correct, nc_question, nc_type, idnum, tv_qq;
    public LinearLayout ll_correct, ic_danxuan, ic_duoxuan;
    public int qid = 0;
    public int maxid;
    public Vibrator vibrator;
    private AlertDialog.Builder builder;
    public String subjectName="国际商务谈判(00186)";
    public Toolbar Toolbar;
    public ProgressBar pb_progressbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      // CrashHandler crashHandler = CrashHandler.getInstance();
       // crashHandler.init(getApplicationContext());
        setContentView(R.layout.activity_main);


        Intent intent = new Intent(this, InitActivity.class);
        startActivity(intent);
        SystemUtil.setStatusBarColor(this, Color.parseColor("#20B2AA"));
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        LoadToolbar();
        //获取总数
        // http://103.45.145.67/nufe/maxnum.php
        HttpResponse2("http://103.45.145.67/nufe/subnum.php?nc_sub="+subjectName);
//http://103.45.145.67/nufe/subjectlist.php?nc_sub=国际商务谈判(00186)&nc_id=2
        gerfindview();

        //请求
        HttpResponse("http://103.45.145.67/nufe/questionsub.php?nc_sub="+subjectName+"&nc_id=" + 1);

    }



    private void LoadToolbar() {
        //新页面接收数据


        Toolbar = (Toolbar) findViewById(R.id.toolbar);
        Toolbar.setTitle(subjectName);
        setSupportActionBar(Toolbar);//利用Toolbar代替ActionBar
        Toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到用户设置页面
                finish();
            }
        });
        //添加menu 菜单点击事件
        Toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_subject:
                        requestSubJect("http://103.45.145.67/nufe/sublist.php");
                        break;
//                    case R.id.action_qq:
//                        joinQQGroup("n7YgzJ4U9rc-CCYWZ722oRIXD4r4BYuy");
//                        break;
                }
                return true;
            }
        });
    }
    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        getMenuInflater().inflate(R.menu.menu_subject, menu);//toolbar添加menu菜单
        return true;
    }


    private void bottomListDialog(List<String> list) {
        new TListDialog.Builder(getSupportFragmentManager())
                .setScreenHeightAspect(this, 0.6f)
                .setScreenWidthAspect(this, 1.0f)
                .setGravity(Gravity.BOTTOM)
                .setAdapter(new TBaseAdapter<String>(R.layout.item_bottom_list_text,list) {

                    protected void onBind(BindViewHolder holder, int position, String s) {
                        //处理一下显示,和最后点击没有任何关系

                        JSONObject objsd = JSONObject.parseObject(s);
                        String nc_subject = objsd.getString("nc_subject");
                        holder.setText(R.id.tv, nc_subject);
                    }
                })
                .setOnAdapterItemClickListener(new TBaseAdapter.OnAdapterItemClickListener<String>() {

                    public void onItemClick(BindViewHolder holder, int position, String s, TDialog tDialog) {
                        //Toast.makeText(NavDataActivity.this, "click:" + s, Toast.LENGTH_SHORT).show();

                        //解析字符串
                        JSONObject objsd = JSONObject.parseObject(s);
                        String nc_subject = objsd.getString("nc_subject");
                        String count1 = objsd.getString("count");
                        maxid=Integer.parseInt(count1);
                        subjectName = nc_subject;
                        Toolbar.setTitle(subjectName);
                        //tv_qq.setText(subjectName);
                        qid=1;
                        HttpResponse2("http://103.45.145.67/nufe/subnum.php?nc_sub="+subjectName);
                        HttpResponse("http://103.45.145.67/nufe/questionsub.php?nc_sub="+subjectName+"&nc_id=" + qid);

                        tDialog.dismiss();
                    }
                })

                .create()
                .show();
    }

    private void requestSubJect(String moreUrl){

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
                String res = response.body().string();
                //Log.i("response===========>",res);

//                        String nres2=nres1.replace("\"}","");

                JSONArray jsArr = JSONObject.parseArray(res);
                List<String> list = JSONObject.parseArray(jsArr.toJSONString(), String.class);
                bottomListDialog(list);
            }
        });

    }
    private void gerfindview() {
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
//    rb_a.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if(!rb_a.isChecked()){
//                rb_a.setChecked(true);
//            } if (rb_a.isChecked()){
//                rb_a.setChecked(false);
//            }
//        }
//    });
//    rb_b.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if(!rb_b.isChecked()){
//                rb_b.setChecked(true);
//            } if (rb_b.isChecked()){
//                rb_b.setChecked(false);
//            }
//        }
//    });
//    rb_c.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if(!rb_c.isChecked()){
//                rb_c.setChecked(true);
//            } if (rb_c.isChecked()){
//                rb_c.setChecked(false);
//            }
//        }
//    });
//    rb_d.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if(!rb_d.isChecked()){
//                rb_d.setChecked(true);
//            } if (rb_d.isChecked()){
//                rb_d.setChecked(false);
//            }
//        }
//    });
//    rb_e.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if(!rb_e.isChecked()){
//                rb_e.setChecked(true);
//            }
//            if (rb_e.isChecked()){
//                rb_e.setChecked(false);
//            }
//        }
//    });

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
        tv_qq = (TextView) findViewById(R.id.tv_qq);
        nc_up = (TextView) findViewById(R.id.nc_up);
        bt_ok = (TextView) findViewById(R.id.bt_ok);
        nc_next = (TextView) findViewById(R.id.nc_next);
        ll_correct = (LinearLayout) findViewById(R.id.ll_correct);
        ic_danxuan = (LinearLayout) findViewById(R.id.ic_danxuan);
        ic_duoxuan = (LinearLayout) findViewById(R.id.ic_duoxuan);

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //int checkboxnum = 0;
                //先判断答案有几个
                String tvcorrect = nc_correct.getText().toString();
//                //getSubCount(tvcorrect,",");
//                if (rb_a.isChecked()) {
//                    checkboxnum = checkboxnum + 1;
//                }
//                if (rb_b.isChecked()) {
//                    checkboxnum = checkboxnum + 1;
//                }
//                if (rb_c.isChecked()) {
//                    checkboxnum = checkboxnum + 1;
//                }
//                if (rb_d.isChecked()) {
//                    checkboxnum = checkboxnum + 1;
//                }
//                if (rb_e.isChecked()) {
//                    checkboxnum = checkboxnum + 1;
//                }

                //Toast.makeText(MainActivity.this, checkboxnum + "ddd" + getSubCount(tvcorrect, ",") + 1, Toast.LENGTH_SHORT).show();


//                    if (tvcorrect.contains(rb_a.getText().toString())) {
//
//                    }

//                    nc_next.setEnabled(false);
//                    nc_up.setEnabled(false);
//                    vibrator.vibrate(30);//vibrate有多个重载方法，可以实现多种效果，这个括号中是持续时间

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


                }else if(correctNum()<= getSubCount(tvcorrect, ",")){
                    Toast.makeText(MainActivity.this, "缺少选项" , Toast.LENGTH_SHORT).show();
                }else if(correctNum() ==7){
                    //Toast.makeText(MainActivity.this, "错误" , Toast.LENGTH_SHORT).show();
                }






            }
        });

        tv_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinQQGroup("n7YgzJ4U9rc-CCYWZ722oRIXD4r4BYuy");
            }
        });
        nc_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nc_choice.clearCheck();
                if (qid == maxid) {
                    Toast.makeText(MainActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
                } else {
                    abcdStata();
                    HttpResponse("http://103.45.145.67/nufe/questionsub.php?nc_sub="+subjectName+"&nc_id=" + qid++);

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
                    HttpResponse("http://103.45.145.67/nufe/questionsub.php?nc_sub="+subjectName+"&nc_id=" + qid--);

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

                        }
                        break;
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
                            HttpResponse("http://103.45.145.67/nufe/questionsub.php?nc_sub="+subjectName+"&nc_id=" + editText.getText().toString());
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
                        HttpResponse("http://103.45.145.67/nufe/questionsub.php?nc_sub="+subjectName+"&nc_id=" + qid++);

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

                    idnum.setText(qid + "/" + maxid);
                    nc_response.setText(nc_subject1+"/"+nc_analysis1);
                    nc_correct.setText(nc_correct1);
                    nc_question.setText(nc_question1);
                    nc_type.setText(nc_type1);
                    if (nc_type1.equals("单选")) {
                        ic_danxuan.setVisibility(View.VISIBLE);
                        ic_duoxuan.setVisibility(View.GONE);
                        // idnum.setText(idnum1 + "/" + maxid);
                        //nc_response.setText(nc_analysis1);
                        //nc_correct.setText(nc_correct1);
                        nc_a.setText(nc_a1);
                        nc_b.setText(nc_b1);
                        nc_c.setText(nc_c1);
                        nc_d.setText(nc_d1);
                        //nc_question.setText(nc_question1);
                        //nc_type.setText(nc_type1);
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
