package timsterzel.de.soundpusher;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tim on 13.03.16.
 */
public class AdapterSounds extends RecyclerView.Adapter<AdapterSounds.SoundViewHolder> {

    private boolean m_inEditMode;

    public interface OnHandleAdapterItemSoundActions {
        void onPlaySoundOfEntry(SoundEntry soundEntry, MediaHandler.OnPlayingComplete listener);
        void onStopSoundOfEntry(SoundEntry soundEntry);
        void onDeleteSoundEntry(SoundEntry soundEntry);
    }

    private static final String TAG = AdapterSounds.class.getSimpleName();

    private Context m_context;

    private ArrayList<SoundEntry> m_soundEntries;


    public AdapterSounds(Context context, ArrayList<SoundEntry> soundEntries) {
        m_context = context;
        m_soundEntries = soundEntries;
        m_inEditMode = false;
    }

    public void setIsInEditMode(boolean inEditMode) { m_inEditMode = inEditMode; }

    public boolean isInEditMode() { return m_inEditMode; }

    @Override
    public SoundViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_sounds, parent, false);

        return new SoundViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SoundViewHolder holder, int position) {
        SoundEntry soundEntry = m_soundEntries.get(position);
        holder.m_isSoundPlaying = false;
        holder.m_inEditMode = m_inEditMode;
        holder.m_soundEntry = soundEntry;
        holder.m_txtSoundName.setText(soundEntry.getName());
        holder.m_listener = (OnHandleAdapterItemSoundActions) m_context;
        if (m_inEditMode) {
            holder.m_imageViewSound.setImageResource(R.drawable.ic_delete_128dp);
        }
        else {
            holder.m_imageViewSound.setImageResource(R.drawable.ic_play_circle_fill_128dp);
        }
    }

    @Override
    public int getItemCount() {
        return m_soundEntries.size();
    }

    public static class SoundViewHolder extends RecyclerView.ViewHolder {

        private boolean m_isSoundPlaying;

        private boolean m_inEditMode;

        private OnHandleAdapterItemSoundActions m_listener;

        private ImageView m_imageViewSound;
        private TextView m_txtSoundName;

        private SoundEntry m_soundEntry;


        public SoundViewHolder(View itemView) {
            super(itemView);
            m_imageViewSound = (ImageView) itemView.findViewById(R.id.imageViewSound);
            m_txtSoundName = (TextView) itemView.findViewById(R.id.txtSoundName);
            // Let Activity play the sound
            m_imageViewSound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (!m_inEditMode && m_listener != null && !m_isSoundPlaying) {
                        m_isSoundPlaying = true;
                        m_imageViewSound.setImageResource(R.drawable.ic_pause_circle_fill_128dp);
                        MediaHandler.OnPlayingComplete playCompleteListener = new MediaHandler.OnPlayingComplete() {
                            @Override
                            public void onPlayingComplete() {
                                m_isSoundPlaying = false;
                                m_imageViewSound.setImageResource(R.drawable.ic_play_circle_fill_128dp);
                            }
                        };
                        m_listener.onPlaySoundOfEntry(m_soundEntry, playCompleteListener);
                    }
                    else if (!m_inEditMode && m_listener != null && m_isSoundPlaying) {
                        m_isSoundPlaying = false;
                        m_imageViewSound.setImageResource(R.drawable.ic_play_circle_fill_128dp);
                        m_listener.onStopSoundOfEntry(m_soundEntry);
                    }
                    else if (m_inEditMode) {
                        m_listener.onDeleteSoundEntry(m_soundEntry);
                    }
                }
            });

        }

    }

}
