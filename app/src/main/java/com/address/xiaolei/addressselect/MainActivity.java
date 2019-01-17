package com.address.xiaolei.addressselect;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.address.xiaolei.addressselect.adapter.PickCityAdapter;
import com.address.xiaolei.addressselect.sqlutil.DBManager;
import com.address.xiaolei.addressselect.utils.PinyinUtil;
import com.address.xiaolei.addressselect.utils.RxUtils;
import com.address.xiaolei.addressselect.utils.UiUtils;
import com.address.xiaolei.addressselect.view.FloatingItemDecoration;
import com.address.xiaolei.addressselect.view.SlideBar;
import com.address.xiaolei.addressselect.vo.CityVo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * xiaolei
 */
public class MainActivity extends AppCompatActivity {
    DBManager dbManager;
    SQLiteDatabase sqLiteDatabase;
    @BindView(R.id.iv_clear)
    ImageView ivClear;
    @BindView(R.id.et_city_name)
    EditText etCityName;
    @BindView(R.id.ll_search)
    RelativeLayout llSearch;
    @BindView(R.id.rv_city)
    RecyclerView rvCity;
    @BindView(R.id.slideBar)
    SlideBar slideBar;


    private View headerView;
    private PickCityAdapter pickCityAdapter;
    //悬浮itemDecoration
    private FloatingItemDecoration floatingItemDecoration;
    private HashMap<Integer, String> keys;
    private HashMap<String, Integer> letterIndexes = new HashMap<>();
    private LinearLayoutManager llm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initDb();
    }


    /**
     * 初始化视图
     */
    private void initView() {
        pickCityAdapter = new PickCityAdapter();
        rvCity.setLayoutManager(new LinearLayoutManager(this));
        rvCity.setAdapter(pickCityAdapter);
        //头部视图
        headerView = getLayoutInflater().inflate(R.layout.layout_city_header, (ViewGroup) rvCity.getParent(),
                false);
        RecyclerView rvHotCity = (RecyclerView) headerView.findViewById(R.id.rv_hot_city);
        rvHotCity.setLayoutManager(new GridLayoutManager(this, 3));
        PickCityAdapter hotCityAdapter = new PickCityAdapter();
        hotCityAdapter.setNewData(generateHotCity());
        rvHotCity.setAdapter(hotCityAdapter);
        //分割线
        floatingItemDecoration = new FloatingItemDecoration(this,
                this.getResources().getColor(R.color.divider_normal), 100, 1);
        floatingItemDecoration.setmTitleHeight(UiUtils.dp2px(this, 27));
        floatingItemDecoration.setShowFloatingHeaderOnScrolling(true);//悬浮
        rvCity.addItemDecoration(floatingItemDecoration);
        llm = new LinearLayoutManager(this);
        rvCity.setLayoutManager(llm);

        //右侧滑动选择
        slideBar.setOnTouchingLetterChangedListener(new SlideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s, int offset) {
                int position = letterIndexes.get(s) == null ? -1 : letterIndexes.get(s);
                llm.scrollToPositionWithOffset(position, offset);
            }
        });
    }


    /**
     * 初始化数据库
     */
    private void initDb() {
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
                doData(cityVoList);
            }
        });
    }


    /**
     *数据排序
     */
    private   void   doData(List<CityVo>cityVoList){
      Observable.just(cityVoList).doOnNext(new Consumer<List<CityVo>>() {
          @Override
          public void accept(List<CityVo> cityVoList) throws Exception {
              setPingyin(cityVoList);
          }
      }).doOnNext(new Consumer<List<CityVo>>() {
          @Override
          public void accept(List<CityVo> cityVoList) throws Exception {
              sortData(cityVoList);
          }
      }).subscribe(new Consumer<List<CityVo>>() {
          @Override
          public void accept(List<CityVo> cityVoList) throws Exception {
              Log.d("aaaaa","aaaa");
          }
      });


    }


    /**
     * 处理拼音
     */
    private   List<CityVo> setPingyin(final List<CityVo>cityVoList){
        for (CityVo  cityVo :cityVoList){
            cityVo.setPinYin(PinyinUtil.getPingYin(cityVo.getCityName()));
        }
        return  cityVoList;
    }

    /**
     * 排序
     */
    private   void sortData(final List<CityVo>cityVoList){
        keys = new HashMap<>();
      Observable.fromIterable(cityVoList).
                toSortedList(new Comparator<CityVo>() {
            @Override
            public int compare(CityVo o1, CityVo o2) {
                //a-z排序
                String a = o1.getPinYin();
                String b = o2.getPinYin();
                return a.compareTo(b);
            }
        }).subscribe(new Consumer<List<CityVo>>() {
          @Override
          public void accept(List<CityVo> cityVoList) throws Exception {
              pickCityAdapter.addHeaderView(headerView);
              pickCityAdapter.setNewData(cityVoList);
              //添加了头部,所以keys要从1开始
              keys.put(1, "A");
              letterIndexes.put("#", 0);
              letterIndexes.put("A", 1);
              for (int i = 0; i < cityVoList.size(); i++) {
                  if (i < cityVoList.size() - 1) {
                      //首字母不同,设为ky
                      String pre = cityVoList.get(i).getPinYin().substring(0, 1).toUpperCase();
                      String next = cityVoList.get(i + 1).getPinYin().substring(0, 1).toUpperCase();
                      if (!pre.equals(next)) {
                          keys.put(i + 2, next);
                          letterIndexes.put(next, i + 2);
                      }
                  }
              }
              floatingItemDecoration.setKeys(keys);
          }
      });
    }


    /**
     * 热门城市
     *
     * @return
     */
    private List<CityVo> generateHotCity() {
        Gson gson = new Gson();
        Type type = new TypeToken<List<CityVo>>(){}.getType();
        String jsonString=this.getResources().getString(R.string.hot_city_json);
        List<CityVo>cityVoList = gson.fromJson(jsonString,type);
        return cityVoList;
    }


}
