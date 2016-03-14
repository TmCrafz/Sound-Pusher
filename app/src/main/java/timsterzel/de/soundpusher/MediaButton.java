package timsterzel.de.soundpusher;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by tim on 14.03.16.
 */
public class MediaButton extends ImageButton implements View.OnTouchListener {

    private static  final String TAG = MediaButton.class.getSimpleName();

    private int m_standardDrawable;
    private int m_activeDrawable;
    private int m_activeBackgroundDrawable;

    private boolean m_active;

    public MediaButton(Context context) {
        super(context);
        init(context, null);
    }

    public MediaButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MediaButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attr) {
        m_active = false;
        m_activeBackgroundDrawable = R.drawable.round_background_click;
        setBackgroundResource(0);
        setOnTouchListener(this);
        TypedArray a = context.getTheme().obtainStyledAttributes(attr, R.styleable.MediaButton, 0, 0);
        try {
            m_standardDrawable = a.getResourceId(R.styleable.MediaButton_standardSrc, 0);
            m_activeDrawable = a.getResourceId(R.styleable.MediaButton_activeSrc, 0);
        } finally {
            a.recycle();
        }
        setImageResource(m_standardDrawable);
    }

    public boolean isActive() { return m_active; }

    public void changeState() {
        m_active = !m_active;
        if (m_active) {
            setImageResource(m_activeDrawable);
        }
        else {
            setImageResource(m_standardDrawable);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // Return false every time, so OnClickListener and other listeners are handled
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            setBackgroundResource(m_activeBackgroundDrawable);
            return false;
        }
        else if (action == MotionEvent.ACTION_UP) {
            setBackgroundResource(0);
            return false;
        }
        return false;
    }

}
