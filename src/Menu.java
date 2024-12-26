import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Menu
 * Displays menu and houses simulations
 * 
 * @author Amanda
 * @version June 2023
 */

public class Menu {
	/**Array of available simulations*/
	private Simulation[] simulations = {new KinematicsSim(this), new EnergySim(this), new WavesSim(this)};
	/**Menu mouse event listener*/
	private BasicMouseListener mouseListener;
	/**Menu frame window*/
	private JFrame menuWindow;
	/**Menu graphic JPanel*/
	private GraphicsPanel canvas;
	/**Background image*/
	private BufferedImage bg;
	
	/**
	 * Constructs a menu object 
	 */
	public Menu(){
		//set jFrame
		menuWindow = new JFrame("Menu Window");
	    menuWindow.setSize(Constants.WIDTH, Constants.HEIGHT);
	    menuWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    //Graphics Panel
	    canvas = new GraphicsPanel();
	    
	    menuWindow.add(canvas);
	    
	    //Listeners
	    mouseListener = new BasicMouseListener();    
	    canvas.addMouseListener(mouseListener);
	    
	    menuWindow.setVisible(true);
	    
	    //set background image
	    InputStream inputStream = null;
	    try {
	    	inputStream = new FileInputStream("Images/Menu.png");
			bg = ImageIO.read(inputStream);
		} catch (IOException e) {
			System.out.println("Menu background image missing");
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
	 * Runs menu screen
	 */
	public void run(){
		menuWindow.setVisible(true);
		//create new thread
		Thread menuThread = new Thread(() -> {
			//redraw screen 
			while (menuWindow.isVisible()) {
				try  {Thread.sleep(Constants.FRAME_PERIOD);} catch(Exception e){}
				menuWindow.repaint();
			}
		});
		menuThread.start();
	}
	
	/**
	 * Selects and runs a simulation 
	 * @param mouseX X-Position of mouse
	 * @param mouseY Y-Position of mouse
	 */
	public void select(int mouseX, int mouseY) {
		for (Simulation sim: simulations) {
			if (sim.getPreview().checkCollision(mouseX, mouseY)) {
				menuWindow.dispose();
				sim.run();
			}
		}
	}
	
	/**
	 * BasicMouseListener
	 * Implements MouseListener for menu mouse controls
	 * 
	 * @author Amanda
	 * @version June 2023
	 */
	private class BasicMouseListener implements MouseListener{
		/**
		 * Called when mouse button clicked
		 * 
		 * @param e MouseEvent representing click
		 */
		public void mouseClicked(MouseEvent e){
			int mouseX = e.getX();
			int mouseY = e.getY();
			
			select(mouseX, mouseY);
	    }

	    public void mousePressed(MouseEvent e){  
	    }

	    public void mouseReleased(MouseEvent e){ 
	    }

	    public void mouseEntered(MouseEvent e){ 
	    }
	    
	    public void mouseExited(MouseEvent e){ 
	    }
	  }
	
	/**
	 * GraphicsPanel
	 * Custom subclass of Jpanel for displaying menu graphics
	 * 
	 * @author Amanda
	 * @version June 2023
	 */
	private class GraphicsPanel extends JPanel{
		/**
		 * Constructs a GraphicsPanel
		 */
	  	public GraphicsPanel(){
		    setFocusable(true);
    		requestFocusInWindow();
	  	}
	  
	  	/**
	  	 * Draws menu items to screen
	  	 */
	  	public void paintComponent(Graphics g){
		    super.paintComponent(g); 
		    //Draw background
		    g.drawImage(bg, 0,0,null);
		    
			//draw previews
			for (Simulation sim: simulations) {
				sim.getPreview().draw(g);
			}

  		}
	}
}
