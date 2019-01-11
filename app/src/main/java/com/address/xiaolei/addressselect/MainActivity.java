package com.address.xiaolei.addressselect;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.address.xiaolei.addressselect.sqlutil.DBManager;
import com.address.xiaolei.addressselect.utils.RxUtils;
import com.address.xiaolei.addressselect.vo.CityVo;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * xiaolei
 */
public class MainActivity extends AppCompatActivity {
    DBManager dbManager;
    SQLiteDatabase  sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDb();
    }


    /**
     * 初始化数据库
     */
    private void  initDb(){
        dbManager = new DBManager(MainActivity.this);
        Observable.just(dbManager).map(new Function<DBManager, Object>() {
         @Override
         public Object apply(DBManager dbManager) throws Exception {
             sqLiteDatabase = dbManager.initDataBase(getPackageName());
             String[] columns = new String[]//列属性
                     {"cityType", "cityID", "cityName", "parentId"};
             String selection = "cityType=?";
             String[] selectionArgs = new String[]{"3"};//type为3时查询的是市
             List<CityVo> cityVoList = dbManager.query(sqLiteDatabase, columns, selection, selectionArgs);
             sqLiteDatabase.close();
             return cityVoList;
         }
     }).compose(RxUtils.schedulersTransformer()).subscribe(new Consumer<List<CityVo>>() {
         @Override
         public void accept(List<CityVo> cityVoList) throws Exception {
             Log.d("******",cityVoList.size()+"");
         }
     });
    }
}
