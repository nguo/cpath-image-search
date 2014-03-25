package codepath.apps.imagesearch;

import android.content.Context;
import android.util.AttributeSet;
import com.loopj.android.image.SmartImageView;

/**
 * SquareSmartImageView - square version of SmartImageView
 */
public class SquareSmartImageView extends SmartImageView {
	public SquareSmartImageView(Context context) {
		super(context);
	}

	public SquareSmartImageView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	public SquareSmartImageView(Context context, AttributeSet attributeSet, int i) {
		super(context, attributeSet, i);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
	}
}
