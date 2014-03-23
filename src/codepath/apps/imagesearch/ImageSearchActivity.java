package codepath.apps.imagesearch;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ImageSearchActivity extends Activity {
	/** edit text to type in search term */
	EditText etSearch;
	/** grid view of image results */
	GridView gvResults;

	/** list of image results we got back from the query */
	ArrayList<ImageResult> imagesResults = new ArrayList<ImageResult>();

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_search);
		setupViews();
	}

	/** setup the views variables */
	private void setupViews() {
		etSearch = (EditText) findViewById(R.id.etSearch);
		gvResults = (GridView) findViewById(R.id.gvResults);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);
		return true;
	}

	/** settings menu callback */
	public void onSearchClick(MenuItem mi) {
		String query = etSearch.getText().toString();
		Toast.makeText(this, "Searching for " + query + "...", Toast.LENGTH_SHORT).show();
		sendImageQuery(query, 0);
	}

	/**
	 * sends an image query
	 * @param query			query term to query images for
	 * @param startIndex	the start index of the query
	 */
	private void sendImageQuery(String query, int startIndex) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get( "https://ajax.googleapis.com/ajax/services/search/images?rsz=8"
				+ "&start=" + startIndex + "&v=1.0&q=" + Uri.encode(query),
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject response) {
						JSONArray imageJsonResults = null;
						try {
							imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");
							imagesResults.clear();
							imagesResults.addAll(ImageResult.fromJSONArray(imageJsonResults));
							Log.d("DEBUG", imagesResults.toString());
						} catch (JSONException e) {
							e.printStackTrace();;
						}
					}
				});
	}
}
