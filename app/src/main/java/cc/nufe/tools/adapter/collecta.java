package cc.nufe.tools.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import cc.nufe.tools.R;
import cc.nufe.tools.model.collect;

import java.util.ArrayList;

public class collecta extends BaseAdapter implements ListAdapter {

    private ArrayList<collect> collects;
    private int id;
    private Context context;
    public collecta(int item, Context context, ArrayList<collect> collects) {
        this.collects = collects;
        this.context = context;
        this.id = item;
    }

    @Override
    public int getCount() {
        return collects.size();
    }

    @Override
    public Object getItem(int i) {
        return collects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    
    @SuppressLint("WrongConstant")
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        TextView tv_question = null;
        TextView tv_a= null,tv_b= null,tv_c= null,tv_d= null,tv_e= null,tv_id= null;
        TextView tv_type = null;
        TextView tv_correct=null;
        if (context != null) {
            ViewHolder viewHolder;
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.item_exercise, null);
                tv_id = (TextView) view.findViewById(R.id.tv_id);
                tv_type = (TextView) view.findViewById(R.id.tv_type);
                tv_question = (TextView) view.findViewById(R.id.tv_question);
                tv_correct = (TextView) view.findViewById(R.id.tv_correct);

                tv_a = (TextView) view.findViewById(R.id.tv_a);
                tv_b = (TextView) view.findViewById(R.id.tv_b);
                tv_c = (TextView) view.findViewById(R.id.tv_c);
                tv_d = (TextView) view.findViewById(R.id.tv_d);
                tv_e = (TextView) view.findViewById(R.id.tv_e);
                view.setTag(new ViewHolder(tv_id,tv_type,tv_question,tv_correct,tv_a,tv_b,tv_c,tv_d,tv_e));
            } else {
                ViewHolder viewHolder1 = (ViewHolder) view.getTag(); // 重新获取ViewHolder
                tv_id = viewHolder1.tv_id;
                tv_type = viewHolder1.tv_type;
                tv_question = viewHolder1.tv_question;
                tv_correct = viewHolder1.tv_correct;

                tv_a = viewHolder1.tv_a;
                tv_b = viewHolder1.tv_b;
                tv_c = viewHolder1.tv_c;
                tv_d = viewHolder1.tv_d;
                tv_e = viewHolder1.tv_e;

            }
            collect sjt = (collect) collects.get(i); // 获取当前项的实例
            tv_id.setText(sjt.getNc_id().toString());
            tv_type.setText(sjt.getType().toString());
            tv_question.setText(sjt.getQuestion().toString());
            tv_a.setText(sjt.getStr_a().toString());
            tv_b.setText(sjt.getStr_b().toString());
            tv_c.setText(sjt.getStr_c().toString());
            tv_d.setText(sjt.getStr_d().toString());
            tv_e.setText(sjt.getStr_e().toString());
            tv_correct.setText(sjt.getCorrect().toString());
            int  color = Color.rgb(0,139,139);
            int  color2 = Color.rgb(105 ,105 ,105);
            System.out.println("tv_correct"+tv_correct.getText().toString());
            if(tv_a.getText().toString().contains(tv_correct.getText().toString())){
                tv_a.setTextColor(color);
                tv_b.setTextColor(color2);
                tv_c.setTextColor(color2);
                tv_d.setTextColor(color2);
                tv_e.setTextColor(color2);
            }
            if(tv_b.getText().toString().contains(tv_correct.getText().toString())){
                tv_b.setTextColor(color);
                tv_c.setTextColor(color2);
                tv_d.setTextColor(color2);
                tv_e.setTextColor(color2);
                tv_a.setTextColor(color2);
            }
            if(tv_c.getText().toString().contains(tv_correct.getText().toString())){
                tv_c.setTextColor(color);
                tv_d.setTextColor(color2);
                tv_e.setTextColor(color2);
                tv_a.setTextColor(color2);
                tv_b.setTextColor(color2);
            }
            if(tv_d.getText().toString().contains(tv_correct.getText().toString())){
                tv_d.setTextColor(color);
                tv_e.setTextColor(color2);
                tv_a.setTextColor(color2);
                tv_b.setTextColor(color2);
                tv_c.setTextColor(color2);
            }
            if(tv_e.getText().toString().contains(tv_correct.getText().toString())){
                tv_e.setTextColor(color);
                tv_a.setTextColor(color2);
                tv_b.setTextColor(color2);
                tv_c.setTextColor(color2);
                tv_d.setTextColor(color2);
            }



        }



            return view;
        }
        
        private final class ViewHolder {
            TextView tv_question = null;
            TextView tv_a= null,tv_b= null,tv_c= null,tv_d= null,tv_e= null,tv_id= null;
            TextView tv_type = null;
            TextView tv_correct=null;
            public ViewHolder(TextView tv_id,TextView tv_type,TextView tv_question,TextView tv_correct,TextView tv_a,TextView tv_b,TextView tv_c,TextView tv_d,TextView tv_e) {
                this.tv_id = tv_id;
                this.tv_type= tv_type;
                this.tv_question = tv_question;
                this.tv_correct = tv_correct;
                this.tv_a= tv_a;
                this.tv_b= tv_b;
                this.tv_c= tv_c;
                this.tv_d= tv_d;
                this.tv_e= tv_e;

            }
         }


}
