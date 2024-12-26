import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import java.awt.Point;

/**
 * EnergySim
 * An energy simulation that displays energy graphs for a ball rolling down a ramp
 * 
 * @author Amanda
 * @version June 2023
 */

public class EnergySim extends Simulation {
	/**Mouse listener for energy simulation*/
	private BasicMouseListener mouseListener;
	/**Energy frame window*/
	private JFrame energyWindow;
	/**Energy graphics panel*/
	private GraphicsPanel canvas;
	/**Energy options panel*/
	private OptionPanel options;
	
	/**Energy ball*/
	private EnergyBall ball;
	/**Energy bar graphs*/
	private EnergyGraph[] graphs = new EnergyGraph[4];
	/**Start button*/
	private AlternatingImage startButton;
	/**True if running, false if not*/
	private boolean running;
	/**True if slowmo, false if regular*/
	private boolean slowMotionOn;
	/**Height of ramp*/
	private int height;
	/**Slope of ramp*/
	private double slope;
	/**Length of ramp*/
	private double distance;
	/**Friction of ramp in Newtons*/
	private double friction;
	
	
	/**X-position of ramp's left point*/
	private final int RAMPX = 50;
	/**Horizontal length of ramp*/
	private final int SLOPE_LENGTH = 35;
	/**Conversion ratio of pixels to meters*/
	private final int PIXELS_PER_M = 10;
	/**Point where ramp ends*/
	private final Point SLOPE_ENDPOINT = new Point(RAMPX+(SLOPE_LENGTH*PIXELS_PER_M),300);
	
	
	
	/**
	 * Constructs an Energy Simulation
	 * @param menu Menu from which the simulation is loaded
	 */
	public EnergySim(Menu menu) {
		super(menu);
		InputStream inputStream = null;
		try {
			//load images
			inputStream = new FileInputStream("Images/EnergyPreview.png");
			BufferedImage img = ImageIO.read(inputStream);
			setPreview(new Image(img, Constants.CENTER-(img.getWidth()/2), 200));
			inputStream = new FileInputStream("Images/EnergyInstructions.png");
			setInstructionsScreen(ImageIO.read(inputStream));
			inputStream = new FileInputStream("Images/start.png");
			BufferedImage start = ImageIO.read(inputStream);
			inputStream = new FileInputStream("Images/stop.png");
		    BufferedImage stop = ImageIO.read(inputStream);
		    
		    //create start button as an image that changes based on running status
		    startButton = new AlternatingImage(start, stop, 200, 400);
		    
		} catch (IOException e) {
			System.out.println("Energy images missing");
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
		energyWindow = new JFrame("Energy Window");
	    
	   	energyWindow.setLayout(new BorderLayout());
	    energyWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    //Graphics Panel
	    canvas = new GraphicsPanel();
	    canvas.setBackground(Color.WHITE);
	    
	    energyWindow.add(canvas, BorderLayout.CENTER);
	    
	    //Listeners
	    mouseListener = new BasicMouseListener();    
	    canvas.addMouseListener(mouseListener);
	    
	    //set values
		reset();
	    
	    energyWindow.setVisible(false);
	    
	    
	}
	

	
	/**
     * Runs energy simulation
     */
	@Override
	public void run() {
		//reset variables
		reset();
		
		energyWindow.setVisible(true);
		
		//create energy thread
		Thread energyThread = new Thread(() -> {
			while (energyWindow.isVisible()) {
				try  {Thread.sleep(Constants.FRAME_PERIOD);} catch(Exception e){}
				if (running) {
					//run simulation if not paused
					if (!getPaused()) {
						//calculate ball and energy values
						calculateValues();
					}
					//if ball reaches end of ramp, stop simulation 
					if (ball.getX()>SLOPE_ENDPOINT.x+100) {
						stopSim();
					}
				}
				//paint energy window
				energyWindow.repaint();
				
			}
		});
		energyThread.start();

	}

	/**
	 * Draws energy simulation to screen
	 * @param g Graphics screen
	 */
	@Override
	public void draw(Graphics g) {
		super.draw(g);
		startButton.draw(g);
		
		//draw ramp lines
		g.setColor(Color.BLACK);
		g.drawLine(RAMPX, SLOPE_ENDPOINT.y-(height*PIXELS_PER_M), SLOPE_ENDPOINT.x, SLOPE_ENDPOINT.y);
		g.drawLine(SLOPE_ENDPOINT.x, SLOPE_ENDPOINT.y, SLOPE_ENDPOINT.x+100, SLOPE_ENDPOINT.y);
		
		//draw graphs
		for (EnergyGraph graph: graphs) {
			graph.draw(g);
		}
		
		if (running) {
			g.setFont(new Font("Futura", Font.BOLD, 15));
			g.setColor(Color.black);
			//draw ball's y position
			g.drawString("Ball Height: "+ Math.round(ball.getHeight()*100.0)/100.0 + "m", 350, 100);
			//draw ball's velocity
			g.drawString("Current velocity: "+ Math.round(ball.getSpeed()*100.0)/100.0 + "m/s", 350, 120);
			//draw ball
			ball.draw(g);
		}
	}


	/**
	 * Resets energy simulation values
	 */
	@Override
	public void reset() {
		super.reset();

		//resets variables
		height = 5;
		running = false;
		startButton.switchIMG(running);
		slowMotionOn = false;
		energyWindow.setSize(Constants.WIDTH, Constants.HEIGHT);
		
		InputStream inputStream = null;
		try {
			//reset energy graphs
			inputStream = new FileInputStream("Images/gValueInfo.png");
			graphs[0] = new EnergyGraph(Color.BLUE, "Potential Energy", ImageIO.read(inputStream), 600,100);
			inputStream = new FileInputStream("Images/kValueInfo.png");
			graphs[1] = new EnergyGraph(Color.RED, "Kinetic Energy", ImageIO.read(inputStream), 800,100);
			inputStream = new FileInputStream("Images/mValueInfo.png");
			graphs[2] = new EnergyGraph(Color.GREEN, "Mechanical Energy", ImageIO.read(inputStream), 600,320);
			inputStream = new FileInputStream("Images/tValueInfo.png");
			graphs[3] = new EnergyGraph(Color.ORANGE, "Thermal Energy", ImageIO.read(inputStream), 800,320);
		}catch (IOException e){
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
		//create a new energy ball
		ball = new EnergyBall();
	}
	
	/**
	 * Resets simulation track
	 */
	public void resetTrack() {
		//set height, friction, and mass
		distance = 0;
		height = options.getBallHeight();
		ball.setMass(options.getBallMass());
		friction = ((ball.getMass()*9.81)*(options.getBallFriction()/100.0));

		//calculate slope
		slope = ((double)height)/SLOPE_LENGTH;
		
		//set ball's initial position
		ball.setX(RAMPX+10);
		ball.setHeight(height-slope);
		
		//reset graphs
		for (EnergyGraph graph : graphs) {
			graph.setValue(0);
		}
	}

	/**
	 * Calculates and updates variables
	 */
	public void calculateValues() {
		int vIncrement;
		//slow speed according to state 
		if(slowMotionOn) {
			vIncrement = 30;
		}else {
			vIncrement = 5;
		}
		
		//update the ball position by speed (convert meters to pixels)
		ball.setX(ball.getX()+(PIXELS_PER_M*(ball.getSpeed())/vIncrement));
		
		//update ball height if greater than 0
		if ((ball.getHeight()-(slope*(ball.getSpeed())/vIncrement))>0) {
			ball.setHeight(ball.getHeight()-(slope*(ball.getSpeed())/vIncrement));
		}else {
			ball.setHeight(0);
		}
		
		//change the y position of ball based on height in meters
		ball.setY(SLOPE_ENDPOINT.y-(ball.getHeight()*PIXELS_PER_M)-ball.getWidth());
		
		//set gravitational energy
		double gEnergy = ball.getHeight()*ball.getMass()*9.81;
		
		//calculate distance traveled by ball using distance formula
		distance = Math.sqrt(Math.pow((ball.getX()-RAMPX)/PIXELS_PER_M,2) + Math.pow(height-ball.getHeight(), 2));
		//update thermal energy
		double tEnergy = distance*friction;
		
		//update mechanical energy (initial gravitational energy - thermal energy)
		double mEnergy = (height*ball.getMass()*9.81)-tEnergy;
		
		//update kinetic energy
		double kEnergy = mEnergy-gEnergy;
		
		//update car speed
		double carV = Math.sqrt((2*kEnergy)/ball.getMass());

		//update graph values
		graphs[0].setValue(gEnergy);
		graphs[1].setValue(kEnergy);
		graphs[2].setValue(mEnergy);
		graphs[3].setValue(tEnergy);
	
		//update velocity
		ball.setSpeed(carV);

	}
	
	/**
	 * Stops running ball simulation
	 */
	public void stopSim() {
		//switch start button
		running = !running;
		startButton.switchIMG(running);
		//make paused false
		if ((!running)&&getPaused()) {
			setPaused(false);
		}
	}
	
	/**
	 * Exit energy simulation
	 */
	@Override
	public void exit() {
		energyWindow.remove(options);
		energyWindow.dispose();
		super.exit();
	}
	
	/**
	 * GraphicsPanel
	 * Custom subclass of Jpanel for displaying energy graphics
	 * 
	 * @author Amanda
	 * @version June 2023
	 */
	private class GraphicsPanel extends JPanel{
		/**
		 * Constructs a GraphicsPanel and sets initial dimensions
		 */
	  	public GraphicsPanel(){
	  		setPreferredSize(new Dimension(Constants.WIDTH, Constants.HEIGHT));
	  	}
	  	/**
	  	 * Draws energy simulation components to screen
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
	 * OptionPanel
	 * Custom subclass of Jpanel for displaying energy options
	 * 
	 * @author Amanda
	 * @version June 2023
	 */
	private class OptionPanel extends JPanel{
		/**Checkbox for slow motion option*/
		private JCheckBox slowMotion;
		/**Slider for mass (kg)*/
		private JSlider massSlider;
		/**Slider for height (m)*/
		private JSlider heightSlider;
		/**Slider for friction coefficient (N/N)*/
		private JSlider frictionSlider;
		
		/**
		 * Constructs an options panel
		 */
		public OptionPanel(){
			//slow motion checkbox
			slowMotion = new JCheckBox("Slow Motion");
			slowMotion.addActionListener(new ActionListener() {
				/**
				 * Called when checkbox clicked, toggles slow motion
				 * 
				 * @param e ActionEvent representing toggle
				 */
            	@Override
	            public void actionPerformed(ActionEvent e) {
            		slowMotionOn = !slowMotionOn;
	            }
			});

	         //mass slider
	         JLabel massLabel = new JLabel("Mass (kg)");
	
	         massSlider = new JSlider(JSlider.HORIZONTAL, 1, 5, 1);
	         massSlider.setMajorTickSpacing(1);
	         massSlider.setPaintTicks(true);
	         massSlider.setPaintLabels(true);

	         
	         //height slider
	         JLabel hLabel = new JLabel("Height (m)");
	
	         heightSlider = new JSlider(JSlider.HORIZONTAL, 5, 20, 5);
	         heightSlider.setMajorTickSpacing(5);
	         heightSlider.setMinorTickSpacing(1);
	         heightSlider.setPaintTicks(true);
	         heightSlider.setPaintLabels(true);

	        
	         //friction slider
	         JLabel fLabel = new JLabel("Friction Coefficient (%)");
	
	         frictionSlider = new JSlider(JSlider.HORIZONTAL, 0, 10, 0);
	         frictionSlider.setMajorTickSpacing(2);
	         frictionSlider.setMinorTickSpacing(1);
	         frictionSlider.setPaintTicks(true);
	         frictionSlider.setPaintLabels(true);

	         //add components to panel
	         add(slowMotion);
	         add(massLabel);
	         add(massSlider);
	         add(hLabel);
	         add(heightSlider);
	         add(fLabel);
	         add(frictionSlider);
	         
	         //set panel size
	         setPreferredSize(new Dimension(200, Constants.HEIGHT));
	  	}
		
		/**
		 * Gets value of height slider
		 * @return Track height
		 */
		public int getBallHeight() {
			return heightSlider.getValue();
		}
		
		/**
		 * Gets value of mass slider
		 * @return Ball mass
		 */
		public int getBallMass() {
			return massSlider.getValue();
		}
		
		/**
		 * Gets value of friction slider
		 * @return Track friction
		 */
		public int getBallFriction() {
			return frictionSlider.getValue();
		}
	}
	
	/**
	 * BasicMouseListener
	 * Implements MouseListener for energy mouse controls
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
			
			//if instructions clicked, add the options panel to frame
			if (isInstructionsOpen()) {
				setInstructionsOpen(false);
				options = new OptionPanel();
				options.setLayout(new GridLayout(0,1));
				energyWindow.add(options, BorderLayout.EAST);
		        energyWindow.setSize(Constants.WIDTH+200, Constants.HEIGHT);
		        energyWindow.setVisible(true);
			}else {
				//exit
				if (getExitButton().checkCollision(mouseX,mouseY)) {
					exit();
				}
				//pause
				if ((running)&&(getPauseButton().checkCollision(mouseX, mouseY))) {
					pause();
				}
				//start or stop sim
				if(startButton.checkCollision(mouseX, mouseY)) {
					stopSim();
					if (running) {
						resetTrack();
					}
				}
				
				//toggle graph information
				for (EnergyGraph graph: graphs) {
					if (graph.getInfoIcon().checkCollision(mouseX, mouseY)) {
						graph.toggleInfo();
					}
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

}
