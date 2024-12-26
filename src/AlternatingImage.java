import java.awt.image.BufferedImage;

/**
 * AlternatingImage
 * Subclass of Image for two display images
 * @author Amanda
 * @version June 2023
 */

public class AlternatingImage extends Image {
	/**First image*/
	private BufferedImage image1;
	/**Second (alternate) image */
	private BufferedImage image2;
	
	/**
	 * Constructs an alternating image with two BufferedImages and coordinates
	 * @param image1 First image
	 * @param image2 Alternate image
	 * @param x X-Position
	 * @param y Y-Position
	 */
	public AlternatingImage(BufferedImage image1, BufferedImage image2 , int x, int y) {
		super(image1, x, y);
		this.image1 = image1;
		this.image2 = image2;
	}
	
	/**
	 * Switches image depending on condition
	 * @param condition True for first image, false for alternate
	 */
	public void switchIMG(boolean condition) {
		if (condition) {
			setImage(image2);
		}else {
			setImage(image1);
		}
	}

}
