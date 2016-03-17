package timsterzel.de.soundpusher;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements
        ActionMode.Callback,
        DialogFragmentNewRecordEntry.OnNewRecordEntryCreatedListener,
        AdapterSounds.OnHandleAdapterItemSoundActions {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ActionMode m_actionMode;

    private RecyclerView m_recyclerView;

    private RecyclerView.LayoutManager m_layoutManager;

    private AdapterSounds m_adapterSounds;

    private DataHandlerDB m_dataHandlerDB;

    private ArrayList<SoundEntry> m_soundEntries;

    private MediaHandler m_mediaHandler;

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
        loadSoundEntries();

        m_mediaHandler = new MediaHandler(this);

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

    private void loadSoundEntries() {
        m_soundEntries = m_dataHandlerDB.getAllSoundEntries();
        m_adapterSounds = new AdapterSounds(this, m_soundEntries);
        m_recyclerView.setAdapter(m_adapterSounds);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_main_edit, menu);
        m_actionMode = mode;
        m_actionMode.setTitle(getString(R.string.txt_actionModeMainTitle));
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        m_adapterSounds.setIsInEditMode(false);
        m_adapterSounds.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (m_actionMode != null) {
            m_actionMode.finish();
        }
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
        if (id == R.id.action_deleteAll) {
            m_dataHandlerDB.deleteTable(DataHandlerDB.SOUND_TABLE);
            loadSoundEntries();
            return true;

        }
        if (id == R.id.action_edit) {
            startSupportActionMode(this);
            m_adapterSounds.setIsInEditMode(true);
            m_adapterSounds.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onNewRecordEntryCreated(SoundEntry soundEntry) {
        m_soundEntries.add(soundEntry);
        m_adapterSounds.notifyItemInserted(m_soundEntries.indexOf(soundEntry));
    }

    @Override
    public void onPlaySoundOfEntries(SoundEntry soundEntry, MediaHandler.OnPlayingComplete listener) {
        m_mediaHandler.startPlaying(soundEntry.getSoundPath(), listener);
    }

    @Override
    public void onDeleteSoundEntry(SoundEntry soundEntry) {
        int pos = m_soundEntries.indexOf(soundEntry);
        m_soundEntries.remove(pos);
        m_dataHandlerDB.deleteSoundEntry(soundEntry.getID());
        m_adapterSounds.notifyItemRemoved(pos);
    }
}
