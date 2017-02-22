package com.wximp.mintweather;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.wximp.mintweather.sqlite.MySqliteOpenhelper;
import com.wximp.mintweather.sqlite.SetInfo;
import com.wximp.mintweather.utils.FutureDao;
import com.wximp.mintweather.utils.ImgCode;
import com.wximp.mintweather.utils.NowDao;
import com.wximp.mintweather.utils.SuggestionDao;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static java.lang.Math.max;
import static java.lang.Math.min;


public class MainActivity extends AppCompatActivity {
    private URL url;
    private InputStream in;
    private ImageView iv_bg;
    private TextView tv_location;
    private TextView hintnow;
    private TextView tv_nowbigcode;
    private TextView tv_nowmax;
    private TextView tv_nowmin;
    private TextView tv_nowwind;
    private String reposition;
    private String loacationcity;
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
                    reposition = String.valueOf((latitude + ":" + longitude));
                    getCode(400, reposition);


                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                    Intent intent = new Intent(getApplicationContext(), Province.class);
                    startActivity(intent);

                }
            }
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:

                    Toast.makeText(getApplicationContext(), "加载失败了/(ㄒoㄒ)/~~", Toast.LENGTH_SHORT).show();
                    break;
                case 100:
                    String content = (String) msg.obj;
                    Log.i("content", content);
                    resolveJson(msg.what, content);
                    //  Toast.makeText(getApplicationContext(), "true", Toast.LENGTH_SHORT).show();
                    break;
                case 200:
                    String content1 = (String) msg.obj;
                    Log.i("content", content1);
                    resolveJson(msg.what, content1);
                    //Toast.makeText(getApplicationContext(), "true", Toast.LENGTH_SHORT).show();
                    break;
                case 300:
                    String content2 = (String) msg.obj;
                    Log.i("content", content2);
                    resolveJson(msg.what, content2);
                    // Toast.makeText(getApplicationContext(), "true", Toast.LENGTH_SHORT).show();
                    break;
                case 110:
                    String content3 = (String) msg.obj;
                    Gson mGson8 = new Gson();
                    NowDao nowBean1 = mGson8.fromJson(content3, NowDao.class);
                    loacationcity = nowBean1.getResults().get(0).getLocation().getName();
                    //Toast.makeText(getApplicationContext(), loacationcity, Toast.LENGTH_SHORT).show(); 获取定位的城市名
                    dialog();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "加载失败了/(ㄒoㄒ)/~", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };
    private GridView gv_sm;
    private FutureDao futureBean;
    private SwipeRefreshLayout refrelo;
    private int anInt;
    private GridView gv_hint;
    private SuggestionDao suggestionDao;
    private SetInfo setinfo;
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        //  getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); //图片沉浸状态栏
        setContentView(R.layout.activity_main);


        MySqliteOpenhelper mysql = new MySqliteOpenhelper(getApplicationContext());
        SQLiteDatabase db = mysql.getReadableDatabase();
        setinfo = new SetInfo(getApplicationContext());
        iv_bg = (ImageView) findViewById(R.id.weather_icon);
        tv_location = (TextView) findViewById(R.id.tv_location);
        hintnow = (TextView) findViewById(R.id.tv_nowbigoc);
        tv_nowbigcode = (TextView) findViewById(R.id.tv_nowbigcode);
        tv_nowmax = (TextView) findViewById(R.id.tv_nowmax);
        tv_nowmin = (TextView) findViewById(R.id.tv_nowmin);
        tv_nowwind = (TextView) findViewById(R.id.tv_nowwind);
        gv_sm = (GridView) findViewById(R.id.gv_sm);
        // refrelo = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
        gv_hint = (GridView) findViewById(R.id.gv_hint);
        Intent getintent = getIntent();
        id = getintent.getStringExtra("city_id");



      /*  refrelo.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCode(200, reposition);
                Toast.makeText(getApplicationContext(), "更新成功", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refrelo.setRefreshing(false);
                    }
                }, 200);


            }
        });*/
        if (id != null) {
            reposition = id.trim();
            getCode(200, reposition);
            setinfo.DelInfo();
            setinfo.setReId(reposition);
        } else {
            int num = setinfo.getNum();//通过数据库判断是否存在保存的城市ID
            Log.e("num", String.valueOf(num));

            if (num == 999) {
                //没数据开启定位
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

            } else if (num > 0) {
                String setId = setinfo.getSetId();
                reposition = setId.trim();
                getCode(200, reposition);


            }
        }


    }

    public void nowweather(View v) {
        //  getCode(200,);
    }

    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getCode(final int requestcode, final String ps) {


        new Thread() {
            private String forwardurl;

            @Override
            public void run() {
                super.run();
                try {
                    switch (requestcode) {
                        case 100:
                            forwardurl = " https://api.thinkpage.cn/v3/weather/daily.json?key=trz4wco2izfd5t0m&location=" + ps + "&language=zh-Hans&unit=c&start=0&days=3";

                            break;
                        case 200:
                            forwardurl = "https://api.thinkpage.cn/v3/weather/now.json?key=trz4wco2izfd5t0m&location=" + ps + "&language=zh-Hans&unit=c";
                            break;
                        case 300:
                            forwardurl = "https://api.thinkpage.cn/v3/life/suggestion.json?key=trz4wco2izfd5t0m&location=" + ps + "&language=zh-Hans&unit=c";
                            break;
                        case 400:
                            forwardurl = "https://api.thinkpage.cn/v3/weather/now.json?key=trz4wco2izfd5t0m&location=" + ps + "&language=zh-Hans&unit=c";
                            break;
                    }

                    Log.i("URL", forwardurl);
                    url = new URL(forwardurl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5000);
                    if (conn.getResponseCode() == 200) {
                        in = conn.getInputStream();
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        int len = -1;
                        byte[] buffer = new byte[1024];
                        while ((len = in.read(buffer)) != -1) {
                            bos.write(buffer, 0, len);
                        }
                        in.close();
                        String returncode = new String(bos.toByteArray());
                        Message msg = Message.obtain();
                        switch (requestcode) {
                            case 100:
                                msg.what = 100;
                                break;
                            case 200:
                                msg.what = 200;
                                break;
                            case 300:
                                msg.what = 300;
                                break;
                            case 400:
                                msg.what = 110;
                                break;
                        }
                        msg.obj = returncode;
                        handler.sendMessage(msg);


                    } else {
                        Message msg = Message.obtain();
                        msg.what = 0;
                        msg.obj = conn.getResponseCode();
                        handler.sendMessage(msg);
                    }

                } catch (Exception e) {
                    Message msg = Message.obtain();
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }

    public void resolveJson(int rqcode, String content) {


        switch (rqcode) {

            case 100:
                try {
                    Gson mGson = new Gson();
                    futureBean = mGson.fromJson(content, FutureDao.class);
                    int textcode = futureBean.getResults().get(0).getDaily().get(0).getCode_day();
                    String textday = futureBean.getResults().get(0).getDaily().get(0).getText_day();
                    String maxone = futureBean.getResults().get(0).getDaily().get(0).getHigh();
                    String minone = futureBean.getResults().get(0).getDaily().get(0).getLow();
                    String wind = futureBean.getResults().get(0).getDaily().get(0).getWind_direction();
                    String windScale = futureBean.getResults().get(0).getDaily().get(0).getWind_scale();
                    Log.i("城市", futureBean.getResults().get(0).getLocation().getName());
                    Log.i("最高温", maxone);
                    Log.i("最低温", minone);
                    Log.i("风向", futureBean.getResults().get(0).getDaily().get(0).getWind_direction());
                    tv_nowbigcode.setText(textday);
                    iv_bg.setBackgroundResource(ImgCode.tocode(textcode));
                    tv_nowmax.setText("↑ " + maxone + "℃");
                    tv_nowmin.setText("↓ " + minone + "℃");
                    tv_nowwind.setText("风向：" + wind);
                    if (windScale.length() > 0) {
                        tv_nowwind.setText("风向：" + wind + "　" + windScale + "级");
                    } else {
                        tv_nowwind.setText("风向：" + wind);
                    }
                    gv_sm.setAdapter(new MyAdapter());
                    getCode(300, reposition);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;


            case 200:
                Gson mGson1 = new Gson();
                NowDao nowBean = mGson1.fromJson(content, NowDao.class);
                int code = nowBean.getResults().get(0).getNow().getCode();

                String city = nowBean.getResults().get(0).getLocation().getName();
                String temperature = nowBean.getResults().get(0).getNow().getTemperature();//当前气温
                String weather = nowBean.getResults().get(0).getNow().getText();//天气文字
                Log.i("城市", city);
                Log.i("气温", temperature);

                Log.i("天气", weather);
                Log.i("code", String.valueOf(code));
                // iv_bg.setBackgroundResource(ImgCode.tocode(code));
                tv_location.setText(city);
                hintnow.setText("今日 " + temperature + "℃");
                // tv_nowbigcode.setText(weather);
                getCode(100, reposition);
                break;

            case 300:
                Gson mGson2 = new Gson();
                suggestionDao = mGson2.fromJson(content, SuggestionDao.class);
                gv_hint.setAdapter(new HintAdapter());
                break;
        }

    }

    private String hmac_sha1(String key, String datas) {
        String reString = "";

        try {
            byte[] data = key.getBytes("UTF-8");
            //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
            SecretKey secretKey = new SecretKeySpec(data, "HmacSHA1");
            //生成一个指定 Mac 算法 的 Mac 对象
            Mac mac = Mac.getInstance("HmacSHA1");
            //用给定密钥初始化 Mac 对象
            mac.init(secretKey);

            byte[] text = datas.getBytes("UTF-8");
            //完成 Mac 操作
            byte[] text1 = mac.doFinal(text);

            reString = Base64.encodeToString(text1, Base64.DEFAULT);

        } catch (Exception e) {
            // TODO: handle exception
        }
        Log.i("RESTRING", reString);
        return reString;
    }

    public String getTime() {

        long time = System.currentTimeMillis() / 1000;

        String str = String.valueOf(time);
        Log.i("GETTIME", str);
        return str;

    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return futureBean.getResults().get(0).getDaily().size();
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
                myview = View.inflate(getApplication(), R.layout.smallitems, null);

            } else {
                myview = view;
            }
            myview.getBackground().setAlpha(60);
            ImageView smallicon = (ImageView) myview.findViewById(R.id.iv_small);
            TextView smdate = (TextView) myview.findViewById(R.id.tv_data);
            TextView smcodetext = (TextView) myview.findViewById(R.id.tv_smoc);
            TextView smwind = (TextView) myview.findViewById(R.id.tv_smwind);
            TextView smmaxoc = (TextView) myview.findViewById(R.id.tv_smmaxoc);
            TextView smminoc = (TextView) myview.findViewById(R.id.tv_smminoc);

            int code_day = futureBean.getResults().get(0).getDaily().get(i).getCode_day();
            smcodetext.setText(futureBean.getResults().get(0).getDaily().get(i).getText_day());
            String subdate = futureBean.getResults().get(0).getDaily().get(i).getDate();
            String wind_scale = futureBean.getResults().get(0).getDaily().get(i).getWind_scale();
            switch (i) {
                case 0:
                    smdate.setText("今日 " + subdate.substring(5));
                    break;
                case 1:

                    smdate.setText("明日 " + subdate.substring(5));
                    break;
                case 2:
                    smdate.setText("后日 " + subdate.substring(5));
                    break;
            }

            smmaxoc.setText("↑ " + futureBean.getResults().get(0).getDaily().get(i).getHigh() + "℃");

            smminoc.setText("↓ " + futureBean.getResults().get(0).getDaily().get(i).getLow() + "℃");
            if (wind_scale.length() > 0) {
                smwind.setText("风向:" + futureBean.getResults().get(0).getDaily().get(i).getWind_direction() + " " + wind_scale + "级");
            } else {
                smwind.setText("风向:" + futureBean.getResults().get(0).getDaily().get(i).getWind_direction());
            }
            smallicon.setBackgroundResource(ImgCode.tocode(code_day));
            return myview;

        }


    }

    private class HintAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 4;
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
                myview = View.inflate(getApplicationContext(), R.layout.hintitem, null);

            } else {
                myview = view;
            }
            myview.getBackground().setAlpha(20);
            ImageView ch_img = (ImageView) myview.findViewById(R.id.iv_ch_img);
            TextView ch_title = (TextView) myview.findViewById(R.id.ch_title);
            TextView ch_code = (TextView) myview.findViewById(R.id.ch_code);
            switch (i) {
                case 0:
                    ch_img.setBackgroundResource(R.drawable.uv);
                    ch_title.setText("紫外线指数");
                    String uv = suggestionDao.getResults().get(0).getSuggestion().getUv().getBrief();
                    ch_code.setText(uv);
                    break;
                case 1:
                    ch_img.setBackgroundResource(R.drawable.sport);
                    ch_title.setText("运动指数");
                    String sport = suggestionDao.getResults().get(0).getSuggestion().getSport().getBrief();
                    ch_code.setText(sport);
                    break;
                case 2:
                    ch_img.setBackgroundResource(R.drawable.flu);
                    ch_title.setText("感冒指数");
                    String flu = suggestionDao.getResults().get(0).getSuggestion().getFlu().getBrief();
                    ch_code.setText(flu);
                    break;
                case 3:
                    ch_img.setBackgroundResource(R.drawable.dressing);
                    ch_title.setText("穿衣指数");
                    String dressing = suggestionDao.getResults().get(0).getSuggestion().getDressing().getBrief();
                    ch_code.setText(dressing);
                    break;
            }


            return myview;
        }
    }

    public void dialog() {

        LayoutInflater inflaterDl = LayoutInflater.from(this);
        final RelativeLayout dialoglayout = (RelativeLayout) inflaterDl.inflate(R.layout.dialog, null);
        //对话框

        TextView tv_location = (TextView) dialoglayout.findViewById(R.id.dialog_text);
        if (loacationcity != null) {
            tv_location.setText("是否选择定位的城市:" + loacationcity);
        }
        final Dialog dialog = new AlertDialog.Builder(MainActivity.this).create();
        dialog.show();


        dialog.getWindow().setContentView(dialoglayout);

        Button btnCancel = (Button) dialoglayout.findViewById(R.id.dialog_on);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getCode(200, reposition);
                setinfo.setId(loacationcity);
                dialog.dismiss();
            }
        });


        Button btnOK = (Button) dialoglayout.findViewById(R.id.dialog_off);
        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Province.class);
                startActivity(intent);
                dialog.dismiss();

            }
        });
    }

    public void selectCity(View v) {
        Intent intent = new Intent(getApplicationContext(), Province.class);
        startActivity(intent);


    }

}
