package timsterzel.de.soundpusher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.IOException;

/**
 * Created by tim on 13.03.16.
 */
public class DialogFragmentNewRecordEntry extends DialogFragment {

    private static final String TAG = DialogFragmentNewRecordEntry.class.getSimpleName();

    public static final String TAG_SHOWN = "DialogFragmentNewRecordEntry";

    private MediaButton m_btnRecord;

    private MediaButton m_btnPlay;

    private MediaButton m_btnSave;


    private MediaRecorder m_recorder;

    private MediaPlayer m_player;

    private String m_recordPath;

    private boolean m_recording;

    private boolean m_playing;


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

        m_recordPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RecordSounds";

        m_btnRecord = (MediaButton) view.findViewById(R.id.btnRecord);
        m_btnPlay = (MediaButton) view.findViewById(R.id.btnPlay);
        m_btnSave = (MediaButton) view.findViewById(R.id.btnSave);

        m_btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasMicro()) {
                    onRecord(!m_recording);
                    if (m_recording) {
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
                onPlay(!m_playing);
                if (m_playing) {
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
            startRecording();
            // if something is recorded user cant play something
            m_btnPlay.setEnabled(false);
            // User can now save record while it is recording
            m_btnSave.setEnabled(false);
        }
        else {
            stopRecording();
            // If recording is finished, user can play sound
            m_btnPlay.setEnabled(true);
            // User can save the sound now
            m_btnSave.setEnabled(true);
        }
    }

    private boolean hasMicro() {
        PackageManager packageManager = getActivity().getPackageManager();
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
            // If something is played, user can not record something
            m_btnRecord.setEnabled(false);
        }
        else {
            stopPlaying();
            // If playback is completed, user can replay the sound or record a new one
            m_btnRecord.setEnabled(true);
            m_btnPlay.setActive(false);
        }
    }

    private void startPlaying() {
        m_player = new MediaPlayer();
        m_player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // If playback is completed, user can replay the sound or record a new one
                m_btnRecord.setEnabled(true);
                m_btnPlay.setActive(false);
            }
        });
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


}
