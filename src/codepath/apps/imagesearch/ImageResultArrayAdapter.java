package codepath.apps.imagesearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * ImageResultArrayAdapter - custom array adapter for ImageResult
 */
public class ImageResultArrayAdapter extends ArrayAdapter<ImageResult> {

	/** constructor */
	public ImageResultArrayAdapter(Context context, List<ImageResult> objects) {
		super(context, R.layout.item_image_result, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageResult imageResult = getItem(position);
		View layout = convertView;
		SquareSmartImageView ivSquare;
		TextView tvSquare;

		if(layout == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			layout = inflater.inflate(R.layout.item_image_result, parent, false);
		}

		ivSquare = (SquareSmartImageView)layout.findViewById(R.id.ivSquare);
		tvSquare = (TextView)layout.findViewById(R.id.tvSquare);

		ivSquare.setImageUrl(imageResult.getThumbUrl());
		String visibleUrl = imageResult.getVisibleUrl();
		if (visibleUrl.indexOf("www.") == 0) {
			visibleUrl = visibleUrl.substring(4);
		}
		tvSquare.setText(visibleUrl);

		return layout;
	}
}
