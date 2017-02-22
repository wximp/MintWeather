package com.wximp.mintweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.wximp.mintweather.sqlite.City;
import com.wximp.mintweather.utils.NowDao;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by smile on 2016/12/15.
 */


public class Province extends Activity {
    private String position;
    private String[] provincedata;
    private String myid;
    private int setcode = 100;//100无法获取定位城市 200可以获取定位城市
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationClientOption mLocationOption = null;
    public AMapLocationListener mLocationListener = new AMapLocationListener() {

        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            // TODO Auto-generated method stub
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    double locationType = amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    double latitude = amapLocation.getLatitude();//获取纬度
                    double longitude = amapLocation.getLongitude();
                    Log.e("Amap==纬度", "locationType:" + locationType + ",latitude:" + latitude);
                    Log.e("Amap==经度", "locationType:" + locationType + ",longitude:" + longitude);
                    position = String.valueOf((latitude + ":" + longitude));
                    getcode(position);


                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                    provincedata = new String[]{"西藏", "新疆", "云南", "海南", "广西", "广东", "青海", "四川", "贵州", "湖南", "重庆", "甘肃", "湖北", "陕西", "宁夏", "内蒙古", "山西", "河南", "福建", "江西", "浙江", "安徽", "江苏", "上海", "山东", "河北", "天津", "辽宁", "北京", "吉林", "黑龙江", "香港", "澳门", "台湾"};
                    gv_province.setAdapter(new ProvinceAdapter());

                }
            }
        }
    };
    private String[] cityid;
    private InputStream in;
    private String city;
    private String returnname;
    private GridView gv_province;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.province);
        gv_province = (GridView) findViewById(R.id.gv_city);
        final AutoCompleteTextView auto_tv = (AutoCompleteTextView) findViewById(R.id.auto_tv);
        cityid = getResources().getStringArray(R.array.cityid);
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        mLocationOption.setOnceLocation(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.city));
        auto_tv.setAdapter(arrayAdapter);

        auto_tv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String getname = adapterView.getItemAtPosition(i).toString();
                String substring = getname.substring(0, getname.indexOf(" "));
                Log.e("a", substring);
                City city = new City(getApplicationContext());
                String cityId = city.getCityId(substring);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("city_id", cityId);
                startActivity(intent);
                finish();
            }
        });

        gv_province.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (setcode == 200) {
                    if (position == 0) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("city_id", myid);
                        startActivity(intent);
                        finish();

                    } else {
                        Intent intent = new Intent(getApplicationContext(), SelectCity.class);
                        intent.putExtra("province", provincedata[position]);
                        startActivity(intent);

                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(), SelectCity.class);
                    intent.putExtra("province", provincedata[position]);
                    startActivity(intent);

                }

            }
        });

    }

    public void getcode(final String ps) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String forwardurl = "https://api.thinkpage.cn/v3/weather/now.json?key=trz4wco2izfd5t0m&location=" + ps + "&language=zh-Hans&unit=c";
                    Log.e("url", forwardurl);
                    URL url = new URL(forwardurl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5000);
                    int responseCode = conn.getResponseCode();

                    Log.e("url", String.valueOf(responseCode));
                    if (responseCode == 200) {
                        in = conn.getInputStream();
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        int len = -1;
                        byte[] buffer = new byte[1024];
                        if ((len = in.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, len);

                        }
                        in.close();
                        String returncode = new String(outputStream.toByteArray());
                        Gson mGson8 = new Gson();
                        NowDao nowBean1 = mGson8.fromJson(returncode, NowDao.class);
                        city = nowBean1.getResults().get(0).getLocation().getName();
                        myid = nowBean1.getResults().get(0).getLocation().getId();
                        Log.e("now", city);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (city != null) {
                                    provincedata = new String[]{"定位:" + city, "西藏", "新疆", "云南", "海南", "广西", "广东", "青海", "四川", "贵州", "湖南", "重庆", "甘肃", "湖北", "陕西", "宁夏", "内蒙古", "山西", "河南", "福建", "江西", "浙江", "安徽", "江苏", "上海", "山东", "河北", "天津", "辽宁", "北京", "吉林", "黑龙江", "香港", "澳门", "台湾"};
                                    setcode = 200;
                                } else {
                                    provincedata = new String[]{"西藏", "新疆", "云南", "海南", "广西", "广东", "青海", "四川", "贵州", "湖南", "重庆", "甘肃", "湖北", "陕西", "宁夏", "内蒙古", "山西", "河南", "福建", "江西", "浙江", "安徽", "江苏", "上海", "山东", "河北", "天津", "辽宁", "北京", "吉林", "黑龙江", "香港", "澳门", "台湾"};
                                    setcode = 200;
                                }
                                gv_province.setAdapter(new ProvinceAdapter());
                            }
                        });


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }


    private class ProvinceAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return provincedata.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {

            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myview;
            if (view == null) {
                myview = View.inflate(getApplicationContext(), R.layout.cityitem, null);
            } else {
                myview = view;
            }
            TextView provincename = (TextView) myview.findViewById(R.id.tv_cityname);

            provincename.setText(provincedata[i]);

            myview.getBackground().setAlpha(50);

            return myview;
        }
    }
}
