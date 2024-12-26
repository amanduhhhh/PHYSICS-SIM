import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * KinematicsSim
 * A kinematics simulation where user drags car to create kinematics graphs
 * 
 * @author Amanda
 * @version June 2023
 */
public class KinematicsSim extends Simulation{
	/**Simulation car*/
	private KinematicCar car;
	/**Position values of each frame*/
	private int[] xValues;
	/**Velocity values of each frame*/
	private int[] vValues;
	/**Kinematics value graphs*/
	private KinematicGraph[] graphs;
	
	/**Mouse Listener for kinematics simulation*/
	private BasicMouseListener mouseListener;
	/**Mouse motion listener for kinematics simulation*/
	private BasicMouseMotionListener motionListener;
	/**Kinematics frame window*/
	private JFrame kinWindow;
	/**Kinematics graphics panel*/
	private GraphicsPanel canvas;
	
	/**Current frame count*/
	private int frames;
	/**Amount of frames in a graph period*/
	private final int PERIOD = 100;
	
	/**
	 * Constructs a Kinematics Simulation
	 * @param menu Menu from which the simulation is loaded
	 */
	public KinematicsSim(Menu menu) {
		super(menu);
		InputStream inputStream = null;
		try {
			//set images
			inputStream = new FileInputStream("Images/kinPreview.png");
			BufferedImage img = ImageIO.read(inputStream);
			setPreview(new Image(img, 50,200));
			inputStream = new FileInputStream("Images/kinInstructions.png");
			setInstructionsScreen(ImageIO.read(inputStream));
			inputStream = new FileInputStream("Images/kinBG.png");
			setBackground(ImageIO.read(inputStream));
			inputStream = new FileInputStream("Images/kinematicCar.png");
			car = new KinematicCar(ImageIO.read(inputStream));
			
		} catch (IOException e) {
			System.out.println("Kinematics images missing");
		} finally {
			if (inputStream != null) {
		        try {
		            inputStream.close();
		        } catch (IOException e) {
		            System.out.println("Stream error");
		        }
		    }
		}
		
		//set jFrame
		kinWindow = new JFrame("Kinematics Window");
	    kinWindow.setSize(Constants.WIDTH, Constants.HEIGHT);
	    kinWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    //Graphics Panel
	    canvas = new GraphicsPanel();
	    
	    kinWindow.add(canvas);
	    
	    //Listeners
	    mouseListener = new BasicMouseListener();    
	    motionListener = new BasicMouseMotionListener();
	    canvas.addMouseListener(mouseListener);
	    canvas.addMouseMotionListener(motionListener);
	    
	    //set values
		reset();
	    
	    kinWindow.setVisible(false);
	}
	

	
	/**
     * Runs kinematics simulation
     */
	@Override
	public void run() {
		//reset variables
		reset();
		kinWindow.setVisible(true);
		//create kinematics thread
		Thread kinematicsThread = new Thread(() -> {
			while (kinWindow.isVisible()) {
				try  {Thread.sleep(Constants.FRAME_PERIOD);} catch(Exception e){}
				//count frames
				if ((!getPaused())&&(!isInstructionsOpen())) {
					frames++;
					//reset frames
					if (frames>=xValues.length) {
						frames = 0;
					}
					//fill position array with car position
					xValues[frames] = car.getX()-Constants.CENTER;
					calculateValues();
					
				}
				//repaint window
				kinWindow.repaint();
			}
		});
		kinematicsThread.start();

	}

	/**
	 * Draws kinematic simulation to screen
	 * @param g Graphics screen
	 */
	@Override
	public void draw(Graphics g) {
		super.draw(g);
		//draw car
		car.draw(g);
		//draw graphs
		for (KinematicGraph graph: graphs) {
			graph.draw(g);
		}
	}
	
	/**
	 * Resets kinematic simulation values
	 */
	@Override
	public void reset() {
		super.reset();
		//reset frames
		frames = 0;
		//reset points array
		xValues = new int[PERIOD];
		vValues = new int[PERIOD];
		
		//reset graphs
		graphs = new KinematicGraph[3];
		graphs[0] =  new KinematicGraph("Position", xValues.length, Color.blue);
		graphs[0].setxPosition(30);
		
		graphs[1] = new KinematicGraph("Velocity", xValues.length, new Color(0,212,134));
		graphs[1].setxPosition(Constants.CENTER-(graphs[1].getWidth()/2)-10);
		
		graphs[2] = new KinematicGraph("Acceleration", xValues.length, Color.red);
		graphs[2].setxPosition(Constants.WIDTH-40-graphs[2].getWidth()-10);
		
		//reset car
		car.setX(Constants.CENTER);
		
	}

	/**
	 * Calculates and updates variables
	 */
	public void calculateValues() {
		//position graph
		graphs[0].addPoint(frames, (xValues[frames]/5));

		if (frames>=1) {
			//update velocity values
			vValues[frames-1]= (xValues[frames]-xValues[frames-1])/2;
			
			//update velocity graph by subtracting positions
			int vPosition = Math.min((vValues[frames-1]),100);
			//make sure absolute value is within 100
			vPosition = Math.max(vPosition,-100);
			graphs[1].addPoint(frames-1, vPosition);
			
			if (frames>1) {
				int startFrame = frames-1;
				//update acceleration value by subtracting velocities
				int aPosition = Math.min(100,(vValues[startFrame]-vValues[startFrame-1])/2);
				//make sure absolute value is within 100;
				aPosition = Math.max(aPosition, -100);
				graphs[2].addPoint(frames-2, aPosition);
			}
		}
	}
	
	/**
	 * Exit kinematics simulation
	 */
	@Override
	public void exit() {
		kinWindow.dispose();
		super.exit();
	}
	
	/**
	 * GraphicsPanel
	 * Custom subclass of Jpanel for displaying kinematics graphics
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
	  	 * Draws kinematics simulation components to screen
	  	 * @param g Graphics screen
	  	 */
	  	@Override
	  	public void paintComponent(Graphics g){
		    super.paintComponent(g);
		    if (isInstructionsOpen()) {
		    	drawInstructions(g);
		    }else{
		    	draw(g);
		    }
		    
  		}
	}
	
	/**
	 * BasicMouseListener
	 * Implements MouseListener for kinematics mouse controls
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
			
			if (isInstructionsOpen()) {
				setInstructionsOpen(false);
			}else {
				//exit
				if (getExitButton().checkCollision(mouseX,mouseY)) {
					exit();
				}
				//pause
				if (getPauseButton().checkCollision(mouseX, mouseY)) {
					pause();
				}
				//toggle graphs
				for (KinematicGraph graph: graphs) {
					graph.toggleShow(mouseX, mouseY);
				}
			}
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
	 * BasicMouseMotionListener
	 * Implements MouseMotionListener for menu mouse controls
	 * 
	 * @author Amanda
	 * @version June 2023
	 */
	private class BasicMouseMotionListener implements MouseMotionListener{
		/**
		 * Called when mouse is dragged
		 * 
		 * @param e MouseEvent representing drag
		 */
		@Override
		public void mouseDragged(MouseEvent e) {
			int mouseX = e.getX();
			int mouseY = e.getY();
			if ((!isInstructionsOpen())&&(0<mouseX)&&(mouseX<Constants.WIDTH)&&(car.checkCollision(mouseX,mouseY))&&(!getPaused())) {
				car.setX(mouseX);
			}
		}

		public void mouseMoved(MouseEvent e) {
		}
	}
	
}
