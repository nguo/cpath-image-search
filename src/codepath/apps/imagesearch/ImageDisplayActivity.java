package codepath.apps.imagesearch;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import com.loopj.android.image.SmartImageView;

import java.io.File;

/**
 * ImageDisplayActivity - activity that displays the full image and related info
 */
public class ImageDisplayActivity extends Activity {
	/** downloaded image file name */
	private static final String DISPLAY_IMAGE_FILE_NAME = "displayImage.png";

	/** share action provider on the menu */
	private ShareActionProvider miShareAction;
	/** file directory for the image */
	private File downloadsDir;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_display);
		downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		ImageResult imageResult = (ImageResult) getIntent().getSerializableExtra(ImageSearchActivity.IMAGE_RESULT_EXTRA);
		SmartImageView ivLargeImg = (SmartImageView) findViewById(R.id.ivLargeImg);
		new DownloadImageTask(ivLargeImg, downloadsDir, DISPLAY_IMAGE_FILE_NAME).execute(imageResult.getFullUrl());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate menu resource file.
		getMenuInflater().inflate(R.menu.image_display, menu);
		// Locate MenuItem with ShareActionProvider
		MenuItem item = menu.findItem(R.id.menu_item_share);
		// Fetch and store ShareActionProvider
		miShareAction = (ShareActionProvider) item.getActionProvider();
		// Return true to display menu
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("image/*");
		shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+downloadsDir+"/"+DISPLAY_IMAGE_FILE_NAME));
		miShareAction.setShareIntent(shareIntent);
		return true;
	}
}