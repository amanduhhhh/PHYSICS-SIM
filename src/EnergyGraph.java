import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * EnergyGraph
 * Graph object for energy class
 * 
 * @author Amanda
 * @version June 2023
 */
public class EnergyGraph {
	/**Graph y-axis label*/
	private String yLabel;
	/**BufferedImage of grid*/
	private BufferedImage grid;
	/**Information toggle icon*/
	private Image infoIcon;
	/**Information bubble*/
	private Image valueInfo;
	/**True if info is shown, false if hidden*/
	private boolean showInfo;
	/**Color of bar graph*/
	private Color color;
	/**Value of bar graph*/
	private double value;
	/**Graph x-position*/
	private int xPosition;
	/**Graph y-position*/
	private int yPosition;
	
	/**Width of graph*/
	private final int WIDTH = 60;
	
	/**
	 * Constructs an energy graph with color, label, information, and coordinates
	 * @param color Color of bar graph
	 * @param yLabel Label of y-axis
	 * @param infoIMG Information bubble image
	 * @param x X-position of grid
	 * @param y Y-position of grid
	 */
	public EnergyGraph(Color color, String yLabel, BufferedImage infoIMG, int x, int y) {
		InputStream inputStream = null;
		try {
			//upload images
			inputStream = new FileInputStream("Images/energyGrid.png");
			grid = ImageIO.read(inputStream);
			inputStream = new FileInputStream("Images/infoIcon.jpg");
			infoIcon = new Image(ImageIO.read(inputStream));
			infoIcon.setX(x-infoIcon.getWidth()-10);
			infoIcon.setY(y+infoIcon.getHeight());
			this.valueInfo = new Image(infoIMG);
			valueInfo.setX(infoIcon.getX()-valueInfo.getWidth());
			valueInfo.setY(infoIcon.getY());
			
		} catch (IOException e) {
			System.out.println("Energy graph images missing");
		}finally {
			if (inputStream != null) {
		        try {
		            inputStream.close();
		        } catch (IOException e) {
		            System.out.println("Stream error");
		        }
		    }
		}
		this.color = color; 
		this.yLabel = yLabel;
		this.xPosition = x;
		this.yPosition = y; 
		
	}
	
	/**
	 * Draws graph to screen
	 * @param g Graphics screen
	 */
	public void draw(Graphics g) {
		//draw grid
		g.drawImage(grid, xPosition, yPosition, null);
		g.setColor(color);
		
		//draw bar graph
		double displayVal = value/6;
		g.fillRect(xPosition + (grid.getWidth()-WIDTH)/2, yPosition+grid.getHeight()-(int)displayVal, WIDTH, (int)displayVal);
		
		//draw label
		g.setFont(new Font("Futura", Font.BOLD, 15));
		String newLabel = yLabel+": "+ Math.round(value*100.0)/100.0 +"J";
		int strWidth = g.getFontMetrics().stringWidth(newLabel);
		g.setColor(Color.black);
		g.drawString(newLabel, xPosition+(grid.getWidth()-strWidth)/2, yPosition+grid.getHeight()+20);
		
		//draw info icon
		infoIcon.draw(g);
		
		//show info
		if (showInfo) {
			valueInfo.draw(g);
		}
		
	}

	/**
	 * Gets value 
	 * @return Value of energy graph (Joules)
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Sets value 
	 * @param value Value of energy graph (Joules)
	 */
	public void setValue(double value) {
		this.value = value;
	}
	
	/**
	 * Sets information to visible/hidden
	 */
	public void toggleInfo() {
		showInfo = !showInfo;
	}
	
	/**
	 * Gets information icon
	 * @return Graph information toggle icon
	 */
	public Image getInfoIcon() {
		return infoIcon;
	}
}
