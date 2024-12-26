import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;

/**
 * KinematicGraph
 * Graph for kinematics simulation
 * 
 * @author Amanda
 * @version June 2023
 */
public class KinematicGraph {
	/**Y-Axis label*/
	private String yLabel;
	/**Image of grid*/
	private BufferedImage grid;
	/**Toggle view icon*/
	private Image showIcon;
	/**Graph points*/
	private Point[] points;
	/**True if visible, false if hidden*/
	private boolean show;
	/**X-Position of graph*/
	private int xPosition;
	/**yPosition of graph*/
	private final int Y_POSITION = 320;
	/**Width of graph*/
	private int width;
	/**Height of graph*/
	private int height;
	/**Center of graph*/
	private int center;
	/**Graph line color*/
	private Color color;
	
	/**
	 * Constructs a kinematics graph
	 * @param yLabel Y-Axis label
	 * @param numPoints Number of points on x-axis
	 * @param color Color of graph line
	 */
	KinematicGraph(String yLabel, int numPoints, Color color){
		InputStream inputStream = null;
		try {
			//load images
			inputStream = new FileInputStream("Images/kinGraphBGopacity.png");
			grid = ImageIO.read(inputStream);
			inputStream = new FileInputStream("Images/showIcon.png");
			showIcon = new Image(ImageIO.read(inputStream));
		} catch (IOException e) {
			System.out.println("Kinematics graph images missing");
		} finally {
			if (inputStream != null) {
		        try {
		            inputStream.close();
		        } catch (IOException e) {
		            System.out.println("Stream error");
		        }
		    }
		}
		//create label 
		this.yLabel = yLabel;
		//create points array
		points = new Point[numPoints];
		//generate graph properties
		width = grid.getWidth();
		height = grid.getHeight();
		center = Y_POSITION+(height/2);

		show = true;
		this.color = color;
		
	}
	
	/**
	 * Draws graph to screen
	 * @param g Graphics screen
	 */
	public void draw(Graphics g) {
		//draw show icon
		showIcon.draw(g);
		if (show) {
			//draw grid
			g.drawImage(grid, xPosition, Y_POSITION, null);
			//draw label
			g.setFont(new Font("Futura", Font.BOLD, 20));
			g.setColor(Color.black);
			g.drawString(yLabel, xPosition+40, Y_POSITION);
			
			//draw line
			for (int p = 0; p<points.length-1; p++) {
				if ((points[p]!=null)&&(points[p+1]!=null)) {
					g.setColor(color);
					g.drawLine(points[p].x, points[p].y, points[p+1].x, points[p+1].y);
					g.drawLine(points[p].x, points[p].y-1, points[p+1].x, points[p+1].y-1);
				}
			}
		}
	}
	
	/**
	 * Add a point to kinematics graph
	 * @param x X-coordinate of graph point
	 * @param y Y-coordinate of graph point
	 */
	public void addPoint(int x, int y) {
		if (x==0) {
			//reset graph if first value updated
			Arrays.fill(points, null);
		}
		
		//Create new point object for data point
		points[x] = new Point(xPosition+(x*(200/points.length))+20,center-y);
	}
	
	/**
	 * Shows or hides graph if button pressed
	 * @param mouseX X-Coordinate of mouse
	 * @param mouseY Y-Coordinate of mouse
	 */
	public void toggleShow(int mouseX, int mouseY) {
		if (showIcon.checkCollision(mouseX, mouseY)) {
			show = !show;
		}
	}

	/**
	 * Gets width of graph
	 * @return Width of kinematic graph
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Sets the position of kinematics graph
	 * @param xPosition X-Position of top left corner
	 */
	public void setxPosition(int xPosition) {
		this.xPosition = xPosition;
		//position the show icon
		showIcon.setX(xPosition+200);
		showIcon.setY(Y_POSITION-25);
	}
}
