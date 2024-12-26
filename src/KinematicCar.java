import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * KinematicCar
 * Car for kinematic simulation
 * 
 * @author Amanda
 * @version June 2023
 */
public class KinematicCar extends Image{	
	/**
	 * Constructs a kinematic car with image
	 * @param image Display image
	 */
	public KinematicCar(BufferedImage image){
		super(image);
		setY(120);
		setX(Constants.CENTER);
	}
	
	/**
	 * Draws kinematic car to the screen with centered x value
	 * @param g Grpahics screen
	 */
	@Override
	public void draw(Graphics g){
		g.drawImage(getImage(), getX()-(getWidth()/2), getY(), null);
	}
	
	/**
	 * Checks whether mouse is on car
	 * 
	 * @param mouseX X-Position of mouse
	 * @param mouseY Y-Position of mouse
	 */
	@Override
	public boolean checkCollision(int mouseX, int mouseY) {
		return ((mouseX>getX()-getWidth()/2)&&(mouseX<getX()+getWidth()/2)&&(mouseY>getY())&&(mouseY<getY()+getHeight()));
	}

}
