import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * Simulation
 * Abstract superclass for physics simulations
 * 
 * @author Amanda
 * @version June 2023
 */
public abstract class Simulation {
	/**Simulation menu preview*/
	private Image preview;
	
	/**True if paused, false if running*/
	private boolean paused;
	/**True if instructions open, false if simulation running*/
	private boolean instructionsOpen;
	
	/**Button to return to menu*/
	private Image exitButton;
	/**Button to pause/play simulation*/
	private AlternatingImage pauseButton;
	/**Image of pause button*/
	private BufferedImage pause;
	/**Image of play button*/
	private BufferedImage play;
	/**Instructions image*/
	private BufferedImage instructionsScreen;
	/**Simulation background image*/
	private BufferedImage background;
	
	/**Simulation's menu*/
	private Menu menu;
	
	/**
	 * Constructs a physics simulation 
	 * @param menu Menu from which the simulation is loaded
	 */
	public Simulation (Menu menu) {
		InputStream inputStream = null;
		try {
			//set up images
			inputStream = new FileInputStream("Images/xButton.png");
			BufferedImage exitIMG = ImageIO.read(inputStream);
			exitButton = new Image(exitIMG, 910,30);
			inputStream = new FileInputStream("Images/pauseButton.png");
			pause = ImageIO.read(inputStream);
			inputStream = new FileInputStream("Images/play.png");
			play = ImageIO.read(inputStream);
			pauseButton = new AlternatingImage(pause, play, 840,30);
		} catch (IOException e) {
			System.out.println("Simulation images missing");
		}finally {
			if (inputStream != null) {
		        try {
		            inputStream.close();
		        } catch (IOException e) {
		            System.out.println("Stream error");
		        }
		    }
		}
		this.menu = menu;
	}
	
	/**
	 * Abstract method to run simulation
	 */
	public abstract void run ();
	
	/**
	 * Draws simulation to screen
	 * @param g Graphics screen
	 */
	public void draw (Graphics g) {
		g.drawImage(background,0,0, null);
		exitButton.draw(g);
		pauseButton.draw(g);
	}
	
	/**
	 * Draws simualtion's instructions
	 * @param g Graphics screen
	 */
	public void drawInstructions(Graphics g) {
		g.drawImage(instructionsScreen, 0, 0, null);
	}
	
	/**
	 * Toggles the pause/play button
	 * @return Boolean representing whether simulation is paused
	 */
	public boolean pause() {
		paused = !paused;
		pauseButton.switchIMG(paused);
		return paused;
	}
	
	/**
	 * Sets the pause button to a specified value
	 * @param p True for paused, false for playing
	 */
	public void setPaused(boolean p) {
		this.paused = p;
		pauseButton.setImage(pause);
	}
	
	/**
	 * Resets the simulation
	 */
	public void reset() {
		setPaused(false);
		instructionsOpen = true;
	}
	
	/**
	 * Returns to menu
	 */
	public void exit() {
		menu.run();
	}
	
	/**
	 * Sets simulation preview to display image
	 * @param img Image to display
	 */
	public void setPreview(Image img) {
		this.preview = img;
	}
	
	/**
	 * Gets preview for simulation
	 * @return Simulation preview image
	 */
	public Image getPreview() {
		return this.preview;
	}
	
	/**
	 * Gets paused state
	 * @return True for paused, false for running
	 */
	public boolean getPaused() {
		return this.paused;
	}
	
	/**
	 * Gets simulation exit button
	 * @return Image exit button
	 */
	public Image getExitButton() {
		return exitButton;
	}
	
	/**
	 * Gets simulation pause button
	 * @return Image pause button
	 */
	public Image getPauseButton() {
		return pauseButton;
	}
	
	/**
	 * Sets simulation background
	 * @param background Display image
	 */
	public void setBackground(BufferedImage background) {
		this.background = background;
	}
	
	/**
	 * Gets whether instructions is open
	 * @return True for open, false otherwise
	 */
	public boolean isInstructionsOpen() {
		return instructionsOpen;
	}
	
	/**
	 * Sets the instructions state 
	 * @param instructionsOpen True for open, false otherwise
	 */
	public void setInstructionsOpen(boolean instructionsOpen) {
		this.instructionsOpen = instructionsOpen;
	}
	
	/**
	 * Sets the instruction image
	 * @param instructionsScreen BufferedImage of instructions 
	 */
	public void setInstructionsScreen(BufferedImage instructionsScreen) {
		this.instructionsScreen = instructionsScreen;
	}
	
	
	
	
}
