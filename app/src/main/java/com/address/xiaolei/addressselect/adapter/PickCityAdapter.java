package com.address.xiaolei.addressselect.adapter;

import android.view.View;

import com.address.xiaolei.addressselect.R;
import com.address.xiaolei.addressselect.vo.CityVo;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


/**
 * Created by Cicinnus on 2017/6/8.
 */

public class PickCityAdapter extends BaseQuickAdapter<CityVo, BaseViewHolder> {


    private OnCityClickListener onCityClickListener;

    public PickCityAdapter() {
        super(R.layout.item_city);
    }
    @Override
    protected void convert(BaseViewHolder helper, final CityVo item) {
        helper.setText(R.id.tv_city_name, item.getCityName());
    }


    public interface OnCityClickListener{
        void onClick(CityVo item);
    }


}
