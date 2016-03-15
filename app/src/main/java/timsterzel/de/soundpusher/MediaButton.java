package timsterzel.de.soundpusher;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
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

    private int m_currentShownDrawableRes;

    private int m_standardDrawableRes;
    private int m_activeDrawableRes;
    private int m_activeBackgroundDrawableRes;

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
        m_activeBackgroundDrawableRes = R.drawable.round_background_click;
        setBackgroundResource(0);
        setOnTouchListener(this);
        TypedArray a = context.getTheme().obtainStyledAttributes(attr, R.styleable.MediaButton, 0, 0);
        try {
            m_standardDrawableRes = a.getResourceId(R.styleable.MediaButton_standardSrc, 0);
            m_activeDrawableRes = a.getResourceId(R.styleable.MediaButton_activeSrc, 0);
        } finally {
            a.recycle();
        }
        changeShownImageResource(m_standardDrawableRes);

    }

    public boolean isActive() { return m_active; }

    public void changeState() {
        m_active = !m_active;
        if (m_active) {
            changeShownImageResource(m_activeDrawableRes);
        }
        else {
            changeShownImageResource(m_standardDrawableRes);
        }
    }

    private void changeShownImageResource(int resId) {
        m_currentShownDrawableRes = resId;
        setImageResource(resId);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        Drawable originalIcon = ContextCompat.getDrawable(getContext(), m_currentShownDrawableRes);
        Drawable shownDrawable = enabled ? originalIcon : convertDrawableToDisabled(originalIcon);
        setImageDrawable(shownDrawable);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // Return false every time, so OnClickListener and other listeners are handled
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            setBackgroundResource(m_activeBackgroundDrawableRes);
            return false;
        }
        else if (action == MotionEvent.ACTION_UP) {
            setBackgroundResource(0);
            return false;
        }
        return false;
    }

    // Make drawable transparent, so it looks disabled
    private Drawable convertDrawableToDisabled(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Drawable res = drawable.mutate();
        // Set alpha between 0 (full transparent) and 255 (full shown)
        res.setAlpha(100);
        return res;
    }

}
