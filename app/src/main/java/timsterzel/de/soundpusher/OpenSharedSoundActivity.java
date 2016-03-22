package timsterzel.de.soundpusher;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class OpenSharedSoundActivity extends AppCompatActivity {

    private static final String TAG = OpenSharedSoundActivity.class.getSimpleName();

    private enum OpenMode {
        REGULAR, ATTACHMENT
    }


    private File m_fileSound;
    private MediaHandler m_mediaHandler;
    private boolean m_soundLoadingSucces;
    private boolean m_sounSavingSucces;


    private ProgressDialog m_progressDialogLoad;
    private ProgressDialog m_progressDialogSave;
    private ImageView m_imageViewPlaySound;
    private EditText m_edTxtRecordName;
    private Button m_btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_shared_sound);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        m_mediaHandler = new MediaHandler(this);

        m_imageViewPlaySound = (ImageView) findViewById(R.id.imageViewPlaySound);
        m_edTxtRecordName = (EditText) findViewById(R.id.edTxtRecordName);
        m_btnSave = (Button) findViewById(R.id.btnSave);

        m_imageViewPlaySound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_imageViewPlaySound.setImageResource(R.drawable.ic_pause_circle_fill_128dp);
                if (!m_mediaHandler.isPlaying()) {
                    Log.d(TAG, "SOUND TEST file sound absolute path: " + m_fileSound.getAbsolutePath());
                    m_mediaHandler.startPlaying(m_fileSound.getAbsolutePath(), new MediaHandler.OnPlayingComplete() {
                        @Override
                        public void onPlayingComplete() {
                            m_imageViewPlaySound.setImageResource(R.drawable.ic_play_circle_fill_128dp);
                        }
                    });
                }
                else {
                    m_mediaHandler.stopPlaying();

                }
            }
        });

        m_btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSavingFile();
            }
        });

        initProgressDialogs();
        startLoadingSound();
    }

    private void initProgressDialogs() {
        m_progressDialogLoad = new ProgressDialog(this);
        m_progressDialogLoad.setMessage(getString(R.string.pb_loading));
        m_progressDialogLoad.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_progressDialogLoad.setCancelable(false);

        m_progressDialogSave = new ProgressDialog(this);
        m_progressDialogSave.setMessage(getString(R.string.pb_saving));
        m_progressDialogSave.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_progressDialogSave.setCancelable(false);
    }

    private void startLoadingSound() {
        // Load sound in a thread so we can show a progressbar
        m_progressDialogLoad.show();
        Thread loadSoundThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    loadSound();
                    m_soundLoadingSucces = true;
                } catch (IOException e) {
                    m_soundLoadingSucces = false;
                    Log.e(TAG, "Error by loading sound", e);
                }
                // Close progressbar and inform user about errors by loading if there are errors
                // Have to run on UI Thread because its update the Ui
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        m_progressDialogLoad.dismiss();
                        if (!m_soundLoadingSucces) {
                            Toast.makeText(getApplicationContext(), getString(R.string.error_loadingSharedFile), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        loadSoundThread.start();
    }

    private void startSavingFile() {
        m_progressDialogSave.show();
        Thread saveSoundThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    saveSound();
                    m_sounSavingSucces = true;
                } catch (IOException e) {
                    m_sounSavingSucces = false;
                    Log.e(TAG, "Error by saving sound", e);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        m_progressDialogSave.dismiss();
                        if (!m_sounSavingSucces) {
                            Toast.makeText(getApplicationContext(), getString(R.string.error_addSharedFile), Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), getString(R.string.txt_savedFile), Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });
            }
        });
        saveSoundThread.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                                    // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean loadSound() throws IOException {
        String name = m_edTxtRecordName.getText().toString();
        name = name.trim();
        if (name.length() == 0) {
            name = getString(R.string.txt_newSoundNameDummy);
        }
        String fileName = FileHandler.createLegalFilename(name);

        Uri uri = null;
        OpenMode openMode = null;
        if (getIntent().getData().getScheme().equals("file")) {
            uri = Uri.parse(getIntent().getData().getPath());
            openMode = OpenMode.REGULAR;
        }
        else {
            uri = getIntent().getData();
            openMode = OpenMode.ATTACHMENT;
        }

        if (uri == null) {
            return false;
        }
        //File file = null;
        if (openMode == OpenMode.REGULAR) {
            m_fileSound = new File(uri.toString());
            m_fileSound = FileHandler.moveFileTo(m_fileSound, FileHandler.getTmpSoundPath() + "/" + m_fileSound.getName());
            if (m_fileSound == null)
                Log.e(TAG, "m_fileSound is null O.o");

        }
        // If the file is a attachment we have only the output stream instead of a file
        // so we have to create a file from the output stream
        else {
            InputStream inputStream = null;
            ContentResolver cr = getContentResolver();
            inputStream = cr.openInputStream(uri);
            // Get the extension of the file
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String fileExtension = mime.getExtensionFromMimeType(cr.getType(uri));

            m_fileSound = new File(FileHandler.getTmpSoundPath() + "/" + fileName + "." + fileExtension);
            FileOutputStream outputStream = new FileOutputStream(m_fileSound);

            byte[] buffer = new byte[1024];
            int len = 0;
            while ( (len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.close();
            inputStream.close();
        }
        return true;

    }

    private boolean saveSound() throws IOException {
        // Entry name is the name which is later shown in the app for this sound
        String entryName = m_edTxtRecordName.getText().toString();
        if (entryName.equals("")) {
            // if user input no name, give sound a standard name
            entryName = getString(R.string.txt_newSoundNameDummy);
        }
        // Create filename and add file extension
        String fileName = entryName + "." + FileHandler.getFileExtension(m_fileSound.getName());
        fileName = FileHandler.createLegalFilename(fileName);

        File fileNew = FileHandler.moveFileTo(m_fileSound, FileHandler.getSoundPath() + "/" + fileName);

        // Create new Sound entry
        DataHandlerDB dataHandlerDB = new DataHandlerDB(this);
        SoundEntry soundEntry = new SoundEntry(0, fileNew.getAbsolutePath(), false, null, entryName, false);
        dataHandlerDB.addSoundEntry(soundEntry);

        return true;
    }

}
