package codepath.apps.imagesearch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.*;
import java.net.URL;

/**
 * DownloadImageTask - Async task to download an image from a URL and set it to an ImageView
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	/** file directory to save image in */
	private File fileDir;
	/** file name to save image as */
	private String fileName;
	/** image view to set image to */
	private ImageView bmImage;

	/**
	 * Constructor
	 */
	public DownloadImageTask(ImageView bmImage, File fileDir, String fileName) {
		this.bmImage = bmImage;
		this.fileDir = fileDir;
		this.fileName = fileName;
	}

	@Override
	protected Bitmap doInBackground(String... urls) {
		String src = urls[0];
		Bitmap mIcon11 = null;
		try {
			// save the image to a file
			URL url = new URL (src);
			InputStream input = url.openStream();
			try {
				OutputStream output = new FileOutputStream (new File(fileDir, fileName));
				try {
					byte[] buffer = new byte[2048];
					int bytesRead = 0;
					while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
						output.write(buffer, 0, bytesRead);
					}

				} finally {
					output.close();
				}
			} finally {
				input.close();
			}
			// downsize the bitmap so we don't run out of memory
			mIcon11 = downsizeBitmap(new File(fileDir, fileName));;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mIcon11;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		//set image of your imageview
		bmImage.setImageBitmap(result);
	}

	/**
	 * Downsizes the file's bitmap so that we won't run out of memory
	 * @param f		file to read
	 * @return		shrunken bitmap
	 */
	private Bitmap downsizeBitmap(File f) {
		try {
			//Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f),null,o);

			//The new size we want to scale to
			final int REQUIRED_SIZE=bmImage.getWidth();

			//Find the correct scale value. It should be the power of 2.
			int scale=1;
			while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
				scale*=2;

			//Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize=scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}