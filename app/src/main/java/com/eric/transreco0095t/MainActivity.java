package com.eric.transreco0095t;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecoDataHelper helper;
    private RecoDataAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.getItemAnimator().setRemoveDuration(1000);
        helper = new RecoDataHelper(this);
        Cursor cursor = helper.getReadableDatabase()
                .query("RECODATA", null, null, null, null, null, null);
        adapter = new RecoDataAdapter(cursor);
        recyclerView.setAdapter(adapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH)+1;
                int date = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int min = calendar.get(Calendar.MINUTE);
                int sec = calendar.get(Calendar.SECOND);

                String name =String.valueOf(year)+String.valueOf(month)+String.valueOf(date)
                        +String.valueOf(hour)+String.valueOf(min);
                String name1 =name+String.valueOf(sec);
                String hint = "TEST"+name;


                String userid = getSharedPreferences("Timi", MODE_PRIVATE)
                        .getString("USERID", null);
                RecoDataHelper helper = new RecoDataHelper(MainActivity.this);
                ContentValues values = new ContentValues();
                values.put("COL_NAME", name1);
                values.put("COL_NAME2", name);
                values.put("COL_HOUR", hour);
                values.put("COL_MIN", min);
                values.put("COL_YEAR", year);
                values.put("COL_MONTH", month);
                values.put("COL_DATE", date);
                values.put("COL_TEXT", hint);

                helper.getWritableDatabase().insert("RECODATA", null, values);
                Intent toedit = new Intent(MainActivity.this, DataEditActivity.class);
                toedit.putExtra("NAME", name1);
                toedit.putExtra("CODE", "new");
                toedit.putExtra("ID_NUM", 0);

                startActivity(toedit);

            }
        });
    }

    private class RecoDataAdapter  extends RecyclerView.Adapter<RecoDataAdapter.RecoDataHolder>implements View.OnClickListener {
        Cursor cursor;

        public RecoDataAdapter(Cursor cursor) {
            this.cursor=cursor;
        }

        @NonNull
        @Override
        public RecoDataAdapter.RecoDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_rdata,parent,false);
            return new RecoDataHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecoDataAdapter.RecoDataHolder holder, final int position) {
            cursor.moveToPosition(position);
            final String name = cursor.getString(cursor.getColumnIndex("COL_NAME"));
            final String name2 = cursor.getString(cursor.getColumnIndex("COL_NAME2"));
            final int id_num = cursor.getInt(cursor.getColumnIndex("COL_id"));
//            int hr = cursor.getInt(cursor.getColumnIndex("COL_HOUR"));
//            int min = cursor.getInt(cursor.getColumnIndex("COL_MIN"));
//            int year = cursor.getInt(cursor.getColumnIndex("COL_YEAR"));
//            int month = cursor.getInt(cursor.getColumnIndex("COL_MONTH"));
//            int date = cursor.getInt(cursor.getColumnIndex("COL_DATE"));
            final String text = cursor.getString(cursor.getColumnIndex("COL_TEXT"));

            holder.nametext.setText(name2);
//            holder.hrtext.setText(valueOf(hr));
//            holder.mintext.setText(valueOf(min));
//            holder.yeartext.setText(valueOf(year));
//            holder.monthtext.setText(valueOf(month));
//            holder.datetext.setText(valueOf(date));
//            holder.imageView.setImageDrawable(rb);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toedit = new Intent(MainActivity.this, DataEditActivity.class);
                    toedit.putExtra("NAME", name);
                    toedit.putExtra("ID_NUM", id_num);
                    toedit.putExtra("CODE", "edit");
                    startActivity(toedit);
                }});

            holder.aDel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    cursor.moveToPosition(position);//沒定址會刪到最下面的資料
                    String id_num = cursor.getString(cursor.getColumnIndex("COL_id"));
                    ContentValues values = new ContentValues();
//                    helper.getWritableDatabase().update("RECODATA", values, "COL_NAME =?",new String[]{name});
                    long check = helper.getWritableDatabase().delete(
                            "RECODATA", "COL_id =?",new String[]{id_num});//num->string
                    //  重整畫面
                    startActivity(new Intent(MainActivity.this, MainActivity.class));


                }
            });
        }

        @Override
        public int getItemCount() {
            return cursor.getCount();
        }

        @Override
        public void onClick(View v) {

        }

        public class RecoDataHolder extends RecyclerView.ViewHolder {
            TextView nametext;
//            TextView hrtext;
//            TextView mintext;
//            TextView yeartext;
//            TextView monthtext;
//            TextView datetext;

            ImageView aDel;
            public RecoDataHolder(View itemView) {
                super(itemView);
                nametext = itemView.findViewById(R.id.tv_name);
//                hrtext = itemView.findViewById(R.id.tv_hour);
//                mintext = itemView.findViewById(R.id.tv_min);
//                yeartext = itemView.findViewById(R.id.tv_yy);
//                monthtext = itemView.findViewById(R.id.tv_mm);
//                datetext = itemView.findViewById(R.id.tv_dd);
                aDel = itemView.findViewById(R.id.del);

            }
        }
    }
}
