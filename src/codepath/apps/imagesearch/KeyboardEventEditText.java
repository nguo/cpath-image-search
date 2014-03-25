package codepath.apps.imagesearch;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * KeyboardEventEditText - edit text that handles keyboard events
 */
public class KeyboardEventEditText extends EditText {
	public KeyboardEventEditText(Context context) {
		super(context);
	}

	public KeyboardEventEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public KeyboardEventEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			ImageSearchActivity activity = (ImageSearchActivity) getContext();
			activity.onNoNewSearch();
		}
		return super.onKeyPreIme(keyCode, event);
	}
}
