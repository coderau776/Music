package com.android.example.music;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    String allSongs[];
    ListView songsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        songsList = findViewById(R.id.list);
        storagePermission();
    }

    private void storagePermission()
    {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                        displaySongs();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {

                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private ArrayList<File> getSongNames(File file)
    {
        ArrayList<File> files = new ArrayList<>();
        //Toast.makeText(getApplicationContext(),file.getName(),Toast.LENGTH_SHORT).show();
        File[] f = file.listFiles();
        //Toast.makeText(getApplicationContext(),String.valueOf(a),Toast.LENGTH_SHORT).show();
        if(f!=null)
        {

            for (File f1:f)
            {
                files.addAll(getSongNames(f1));
            }
        }
        else
        {
            if(file.getName().endsWith(".mp3")||file.getName().endsWith(".aac")||file.getName().endsWith(".wav")||file.getName().endsWith(".wma"))
            {
                files.add(file);
            }
        }
        return files;
    }

    private void displaySongs()
    {
        Toast.makeText(getApplicationContext(),Environment.getExternalStorageDirectory().getName(),Toast.LENGTH_SHORT).show();
        final ArrayList<File> audioSongs = getSongNames(Environment.getExternalStorageDirectory());
        allSongs = new String[audioSongs.size()];

        if(audioSongs.size()==0)
        {
            //Toast.makeText(getApplicationContext(),"Nothing to display",Toast.LENGTH_SHORT).show();
        }

        for (int i=0;i<allSongs.length;i++)
        {
            allSongs[i]=audioSongs.get(i).getName();
//            Log.i("songName:",allSongs[i]);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,allSongs);
        songsList.setAdapter(arrayAdapter);

        songsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String song = allSongs[position];
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("songName",song);
                intent.putExtra("songs",audioSongs);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
    }
}
