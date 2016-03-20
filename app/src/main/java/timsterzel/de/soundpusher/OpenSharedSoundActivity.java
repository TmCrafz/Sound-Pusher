package timsterzel.de.soundpusher;

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

        m_imageViewPlaySound = (ImageView) findViewById(R.id.imageViewPlaySound);
        m_edTxtRecordName = (EditText) findViewById(R.id.edTxtRecordName);
        m_btnSave = (Button) findViewById(R.id.btnSave);
        m_btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveSound();
                    Toast.makeText(getApplicationContext(), getString(R.string.txt_savedFile), Toast.LENGTH_LONG).show();

                    finish();
                } catch (IOException e) {
                    Log.e(TAG, "Error by saving sound", e);
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_addSharedFile), Snackbar.LENGTH_LONG).show();
                }
            }
        });
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

    private boolean saveSound() throws IOException {
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
        File file = null;
        if (openMode == OpenMode.REGULAR) {
            file = new File(uri.toString());
            Log.d(TAG, "OpenMode Regular File.getName(): " + file.getName() );
            FileHandler.moveFileToSoundPath(file, file.getName());
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

            file = new File(FileHandler.getSoundPath() + "/" + fileName + "." + fileExtension);
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int len = 0;
            while ( (len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.close();
            inputStream.close();
        }

        // Create new Sound entry
        DataHandlerDB dataHandlerDB = new DataHandlerDB(this);
        SoundEntry soundEntry = new SoundEntry(0, file.getAbsolutePath(), false, null, name, false);
        dataHandlerDB.addSoundEntry(soundEntry);

        return true;
    }

}
