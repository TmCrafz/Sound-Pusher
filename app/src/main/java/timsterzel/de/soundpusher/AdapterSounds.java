package timsterzel.de.soundpusher;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
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
        void onLongClickedItem(int position);
    }

    private static final String TAG = AdapterSounds.class.getSimpleName();

    private Activity m_context;

    private ArrayList<SoundEntry> m_soundEntries;


    public AdapterSounds(Activity context, ArrayList<SoundEntry> soundEntries) {
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
        holder.m_context = m_context;
        holder.m_isSoundPlaying = false;
        holder.m_inEditMode = m_inEditMode;
        holder.m_soundEntry = soundEntry;
        holder.m_position = position;
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

    public static class SoundViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, View.OnCreateContextMenuListener {

        private Activity m_context;
        private boolean m_isSoundPlaying;
        private boolean m_inEditMode;

        private OnHandleAdapterItemSoundActions m_listener;

        private ImageView m_imageViewSound;
        private TextView m_txtSoundName;

        private SoundEntry m_soundEntry;
        private int m_position;


        public SoundViewHolder(View itemView) {
            super(itemView);
            m_imageViewSound = (ImageView) itemView.findViewById(R.id.imageViewSound);
            m_txtSoundName = (TextView) itemView.findViewById(R.id.txtSoundName);
            // Let Activity play the sound
            /*m_imageViewSound.setOnClickListener(new View.OnClickListener() {
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
            });*/
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            Log.d(TAG, "TESTTEST LONG CLICK");
            // We call this method so we can inform the activity about the clicked item position,
            // because "item.getMenuInfo()" in "onContextItemSelected" of the activity is every time null
            // (A bug?) and so we need another wy to get the position of the items position
            m_listener.onLongClickedItem(m_position);
            // Return false so onCreateContextMenu is called
            return false;
        }

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

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuInflater inflater = m_context.getMenuInflater();
            inflater.inflate(R.menu.menu_context_sound_entry, menu);
        }

    }

}
