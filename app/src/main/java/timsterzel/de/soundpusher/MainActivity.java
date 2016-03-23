package timsterzel.de.soundpusher;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements
        ActionMode.Callback,
        DialogFragmentNewRecordEntry.OnNewRecordEntryCreatedListener,
        AdapterSounds.OnHandleAdapterItemSoundActions {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ActionMode m_actionMode;

    private RecyclerView m_recyclerView;

    private /*RecyclerView.LayoutManager*/ GridLayoutManager m_layoutManager;

    private AdapterSounds m_adapterSounds;

    private DataHandlerDB m_dataHandlerDB;

    private ArrayList<SoundEntry> m_soundEntries;

    private MediaHandler m_mediaHandler;

    // The position of the item on which the user clicked long to get the context menu
    private int m_contextItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FileHandler.createSoundFilePathIfNotExists(this);

        m_recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        m_recyclerView.setHasFixedSize(true);
        //m_layoutManager = new LinearLayoutManager(this);
        m_layoutManager = new GridLayoutManager(this, 2);


        m_recyclerView.setLayoutManager(m_layoutManager);

        m_dataHandlerDB = new DataHandlerDB(this);
        loadSoundEntries();

        registerForContextMenu(m_recyclerView);

        m_mediaHandler = new MediaHandler(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                DialogFragmentNewRecordEntry dialog = DialogFragmentNewRecordEntry.newInstance();
                dialog.show(fragmentManager, DialogFragmentNewRecordEntry.TAG_SHOWN);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
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

        if (id == R.id.action_settings) {
            // Tmp
            Intent intent = new Intent(MainActivity.this, OpenSharedSoundActivity.class);
            startActivity(intent);
            return true;
        }
        /*
        if (id == R.id.action_deleteAll) {
            m_dataHandlerDB.deleteTable(DataHandlerDB.SOUND_TABLE);
            loadSoundEntries();
            return true;

        }*/
        if (id == R.id.action_zoom_out_col) {
            //m_layoutManager.add
            int columns = m_layoutManager.getSpanCount();
            if (columns < 8) {
                columns++;
                changeLayoutColumns(columns);
            }
            return true;
        }
        if (id == R.id.action_zoom_in_col) {
            //m_layoutManager.add
            int columns = m_layoutManager.getSpanCount();
            if (columns > 1) {
                columns--;
                changeLayoutColumns(columns);
            }
            return true;
        }
        if (id == R.id.action_edit) {
            startSupportActionMode(this);
            m_adapterSounds.setIsInEditMode(true);
            m_adapterSounds.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeLayoutColumns(int columns) {
        m_layoutManager.setSpanCount(columns);
        m_recyclerView.setLayoutManager(m_layoutManager);
        m_adapterSounds.notifyDataSetChanged();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share :
                SoundEntry soundEntry = m_soundEntries.get(m_contextItemPosition);
                shareSoundEntry(soundEntry);
                return true;
        }

        return false;
    }

    private void shareSoundEntry(SoundEntry soundEntry) {
        /*
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("audio/*");
        File file = new File(soundEntry.getSoundPath());
        Uri uri = Uri.fromFile(file);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (shareIntent.resolveActivity(getPackageManager()) != null)
            startActivity(shareIntent);
        else {
            View rootView = findViewById(android.R.id.content);
            Snackbar.make(rootView, getString(R.string.error_send), Snackbar.LENGTH_LONG).show();
        } */

        // Sharing files from internal storage:
        // http://developer.android.com/training/secure-file-sharing/setup-sharing.html
        // http://developer.android.com/training/secure-file-sharing/share-file.html
        File file = new File(soundEntry.getSoundPath());
        Uri uri = FileProvider.getUriForFile(MainActivity.this, "timsterzel.de.soundpusher.fileprovider", file);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("audio/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (shareIntent.resolveActivity(getPackageManager()) != null)
            startActivity(shareIntent);
        else {
            View rootView = findViewById(android.R.id.content);
            Snackbar.make(rootView, getString(R.string.error_send), Snackbar.LENGTH_LONG).show();
        }

    }


    @Override
    public void onNewRecordEntryCreated(SoundEntry soundEntry) {
        m_soundEntries.add(soundEntry);
        m_adapterSounds.notifyItemInserted(m_soundEntries.indexOf(soundEntry));
    }

    @Override
    public void onPlaySoundOfEntry(SoundEntry soundEntry, MediaHandler.OnPlayingComplete listener) {
        m_mediaHandler.startPlaying(soundEntry.getSoundPath(), listener);
    }

    @Override
    public void onStopSoundOfEntry(SoundEntry soundEntry) {
        m_mediaHandler.stopPlaying();
    }

    @Override
    public void onDeleteSoundEntry(SoundEntry soundEntry) {
        int pos = m_soundEntries.indexOf(soundEntry);
        m_soundEntries.remove(pos);
        FileHandler.delete(soundEntry.getSoundPath());
        if (soundEntry.hasPicture()) {
            FileHandler.delete(soundEntry.getPicturePath());
        }
        m_dataHandlerDB.deleteSoundEntry(soundEntry.getID());
        m_adapterSounds.notifyItemRemoved(pos);
    }

    @Override
    public void onLongClickedItem(int position) {
        m_contextItemPosition = position;
    }


}
