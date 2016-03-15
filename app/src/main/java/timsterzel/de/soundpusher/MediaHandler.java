package timsterzel.de.soundpusher;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;

/**
 * Created by tim on 15.03.16.
 */
public class MediaHandler {

    private static final String TAG = MediaHandler.class.getSimpleName();

    private Context m_context;

    private boolean m_recording;

    private boolean m_playing;

    private MediaRecorder m_recorder;

    private MediaPlayer m_player;

    private String m_recordPath;

    private OnPlayingComplete m_onPlayingCompleteListener;

    interface OnPlayingComplete {
        void onPlayingComplete();
    }


    public MediaHandler(Context context) {
        m_context = context;
        m_recordPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RecordSounds";

    }

    public void setOnPlayingCompleteListener(OnPlayingComplete listener) { m_onPlayingCompleteListener = listener; }


    public boolean isRecording() { return m_recording; }

    public boolean isPlaying() { return m_playing; }

    public boolean hasMicro() {
        PackageManager packageManager = m_context.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    public void startRecording() {
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

    public void stopRecording() {
        if (m_recording) {
            m_recorder.stop();
            m_recorder.release();
            m_recorder = null;
            m_recording = false;
        }
    }

    public void startPlaying() {
        m_player = new MediaPlayer();
        m_player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (m_onPlayingCompleteListener != null) {
                    m_onPlayingCompleteListener.onPlayingComplete();
                }
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

    public void stopPlaying() {
        if (m_playing) {
            m_player.release();
            m_player = null;
            m_playing = false;
        }
    }

}
