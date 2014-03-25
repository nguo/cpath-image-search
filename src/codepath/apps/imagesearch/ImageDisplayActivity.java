package codepath.apps.imagesearch;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ShareActionProvider;
import android.widget.TextView;
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
	/** text view displaying image info */
	private TextView tvImageInfo;
	/** image result */
	private ImageResult imageResult;
	/** progress bar indicating loading */
	private ProgressBar pbLoading;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_display);
		downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		imageResult = (ImageResult) getIntent().getSerializableExtra(ImageSearchActivity.IMAGE_RESULT_EXTRA);
		tvImageInfo = (TextView) findViewById(R.id.tvImageInfo);
		tvImageInfo.setMovementMethod(LinkMovementMethod.getInstance());
		pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
		new DownloadImageTask((SmartImageView) findViewById(R.id.ivLargeImg), downloadsDir, DISPLAY_IMAGE_FILE_NAME, this).execute(imageResult.getFullUrl());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate menu resource file.
		getMenuInflater().inflate(R.menu.image_display, menu);
		// Locate MenuItem with ShareActionProvider
		MenuItem item = menu.findItem(R.id.miShare);
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

	/** called once image has loaded */
	public void onImageLoaded() {
		// hide progress and display text
		pbLoading.setVisibility(View.INVISIBLE);
		tvImageInfo.setText(Html.fromHtml(imageResult.getTitle() + " (" + imageResult.getContent() + ") "
				+ "<a href=\"" + imageResult.getFullUrl() + "\">" + imageResult.getFullUrl() + "</a>"));
	}
}