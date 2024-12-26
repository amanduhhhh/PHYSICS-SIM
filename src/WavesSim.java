import java.awt.BorderLayout;
import java.awt.Dimension;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * WavesSim
 * Waves simulation that shows interference of different frequencies and amplitudes
 * 
 * @author Amanda
 * @version June 2023
 */
public class WavesSim extends Simulation {
	/**Waves object*/
	private WaveString wave;
	
	/**Waves frame window*/
	private JFrame waveWindow;
	/**Waves graphics panel*/
	private GraphicsPanel canvas;
	/**Waves options settings*/
	private OptionPanel options;
	/**Mouse listener for waves simulation*/
	private BasicMouseListener mouseListener;
	
	/**Start button*/
	private AlternatingImage startButton;
	/**True if running, false if not*/
	private boolean running;
	
	/**
	 * Constructs an Wave Simulation
	 * @param menu Menu from which the simulation is loaded
	 */
	public WavesSim(Menu menu) {
		super(menu);
		InputStream inputStream = null;
		try {
			//loads images
			inputStream = new FileInputStream("Images/WavePreview.png");
			BufferedImage img = ImageIO.read(inputStream);
			setPreview(new Image(img, Constants.WIDTH-img.getWidth()-50,200));
			inputStream = new FileInputStream("Images/WaveBG.png");
			setBackground(ImageIO.read(inputStream));
			inputStream = new FileInputStream("Images/WaveInstructions.png");
			setInstructionsScreen(ImageIO.read(inputStream));
			inputStream = new FileInputStream("Images/start.png");
			BufferedImage start = ImageIO.read(inputStream);
			inputStream = new FileInputStream("Images/stop.png");
		    BufferedImage stop = ImageIO.read(inputStream);
		    
		    //create start button as an image that changes based on running status
		    startButton = new AlternatingImage(start, stop, Constants.CENTER-start.getWidth()/2, 400);
		    
		    
		} catch (IOException e) {
			System.out.println("Waves images missing");
		}finally {
			if (inputStream != null) {
		        try {
		            inputStream.close();
		        } catch (IOException e) {
		            System.out.println("Stream error");
		        }
		    }
		}
		
		//set jFrame
		waveWindow = new JFrame("Waves Window");
	    waveWindow.setLayout(new BorderLayout());
	    waveWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    //Graphics Panel
	    canvas = new GraphicsPanel();
	    waveWindow.add(canvas, BorderLayout.CENTER);
	    
	    waveWindow.setVisible(false);
	    
	    //mouse listener
	    mouseListener = new BasicMouseListener(); 
	    canvas.addMouseListener(mouseListener);
	    
	    wave = new WaveString();
	    
	    //set values
		reset();
	    
	    
	}
	
	/**
     * Runs wave simulation
     */
	@Override
	public void run() {
		waveWindow.setVisible(true);
		reset();
		Thread waveThread = new Thread(() -> {
			while (waveWindow.isVisible()) {
				try  {Thread.sleep(Constants.FRAME_PERIOD);} catch(Exception e){}
				if ((running)&&(!getPaused())) {
					wave.moveWave();
				}
				waveWindow.repaint();
			}
		});
		waveThread.start();

	}

	/**
	 * Draws waves simulation to screen
	 * @param g Graphics screen
	 */
	@Override
	public void draw(Graphics g) {
		super.draw(g);
		startButton.draw(g);
		if (running) {
    		wave.draw(g);
    	}

	}
	
	
	/**
	 * Resets wave simulation values
	 */
	@Override
	public void reset() {
		super.reset();
		running = false;
		startButton.switchIMG(running);
		waveWindow.setSize(Constants.WIDTH, Constants.HEIGHT);
		wave.reset();

	}
	
	/**
	 * Exit wave simulation
	 */
	@Override
	public void exit() {
		waveWindow.remove(options);
		waveWindow.dispose();
		super.exit();
	}
	
	/**
	 * GraphicsPanel
	 * Custom subclass of Jpanel for displaying wave graphics
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
	  	 * Draws wave simulation components to screen
	  	 * @param g Graphics screen
	  	 */
	  	@Override
	  	public void paintComponent(Graphics g){
		    super.paintComponent(g);
		    //draw
		    if (isInstructionsOpen()) {
		    	drawInstructions(g);
		    }else {
		    	draw(g);
		    	
		    }
		    
  		}
	}
	
	/**
	 * BasicMouseListener
	 * Implements MouseListener for wave mouse controls
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
			
			//adds option panel to frame
			if (isInstructionsOpen()) {
				setInstructionsOpen(false);
				options = new OptionPanel();
				options.setLayout(new GridLayout(0,1));
				waveWindow.add(options, BorderLayout.EAST);
		        waveWindow.setSize(Constants.WIDTH+200, Constants.HEIGHT);
		        waveWindow.setVisible(true);
			    
			}else {
				//exit
				if (getExitButton().checkCollision(mouseX,mouseY)) {
					exit();
				}
				//pause
				if ((getPauseButton().checkCollision(mouseX, mouseY))&&(running)) {
					pause();
				}
				//starts/stops simulation
				if(startButton.checkCollision(mouseX, mouseY)) {
					running = !running;
					startButton.switchIMG(running);
					if ((!running)&&getPaused()) {
						setPaused(false);
					}
					if (running) {
						wave.restartWave();
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
	
	/**
	 * OptionPanel
	 * Custom subclass of Jpanel for displaying wave options
	 * 
	 * @author Amanda
	 * @version June 2023
	 */
	private class OptionPanel extends JPanel{
		/**Checkbox for showing initial wave*/
		private JCheckBox wave1Option;
		/**Checkbox for showing second wave*/
		private JCheckBox wave2Option;
		/**Checkbox for showing interference wave*/
		private JCheckBox interferenceOption;
		/**Checkbox for simulation speed*/
		private JCheckBox slowMotion;
		/**Slider for frequency wave 1*/
		private JSlider fSlider1;
		/**Slider for amplitude wave 1*/
		private JSlider aSlider1;
		/**Slider for frequency wave 2*/
		private JSlider fSlider2;
		/**Slider for amplitude wave 2*/
		private JSlider aSlider2;
		
		/**
		 * Constructs an options panel
		 */
	  	public OptionPanel(){
	  		 //show wave 1
	  		 wave1Option = new JCheckBox("Show Wave 1 (Blue)");
	  		 wave1Option.setSelected(true);
	  		 wave1Option.addActionListener(new ActionListener() {
	  			/**
				 * Called when checkbox clicked, toggles wave 1
				 * 
				 * @param e ActionEvent representing toggle
				 */
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	wave.setInitial(wave1Option.isSelected());
	            }
  			 });
	  		 
	  		 //show wave 2
	         wave2Option = new JCheckBox("Show Wave 2 (Red)");
	         wave2Option.setSelected(true);
	         wave2Option.addActionListener(new ActionListener() {
	        	 	/**
					 * Called when checkbox clicked, toggles wave 2
					 * 
					 * @param e ActionEvent representing toggle
					 */
		            @Override
		            public void actionPerformed(ActionEvent e) {
		            	wave.setReflect(wave2Option.isSelected());
		            }
  			 });
	         
	         //show interference wave
	         interferenceOption = new JCheckBox("Show Interference Wave");
	         interferenceOption.setSelected(true);
	         interferenceOption.addActionListener(new ActionListener() {
	        	 /**
					 * Called when checkbox clicked, toggles interference wave
					 * 
					 * @param e ActionEvent representing toggle
					 */
		            @Override
		            public void actionPerformed(ActionEvent e) {
		            	wave.setInterference(interferenceOption.isSelected());
		            }
  			 });

	         //speed state
	         slowMotion = new JCheckBox("Slow Motion");
	         slowMotion.addActionListener(new ActionListener() {
	        	 	/**
					 * Called when checkbox clicked, toggles slow motion
					 * 
					 * @param e ActionEvent representing toggle
					 */
		            @Override
		            public void actionPerformed(ActionEvent e) {
		            	wave.setSlowMotion(slowMotion.isSelected());
		            }
			 });
	         
	         //frequency slider for wave 1
	         JLabel fLabel1 = new JLabel("Frequency Wave 1: 20");
	         fSlider1 = new JSlider(JSlider.HORIZONTAL, 10, 100, 20);
	         fSlider1.setMajorTickSpacing(10);
	         fSlider1.setMinorTickSpacing(5);
	         fSlider1.setPaintTicks(true);
	         fSlider1.setPaintLabels(true);
	         fSlider1.addChangeListener(new ChangeListener() {
	        	 /**
				 * Called when slider changed, changes frequency wave1
				 * 
				 * @param e ChangeEvent representing slider change
				 */
	             @Override
	             public void stateChanged(ChangeEvent e) {
	                 int value = fSlider1.getValue();
	                 fLabel1.setText("Frequency Wave 1: " + value);
	                 wave.setFrequency1(value/1000.0);
	             }
	         });
	         
	         //amplitude slider for wave 1
	         JLabel aLabel1 = new JLabel("Amplitude Wave 1: 50");

	         aSlider1 = new JSlider(JSlider.HORIZONTAL, 20, 100, 50);
	         aSlider1.setMajorTickSpacing(10);
	         aSlider1.setMinorTickSpacing(5);
	         aSlider1.setPaintTicks(true);
	         aSlider1.setPaintLabels(true);
	         aSlider1.addChangeListener(new ChangeListener() {
	        	 /**
					 * Called when slider changed, changes amplitude wave1
					 * 
					 * @param e ChangeEvent representing slider change
					 */
	             @Override
	             public void stateChanged(ChangeEvent e) {
	                 int value = aSlider1.getValue();
	                 aLabel1.setText("Amplitude Wave 1: " + value);
	                 wave.setAmplitude1(value);
	             }
	         });
	         
	         //frequency slider for wave 2
	         JLabel fLabel2 = new JLabel("Frequency Wave 2: 20");

	         fSlider2 = new JSlider(JSlider.HORIZONTAL, 10, 100, 20);
	         fSlider2.setMajorTickSpacing(10);
	         fSlider2.setMinorTickSpacing(5);
	         fSlider2.setPaintTicks(true);
	         fSlider2.setPaintLabels(true);
	         fSlider2.addChangeListener(new ChangeListener() {
	        	 /**
					 * Called when slider changed, changes frequency wave2
					 * 
					 * @param e ChangeEvent representing slider change
					 */
	             @Override
	             public void stateChanged(ChangeEvent e) {
	                 int value = fSlider2.getValue();
	                 fLabel2.setText("Frequency Wave 2: " + value);
	                 wave.setFrequency2(value/1000.0);
	             }
	         });
	         
	         //aplitude slider for wave 2
	         JLabel aLabel2 = new JLabel("Amplitude Wave 2: 50");

	         aSlider2 = new JSlider(JSlider.HORIZONTAL, 20, 100, 50);
	         aSlider2.setMajorTickSpacing(10);
	         aSlider2.setMinorTickSpacing(5);
	         aSlider2.setPaintTicks(true);
	         aSlider2.setPaintLabels(true);
	         aSlider2.addChangeListener(new ChangeListener() {
	        	 /**
					 * Called when slider changed, changes amplitude wave2
					 * 
					 * @param e ChangeEvent representing slider change
					 */
	             @Override
	             public void stateChanged(ChangeEvent e) {
	                 int value = aSlider2.getValue();
	                 aLabel2.setText("Amplitude Wave 2: " + value);
	                 wave.setAmplitude2(value);
	             }
	         });
	         
	         // Add the elements to the frame
	         add(wave1Option);
	         add(wave2Option);
	         add(interferenceOption);
	         add (slowMotion);
	         add(fLabel1);
	         add(fSlider1);
	         add(aLabel1);
	         add(aSlider1);
	         add(fLabel2);
	         add(fSlider2);
	         add(aLabel2);
	         add(aSlider2);
	         
	         setPreferredSize(new Dimension(200, Constants.HEIGHT));
	  	}
	}


}
