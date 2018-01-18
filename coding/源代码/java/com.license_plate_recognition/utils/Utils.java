package com.license_plate_recognition.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {
    public static boolean isPhoneNum(String phone)
    {
        Pattern pattern = Pattern.compile("((1[358][0-9])|(14[57])|(17[0678]))\\d{8}$");
        Matcher m = pattern.matcher(phone);
        return m.matches();
    }
    public static void toast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    public static void toast(Context context, int msgId){
        Toast.makeText(context, msgId, Toast.LENGTH_SHORT).show();
    }
    public static boolean checkNetwork(Context context)
    {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean isEmailValid(String email)
    {
        String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        return email.matches(regex);
    }
}
