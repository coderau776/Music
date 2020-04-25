package com.android.example.music;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    private RelativeLayout relativeLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String keeper = "";

    ArrayList<File> songList;
    MediaPlayer mediaPlayer;
    int position;
    String song;

    ImageView prev;
    ImageView play;
    ImageView next;
    ImageView voice;
    TextView songName;
    Button btn;
    TextView mode;
    int flag=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        relativeLayout = findViewById(R.id.mainxml);
        prev = findViewById(R.id.prev);
        play = findViewById(R.id.play);
        next = findViewById(R.id.next);
        songName = findViewById(R.id.name);
        mode = findViewById(R.id.mode);
        btn = findViewById(R.id.voice_btn);
        voice = findViewById(R.id.vc);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        validateReceivedValuesAndStartPlaying();

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> res = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                Log.i("keeper",res.get(0));
                //Toast.makeText(getApplicationContext(),keeper,Toast.LENGTH_SHORT).show();
                if(res!=null)
                {
                    keeper = res.get(0);
                    if(keeper.equalsIgnoreCase("top")||keeper.equalsIgnoreCase("talk")||keeper.equalsIgnoreCase("stop")||keeper.equalsIgnoreCase("pause")||keeper.equalsIgnoreCase("forge")||keeper.equalsIgnoreCase("false")||keeper.equalsIgnoreCase("call"))
                    {
                        playPauseSong(0);
                        //mode.setText("paused...");
                        Toast.makeText(getApplicationContext(),keeper,Toast.LENGTH_SHORT).show();
                    }
                    else if(keeper.equalsIgnoreCase("Play"))
                    {
                        playPauseSong(1);
                        Toast.makeText(getApplicationContext(),keeper,Toast.LENGTH_SHORT).show();
                    }
                    else if(keeper.equalsIgnoreCase("night")||keeper.equalsIgnoreCase("nike")||keeper.equalsIgnoreCase("next"))
                    {
                        playNextSong(0);
                    }
                    else if(keeper.equalsIgnoreCase("previous")||keeper.equalsIgnoreCase("TVS")||keeper.equalsIgnoreCase("serious"))
                    {
                        playNextSong(1);
                    }
                    else
                    {
                        mediaPlayer.start();
                    }

                }

            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

//        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                switch (event.getAction())
//                {
//                    case MotionEvent.ACTION_DOWN:
//                        speechRecognizer.startListening(speechRecognizerIntent);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        speechRecognizer.stopListening();
//                        break;
//                }
//                return false;
//            }
//        });



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   if(flag==0)
                   {
                       flag=1;
                       btn.setText("Handle with audio");
                       mode.setText("Voice mode off");
                       play.setVisibility(View.VISIBLE);
                       prev.setVisibility(View.VISIBLE);
                       next.setVisibility(View.VISIBLE);
                       voice.setVisibility(View.INVISIBLE);
                   }
                   else
                   {
                       flag=0;
                       btn.setText("Handle Normally");
                       mode.setText("Voice mode on");
                       play.setVisibility(View.INVISIBLE);
                       prev.setVisibility(View.INVISIBLE);
                       next.setVisibility(View.INVISIBLE);
                       voice.setVisibility(View.VISIBLE);
                   }
            }
        });

        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
                speechRecognizer.startListening(speechRecognizerIntent);
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            speechRecognizer.stopListening();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                };
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPauseSong();
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextSong(1);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextSong(0);
            }
        });

    }

    private void validateReceivedValuesAndStartPlaying()
    {
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        //Log.i("bundlecheck",String.valueOf(intent.getIntExtra("position",0)));

        position = bundle.getInt("position",0);
        songList = (ArrayList) bundle.getParcelableArrayList("songs");
        song = songList.get(position).getName();
        String songNam = intent.getStringExtra("songName");

        songName.setText(songNam);
        songName.setSelected(true);

        Uri uri = Uri.parse(songList.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();

    }

    private void checkPermission()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            if (!(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED))
            {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getPackageName()));
                startActivity(intent);
                finish();
            }
    }

    private void playPauseSong(int i)
    {
        if(mediaPlayer.isPlaying() && i==0)
        {
            play.setImageResource(R.drawable.play);
            mediaPlayer.pause();
        }
        else
        {
            if(i==1 && !mediaPlayer.isPlaying())
            {
                play.setImageResource(R.drawable.pause);
                mediaPlayer.start();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"You already did it",Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void playPauseSong()
    {
        if(mediaPlayer.isPlaying())
        {
            play.setImageResource(R.drawable.play);
            mediaPlayer.pause();
        }
        else
        {
            play.setImageResource(R.drawable.pause);
            mediaPlayer.start();
        }

    }
    private void playNextSong(int i)
    {
        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.release();

        if(i==0)
        {
            position=((position+1)%songList.size());
        }
        else
        {

            if(position!=0)
                position=((position-1));
            else
                Toast.makeText(getApplicationContext(),"You are on the first song",Toast.LENGTH_SHORT).show();
        }


        Uri uri = Uri.parse(songList.get(position).toString());

        songName.setText(songList.get(position).getName());
        songName.setSelected(true);

        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();


    }

}
