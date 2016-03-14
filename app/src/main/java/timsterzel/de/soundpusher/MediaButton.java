package timsterzel.de.soundpusher;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * Created by tim on 14.03.16.
 */
public class MediaButton extends ImageButton {

    private int m_standardDrawable;
    private int m_activeDrawable;

    private boolean m_active;

    public MediaButton(Context context) {
        super(context);
    }

    public MediaButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MediaButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attr) {
        m_active = false;
        TypedArray a = context.getTheme().obtainStyledAttributes(attr, R.styleable.MediaButton, 0, 0);
        try {
            m_standardDrawable = a.getResourceId(R.styleable.MediaButton_standardSrc, R.drawable.round_background_click);
            m_activeDrawable = a.getResourceId(R.styleable.MediaButton_activeSrc, R.drawable.round_background_click);
        } finally {
            a.recycle();
        }
        setImageResource(m_standardDrawable);
    }

    public boolean isActive() { return m_active; }

    public void changeState() {
        m_active = !m_active;
        if (m_active) {
            setBackgroundResource(m_activeDrawable);
        }
        else {
            setBackgroundResource(m_standardDrawable);
        }
    }


}
