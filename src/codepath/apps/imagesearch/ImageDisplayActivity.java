package codepath.apps.imagesearch;

import android.app.Activity;
import android.os.Bundle;
import com.loopj.android.image.SmartImageView;

/**
 * ImageDisplayActivity - activity that displays the full image and related info
 */
public class ImageDisplayActivity extends Activity {
	/** downloaded image file name */
	private static final String DISPLAY_IMAGE_FILE_NAME = "displayImage.png";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_display);
		ImageResult imageResult = (ImageResult) getIntent().getSerializableExtra(ImageSearchActivity.IMAGE_RESULT_EXTRA);
		SmartImageView ivLargeImg = (SmartImageView) findViewById(R.id.ivLargeImg);
		new DownloadImageTask(ivLargeImg, getFilesDir(), DISPLAY_IMAGE_FILE_NAME).execute(imageResult.getFullUrl());
	}
}