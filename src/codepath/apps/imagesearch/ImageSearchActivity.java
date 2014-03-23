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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ImageSearchActivity extends Activity {
	/** key for the extra param to pass with the intent */
	public static final String IMAGE_RESULT_EXTRA = "image_result";
	/** number of requests to send for each query */
	public static final int NUM_REQUESTS_PER_QUERY = 3;
	/** number of images to request */
	public static final int NUM_IMAGES_PER_REQUEST = 8;
	/** edit text to type in search term */
	private EditText etSearch;
	/** grid view of image results */
	private GridView gvResults;

	/** list of image results we got back from the query */
	private ArrayList<ImageResult> imagesResults = new ArrayList<ImageResult>();
	/** array adapater for image results */
	private ImageResultArrayAdapter imageAdapter;
	/** the current, latest start index for the request */
	private int currentIndex = 0;
	/** the current, latest image query term */
	private String currentQuery = "";

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_search);
		resetCurrentIndex();
		setupViews();
		imageAdapter = new ImageResultArrayAdapter(this, imagesResults);
		gvResults.setAdapter(imageAdapter);
		setupListeners();
	}

	/** setup the views variables */
	private void setupViews() {
		etSearch = (EditText) findViewById(R.id.etSearch);
		gvResults = (GridView) findViewById(R.id.gvResults);
	}

	/** setup listeners for the views */
	private void setupListeners() {
		gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent(getApplicationContext(), ImageDisplayActivity.class);
				i.putExtra(IMAGE_RESULT_EXTRA, imagesResults.get(position));
				startActivity(i);
			}
		});
		etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					onSearchClick(null);
					return true;
				}
				return false;
			}
		});
		gvResults.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				requestMoreImages(page);
			}
		});
	}

	/** hides the software keyboard */
	private void hideKeyboard(View v) {
		((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);
		return true;
	}

	/** settings menu callback */
	public void onSearchClick(MenuItem mi) {
		hideKeyboard(etSearch);
		resetCurrentIndex();
		imagesResults.clear();
		currentQuery = etSearch.getText().toString();
		Toast.makeText(this, "Searching for " + currentQuery + "...", Toast.LENGTH_SHORT).show();
		makeImageQueries();
	}

	/** request more images */
	private void requestMoreImages(int page) {
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
					+ "&v=1.0&q=" + Uri.encode(query),
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject response) {
						try {
							JSONArray imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");
							imageAdapter.addAll(ImageResult.fromJSONArray(imageJsonResults));
							Log.d("DEBUG", imagesResults.toString());
						} catch (JSONException e) {
							e.printStackTrace();;
						}
					}
				});
	}
}
