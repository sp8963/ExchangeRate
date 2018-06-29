package com.example.pingliu.exchangerate;

import java.io.BufferedReader;

import java.io.InputStreamReader;

import java.net.HttpURLConnection;

import java.net.URL;

import java.util.ArrayList;

import java.util.List;


import android.app.Activity;

import android.app.AlertDialog;

import android.os.Bundle;

import android.os.StrictMode;
import android.util.Log;

import android.view.View;

import android.widget.AdapterView;

import android.widget.Button;

import android.widget.Spinner;

import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class MainActivity extends Activity {

    Spinner s;

    Button button;



    String WebURL = "https://rate.bot.com.tw/xrt?Lang=zh-TW";

    List<String> buy_cash;

    List<String> sell_cash;

    List<String> buy_current_rate;

    List<String> sell_current_rate;

    int itemIndex = 0;


    @Override

    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
//HTTP調用
        setContentView(R.layout.activity_main);

        // 2.連結

        s = (Spinner) findViewById(R.id.spinner);

        button = (Button) findViewById(R.id.button);



        buy_cash = new ArrayList<String>();

        sell_cash = new ArrayList<String>();

        buy_current_rate = new ArrayList<String>();

        sell_current_rate = new ArrayList<String>();

        String urlData = null;

        urlData = GetURLData();

        Parser(urlData);

        //3.事件1

        s.setOnItemSelectedListener(new OnItemSelectedListener() {


            @Override

            public void onItemSelected(AdapterView<?> arg0, View arg1,

                                       int arg2, long arg3) {

                // TODO Auto-generated method stub

                itemIndex = arg2;

            }


            @Override

            public void onNothingSelected(AdapterView<?> arg0) {

                // TODO Auto-generated method stub


            }

        });

        //4.事件2

        button.setOnClickListener(new Button.OnClickListener() {


            @Override

            public void onClick(View v) {

                // TODO Auto-generated method stub

                new AlertDialog.Builder(MainActivity.this)

                        .setTitle("選擇匯率為：")

                        .setMessage(

                                "現金匯率：\n買入=" + buy_cash.get(itemIndex)

                                        + "\n賣出=" + sell_cash.get(itemIndex)

                                        + "\n\n即期匯率：\n買入="

                                        + buy_current_rate.get(itemIndex)

                                        + "\n賣出="

                                        + sell_current_rate.get(itemIndex))

                        .setPositiveButton("確定", null).show();

            }

        });

    }

    //讀取所有的網頁HTML原始碼並回傳結果

    public String GetURLData() {

        String urlData = null;

        String decodedString;

        try {

            //建立連線物件

            HttpURLConnection hc = null;

            //建立網址物件

            URL url = new URL(WebURL);

            //連線

            hc = (HttpURLConnection) url.openConnection();

            //hc.setRequestMethod("GET");

            hc.setDoInput(true);

            hc.setDoOutput(true);

            hc.connect();

            //用BufferedReader讀回來

            BufferedReader in = new BufferedReader(new InputStreamReader(

                    hc.getInputStream()));

            while ((decodedString = in.readLine()) != null) {

                urlData += decodedString;

            }

            in.close();

        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.toString() + "連網路撈資料步驟錯誤", Toast.LENGTH_LONG).show();
            Log.e("Error", e.toString());
        }

        return urlData;

    }


    // 傳進來的網頁字串用indexOf比對我們要的資料的位置在哪,再利用substring得到我們要的字串資料
    void Parser(String urlData) {
        if (urlData == null) {
            Toast.makeText(MainActivity.this, "資料為空", Toast.LENGTH_SHORT).show();
        } else {
            try {
                String temp = null;
                int start = 0;
                int end = 0;
                int count = 0;
                do {
                    // 1.現金買入
                    // indexOf中的html碼要到該網頁去擷取這段html,知道我們要的頭和尾
                    start = urlData.indexOf("<td data-table=\"本行現金買入\" class=\"rate-content-cash text-right print_hide\">",
                            end + 1);
                    end = urlData.indexOf("</td>", start + 1);
                    temp = urlData.substring(start + 72, end);
                    //如果無資料
                    if (!temp.equals("-"))
                        buy_cash.add(temp);
                     else
                        buy_cash.add("無資料");

                    // 2.現金賣出
                    start = urlData.indexOf("<td data-table=\"本行現金賣出\" class=\"rate-content-cash text-right print_hide\">",
                            end + 1);
                    end = urlData.indexOf("</td>", start + 1);
                    temp = urlData.substring(start + 72, end);
                    if (!temp.equals("-"))
                        sell_cash.add(temp);
                     else
                        sell_cash.add("無資料");

                    // 3.即期買入
                    start = urlData.indexOf(
                            "<td data-table=\"本行即期買入\" class=\"rate-content-sight text-right print_hide\" data-hide=\"phone\">",
                            end + 1);
                    end = urlData.indexOf("</td>", start + 1);
                    temp = urlData.substring(start + 91, end);
                    if (!temp.equals("-"))
                        buy_current_rate.add(temp);
                     else
                        buy_current_rate.add("無資料");

                    //4. 即期賣出
                    start = urlData.indexOf(
                            "<td data-table=\"本行即期賣出\" class=\"rate-content-sight text-right print_hide\" data-hide=\"phone\">",
                            end + 1);
                    end = urlData.indexOf("</td>", start + 1);
                    temp = urlData.substring(start + 91, end);
                    if (!temp.equals("-"))
                        sell_current_rate.add(temp);
                     else
                        sell_current_rate.add("無資料");

                    count++;
                } while (count < 19);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, e.toString() + "字串切割步驟錯誤", Toast.LENGTH_LONG).show();
            }
        }
    }


}