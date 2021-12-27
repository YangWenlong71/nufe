package cc.nufe.tools.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cc.nufe.tools.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class dbSubject {



    private String SqlTable = "collectTable";//数据表名
    private String SqlDatabase = "db_owlsmart";//数据库名

    private DatabaseHelper helper;
    private SQLiteDatabase db;
    private Context mContext;


    public String getJson( Context context,String str){
        this.mContext = context;
        //从数据库读取数据
        initsqlite(mContext);
        String str1= JSONArray.toJSONString(getResults(str));
        return str1;
    }

    public void initsqlite(Context context) {
        helper = new DatabaseHelper(context, SqlDatabase, null, R.string.db_version);//dbName数据库名
        db = helper.getWritableDatabase();//获取到了 SQLiteDatabase 对象
    }

    //这个函数是读取数据库并转换为json字符串的
    private JSONArray getResults(String str)
    {

        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);

        String strk = m.replaceAll("").trim();

        System.out.println("str"+m.replaceAll("").trim());

        db = helper.getWritableDatabase();
        String searchQuery = " SELECT  * FROM " + SqlTable + " WHERE subject LIKE '%"+strk+"%'";
        Cursor cursor = db.rawQuery(searchQuery, null );
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for( int i=0 ;  i< totalColumn ; i++ )
            {
                if( cursor.getColumnName(i) != null )
                {
                    try
                    {
                        if( cursor.getString(i) != null )
                        {
                            Log.d(SqlTable, cursor.getString(i) );
                            rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                        }
                        else
                        {
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    }
                    catch( Exception e )
                    {
                        Log.d(SqlTable, e.getMessage()  );
                    }
                }
            }
            resultSet.add(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        return resultSet;
    }



}
