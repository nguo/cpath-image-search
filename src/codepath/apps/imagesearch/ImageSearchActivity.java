package codepath.apps.imagesearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ImageSearchActivity extends Activity {
	/** file name for the history */
	private static final String HISTORY_FILE_NAME = "history.txt";
	/** key for the extra param to pass with the intent between the image result activity */
	public static final String IMAGE_RESULT_EXTRA = "image_result";
	/** key for the extra param to pass between the settings activity */
	public static final String SETTINGS_EXTRA = "settings";
	/** request code to send to the settings activity */
	public static final int SETTINGS_REQUEST_CODE = 11;
	/** number of requests to send for each query */
	public static final int NUM_REQUESTS_PER_QUERY = 3;
	/** number of images to request */
	public static final int NUM_IMAGES_PER_REQUEST = 8;

	/** edit text to type in search term */
	private EditText etSearch;
	/** grid view of image results */
	private GridView gvResults;
	/** image button for search button */
	private ImageButton ibtnSearch;
	/** image button for settings button */
	private ImageButton ibtnSettings;
	/** View for the list of history */
	ListView lvHistoryItems;
	/** List of items inside the list view */
	ArrayList<String> historyItems = new ArrayList<String>();
	/** Array adapter for the list of items */
	ArrayAdapter<String> historyAdapter;

	/** list of image results we got back from the query */
	private ArrayList<ImageResult> imagesResults = new ArrayList<ImageResult>();
	/** array adapater for image results */
	private ImageResultArrayAdapter imageAdapter;
	/** the current, latest start index for the request */
	private int currentIndex = 0;
	/** the current, latest image query term */
	private String currentQuery = "";
	/** settings config */
	private SettingsConfig settingsConfig;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_search);
		getActionBar().setDisplayShowTitleEnabled(false);
		resetCurrentIndex();
		setupViews();
		readSettingsConfig();
		readHistoryItems();
		historyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, historyItems);
		lvHistoryItems.setAdapter(historyAdapter);
		imageAdapter = new ImageResultArrayAdapter(this, imagesResults);
		gvResults.setAdapter(imageAdapter);
		setupListeners();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);
		MenuItem mi = menu.findItem(R.id.miEditText);
		View v = mi.getActionView();
		ibtnSearch = (ImageButton) v.findViewById(R.id.ibtnSearch);
		ibtnSettings = (ImageButton) v.findViewById(R.id.ibtnSettings);
		etSearch = (EditText) v.findViewById(R.id.etSearch);
		setupMenuItemListeners();
		return true;
	}

	/** setup the views variables */
	private void setupViews() {
		gvResults = (GridView) findViewById(R.id.gvResults);
		lvHistoryItems = (ListView) findViewById(R.id.lvHistoryItems);
	}

	/** read in the setting config from file */
	private void readSettingsConfig() {
		File filesDir = getFilesDir();
		File settingsFile = new File(filesDir, SettingsActivity.SETTINGS_FILE_NAME);
		try {
			String content = FileUtils.readFileToString(settingsFile);
			JSONObject json = new JSONObject(content);
			settingsConfig = new SettingsConfig(json.getString(SettingsActivity.IMG_SIZE_KEY),
					json.getString(SettingsActivity.IMG_COLOR_KEY),
					json.getString(SettingsActivity.IMG_TYPE_KEY),
					json.getString(SettingsActivity.IMG_SITE_KEY));
		} catch (IOException e) {
			settingsConfig = new SettingsConfig();
			e.printStackTrace();
		} catch (JSONException e) {
			settingsConfig = new SettingsConfig();
			e.printStackTrace();
		}
	}

	/** read in the history items */
	private void readHistoryItems() {
		File filesDir = getFilesDir();
		File historyFile = new File(filesDir, HISTORY_FILE_NAME);
		try {
			historyItems = new ArrayList<String>(FileUtils.readLines(historyFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** save history items to file */
	private void saveHistoryItems() {
		File filesDir = getFilesDir();
		File historyFile = new File(filesDir, HISTORY_FILE_NAME);
		try {
			FileUtils.writeLines(historyFile, historyItems);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** setup listeners for the views */
	private void setupListeners() {
		gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// go to large image activity
				Intent i = new Intent(getApplicationContext(), ImageDisplayActivity.class);
				i.putExtra(IMAGE_RESULT_EXTRA, imagesResults.get(position));
				startActivity(i);
			}
		});
		gvResults.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				requestMoreImages();
			}
		});
		lvHistoryItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> aView, View item, int pos, long id) {
				// delete history item
				historyItems.remove(pos);
				historyAdapter.notifyDataSetInvalidated();
				saveHistoryItems();
				return true;
			}
		});
		lvHistoryItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> aView, View item, int pos, long id) {
				// make query
				etSearch.setText(historyItems.get(pos));
				makeNewImageQueries();
			}
		});
	}

	/** setup listeners for the views inside the action bar */
	private void setupMenuItemListeners() {
		etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					// make query after "search" button clicked on keyboard
					onSearchClick(null);
					return true;
				}
				return false;
			}
		});
		etSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// clear text and show history
				etSearch.setText("");
				toggleHistory(true);
			}
		});
		ibtnSettings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// show settings
				onSettingsClick(null);
			}
		});
		ibtnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// make query
				onSearchClick(null);
			}
		});
	}

	/** hides the software keyboard */
	private void hideKeyboard(View v) {
		((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == SETTINGS_REQUEST_CODE) {
			settingsConfig = (SettingsConfig) data.getSerializableExtra(SETTINGS_EXTRA);
			makeNewImageQueries();
		}
	}

	/** toggles the history list items and grid view below */
	private void toggleHistory(boolean show) {
		if (show) {
			lvHistoryItems.setVisibility(View.VISIBLE);
			gvResults.setAlpha((float) 0.1);
			lvHistoryItems.setAlpha((float)0.9);
		} else {
			gvResults.setAlpha((float) 1);
			lvHistoryItems.setAlpha((float) 0);
			lvHistoryItems.setVisibility(View.INVISIBLE);
		}
	}

	/** callback when settings menu button is clicked */
	public void onSettingsClick(MenuItem mi) {
		Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
		i.putExtra(SETTINGS_EXTRA, settingsConfig);
		startActivityForResult(i, SETTINGS_REQUEST_CODE);
	}

	/** settings menu callback */
	public void onSearchClick(MenuItem mi) {
		makeNewImageQueries();
	}

	/** makes new image queries (clears out old info) */
	private void makeNewImageQueries() {
		hideKeyboard(etSearch);
		toggleHistory(false);
		resetCurrentIndex();
		imagesResults.clear();
		currentQuery = etSearch.getText().toString();
		if (currentQuery.length() > 0) {
			if (historyItems.indexOf(currentQuery) < 0) {
				// save query to history if it's not already saved
				historyAdapter.add(currentQuery);
				saveHistoryItems();
			}
			Toast.makeText(this, "Searching for " + currentQuery + "...", Toast.LENGTH_SHORT).show();
			makeImageQueries();
		} else {
			Toast.makeText(this, "Please enter search term.", Toast.LENGTH_SHORT).show();
		}
	}

	/** request more images */
	private void requestMoreImages() {
		makeImageQueries();
	}

	/** resets the current index */
	private void resetCurrentIndex() {
		currentIndex = -NUM_IMAGES_PER_REQUEST;
	}

	/** makes the queries for the images */
	private void makeImageQueries() {
		for (int i = 1; i <= NUM_REQUESTS_PER_QUERY; i++) {
			sendImagesRequest(currentQuery, currentIndex + (i * NUM_IMAGES_PER_REQUEST));
		}
		currentIndex = currentIndex + (NUM_REQUESTS_PER_QUERY * NUM_IMAGES_PER_REQUEST);
	}

	/**
	 * sends an image query
	 * @param query			query term to query images for
	 * @param startIndex	the start index of the query
	 */
	private void sendImagesRequest(String query, int startIndex) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get("https://ajax.googleapis.com/ajax/services/search/images?"
					+ "rsz=" + NUM_IMAGES_PER_REQUEST
					+ "&start=" + startIndex
					+ "&v=1.0&q=" + Uri.encode(query)
					+ "&imgsz=" + settingsConfig.getImgsz()
					+ "&imgcolor=" + settingsConfig.getImgcolor()
					+ "&imgtype=" + settingsConfig.getImgtype()
					+ "&as_sitesearch=" + settingsConfig.getSite(),
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject response) {
						try {
							JSONArray imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");
							imageAdapter.addAll(ImageResult.fromJSONArray(imageJsonResults));
							Log.d("DEBUG", imagesResults.toString());
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}
}
