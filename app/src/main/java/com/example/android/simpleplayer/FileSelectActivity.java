package com.example.android.simpleplayer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.List;

public class FileSelectActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    String[] mFileNames;

    String mCurrentDirectory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_view);

        // set root dir.
        mCurrentDirectory = Environment.getExternalStorageDirectory().getPath();

        // set ListView.
        ListView listView = (ListView) findViewById(R.id.listView1);
        listView.setOnItemClickListener(this);

        // get data for display.
        List<String> dataList = Utils.getDirList(mCurrentDirectory);
        mFileNames = dataList.toArray(new String[0]);

        // add data.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
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

        // check dir.
        File file = new File(name);

        if (file != null && file.isDirectory()) {
            // into dir-tier.
            mCurrentDirectory = mCurrentDirectory + "/" + name;

            List<String> dataList = Utils.getDirList(mCurrentDirectory);
            mFileNames = dataList.toArray(new String[0]);

            // add data.
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
            for (String dirName : dataList) {
                adapter.add(dirName);
            }

            list.setAdapter(adapter);

        } else {

            // intent getted file-name to Player.
            Intent intent = new Intent(this.getApplicationContext(), Player.class);
            intent.putExtra("FileName", name);
            intent.putExtra("List", mFileNames);
            intent.putExtra("ListNo", position);

            // Activity-transition.
            startActivity(intent);
        }
    }
}
