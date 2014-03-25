package codepath.apps.imagesearch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

/**
 * DownsizeImageTask - Async task to download an image from a URL and set it to an ImageView
 */
public class DownsizeImageTask extends AsyncTask<String, Void, Bitmap> {
	/** image view to set image to */
	private ImageView bmImage;
	/** activity that executed this task */
	private ImageDisplayActivity sourceActivity;

	/**
	 * Constructor
	 */
	public DownsizeImageTask(ImageView bmImage, ImageDisplayActivity sourceActivity) {
		this.bmImage = bmImage;
		this.sourceActivity = sourceActivity;
	}

	@Override
	protected Bitmap doInBackground(String... urls) {
		String src = urls[0];
		Bitmap bitmap = null;

		try {
			// downsize the bitmap so we don't run out of memory
			bitmap = downsizeBitmapFromUrl(new URL(src));

		} catch (IOException e) {
			e.printStackTrace(); // could not write bitmap to bytearray output stream
		}
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		//set image of your imageview
		boolean loadSuccess = (result != null);
		if (loadSuccess) {
			bmImage.setImageBitmap(result);
		}
		sourceActivity.onImageLoaded(loadSuccess);
	}

	/**
	 * Downsizes the file's bitmap so that we won't run out of memory
	 * @param url		file to read
	 * @return		shrunken bitmap
	 */
	private Bitmap downsizeBitmapFromUrl(URL url) {
		try {
			//Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(url.openConnection().getInputStream(),null,o);

			//The new size we want to scale to
			final int REQUIRED_SIZE=bmImage.getWidth();

			//Find the correct scale value. It should be the power of 2.
			int scale=1;
			while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
				scale*=2;

			//Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize=scale;
			return BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, o2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}