package timsterzel.de.soundpusher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by tim on 13.03.16.
 */
public class DialogFragmentNewRecordEntry extends DialogFragment {

    private static final String TAG = DialogFragmentNewRecordEntry.class.getSimpleName();

    public static final String TAG_SHOWN = "DialogFragmentNewRecordEntry";

    private MediaButton m_btnRecord;

    private MediaButton m_btnPlay;

    private MediaButton m_btnSave;

    private MediaHandler m_mediaHandler;



    public interface OnNewRecordEntryCreatedListener {
        void onNewRecordEntryCreated();
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

        m_mediaHandler = new MediaHandler(getActivity());
        m_mediaHandler.setOnPlayingCompleteListener(new MediaHandler.OnPlayingComplete() {
            @Override
            public void onPlayingComplete() {
                Log.d(TAG, "Playing completed listener");
                // If playback is completed, user can replay the sound or record a new one
                m_btnRecord.setEnabled(true);
                m_btnPlay.setActive(false);
            }
        });

        m_btnRecord = (MediaButton) view.findViewById(R.id.btnRecord);
        m_btnPlay = (MediaButton) view.findViewById(R.id.btnPlay);
        m_btnSave = (MediaButton) view.findViewById(R.id.btnSave);

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
                }
                else {
                    m_btnPlay.setActive(false);
                }

            }
        });

        m_btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });
        // User can only save record if something was recorded
        m_btnSave.setEnabled(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setView(view);
        builder.setTitle("");
        return builder.create();
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
