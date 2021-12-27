package cc.nufe.tools.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import cc.nufe.tools.MainActivity;
import cc.nufe.tools.R;
import cc.nufe.tools.model.subject;
import cc.nufe.tools.ui.collectActivity;

import java.util.ArrayList;

public class subjecta extends BaseAdapter implements ListAdapter {
    
    private ArrayList<subject> list;
    private int id;
    private Context context;
    public subjecta(int item, Context context, ArrayList<subject> list) {
        this.list = list;
        this.context = context;
        this.id = item;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    
    @SuppressLint("WrongConstant")
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        TextView tv_name = null;
        ImageView iv_study = null;
        ImageView iv_collect = null;
        if (context != null) {
            ViewHolder viewHolder;
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.item_sub, null);
                tv_name = (TextView) view.findViewById(R.id.tv_name);
                iv_study = (ImageView) view.findViewById(R.id.iv_study);
                iv_collect = (ImageView) view.findViewById(R.id.iv_collect);
                view.setTag(new ViewHolder(tv_name,iv_study,iv_collect));
            } else {
                ViewHolder viewHolder1 = (ViewHolder) view.getTag(); // 重新获取ViewHolder
                tv_name = viewHolder1.tv_name;
                iv_study = viewHolder1.iv_study;
                iv_collect = viewHolder1.iv_collect;
            }
            subject sjt = (subject) list.get(i); // 获取当前项的实例
            tv_name.setText(sjt.getNc_subject().toString());

            final TextView finalTv_name = tv_name;
            iv_study.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("subjectName", finalTv_name.getText().toString());//设置参数,""
                    intent.setClass(context, MainActivity.class);//从哪里跳到哪里
                    context.startActivity(intent);
                }
            });

            iv_collect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("subjectName", finalTv_name.getText().toString());//设置参数,""
                    intent.setClass(context, collectActivity.class);//从哪里跳到哪里
                    context.startActivity(intent);
                }
            });

        }



            return view;
        }
        
        private final class ViewHolder {
            TextView tv_name = null;
            ImageView iv_study = null;
            ImageView iv_collect= null;
            public ViewHolder(TextView tv_name,ImageView iv_study,ImageView iv_collect) {
                this.tv_name = tv_name;
                this.iv_study = iv_study;
                this.iv_collect= iv_collect;
            }
         }


}
