package codepath.apps.imagesearch;

import android.app.Activity;
import android.os.Bundle;
import com.loopj.android.image.SmartImageView;


public class ImageDisplayActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_display);

		ImageResult imageResult = (ImageResult) getIntent().getSerializableExtra(ImageSearchActivity.IMAGE_RESULT_EXTRA);
		SmartImageView ivLargeImg = (SmartImageView) findViewById(R.id.ivLargeImg);
		ivLargeImg.setImageUrl(imageResult.getFullUrl());
	}
}