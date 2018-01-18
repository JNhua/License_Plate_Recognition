package com.license_plate_recognition.recognize.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.license_plate_recognition.R;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by 28782 on 2018/1/17.
 */

public  class Check extends Activity {
    private TextView textView_1;
    private TextView textView_2;
    private TextView textView_3;
    private TextView textView_4;
    private TextView textView_5;
    private String phonenumber;
    private Button btn;
    private  int count=1;

    private  int income=0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check);
        btn=(Button)findViewById(R.id.camera_return);
        textView_1  =(TextView)findViewById(R.id.info_check1);
        textView_2  =(TextView)findViewById(R.id.info_check2);
        textView_3  =(TextView)findViewById(R.id.info_check3);
        textView_4  =(TextView)findViewById(R.id.info_check4);
        textView_5  =(TextView)findViewById(R.id.sum_info);

        Intent get_phone = getIntent();
        phonenumber = get_phone.getStringExtra("phonenumber");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               select();
            }
        });
    }



        public  void select( )
        {

            BmobQuery<tableall> query = new BmobQuery<tableall>();
            query.addWhereEqualTo("in_out",1);
         //   query.setLimit(4);
            query.findObjects(Check.this,new FindListener<tableall>()
            {
                @Override
                public void onSuccess(List<tableall> object) {
                    // TODO Auto-generated method stub

                    Toast.makeText(Check.this, "查询成功：显示前"+object.size()+"条数据。", Toast.LENGTH_SHORT).show();
                    for (tableall database : object) {

                       // income += database.getCost();
                        income+= Integer.valueOf(database.getCost()).intValue();
                      switch ( count){
                          case 1:      textView_1.setText(database.getUser2_id()+":   "+database.getCost()+ "元     "+database.getCreatedAt()) ;
                          case 2:      textView_2.setText(database.getUser2_id()+":   "+database.getCost()+ "元     "+ database.getCreatedAt()) ;
                          case 3:     textView_3.setText(database.getUser2_id()+":   "+database.getCost()+ "元     "+ database.getCreatedAt()) ;
                          case 4:     textView_4.setText(database.getUser2_id()+":   "+database.getCost()+ "元     "+ database.getCreatedAt()) ;

                      }
                        count ++;
                    }
                    String str = Integer.toString(income);
                    textView_5.setText("总收入：:"+str);
                }
                @Override
                public void onError(int code, String msg) {
                    // TODO Auto-generated method stub
                    Toast.makeText(Check.this, "get failure!", Toast.LENGTH_SHORT).show();
                }
            });

        }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent toLogin = new Intent(Check.this, Camera.class);
            toLogin.putExtra("phonenumber",phonenumber);
            startActivity(toLogin);
            finish();
        }
        return  false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
