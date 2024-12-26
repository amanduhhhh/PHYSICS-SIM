import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * Image
 * Class for displayed images
 * 
 * @author Amanda
 * @version June 2023
 */
public class Image {
	/**BufferedImage to be displayed*/
	private BufferedImage image;
	/**X-position of image*/
	private int x;
	/**Y-Position of image*/
	private int y; 
	/**Width of image*/
	private int width;
	/**Height of image*/
	private int height;
	
	/**
	 * Constructs an image object with a buffered image
	 * @param image BufferedImage to display
	 */
	public Image(BufferedImage image){
		this.image = image;
		this.height = image.getHeight();
		this.width = image.getWidth();
	}
	
	/**
	 * Constructs an image with a buffered image and coordinates
	 * @param image BufferedImage to display
	 * @param x X-Position of image
	 * @param y Y-Position of image
	 */
	public Image(BufferedImage image, int x, int y){
		this.image = image;
		this.height = image.getHeight();
		this.width = image.getWidth();
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Gets the displayed image
	 * @return BufferedImage to display
	 */
	public BufferedImage getImage() {
		return this.image;
	}
	
	/**
	 * Sets the displayed image
	 * @param img BufferedImage to display
	 */
	public void setImage(BufferedImage img) {
		this.image = img;
	}
	
	/**
	 * Draws image to screen
	 * @param g Graphics screen
	 */
	public void draw(Graphics g) {
		g.drawImage(image, x, y, null);
	}
	
	/**
	 * Checks if mouse is on image
	 * @param mouseX X-Position of mouse
	 * @param mouseY Y-Position of mouse
	 * @return Boolean representing whether mouse has collided with image
	 */
	public boolean checkCollision(int mouseX, int mouseY) {
		return ((mouseX>x)&&(mouseX<x+width)&&(mouseY>y)&&(mouseY<y+height));
	}
	
	//Getters and setters -------------------------------------------------------------------

	/**
	 * Gets X-Position
	 * @return X-Position of image
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets X-Position
	 * @param x X-Position of image
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Gets Y-Position
	 * @return Y-Position of image
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets Y-Position
	 * @param y Y-Position of image
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Gets width
	 * @return Width of image
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gets Height
	 * @return Height of image
	 */
	public int getHeight() {
		return height;
	}
	
}
