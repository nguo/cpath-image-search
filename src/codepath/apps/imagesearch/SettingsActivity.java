package codepath.apps.imagesearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * SettingsActivity - activity for the settings page for filtering image results
 */
public class SettingsActivity extends Activity {
	/** name of the settings file to save to/read from */
	private static final String SETTINGS_FILE_NAME = "image_search_settings.txt";
	/** json key for image size */
	public static final String IMG_SIZE_KEY = "imgSize";
	/** json key for image color */
	public static final String IMG_COLOR_KEY = "imgColor";
	/** json key for image type */
	public static final String IMG_TYPE_KEY = "imgType";
	/** json key for image site */
	public static final String IMG_SITE_KEY = "imgSite";
	/** spinner denoting the image size */
	private Spinner spImgSize;
	/** spinner denoting the image color */
	private Spinner spImgColor;
	/** spinner for the image type */
	private Spinner spImgType;
	/** edit text for the image site */
	private EditText etImgSite;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		setupViews();
		updateViewsFromConfig((SettingsConfig) getIntent().getSerializableExtra(ImageSearchActivity.SETTINGS_EXTRA));
	}

	/** setup the views */
	private void setupViews() {
		spImgSize = (Spinner) findViewById(R.id.spImgSize);
		spImgColor = (Spinner) findViewById(R.id.spImgColor);
		spImgType = (Spinner) findViewById(R.id.spImgType);
		etImgSite = (EditText) findViewById(R.id.etImgSite);
	}

	/** updates the views given a settings config */
	private void updateViewsFromConfig(SettingsConfig settings) {
		setSelectionByItem(spImgSize, settings.getImgsz());
		setSelectionByItem(spImgColor, settings.getImgcolor());
		setSelectionByItem(spImgType, settings.getImgtype());
		etImgSite.setText(settings.getSite());
	}

	/** helper function to set the selection of the spinner by the item */
	private void setSelectionByItem(Spinner sp, String item) {
		ArrayAdapter adapter = (ArrayAdapter) sp.getAdapter();
		sp.setSelection(adapter.getPosition(item));
	}

	/** callback when apply button is clicked */
	public void onApplySettings(View v) {
		// get selected items
		String size = (String) spImgSize.getSelectedItem();
		String color = (String) spImgColor.getSelectedItem();
		String type = (String) spImgType.getSelectedItem();
		String site = etImgSite.getText().toString();

		// save out the values
		File filesDir = getFilesDir();
		File settingsFile = new File(filesDir, SETTINGS_FILE_NAME);
		try {
			JSONObject json = new JSONObject();
			json.put(IMG_SIZE_KEY, size);
			json.put(IMG_COLOR_KEY, color);
			json.put(IMG_TYPE_KEY, type);
			json.put(IMG_SITE_KEY, site);
			Log.d("json", json.toString());
			FileUtils.writeStringToFile(settingsFile, json.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// create settings object and pass it back
		SettingsConfig settings = new SettingsConfig(size, color, type, site);
		Intent data = new Intent();
		data.putExtra(ImageSearchActivity.SETTINGS_EXTRA, settings);
		setResult(RESULT_OK, data);
		finish();
	}
}