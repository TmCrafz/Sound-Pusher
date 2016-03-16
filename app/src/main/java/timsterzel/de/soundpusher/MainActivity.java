package timsterzel.de.soundpusher;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity implements
        DialogFragmentNewRecordEntry.OnNewRecordEntryCreatedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView m_recyclerView;

    private RecyclerView.LayoutManager m_layoutManager;

    private Button m_btnPlay;

    private Button m_btnRecord;


    private MediaRecorder m_recorder;
    private MediaPlayer m_player;
    private String m_recordPath;

    private boolean m_recording;
    private boolean m_playing;

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        }
        else {
            stopRecording();
        }
    }

    private boolean hasMicro() {
        PackageManager packageManager = getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    private void startRecording() {
        m_recorder = new MediaRecorder();
        m_recorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        m_recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        m_recorder.setOutputFile(m_recordPath + "/TestRecord.3gp");
        //m_recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        m_recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            m_recorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "Record prepare failed: ", e);
        }
        m_recorder.start();
        m_recording = true;
    }

    private void stopRecording() {
        m_recorder.stop();
        m_recorder.release();
        m_recorder = null;
        m_recording = false;
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        }
        else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        m_player = new MediaPlayer();
        try {
            m_player.setDataSource(m_recordPath + "/TestRecord.3gp");
            //m_player.setVolume(50.f, 50.f);
            m_player.prepare();
            m_player.start();
        } catch (IOException e) {
            Log.e(TAG, "Play prepare failed: ", e);
        }
        m_playing = true;
    }

    private void stopPlaying() {
        m_player.release();
        m_player = null;
        m_playing = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        m_recordPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RecordSounds";
        File file = new File(m_recordPath);
        if (!file.exists()) {
            file.mkdir();
        }


        m_recording = false;
        m_playing = false;

        m_recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        m_btnPlay = (Button) findViewById(R.id.btnPlay);
        m_btnRecord = (Button) findViewById(R.id.btnRecord);

        m_btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasMicro()) {
                    onRecord(!m_recording);
                    if (m_recording) {
                        m_btnRecord.setText("Stop recording");
                    }
                    else {
                        m_btnRecord.setText("Record");
                    }
                }
            }
        });

        m_btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(!m_playing);
                if (m_playing) {
                    m_btnPlay.setText("Stop playing");
                }
                else {
                    m_btnPlay.setText("Play");
                }
            }
        });


        m_recyclerView.setHasFixedSize(true);

        m_layoutManager = new GridLayoutManager(this, 2);
        m_recyclerView.setLayoutManager(m_layoutManager);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                DialogFragmentNewRecordEntry dialog = DialogFragmentNewRecordEntry.newInstance();
                dialog.show(fragmentManager, DialogFragmentNewRecordEntry.TAG_SHOWN);


                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onNewRecordEntryCreated(SoundEntry soundEntry) {

    }
}
