import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

/**
 * WaveString
 * Wave object for waves simulation
 * 
 * @author Amanda
 * @version June 2023
 */

public class WaveString {
	/**Default wave amplitude*/
	private final double DEFAULT_AMP = 50;
	/**Default wave frequency*/
	private final double DEFAULT_FREQ = 0.02;
	/**Amplitude of wave 1*/
	private double amplitude1 = DEFAULT_AMP;
	/**Amplitude of wave 2*/
	private double amplitude2 = DEFAULT_AMP;
	/**Frequency of wave 1*/
	private double frequency1 = DEFAULT_FREQ;
	/**Frequency of wave 2*/
	private double frequency2 = DEFAULT_FREQ;
	
	/**Speed of waves measured in pixel movement/frame*/
	private int speed = 10;
	/**Wave shift (degrees)*/
	private int wavePosition = 0;
	
	/**Side margins of waves*/
	private final int MARGIN = 75;
	/**Width of wave display*/
	private final int WAVE_WIDTH = Constants.WIDTH-(2*MARGIN);
	/**Equilibrium point of waves*/
	private final int WAVE_HEIGHT = 219;
	/**Distance between wave points*/
    final double X_INCREMENT = 0.1; 
	
    /**True if initial wave is shown, false if hidden*/
	private boolean initialOn;
	/**True if second wave is shown, false if hidden*/
	private boolean reflectOn;
	/**True if interference wave is shown, false if hidden*/
	private boolean interferenceOn;

	/**
	 * Constructs a wave object 
	 */
	public WaveString() {
		reset();
	}
	
	/**
	 * Resets all values to default
	 */
	public void reset() {
		amplitude1 = DEFAULT_AMP;
		amplitude2 = DEFAULT_AMP;
		frequency1 = DEFAULT_FREQ;
		frequency2 = DEFAULT_FREQ;
		initialOn = true;
		reflectOn = true;
		interferenceOn = true;
		restartWave();
		speed = 10;
	}

	/**
	 * Draws wave to screen
	 * @param g Graphics screen
	 */
	public void draw(Graphics g) {
		//draw each point in wave
        for (double x = 0; x < (WAVE_WIDTH); x += X_INCREMENT) {
        	//use sine formula to calculate initial wave value
            int y = (int) (amplitude1 * Math.sin((x - wavePosition) * frequency1));
        	
            //draw wave 1
            if (initialOn) {
	        	g.setColor(Color.BLUE);
	        	g.drawLine((int) (x+MARGIN), WAVE_HEIGHT + y, (int) (x + X_INCREMENT+MARGIN), WAVE_HEIGHT + y);
            }
        	
            //use sine formula to calculate second wave value
        	int y2 = (int) (amplitude2 * Math.sin((x+wavePosition) * frequency2));
        	
        	//draw wave 2
        	if (reflectOn) {
	        	g.setColor(Color.RED);
	        	g.drawLine((int)(x+MARGIN), WAVE_HEIGHT+y2, (int)(x+MARGIN+X_INCREMENT), WAVE_HEIGHT+y2);
        	}
	        	
        	//add position of both waves
            int interferenceY = (int) (y+y2);
            
            //draw interference wave
            if (interferenceOn) {
	            g.setColor(new Color(17,176,49));
	            g.drawLine((int) (x+MARGIN), WAVE_HEIGHT+interferenceY, (int) (x + X_INCREMENT+MARGIN), WAVE_HEIGHT+interferenceY);
            }
        
        }

	}
	
	/**
	 * Resets wave position
	 */
	public void restartWave() {
		wavePosition = 0;
	}

	/**
	 * Sets amplitude of wave 1
	 * @param amplitude Amplitude of wave 1
	 */
	public void setAmplitude1(double amplitude) {
		this.amplitude1 = amplitude;
	}
	
	/**
	 * Sets amplitude of wave 2
	 * @param amplitude2 Amplitude of wave 2
	 */
	public void setAmplitude2(double amplitude2) {
		this.amplitude2 = amplitude2;
	}
	
	/**
	 * Sets frequency of wave 1
	 * @param frequency Frequency of wave 1
	 */
	public void setFrequency1(double frequency) {
		this.frequency1 = frequency;
	}
	
	/**
	 * Sets frequency of wave 2
	 * @param frequency Amplitude of wave 2
	 */
	public void setFrequency2(double frequency) {
		this.frequency2 = frequency;
	}

	/**
	 * Moves waves according to current speed
	 */
	public void moveWave() {
		wavePosition = wavePosition+speed;
	}
	
	/**
	 * Toggles Wave 1 
	 * @param on True if visible, false if hidden
	 */
	public void setInitial(boolean on) {
		initialOn = on;
	}
	
	/**
	 * Toggles Wave 2 
	 * @param on True if visible, false if hidden
	 */
	public void setReflect(boolean on) {
		reflectOn = on;
	}
	
	/**
	 * Toggles interference wave
	 * @param on True if visible, false if hidden
	 */
	public void setInterference(boolean on) {
		interferenceOn = on;
	}
	
	/**
	 * Toggles slow motion by changing wave speed
	 * @param on True if slowmo, false if regular
	 */
	public void setSlowMotion(boolean on) {
		if (on) {
			speed = 2;
		}else {
			speed = 10;
		}
	}

	
}
