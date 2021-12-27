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

public class dbUtil {


    private String SqlTable = "collectTable";//数据表名
    private String SqlDatabase = "db_owlsmart";//数据库名

    private DatabaseHelper helper;
    private SQLiteDatabase db;
    private Context mContext;

    //删除指定数据库
    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(SqlDatabase);
    }
    //清空某一个表
    public boolean deleteTable(Context context) {
        helper = new DatabaseHelper(context, SqlDatabase, null, R.string.db_version);//dbName数据库名
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(" delete from " + SqlTable);
        return true;
    }

    //清空一个表指定数据
    public boolean deleteTable(Context context,String str) {


        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);

        String strk = m.replaceAll("").trim();


        helper = new DatabaseHelper(context, SqlDatabase, null, R.string.db_version);//dbName数据库名
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(" delete from " + SqlTable + " WHERE subject LIKE '%"+strk+"%'");
        return true;
    }
    //删除某一个表
    public void dropTable(SQLiteDatabase db) {
        db.execSQL("drop table " + SqlTable);
    }

    //删除某一个表指定一条数据
    public void deleteAppoint(Context context,int ncid) {

        helper = new DatabaseHelper(context, SqlDatabase, null, R.string.db_version);//dbName数据库名
        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL("DELETE FROM " + SqlTable+ " WHERE nc_id = " + ncid);
    }


    public String getJson( Context context){
        this.mContext = context;
        //从数据库读取数据
        initsqlite(mContext);


        String str1=JSONArray.toJSONString(getResults(SqlTable));

        return str1;
    }

    public void initsqlite(Context context) {
        helper = new DatabaseHelper(context, SqlDatabase, null, R.string.db_version);//dbName数据库名
        db = helper.getWritableDatabase();//获取到了 SQLiteDatabase 对象
    }

    //这个函数是读取数据库并转换为json字符串的
    private JSONArray getResults(String userTable)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        String searchQuery = "SELECT  * FROM "+userTable ;
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



    public boolean judgeDate(Context context,int str) {

        boolean state = false;
        helper = new DatabaseHelper(context, SqlDatabase, null, R.string.db_version);//dbName数据库名
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select nc_id from "+SqlTable+" where nc_id = " + str, null);
        while (cursor.moveToNext()) {
            //遍历出表名
            int title_str = cursor.getInt(0);
            if (title_str == str) {
                state = true;
            } else {
                state = false;
            }

            System.out.println("title_str:str::"+str);
            System.out.println("title_str:title_str::"+title_str);
            System.out.println("title_str:::"+state);
        }

        return state;
    }



}



