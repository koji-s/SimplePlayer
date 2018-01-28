package com.example.android.simpleplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class FileViewActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    String[] mFileNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_view);

        // set ListView.
        ListView listView = (ListView) findViewById(R.id.listView1);
        listView.setOnItemClickListener(this);

        // get data for display.
        List<String> dataList = Utils.getFileList();
        mFileNames = dataList.toArray(new String[0]);

        // add data.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        for (String name : dataList) {
            adapter.add(name);
        }

        listView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ListView list = (ListView) parent;
        String name = (String) list.getItemAtPosition(position);
        Log.d("ListView", "" + position + ":" + id + ": and Name=" + name);

        // Pass the taken file name to Player using intent.
        Intent intent = new Intent(this.getApplicationContext(), Player.class);
        intent.putExtra("FileName", name);
        intent.putExtra("List", mFileNames);
        intent.putExtra("ListNo", position);

        // Activity-transition.
        startActivity(intent);

    }
}
