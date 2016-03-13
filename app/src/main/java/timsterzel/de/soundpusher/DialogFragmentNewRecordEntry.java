package timsterzel.de.soundpusher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by tim on 13.03.16.
 */
public class DialogFragmentNewRecordEntry extends DialogFragment {

    private static final String TAG = DialogFragmentNewRecordEntry.class.getSimpleName();

    public interface OnNewRecordEntryCreatedListener {
        void onNewRecordEntryCreated();
    }

    private OnNewRecordEntryCreatedListener mListener;


    public static DialogFragmentNewRecordEntry newInstance(String language) {
        DialogFragmentNewRecordEntry fragment = new DialogFragmentNewRecordEntry();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_fragment_new_record_entry, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle("");
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnNewRecordEntryCreatedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNewRecordEntryCreatedListener");
        }
    }



}
