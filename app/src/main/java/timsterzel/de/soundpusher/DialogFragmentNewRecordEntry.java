package timsterzel.de.soundpusher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.io.File;

/**
 * Created by tim on 13.03.16.
 */
public class DialogFragmentNewRecordEntry extends DialogFragment {

    private static final String TAG = DialogFragmentNewRecordEntry.class.getSimpleName();

    public static final String TAG_SHOWN = "DialogFragmentNewRecordEntry";

    private RelativeLayout m_layoutMediaButtonBar;

    private MediaButton m_btnRecord;

    private MediaButton m_btnPlay;

    private MediaButton m_btnSave;

    private MediaHandler m_mediaHandler;

    private TextInputLayout m_edTxtLayRecordName;

    private EditText m_edTxtRecordName;
    // The dialog has to states, the record mode (where the user can record sounds) and a mode where the user can
    // give the sound a name and save it
    private boolean m_inRecordMode;

    private AlertDialog m_alertDialog;

    private DataHandlerDB m_dataHandlerDB;

    public interface OnNewRecordEntryCreatedListener {
        void onNewRecordEntryCreated(SoundEntry soundEntry);
    }

    private OnNewRecordEntryCreatedListener m_listener;


    public static DialogFragmentNewRecordEntry newInstance() {
        DialogFragmentNewRecordEntry fragment = new DialogFragmentNewRecordEntry();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_fragment_new_record_entry, null);

        m_dataHandlerDB = new DataHandlerDB(getActivity());

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.RecordDialogTheme);
        //builder.setTitle("Record");
        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Save entry
                String name = m_edTxtRecordName.getText().toString();
                if (name.equals("")) {
                    // if user input no name, give sound a standard name
                    name = getString(R.string.txt_newSoundNameDummy);
                }
                String soundPath = m_mediaHandler.saveRecordPermanent(name);
                Log.d(TAG, "SoundPath: " + soundPath);
                if (soundPath != null) {
                    SoundEntry soundEntry = new SoundEntry(0, soundPath, false, null, name, true);
                    m_dataHandlerDB.addSoundEntry(soundEntry);
                    m_listener.onNewRecordEntryCreated(soundEntry);
                }




                //SoundEntry soundEntry = new SoundEntry(0, )



            }
        });
        builder.setView(view);
        m_alertDialog = builder.create();

        m_mediaHandler = new MediaHandler(getActivity());
        m_mediaHandler.setOnPlayingCompleteListener(new MediaHandler.OnPlayingComplete() {
            @Override
            public void onPlayingComplete() {
                // If playback is completed, user can replay the sound or record a new one
                m_btnRecord.setEnabled(true);
                m_btnPlay.setActive(false);
            }
        });

        m_layoutMediaButtonBar = (RelativeLayout) view.findViewById(R.id.layoutMediaButtonBar);
        m_btnRecord = (MediaButton) view.findViewById(R.id.btnRecord);
        m_btnPlay = (MediaButton) view.findViewById(R.id.btnPlay);
        m_btnSave = (MediaButton) view.findViewById(R.id.btnSave);

        m_edTxtLayRecordName = (TextInputLayout) view.findViewById(R.id.edTxtLayRecordName);
        m_edTxtRecordName = (EditText) view.findViewById(R.id.edTxtRecordName);

        m_btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_mediaHandler.hasMicro()) {
                    onRecord(!m_mediaHandler.isRecording());
                    if (m_mediaHandler.isRecording()) {
                        m_btnRecord.setActive(true);
                    }
                    else {
                        m_btnRecord.setActive(false);
                    }
                }
            }
        });

        m_btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(!m_mediaHandler.isPlaying());
                if (m_mediaHandler.isPlaying()) {
                    m_btnPlay.setActive(true);
                } else {
                    m_btnPlay.setActive(false);
                }
            }
        });

        m_btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_layoutMediaButtonBar.setVisibility(View.GONE);
                m_edTxtLayRecordName.setVisibility(View.VISIBLE);
                m_inRecordMode = false;
                // Now we can show the positive button so user can save the audio
                m_alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
                //m_alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                //m_edTxtRecordName.setVisibility(View.VISIBLE);
            }
        });
        // User can only save record if something was recorded
        m_btnSave.setEnabled(false);

        m_inRecordMode = true;

        return m_alertDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        // The positive button save the recorded audio ,so it only should be shown after a audio was
        // recorded
        if (m_inRecordMode) {
            m_alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            m_listener = (OnNewRecordEntryCreatedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNewRecordEntryCreatedListener");
        }
    }

    private void onRecord(boolean start) {
        if (start) {
            m_mediaHandler.startRecording();
            // if something is recorded user cant play something
            m_btnPlay.setEnabled(false);
            // User can now save record while it is recording
            m_btnSave.setEnabled(false);
        }
        else {
            m_mediaHandler.stopRecording();
            // If recording is finished, user can play sound
            m_btnPlay.setEnabled(true);
            // User can save the sound now
            m_btnSave.setEnabled(true);
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            m_mediaHandler.startPlaying();
            // If something is played, user can not record something
            m_btnRecord.setEnabled(false);
        }
        else {
            m_mediaHandler.stopPlaying();
            // If playback is completed, user can replay the sound or record a new one
            m_btnRecord.setEnabled(true);
            m_btnPlay.setActive(false);
        }
    }




}
