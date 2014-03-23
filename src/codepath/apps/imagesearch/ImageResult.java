package codepath.apps.imagesearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ImageResult {
	private String fullUrl;
	private String thumbUrl;

	public ImageResult(JSONObject json) {
		try {
			fullUrl = json.getString("url");
			thumbUrl = json.getString("tbUrl");
		} catch (JSONException e) {
			fullUrl = null;
			thumbUrl = null;
		}
	}

	/** getter for full image url */
	public String getFullUrl() {
		return fullUrl;
	}

	/** getter for thumb url */
	public String getThumbUrl() {
		return thumbUrl;
	}

	/**
	 * creates a list of ImageResult items from the json
	 * @param imageJsonResults		list of json items
	 * @return	the list of ImageResult items
	 */
	public static ArrayList<ImageResult> fromJSONArray(JSONArray imageJsonResults) {
		ArrayList<ImageResult> parsedResults = new ArrayList<ImageResult>();
		for (int i=0; i < imageJsonResults.length(); i++) {
			try {
				parsedResults.add(new ImageResult(imageJsonResults.getJSONObject(i)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return parsedResults;
	}
}
