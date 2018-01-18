package com.license_plate_recognition.recognize.activity;

/**
 * Created by 田波 on 2018/1/14.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class PlateRec {
    private String plateNumber = "";

    public String getAuth(){
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String clientId = "2nu8XsVtOrG2A370DQfFKijR";
        String clientSecret = "61TzpFUx8Ozd4EY4cW47GeGwRkyl1o8y";

        String accessToken = authHost
                + "grant_type=client_credentials"
                + "&client_id=" +  clientId
                + "&client_secret=" + clientSecret;

        try{
            URL realURL = new URL(accessToken);
            HttpURLConnection connection = (HttpURLConnection) realURL.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            Map<String,List<String>> map = connection.getHeaderFields();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while((line = in.readLine()) != null){
                result += line;
            }
            JSONObject jsonObject = new JSONObject(result);
            String access_token = jsonObject.getString("access_token");
            return access_token;

        }catch (Exception e){
            e.printStackTrace();

        }
        return null;
    }

    public  void recoPlateNumber(Bitmap bitmap, String accessToken){
        try{
			 ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 90;

            while (baos.toByteArray().length / 1024 > 1000) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset(); // 重置baos即清空baos
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
                options -= 10;// 每次都减少10
            }
            byte[] imgData = baos.toByteArray();

            String imgStr = Base64Util.encode(imgData);
            String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/license_plate";
            String param = "image="+ URLEncoder.encode(imgStr,"UTF-8")+"&language_type=CHN_ENG&detect_direction=true&detect_language=true&probability=true";
            String result = HttpUtil.post(url, accessToken, param);
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.has("words_result"))
                plateNumber = jsonObject.getJSONObject("words_result").getString("number");
            if(plateNumber.equals("") || plateNumber == null){
                plateNumber = "识别失败，请重新拍摄";
            }
        }catch (Exception e){
            String error = e.getMessage();
            e.printStackTrace();
        }
    }

    public  void recoPlateNumber(String path, String accessToken){
        try{

			BitmapFactory.Options newOpts = new BitmapFactory.Options();
            //开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(path,newOpts);//此时返回bm为空

            newOpts.inJustDecodeBounds = false;
            int w = newOpts.outWidth;
            int h = newOpts.outHeight;
            //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
            float hh = 1920f;//这里设置高度为1920f
            float ww = 1080f;//这里设置宽度为1080f
            //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            int be = 1;//be=1表示不缩放
            if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
                be = (int) (newOpts.outWidth / ww);
            } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
                be = (int) (newOpts.outHeight / hh);
            }
            if (be <= 0)
                be = 1;
            newOpts.inSampleSize = be;//设置缩放比例
            //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            bitmap = BitmapFactory.decodeFile(path, newOpts);


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 90;

            while (baos.toByteArray().length / 1024 > 1000) { // 循环判断如果压缩后图片是否大于1000kb,大于继续压缩
                baos.reset(); // 重置baos即清空baos
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
                options -= 10;// 每次都减少10
            }
            byte[] imgData = baos.toByteArray();

            String imgStr = Base64.encodeToString(imgData, Base64.DEFAULT);
            String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/license_plate";
            String param = "image="+ URLEncoder.encode(imgStr,"UTF-8")+"&language_type=CHN_ENG&detect_direction=true&detect_language=true&probability=true";
            String result = HttpUtil.post(url, accessToken, param);
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.has("words_result"))
                plateNumber = jsonObject.getJSONObject("words_result").getString("number");
            if(plateNumber.equals("") || plateNumber == null){
                plateNumber = "识别失败，请重新选择";
            }

        }catch (Exception e){
            String error = e.getMessage();
            e.printStackTrace();
        }

    }

    public String getPlateNumber(){
        return plateNumber;
    }

}
