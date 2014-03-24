package codepath.apps.imagesearch;

import java.io.Serializable;

/**
 * SettingsConfig - configs for the settings
 */
public class SettingsConfig implements Serializable {

	/** image size */
	private String imgsz = "";
	/** image color */
	private String imgcolor = "";
	/** image type */
	private String imgtype = "";
	/** site to search on */
	private String site = "";

	/** constructur */
	public SettingsConfig(String imgsz, String imgcolor, String imgtype, String site) {
		this.imgsz = imgsz;
		this.imgcolor = imgcolor;
		this.imgtype = imgtype;
		this.site = site;
	}

	/** empty constructor */
	public SettingsConfig() {}

	public String getImgsz() {
		return imgsz;
	}

	public String getImgcolor() {
		return imgcolor;
	}

	public String getImgtype() {
		return imgtype;
	}

	public String getSite() {
		return site;
	}
}
