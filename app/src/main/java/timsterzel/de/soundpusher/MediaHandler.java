package timsterzel.de.soundpusher;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by tim on 15.03.16.
 */
public class MediaHandler {

    private static final String TAG = MediaHandler.class.getSimpleName();

    private Context m_context;

    private boolean m_recording;

    private boolean m_playing;

    private OnPlayingComplete m_playCompleteListener;

    private MediaRecorder m_recorder;

    private MediaPlayer m_player;

    /*
    // The path where the sounds finally are stored
    private String m_recordPath;
    */
    // Before sounds get saved by user there are stored in a tmp folder
    private String m_tmpRecordPath;

    private String m_fileExtension;
    // The name of the recors before the user choose one
    private String m_tmpAudioName;

    interface OnPlayingComplete {
        void onPlayingComplete();
    }

    public MediaHandler(Context context) {
        m_context = context;
        //m_recordPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RecordSounds";
        m_tmpRecordPath = context.getCacheDir().getAbsolutePath();
        Log.d(TAG, "m_tmpDirectoryPath: " + m_tmpRecordPath);
        m_tmpAudioName = "sound_tmp_1";
        m_fileExtension = ".aac";
    }

    //public void setOnPlayingCompleteListener(OnPlayingComplete listener) { m_onPlayingCompleteListener = listener; }

    public boolean isRecording() { return m_recording; }

    public boolean isPlaying() { return m_playing; }

    public boolean hasMicro() {
        PackageManager packageManager = m_context.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    public String getTmpFilePath() { return m_tmpRecordPath + "/" + m_tmpAudioName + m_fileExtension; }

    public void startRecording() {
        if (m_recorder != null && m_recording) {
            stopRecording();
        }
        m_recorder = new MediaRecorder();
        m_recorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        //m_recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        m_recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);

        m_recorder.setOutputFile(getTmpFilePath());
        //m_recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        m_recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //m_recorder.setAudioChannels(2);

        try {
            m_recorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "Record prepare failed: ", e);
        }
        m_recorder.start();
        m_recording = true;

    }

    public void stopRecording() {
        if (m_recording) {
            m_recorder.stop();
            m_recorder.release();
            m_recorder = null;
            m_recording = false;
        }
    }

    public void startPlaying(String path, final OnPlayingComplete listener) {
        // Only one media should played at the same time
        if (m_player != null && m_player.isPlaying()) {
            stopPlaying();
        }
        // It is important that the new listener overwrite the old after stopPlaying() is called,
        // because stopPlaying call the listener from the actual playing sound
        m_playCompleteListener = listener;
        m_player = new MediaPlayer();
        m_player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (listener != null) {
                    listener.onPlayingComplete();
                }
            }
        });
        //m_player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            m_player.setDataSource(path);
            //m_player.setVolume(50.f, 50.f);
            m_player.prepare();
            m_player.start();
        } catch (IOException e) {
            Log.e(TAG, "Play prepare failed: ", e);
        }
        m_playing = true;

    }

    public void stopPlaying() {
        if (m_playing) {
            m_player.release();
            m_player = null;
            m_playing = false;
            if (m_playCompleteListener != null) {
                m_playCompleteListener.onPlayingComplete();
            }

        }
    }

    public String saveRecordPermanent(String name) {
        name += m_fileExtension;
        String fileName = FileHandler.createLegalFilename(name);
        File fileTmp = new File(getTmpFilePath());
        File fileNew = FileHandler.moveFileTo(fileTmp, FileHandler.getSoundPath() + "/" + fileName);
        return fileNew.getAbsolutePath();
    }

}
