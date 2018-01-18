package com.license_plate_recognition.recognize.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.license_plate_recognition.R;
import com.license_plate_recognition.recognize.model.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by liuzhen on 2018/1/16.
 */


public class ModifyActivity extends Activity {


    private  int get_text;
    private String get_school;

    private String get_ID = "";
    private String getId = "";
    private EditText editText;
    private Button btn;
    private TextView license;
    private TextView phonenumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        phonenumber = (TextView)findViewById(R.id.textview_number);
        license = (TextView)findViewById(R.id.textview_car);
        Intent i = getIntent();
        btn= (Button) findViewById(R.id.price_modify);
        editText = (EditText)findViewById(R.id.edit_price);


        get_ID =  i.getStringExtra("phonenumber");
        String data2 =  i.getStringExtra("license");

        phonenumber.setText(get_ID);
        license.setText("南京邮电大学");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_text = Integer.parseInt(editText.getText().toString());
                table_select(get_ID);
            }
        });

    }

    public void update_DateBase(final String getId, int price, String school){
        User uesr_exaple = new User();
        uesr_exaple.setValue("price",price);
        uesr_exaple.setValue("school",school);
        uesr_exaple.update(ModifyActivity.this, getId, new UpdateListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(ModifyActivity.this,"修改计费成功", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(int code, String msg) {
                Toast.makeText(ModifyActivity.this, "修改计费失败!", Toast.LENGTH_SHORT).show();
            }
        });
    }







    public  void table_select(String phone)
    {

        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("username",phone);
        query.findObjects(this,new FindListener<User>()
        {
            @Override
            public void onSuccess(List<User> object) {
                // TODO Auto-generated method stub


                //   Toast.makeText(ModifyActivity.this, "查询成功：共"+object.size()+"条数据。", Toast.LENGTH_SHORT).show();
                for (User example_abondon : object) {
                    //获得playerName的信息
                    getId=example_abondon.getObjectId();
                    get_school=example_abondon.getSchool();
                    //获得数据的objectId
                    //获得createdAt数据创建时间（注意是：createdAt，不是createAt）

                    update_DateBase(getId, get_text,get_school);
                }
            }
            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
                Toast.makeText(ModifyActivity.this, "get failure!", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent toLogin = new Intent(ModifyActivity.this, Camera.class);
            toLogin.putExtra("phonenumber",get_ID);
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
