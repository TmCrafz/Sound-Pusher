package timsterzel.de.soundpusher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by tim on 13.03.16.
 */
public class DialogFragmentNewRecordEntry extends DialogFragment {

    private static final String TAG = DialogFragmentNewRecordEntry.class.getSimpleName();

    public static final String TAG_SHOWN = "DialogFragmentNewRecordEntry";

    private MediaButton m_btnRecord;

    private MediaButton m_btnPlay;

    private MediaButton m_btnStop;


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

        m_btnRecord = (MediaButton) view.findViewById(R.id.btnRecord);
        m_btnPlay = (MediaButton) view.findViewById(R.id.btnPlay);
        m_btnStop = (MediaButton) view.findViewById(R.id.btnStop);

        m_btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_btnRecord.changeState();
            }
        });

        m_btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_btnPlay.changeState();
            }
        });

        m_btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_btnStop.changeState();
            }
        });




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





}
