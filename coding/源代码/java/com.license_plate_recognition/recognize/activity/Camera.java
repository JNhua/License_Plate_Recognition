package com.license_plate_recognition.recognize.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.license_plate_recognition.R;
import com.license_plate_recognition.recognize.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class Camera extends Activity {
    private Button bn_photo;
    private Button bn_check;
    private Button upload_in;
    private Button upload_out;
    private Button check;
    private ImageView iv_photo;
    private Intent intent;
    private String phonenumber;

    private TextView plateText;
    private final int OPEN_RESULT = 1;
    private final int PICK_RESULT = 2;
    private String accessToken = "";
    private Bitmap bitmap;
    private String path = "";
    private String plateNumber = "";
    private Button bn_modify;
    private TextView costText;
    private TextView charge;

    private Date startTime;
    private Date stopTime;
    private long time;
    private int allcost=0;
    private int count = 0;
    private int get_price;

	private String getShool="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
		Intent intent_get = getIntent();
        phonenumber =  intent_get.getStringExtra("phonenumber");
        initcharge(phonenumber);

        new Thread(recognition).start();
        bn_photo = (Button) findViewById(R.id.bn_photo);
        bn_check = (Button) findViewById(R.id.bn_check);
        upload_in = (Button) findViewById(R.id.in_upload);
        upload_out = (Button) findViewById(R.id.out_money);
        plateText = (TextView) findViewById(R.id.plateText);
        iv_photo = (ImageView) findViewById(R.id.iv_photo);
        bn_modify = (Button) findViewById(R.id.modify);
        check = (Button) findViewById(R.id.gotocheck) ;
        costText = (TextView) findViewById(R.id.costText);
        charge=(TextView)findViewById(R.id.charge);

        Intent i = getIntent();
        phonenumber = i.getStringExtra("phonenumber");

        bn_modify.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                intent = new Intent(Camera.this, ModifyActivity.class);
                intent.putExtra("phonenumber",phonenumber);
                startActivity(intent);
                finish();
            }
        });

        check.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                intent = new Intent(Camera.this, Check.class);
                intent.putExtra("phonenumber",phonenumber);
                startActivity(intent);
                finish();
            }
        });



        bn_photo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                intent = new Intent(
                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, OPEN_RESULT);
            }
        });

        bn_check.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_RESULT);
            }
        });

        upload_in.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_in.setEnabled(false);
                plateText.setText("");

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // 在这里进行 http request.网络请求相关操作
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString("value", "postin");
                        msg.setData(data);

                        if (bitmap != null) {
                            getPlate(bitmap, accessToken);
                            bitmap = null;
                        } else if (!path.equals("")) {
                            getPlate(path, accessToken);
                            path = "";
                        }


                        handler.sendMessage(msg);
                    }
                }).start();

            }
        });

        upload_out.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                upload_out.setEnabled(false);
                plateText.setText("");
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // 在这里进行 http request.网络请求相关操作
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString("value", "postout");
                        msg.setData(data);

                        if (bitmap != null) {
                            getPlate(bitmap, accessToken);
                        } else if(!path.equals("")) {
                            getPlate(path, accessToken);
                        }


                        handler.sendMessage(msg);
                    }
                }).start();

            }
        });
    }


	protected void initcharge(String phonenumber){
        table_select_phone(phonenumber);
    }											  
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case OPEN_RESULT:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    bitmap = (Bitmap)bundle.get("data");

                    iv_photo.setImageBitmap(bitmap);
                }
                break;
            case PICK_RESULT:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    AbsolutePath absolutePath = new AbsolutePath();
                    path = absolutePath.getImageAbsolutePath(Camera.this,uri);

                    iv_photo.setImageURI(uri);
                }
                break;
            default:
                break;
        }

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            if (val == "access")
                Log.i("mylog", "请求结果为-->" + val);
            else
                Log.i("mylog", "请求结果为->" + val);
            // TODO
            // UI界面的更新等相关操作
            if (val == "postin" && !plateNumber.equals("") && upload_in.isEnabled() == false) {
                plateText.setText(plateNumber);
                if(!plateNumber.equals("识别失败，请重新拍摄")&&!plateNumber.equals("识别失败，请重新选择"))
                    uptosql(phonenumber,getShool,plateNumber,0,"0");
            } else if (val == "postout" && !plateNumber.equals("") && upload_out.isEnabled() == false) {
                plateText.setText(plateNumber);
                //计费
                check_income(plateNumber);
            }

            upload_in.setEnabled(true);
            upload_out.setEnabled(true);

        }
    };


    /**
     * 网络操作相关的子线程
     */
    Runnable recognition = new Runnable() {

        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value", "access");
            accessToken = new PlateRec().getAuth();
            msg.setData(data);

            handler.sendMessage(msg);
        }
    };


    public void getPlate(Bitmap img, String accessToken) {
        PlateRec plate = new PlateRec();
        plate.recoPlateNumber(img, accessToken);
        if (!plate.getPlateNumber().equals("")) {
            plateNumber = plate.getPlateNumber();
        }
    }

    public void getPlate(String path, String accessToken) {
        PlateRec plate = new PlateRec();

        plate.recoPlateNumber(path, accessToken);
        if (!plate.getPlateNumber().equals("")) {
            plateNumber = plate.getPlateNumber();
        }
    }


    public void uptosql(String user1_id, String location, String user2_id, int in_out, String cost) {

        tableall tableTotal = new tableall();
        tableTotal.setUser1_id(user1_id);
        tableTotal.setIn_out(in_out);
        tableTotal.setLocation(location);
        tableTotal.setUser2_id(user2_id);
        tableTotal.setCost(cost);
        tableTotal.save(Camera.this, new SaveListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Toast.makeText(Camera.this, "submit success!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                Toast.makeText(Camera.this, "submit failure!", Toast.LENGTH_SHORT).show();
            }
        });


    }





    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Camera.this);
            alertDialog.setTitle("提示");
            alertDialog.setMessage("确定要退出吗？");
            alertDialog.setPositiveButton("否",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                }
            });
            alertDialog.setNegativeButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    finish();
                }
            });
            alertDialog.show();
        }
        return false;
    }

    public  void  check_income(String license) {
        BmobQuery<tableall> query = new BmobQuery<tableall>();
        query.addWhereEqualTo("user2_id", license);
		query.addWhereEqualTo("user1_id",phonenumber);
        query.findObjects(Camera.this, new FindListener<tableall>() {
            @Override
            public void onSuccess(List<tableall> object) {
                // TODO Auto-generated method stub
                for (tableall table_record : object) {

                    count++;
                    if (count == object.size()) {

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//小写的mm表示的是分钟

                        try {
                            startTime = sdf.parse(table_record.getCreatedAt());
                            stopTime = new Date();
                            time = stopTime.getTime() - startTime.getTime()+(2*3600*1000);  //单位为毫秒
                            time=time/(60 * 60 * 1000);             //一小时内免费，1.5小时计算为1小时
                            allcost = new Long(time).intValue() * get_price;
                            String str = Integer.toString(allcost);
                            if(!plateNumber.equals("识别失败，请重新拍摄")&&!plateNumber.equals("识别失败，请重新选择"))
                            {
                                costText.setText("该次消费金额为："+str+"元");
                                uptosql(phonenumber, getShool, plateNumber, 1, Integer.toString(allcost));
                            }
                            allcost = 0;
                            count = 0;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }
                count = 0;
            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
                Toast.makeText(Camera.this, "get failure!", Toast.LENGTH_SHORT).show();
            }
        });
    }
	  public  void table_select_phone( String phone)
    {

        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("username",phone);
        query.findObjects(this,new FindListener<User>()
        {
            @Override
            public void onSuccess(List<User> object) {
                // TODO Auto-generated method stub
                for (User example_abondon : object) {
                    //获得信息
					getShool=example_abondon.getSchool();									 
                    get_price=example_abondon.getPrice();
                    charge.setText("计费规则:"+get_price+"元/小时");
                }
            }
            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
                Toast.makeText(Camera.this, "get failure!", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
