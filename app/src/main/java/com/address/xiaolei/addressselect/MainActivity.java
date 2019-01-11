package com.address.xiaolei.addressselect;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import com.address.xiaolei.addressselect.utils.RxUtils;
import com.address.xiaolei.addressselect.utils.UiUtils;
import com.address.xiaolei.addressselect.view.FloatingItemDecoration;
import com.address.xiaolei.addressselect.view.SlideBar;
import com.address.xiaolei.addressselect.vo.CityVo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
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
                Log.d("******", cityVoList.size() + "");
                pickCityAdapter.setHeaderView(headerView);
                pickCityAdapter.setNewData(cityVoList);
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
