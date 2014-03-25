package codepath.apps.imagesearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * ImageResult - contains info about an image
 */
public class ImageResult implements Serializable {
	/** url for the full-sized image */
	private String fullUrl;
	/** url for the thumbnail image */
	private String thumbUrl;
	/** image title */
	private String title;
	/** host url for image */
	private String visibleUrl;
	/** image content (description) with html format */
	private String content;

	/** constructor */
	public ImageResult(JSONObject json) {
		try {
			fullUrl = json.getString("url");
			thumbUrl = json.getString("tbUrl");
			title = json.getString("title");
			visibleUrl = json.getString("visibleUrl");
			content = json.getString("content");
		} catch (JSONException e) {
			fullUrl = null;
			thumbUrl = null;
			title = null;
			visibleUrl = null;
			content = null;
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

	public String getTitle() {
		return title;
	}

	public String getVisibleUrl() {
		return visibleUrl;
	}

	public String getContent() {
		return content;
	}

	@Override
	public String toString() {
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
