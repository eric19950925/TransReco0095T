package com.eric.transreco0095t;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;



public class DataEditActivity extends AppCompatActivity  {
    String name,text_tw;
    String edname;
    String edcode;
    int edidnum;
    TextView tv_text;
    Button btn_back;
    private String TAG = DataEditActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 1234;
    private TextView tv_texten;
    private TextView tv_textjp;

    private ImageView iv_deit;
    private RecoDataHelper helper;
    private Cursor cursor;
    private String file_name;
    private String tw_en_text;
    private String tw_jp_text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_edit_activity);
        tv_text=findViewById(R.id.tv_tw2);
        tv_texten=findViewById(R.id.tv_en2);
        tv_textjp=findViewById(R.id.tv_jp2);
        btn_back=findViewById(R.id.btn_back);
        iv_deit = findViewById(R.id.edit);
        Intent intent = getIntent();
        edname = intent.getStringExtra("NAME");
        edidnum = intent.getIntExtra("ID_NUM",0);
        edcode = intent.getStringExtra("CODE");
//        Log.d(TAG, "text:"+edtext);
//        EditText tv_tw02 = findViewById(R.id.tv_tw2);
//        tv_tw02.setText(edtext);

        helper = new RecoDataHelper(this);
        cursor = helper.getReadableDatabase()
                .query("RECODATA", null, "COL_NAME =?", new String[]{edname}, null, null, null);
        if (cursor.moveToNext()) {
            file_name = cursor.getString(cursor.getColumnIndex("COL_NAME2"));
        }
        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        getSupportActionBar().setTitle(file_name); // set the top title

        if(edcode.equals("new")){
            Intent intentreco = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intentreco.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            try{
                startActivityForResult(intentreco, REQUEST_CODE);
            }catch (ActivityNotFoundException e){}

        }
        else{

            try {
                helper = new RecoDataHelper(this);
                cursor = helper.getReadableDatabase()
                        .query("RECODATA", null, "COL_NAME =?", new String[]{edname}, null, null, null);

                if (cursor.moveToNext()) {
                    String text = cursor.getString(cursor.getColumnIndex("COL_TEXT"));
                    tv_text.setText(text);
                    try {
                        TransText(tv_text.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                cursor.close();
            }


        }

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecoDataHelper helper = new RecoDataHelper(DataEditActivity.this);
                ContentValues values = new ContentValues();
//                values.put("COL_NAME", name);
////                values.put("COL_HOUR", hour);
////                values.put("COL_MIN", min);
////                values.put("COL_YEAR", year);
////                values.put("COL_MONTH", month);
////                values.put("COL_DATE", date);
                values.put("COL_TEXT", tv_text.getText().toString());

                helper.getWritableDatabase().update(
                "RECODATA", values, "COL_NAME =?",new String[]{edname});
                Intent tomain = new Intent(DataEditActivity.this, MainActivity.class);
//                toedit.putExtra("NAME", name);
//                toedit.putExtra("CODE", "new");

                startActivity(tomain);
            }
        });

        iv_deit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentreco = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intentreco.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                try{
                    startActivityForResult(intentreco, REQUEST_CODE);
                }catch (ActivityNotFoundException e){}
            }
        });
}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tv_text.setText(result.get(0));
                  try {
                        TransText(tv_text.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

    }

    private void TransText(String s) throws Exception {
        String UTF8_tw = URLEncoder.encode(s, "UTF-8");
            final String url = "https://translate.google.com/m?hl=en&sl=zh-TW&tl=en&ie=UTF-8&prev=_m&q="+UTF8_tw ;
            final String url2 ="https://translate.google.com/m?hl=en&sl=zh-TW&tl=ja&ie=UTF-8&prev=_m&q="+UTF8_tw ;
//            String url = "https://www.google.com.tw/";
//            String url2 ="https://www.google.com.tw/";

        Thread thread = new Thread(){
            public void run(){

                Document doc = null;
                Document doc2 = null;
                try {
                    doc = Jsoup.connect(url).timeout(5000).get();
                    doc2 = Jsoup.connect(url2).timeout(5000).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Element tw_en = doc.select("div.t0").first();
                Element tw_jp = doc2.select("div.t0").first();
                tw_en_text = tw_en.text();
                tw_jp_text = tw_jp.text();
//                Log.d(TAG, "TransText: "+ tw_en_text);
                tv_texten.setText(tw_en_text);
                tv_textjp.setText(tw_jp_text);
            }
        };
        thread.start();
//        WebView webview_en = (WebView) findViewById(R.id.wb_en);
//        WebSettings webSettings_en = webview_en.getSettings();
//        webSettings_en.setJavaScriptEnabled(true);
//        setContentView(webview_en);
//        webview_en.setWebViewClient(new WebViewClient());
//        webview_en.loadUrl(url);
/*
        try {
            Document doc = Jsoup.connect(url).get();
            System.out.println(doc.title());
            Elements h1s = doc.select("div.t0");

            Element thisOne = null;
            for(Iterator it = h1s.iterator(); it.hasNext();)
            {
                thisOne = (Element)it.next();
//                System.out.println(thisOne.html());
                Log.d(TAG, "TransText: "+thisOne.html());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
//        WebView webview_jp = (WebView) findViewById(R.id.wb_jp);
//        WebSettings webSettings_jp = webview_jp.getSettings();
//        webSettings_jp.setJavaScriptEnabled(true);
//        setContentView(webview_jp);
//        webview_jp.setWebViewClient(new WebViewClient());
//        webview_jp.loadUrl(url2);
    }
/*
    private String parseResult(String inputJson) throws Exception{
        JSONArray jsonArray = new JSONArray(inputJson);
            JSONArray jsonArray2 = (JSONArray) jsonArray.get(0);
//        JSONArray jsonArray3 = (JSONArray) jsonArray2.get(0);
            String result = "";

            for (int i = 0; i < jsonArray2.length(); i++) {
                result += ((JSONArray) jsonArray2.get(i)).get(0).toString();
            }
        Log.d(TAG, "parseResult: "+result);
            return result;
    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    public void edit_name(MenuItem mi) {
        final EditText ed_title = new EditText(DataEditActivity.this);
        new AlertDialog.Builder(DataEditActivity.this).setTitle("修改檔案名稱:")
                .setView(ed_title)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String RecoTitle = ed_title.getText().toString();
                        RecoDataHelper helper = new RecoDataHelper(DataEditActivity.this);
                        ContentValues values = new ContentValues();
//                values.put("COL_NAME", name);
////                values.put("COL_HOUR", hour);
////                values.put("COL_MIN", min);
////                values.put("COL_YEAR", year);
////                values.put("COL_MONTH", month);
////                values.put("COL_DATE", date);
                        values.put("COL_NAME2", RecoTitle);

                        helper.getWritableDatabase().update(
                                "RECODATA", values, "COL_NAME =?",new String[]{edname});
                        Intent toedit = new Intent(DataEditActivity.this, DataEditActivity.class);
                        toedit.putExtra("NAME", edname);
                        toedit.putExtra("CODE", "edit");

                        startActivity(toedit);

                    }
                }).setNeutralButton("取消", null).show();

    }
}

