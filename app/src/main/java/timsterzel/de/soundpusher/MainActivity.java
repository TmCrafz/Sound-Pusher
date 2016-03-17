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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements
        DialogFragmentNewRecordEntry.OnNewRecordEntryCreatedListener,
        AdapterSounds.OnPlaySoundOfEntries {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView m_recyclerView;

    private RecyclerView.LayoutManager m_layoutManager;

    private AdapterSounds m_adapterSounds;

    private DataHandlerDB m_dataHandlerDB;

    private ArrayList<SoundEntry> m_soundEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SoundFileHandler.createSoundFilePathIfNotExists();

        m_recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        m_recyclerView.setHasFixedSize(true);
        //m_layoutManager = new LinearLayoutManager(this);
        m_layoutManager = new GridLayoutManager(this, 2);
        m_recyclerView.setLayoutManager(m_layoutManager);

        m_dataHandlerDB = new DataHandlerDB(this);
        m_soundEntries = m_dataHandlerDB.getAllSoundEntries();
        m_adapterSounds = new AdapterSounds(this, m_soundEntries);
        m_recyclerView.setAdapter(m_adapterSounds);

        Log.d(TAG, "SoundEntries size: " + m_soundEntries.size());

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

    @Override
    public void onPlaySoundOfEntries(SoundEntry soundEntry) {

    }
}
