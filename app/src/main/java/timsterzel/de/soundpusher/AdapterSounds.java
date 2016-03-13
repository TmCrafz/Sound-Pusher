package timsterzel.de.soundpusher;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by tim on 13.03.16.
 */
public class AdapterSounds extends RecyclerView.Adapter<AdapterSounds.SoundViewHolder> {


    @Override
    public SoundViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(SoundViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class SoundViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public SoundViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View v) {

        }
    }

}
