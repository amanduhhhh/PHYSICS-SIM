import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;


/**
 * EnergyBall
 * Ball object for energy class
 * 
 * @author Amanda
 * @version June 2023
 */
public class EnergyBall {
	/**Mass of ball*/
	private double mass;
	/**Speed of ball*/
	private double speed;
	/**Ball's height from ground*/
	private double height;
	/**X-Position of ball*/
	private double xPos;
	/**Y-Position of ball*/
	private double yPos;
	/**Width of ball*/
	private int width;
	/**Ball display image*/
	private BufferedImage image;

	/**
	 * Constructs an energy ball 
	 */
	public EnergyBall() {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream("Images/ball.png");
			this.image = ImageIO.read(inputStream);
			width = image.getWidth();
		} catch (IOException e) {
			System.out.println("Energy Ball image missing");
		} finally {
			if (inputStream != null) {
		        try {
		            inputStream.close();
		        } catch (IOException e) {
		            System.out.println("Stream error");
		        }
		    }
		}
	}
	
	/**
	 * Draws ball to screen
	 * @param g Graphics screen
	 */
	public void draw(Graphics g) {
		g.drawImage(image, (int)xPos-width/2, (int)yPos, null);
	}
	
	/**
	 * Gets width 
	 * @return Ball width
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Gets mass 
	 * @return Ball mass
	 */
	public double getMass() {
		return mass;
	}

	/**
	 * Sets mass
	 * @param mass Mass of ball
	 */
	public void setMass(double mass) {
		this.mass = mass;
	}

	/**
	 * Gets x-position 
	 * @return X-coordinate of ball center
	 */
	public double getX() {
		return xPos;
	}

	/**
	 * Sets x-position
	 * @param xPos X-position of ball
	 */
	public void setX(double xPos) {
		this.xPos = xPos;
	}

	/**
	 * Gets y-position
	 * @return Y-coordinate of ball (top)
	 */
	public double getY() {
		return yPos;
	}

	/**
	 * Sets y-position
	 * @param yPos Y-coordinate of ball (top)
	 */
	public void setY(double yPos) {
		this.yPos = yPos;
	}

	/**
	 * Gets speed
	 * @return Current speed of ball
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * Sets speed
	 * @param speed Current speed of ball
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	/**
	 * Gets height of ball 
	 * @return Ball height from ramp bottom (meters)
	 */
	public double getHeight() {
		return height;
	}
	
	/**
	 * Sets height of ball
	 * @param height Height from ramp bottom (meters)
	 */
	public void setHeight(double height) {
		this.height = height;
	}
	
	
}
