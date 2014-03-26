package codepath.apps.imagesearch;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import java.io.FileOutputStream;

/**
 * ImageDisplayActivity - activity that displays the full image and related info
 */
public class ImageDisplayActivity extends Activity {
	/** downloaded image file name */
	private static final String DISPLAY_IMAGE_FILE_NAME = "displayImage.png";

	/** menu item */
	private MenuItem mi;
	/** share action provider on the menu */
	private ShareActionProvider miShareAction;
	/** file directory for the image */
	private File downloadsDir;
	/** image view */
	private SmartImageView ivLargeImg;
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
		if (!downloadsDir.exists()) {
			downloadsDir.mkdirs();
		}

		imageResult = (ImageResult) getIntent().getSerializableExtra(ImageSearchActivity.IMAGE_RESULT_EXTRA);
		tvImageInfo = (TextView) findViewById(R.id.tvImageInfo);
		tvImageInfo.setMovementMethod(LinkMovementMethod.getInstance());
		ivLargeImg = (SmartImageView) findViewById(R.id.ivLargeImg);
		pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
		new DownsizeImageTask(ivLargeImg, this).execute(imageResult.getFullUrl());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate menu resource file.
		getMenuInflater().inflate(R.menu.image_display, menu);
		// Locate MenuItem with ShareActionProvider
		mi = menu.findItem(R.id.miShare);
		// disable menu item while share is hidden
		mi.setEnabled(false);
		// Fetch and store ShareActionProvider
		miShareAction = (ShareActionProvider) mi.getActionProvider();
		// Return true to display menu
		return true;
	}

	/** setup share intent */
	private void setupShareIntent() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("image/*");
		shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(getImageFile()));
		miShareAction.setShareIntent(shareIntent);
	}

	/** @return a new file object of the image file */
	private File getImageFile() {
		return (new File(downloadsDir + "/" + DISPLAY_IMAGE_FILE_NAME));
	}

	/**
	 * called once image has loaded in downsizeimagetask
	 * @param success	if true, then the downsizing succeeded and the image view's bitmap was correctly set
	 */
	public void onImageLoaded(boolean success) {
		if (!success) {
			// when we fail to set the image from the full url, set it to the thumbnail
			ivLargeImg.setImageUrl(imageResult.getThumbUrl());
		}
		downloadBitmapFromImageView(ivLargeImg);
		setupShareIntent();
		// show share menu item
		mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		// hide progress and display text
		pbLoading.setVisibility(View.INVISIBLE);
		tvImageInfo.setText(Html.fromHtml(imageResult.getTitle() + " (" + imageResult.getContent() + ") "
				+ "<a href=\"" + imageResult.getFullUrl() + "\">" + imageResult.getFullUrl() + "</a>"));
	}

	/** downloads the bitmap found in the image view to local file */
	public void downloadBitmapFromImageView(SmartImageView imageView) {
		Bitmap bitmap = getImageBitmap(imageView);
		// Write image to default external storage directory
		File target = getImageFile();
		try {
			FileOutputStream fos = new FileOutputStream(target, false);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** @return the bitmap for the given image view */
	public Bitmap getImageBitmap(SmartImageView imageView) {
		Drawable drawable = imageView.getDrawable();
		Bitmap bmp;
		if (drawable instanceof BitmapDrawable){
			bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
		} else { // workaround to convert color to bitmap
			bmp = Bitmap.createBitmap(drawable.getBounds().width(),
					drawable.getBounds().height(), Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bmp);
			drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			drawable.draw(canvas);
		}
		return bmp;
	}
}