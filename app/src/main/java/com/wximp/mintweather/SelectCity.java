package com.wximp.mintweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.wximp.mintweather.sqlite.City;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by smile on 2016/12/15.
 */

public class SelectCity extends Activity {
    private String father = null;
    private ArrayList<Map<String, Object>> search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city);
        Intent resultintent = getIntent();
        String province = resultintent.getStringExtra("province");
        father = province;
        setCity();
        GridView gv_city = (GridView) findViewById(R.id.gv_city);
        gv_city.setAdapter(new MyCityAdapter());
        gv_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String cityid = (String) search.get(i).get("_id");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("city_id", cityid);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
        });


    }


    public void setCity() {
        City city = new City(getApplicationContext());
        search = city.search(father);

    }


    private class MyCityAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return search.size();
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

            } else myview = view;


            String cityname = (String) search.get(i).get("cn_name");
            TextView tv_cityname = (TextView) myview.findViewById(R.id.tv_cityname);
            tv_cityname.setText(cityname);
            myview.getBackground().setAlpha(50);
            return myview;
        }
    }

}
