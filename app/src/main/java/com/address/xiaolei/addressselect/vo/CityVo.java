package com.address.xiaolei.addressselect.vo;

public class CityVo {
    private String  cityId;
    private  String  cityName;
    private  String  parentId;
    private  String  cityType;
    private  String  pinYin;

    public String getPinYin() {
        return pinYin;
    }

    public void setPinYin(String pinYin) {
        this.pinYin = pinYin;
    }

    public CityVo(){}

    public CityVo(String cityId, String cityName, String parentId, String cityType) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.parentId = parentId;
        this.cityType = cityType;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getCityType() {
        return cityType;
    }

    public void setCityType(String cityType) {
        this.cityType = cityType;
    }
}
