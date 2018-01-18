package com.license_plate_recognition.recognize.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.license_plate_recognition.R;
import com.license_plate_recognition.recognize.model.User;
import com.license_plate_recognition.utils.Utils;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;


/*
* 登录界面*/
public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private EditText edit_account ,edit_password;
    private ImageView img_visible;
    private TextView text_forget,text_register;
    private Button btn_login,btn_login_message,btn_cancel;
    private boolean isHidden=true;
    private static final String BMOB_APPLICATION_ID = "0d76e42cfb6e58e22799e94e4fdf9963";
    private PopupWindow popupWindow;
    private View view;
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};
    private android.support.v7.app.AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            int j = ContextCompat.checkSelfPermission(this, permissions[1]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED||j!= PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                showDialogTipUserRequestPermission();
            }
        }


        Bmob.initialize(this, BMOB_APPLICATION_ID);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 检查该权限是否已经获取
                int i = ContextCompat.checkSelfPermission(this, permissions[0]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (i != PackageManager.PERMISSION_GRANTED) {
                    // 提示用户应该去应用设置界面手动开启权限
                    showDialogTipUserGoToAppSettting();
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
    private void initView()
    {
        edit_account = (EditText) findViewById(R.id.edit_account);
        edit_password = (EditText) findViewById(R.id.edit_password);
        img_visible = (ImageView) findViewById(R.id.password_visible);
        text_forget = (TextView) findViewById(R.id.text_forget);
        text_register = (TextView) findViewById(R.id.text_register);
        btn_login = (Button) findViewById(R.id.btn_login);
        img_visible.setOnClickListener(this);
        text_forget.setOnClickListener(this);
        text_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);

        //for test
        //edit_account.setText("17751779537");
        //edit_password.setText("1234");
    }
    private void initPopWindow()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        view = inflater.inflate(R.layout.popupwindow,null);
        popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha=0.7f;//设置popWindow弹出时,背景变淡
        getWindow().setAttributes(lp);
        popupWindow.showAtLocation(LoginActivity.this.findViewById(R.id.login), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        popupWindow.setFocusable(true);//popWindow获得焦点
        popupWindow.setOutsideTouchable(true);//点击popWindow区域外popWindow消失
        popupWindow.setAnimationStyle(R.style.animation);
        popupWindow.update();
        btn_login_message = (Button) view.findViewById(R.id.btn_message_login);
        btn_cancel = (Button)view.findViewById(R.id.btn_cancel);
        btn_login_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnDismiss();
                Intent loginMessage = new Intent(LoginActivity.this,LoginMesageActivity.class);
                startActivity(loginMessage);
                finish();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               OnDismiss();
            }
        });
        view.setFocusableInTouchMode(true);
        view.setOnClickListener(this);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    OnDismiss();
                    return true;
                }
                return false;
            }
        });
        popupWindow.setOnDismissListener(new onDismissListener());
    }
    class onDismissListener implements PopupWindow.OnDismissListener{
        @Override
        public void onDismiss() {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 1f;//恢复背景亮度
            getWindow().setAttributes(lp);
        }
    }
    private void OnDismiss()
    {
        if (popupWindow!=null&&popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow=null;
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 1f;//恢复背景亮度
            getWindow().setAttributes(lp);
        }
    }
    private void login()
    {
        final String account = edit_account.getText().toString();
        String password = edit_password.getText().toString();
        if (!Utils.checkNetwork(this))
        {
            Utils.toast(LoginActivity.this,"当前无网络连接，请连接网络后重试");
        }else if (!TextUtils.isEmpty(account))
        {
            if (!Utils.isPhoneNum(account))
            {
                Utils.toast(LoginActivity.this, "请输入正确的手机号!");
            }else if (TextUtils.isEmpty(password))
            {
                Utils.toast(LoginActivity.this, "密码不能为空!");
            }
            BmobUser.loginByAccount(this, account, password, new LogInListener<User>() {
                @Override
                public void done(User user, BmobException e) {
                    if (e == null) {
                        btn_login.setEnabled(true);
                        Utils.toast(LoginActivity.this, "登录成功!");
                        Intent toMain = new Intent(LoginActivity.this, Camera.class);
                        toMain.putExtra("phonenumber",account);
                        startActivity(toMain);
                        finish();
                    } else {
                        btn_login.setEnabled(true);
                        Utils.toast(LoginActivity.this, "登录失败!");
                    }
                }
            });
        }else {
            Utils.toast(LoginActivity.this,"手机号码不能为空!");
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.password_visible:
                if (isHidden) {
                    img_visible.setImageResource(R.drawable.password_show);
                    edit_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    img_visible.setImageResource(R.drawable.password_hide);
                    edit_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                isHidden = !isHidden;
                break;
            case R.id.btn_login:
                btn_login.setEnabled(false);
                login();
                break;
            case R.id.text_forget:
                initPopWindow();
                break;
            case R.id.text_register:
                Intent toRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(toRegister);
                finish();
                break;
            default:
                break;
        }
    }

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    // 提示用户该请求权限的弹出框
    private void showDialogTipUserRequestPermission() {

        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("权限不可用")
                .setMessage("车位管理系统需要获取手机权限\n否则，您将无法正常使用")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startRequestPermission();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    // 开始提交请求权限
    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 321);
    }

    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    } else
                        finish();
                } else {
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // 提示用户去应用设置界面手动开启权限

    private void showDialogTipUserGoToAppSettting() {

        dialog = new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("存储权限不可用")
                .setMessage("请在-应用设置-权限-中，允许车位管理系统使用存储权限来保存用户数据")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }


    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        startActivityForResult(intent, 123);
    }

}
