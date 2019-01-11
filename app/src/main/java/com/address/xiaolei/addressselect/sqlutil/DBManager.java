package com.address.xiaolei.addressselect.sqlutil;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.address.xiaolei.addressselect.utils.FileUtil;
import com.address.xiaolei.addressselect.vo.CityVo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * xiaolei
 */
public class DBManager {
    private String DB_NAME = "city_db_53.db";
    private Context mContext;

    public DBManager(Context mContext) {
        this.mContext = mContext;
    }
    //把assets目录下的db文件复制到dbpath下
    public SQLiteDatabase initDataBase(String packName) {
        String dbPath = FileUtil.getCachePath(mContext)+ "/"+packName
                + "/databases/" + DB_NAME;
        File  file=new File(dbPath);
        if (!file.exists()) {
            try {
                // 先得到文件的上级目录，并创建上级目录，在创建文件
               boolean  falg1= file.getParentFile().mkdirs();
                boolean  falg2=   file.createNewFile();
                Log.d("*************",falg1+"&&&"+falg2);
                FileOutputStream out = new FileOutputStream(file);
                InputStream in = mContext.getAssets().open(DB_NAME);
                byte[] buffer = new byte[1024];
                int readBytes = 0;
                while ((readBytes = in.read(buffer)) != -1)
                    out.write(buffer, 0, readBytes);
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return SQLiteDatabase.openOrCreateDatabase(dbPath, null);
    }
    //查询
    public List<CityVo> query(SQLiteDatabase sqliteDB, String[] columns, String selection, String[] selectionArgs) {
        CityVo city = null;
        List<CityVo>cityVoList=new ArrayList<>();
        try {
            String table = "cityTable";//数据库表名
            Cursor cursor = sqliteDB.query(table, columns, selection, selectionArgs,
                    null, null, null);
            while (cursor.moveToNext()) {
                String cityType = cursor.getString(cursor.getColumnIndex("cityType"));
                String cityId = cursor.getString(cursor.getColumnIndex("cityID"));
                String cityName = cursor.getString(cursor.getColumnIndex("cityName"));
                String parentId = cursor.getString(cursor.getColumnIndex("parentId"));
                city = new CityVo(cityId,cityName, parentId,cityType);
                cityVoList.add(city);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cityVoList;
    }

}
