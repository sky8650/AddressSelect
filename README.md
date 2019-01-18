# AddressSelect
在一些项目中经常会用到把一个列表的数据源按照一定的顺序分组排序例如联系人，日期，地址选择等。
   效果如下图所示

# Example 
 <image  
src="https://github.com/sky8650/AddressSelect/blob/master/img/GIF.gif" width="260px"/>   <image src="https://github.com/sky8650/AddressSelect/blob/master/img/device-2019-01-17-185749.png" width="260px"/>    <image 
src="https://github.com/sky8650/AddressSelect/blob/master/img/device-2019-01-17-185822.png" width="260px"/>

# Uasage

## 从数据库读取数据源
```
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
```
## 把数据源进行排序
```
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
```
### 备注： 这里用到了一个把汉字转拼音的jar，pinyin4j-2.5.0.jar  https://github.com/sky8650/AddressSelect/blob/master/app/libs/pinyin4j-2.5.0.jar
       
       

   
   
   
