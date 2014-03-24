package codepath.apps.imagesearch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import org.apache.http.util.ByteArrayBuffer;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

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
		File target = new File(fileDir, fileName);
		try {
			// save the image to a file
			URL url = new URL (src);
			URLConnection conn = url.openConnection();
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int current = 0;

			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}

			FileOutputStream fos = new FileOutputStream(target);
			fos.write(baf.toByteArray());

			fos.close();
			is.close();

			// downsize the bitmap so we don't run out of memory
			mIcon11 = downsizeBitmap(target);
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