package com.license_plate_recognition.recognize.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.license_plate_recognition.R;
import com.license_plate_recognition.recognize.model.User;
import com.license_plate_recognition.utils.Utils;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;


/*
* 注册界面*/
public class RegisterActivity extends BaseActivity implements View.OnClickListener{
    private EditText register_phone_edit,register_password_edit,
            register_password_confirm_edit,register_code_edit,address_edit;
    private ImageView password_visible;
    private TextView register_getcode_tv,tologin_tv;
    private Button register_btn;
    private CountDownTimer countDownTimer;
    private ProgressDialog progressDialog;
    private boolean isHidden=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }
    private void initView()
    {
        register_phone_edit = (EditText) findViewById(R.id.edit_phone);
        register_password_edit = (EditText) findViewById(R.id.edit_password);
        address_edit=(EditText)findViewById(R.id.school);
        password_visible = (ImageView) findViewById(R.id.password_visible);
        register_password_confirm_edit = (EditText) findViewById(R.id.edit_confirm_pwd);
        register_code_edit = (EditText) findViewById(R.id.edit_code);
        register_getcode_tv = (TextView) findViewById(R.id.text_getCode);
        tologin_tv = (TextView) findViewById(R.id.text_login);
        register_btn = (Button) findViewById(R.id.btn_register);
        password_visible.setOnClickListener(this);
        register_getcode_tv.setOnClickListener(this);
        register_btn.setOnClickListener(this);
        tologin_tv.setOnClickListener(this);
/*
        //for test
        register_phone_edit.setText("17751779537");
        register_password_confirm_edit.setText("1234");
        register_password_edit.setText("1234");
        register_code_edit.setText("123341");
        address_edit.setText("南邮南门");
        */
    }

    //获取验证码
    private void requestSmsCode() {
        String phone = register_phone_edit.getText().toString().trim();
        String password = register_password_edit.getText().toString().trim();
        String confirm_pwd = register_password_confirm_edit.getText().toString().trim();
        String address=address_edit.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Utils.toast(RegisterActivity.this, "请先输入手机号!");
        } else {
            if (Utils.isPhoneNum(phone)) {
                if (TextUtils.isEmpty(password)) {
                    Utils.toast(RegisterActivity.this, "密码不能为空!");
                } else if (!password.equals(confirm_pwd)) {
                    Utils.toast(RegisterActivity.this, "两次输入的密码不一致!");
                } else if (TextUtils.isEmpty(address)) {
                    Utils.toast(RegisterActivity.this, "地址不能为空!");
                } else {
                    countDownTimer = new CountDownTimer(60000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            register_getcode_tv.setEnabled(false);
                            register_getcode_tv.setText((millisUntilFinished / 1000) + "秒后重发");
                        }

                        @Override
                        public void onFinish() {
                            register_getcode_tv.setEnabled(true);
                            register_getcode_tv.setText("重新发送验证码");
                        }
                    }.start();
                    BmobSMS.requestSMSCode(RegisterActivity.this, register_phone_edit.getText().toString().trim(), "用户注册", new RequestSMSCodeListener() {
                        @Override
                        public void done(Integer integer, BmobException e) {
                            if (e == null) {
                                Utils.toast(RegisterActivity.this, "验证码已发送!");
                            } else if (e.getErrorCode() == 10010) {
                                Utils.toast(RegisterActivity.this, "操作过于频繁，请稍后再试!");
                            }
                        }
                    });
                }
            } else {
                Utils.toast(RegisterActivity.this, "请输入正确的手机号!");
            }
        }
    }


    private void verifySmsCode()
    {
        String mobilephone = register_phone_edit.getText().toString().trim();
        String code = register_code_edit.getText().toString().trim();
        if (!Utils.checkNetwork(RegisterActivity.this)) {
            Utils.toast(RegisterActivity.this,"当前无网络连接，请连接网络后重试");
        }else if (TextUtils.isEmpty(code)) {
            Utils.toast(RegisterActivity.this,"验证码不能为空");
        }
       else{
            BmobSMS.verifySmsCode(RegisterActivity.this,mobilephone,code, new VerifySMSCodeListener() {
                @Override
                public void done(BmobException e) {
                    //for test
                    //e=null;
                    if (e==null)
                    {
                        register();
                        register_btn.setEnabled(true);
                    }else{
                        register_btn.setEnabled(true);
                        Utils.toast(RegisterActivity.this,"验证失败");
                    }
                }
            });
        }
    }
    private void register()
    {

        String phone = register_phone_edit.getText().toString().trim();
        String password = register_password_edit.getText().toString().trim();
        String confirm_pwd = register_password_confirm_edit.getText().toString().trim();
        String address=address_edit.getText().toString().trim();
        if (TextUtils.isEmpty(phone))
        {
            Utils.toast(RegisterActivity.this,"手机号不能为空!");
        }else if (TextUtils.isEmpty(password))
        {
            Utils.toast(RegisterActivity.this,"密码不能为空!");
        }else if (!password.equals(confirm_pwd))
        {
            Utils.toast(RegisterActivity.this,"两次输入的密码不一致!");
        }else if(TextUtils.isEmpty(address)) {
            Utils.toast(RegisterActivity.this, "地址不能为空!");
        }
        else {
            final User user = new User();
            user.setUsername(phone);
            user.setPassword(password);
            user.setMobilePhoneNumber(phone);
            user.setMobilePhoneNumberVerified(true);
            user.setPrice(5);
            user.setSchool(address);
            progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setMessage("正在注册...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            user.signUp(RegisterActivity.this, new SaveListener() {
                @Override
                public void onSuccess() {
                    progressDialog.dismiss();
                    Utils.toast(RegisterActivity.this, "注册成功!");
                    Intent toLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(toLogin);
                    finish();
                }

                @Override
                public void onFailure(int i, String s) {
                    if(s.indexOf("already taken")!=-1){
                        Utils.toast(RegisterActivity.this, "账号已存在!");
                    }
                    else
                        Utils.toast(RegisterActivity.this, "注册失败!");
                    progressDialog.dismiss();
                }
            });
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.password_visible:
                if (isHidden) {
                    password_visible.setImageResource(R.drawable.password_show);
                    register_password_edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    password_visible.setImageResource(R.drawable.password_hide);
                    register_password_edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                isHidden = !isHidden;
                break;
            case R.id.text_getCode:
                requestSmsCode();
                break;
            case R.id.btn_register:
                register_btn.setEnabled(false);
                verifySmsCode();
                break;
            case R.id.text_login:
                Intent toLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(toLogin);
                finish();
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent toLogin = new Intent(RegisterActivity.this, LoginActivity.class);
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
