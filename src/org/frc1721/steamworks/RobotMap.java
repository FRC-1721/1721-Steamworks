package org.frc1721.steamworks;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.Joystick.AxisType;

import org.frc1721.steamworks.CustomPIDController;
import org.frc1721.steamworks.OI.ControllerMode;

import com.kauailabs.navx.frc.AHRS;
/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */

// TODO CLEAN ALL THE THINGS UP
public class RobotMap {
	
	// --------- DRIVE TRAIN ----------
	
	/** VictorSPs for drive (left, right) **/
	public static VictorSP dtLeft, dtRight;
	
	/** VictorSP for the climbing **/
	public static VictorSP cClimb;
	
	/** VictorSP for the lift **/
	public static VictorSP lLift;
	
	/** PWM ports for drive motor controllers **/
	public static final int dtlPWM = 0, dtrPWM = 1;
	
	/** PWN port for the climbing motor controller**/
	public static final int climbPWM = 2;
	
	/** PWM port for the lift motor controller **/
	public static final int liftPWM = 3;
	
	/** Encoders for drive **/
	public static Encoder dtlEnc, dtrEnc;
	public static boolean leftEncoderDisabled = false;
	public static boolean rightEncoderDisabled = false;
	
	/** Encoder DIO Ports **/
	public static final int dtlEncPA = 0,
							dtlEncPB = 1,
							dtrEncPA = 2,
							dtrEncPB = 3;
	
	/** Limit Switch DIO Port **/
	public static final int gearLs = 4,
							topLs = 5,
							bottomLs = 6;
							
	
	
	public static I2C lcd;
	
	// ---------> DRIVE TRAIN: PID AND GYRO <----------
	
	/** PID Control Variables **/
	public static final double dtP = 2.0, dtI = 0.0, dtD = 0.0, dtF = 0.1;
	// Distance controller
	public static double distTu = 0.2;
	public static double distP = 2.0;
	public static double distI = 0.0*distP*2.0/distTu;
	public static double distD = distP*distTu/3.0;
	
	/** Encoder reversals **/
	public static final boolean dtlEncR = false,
								dtrEncL = false; // TODO FIX
	
	/** Gyro **/
	public static AHRS navx; 
	
	/** Update Speed (Hz) **/
	public static final byte navUpdateHz = 20;
	public static double yawOffset = 0.0;
	public static double xStart = 0.0, yStart = 0.0;
	
	/** NavX PID Controller **/
	// Time scale of oscillations when using only P term
	private static final double navTu = 0.5;
	// Settings from https://en.wikipedia.org/wiki/Ziegler%E2%80%93Nichols_method

	public static double navP = 0.6*0.006;
	public static double navI = 0.0*navP/(0.5*navTu);
	public static double navD = navP*navTu/3.0;
	public static double navF = 0.0;
	
	/**
	 * STEPS TO CALIBRATE THE DPP (Distance Pure Pulse) VALUES:
	 * 
	 * (TODO Jim please make sure the steps are correct)
	 * 
	 * 1.
	 * 	Check Directions of the motors and the encoders.
	 * 		left motor should move forward with a positive value, and the left encoder should grow.
	 * 		right motor should move backwards with a positive value, and the right encoder should shrink.
	 * 2.
	 * 	Put robot in test mode in Driver Station, move robot forward a few feet.
	 * 3.
	 * 	'desired' is the number of feet (exactly) you moved the robot
	 * 	'actual' is the number you got
	 * 	'current' is the current DPP values
	 * 	Use the formula '(desired/actual)*current' distance pure pulse to calculate the new DPP
	 * 	
	 */
	public static final double 	lDPP = 0.0074536447630841,
								rDPP = 0.0074074074074074;
	
	/** Rate Controller for the NavX **/
	public static final double navRateP = 0.0, navRateI = 0.0, navRateD = 0.0, navRateF = 0.001;
//	public static final double navRateP = 0.001, navRateI = 0.0, navRateD = 0.0, navRateF = 0.001;
//	public static final double navRateP = 0.005, navRateI = 0.0, navRateD = 0.0005, navRateF = 0;
	
	/** Drive Train PID Rate controllers **/
	public static CustomPIDController dtLeftController;
	public static CustomPIDController dtRightController;
	
	/** Rate Conversion for drive train **/
	public static double driveRateScale = 10.0; // feet per second
	public static double turnRateScale = 180.0; // Degrees per second
	
	// ---------> OI <----------
	
	/** Joysticks, Input, and Buttons **/
	
	public static ControllerMode controllerMode;
	
	// Name the Logitech Extreme Pro controllers identify with
	public static final String jstick = "Logitech Extreme 3D";
	
	// Name the Logitech F310 Gamepad identifies with
	public static final String gamepad = "Controller (Gamepad F310)";
	
	// Number of USB ports to scan
	/** The Driver Station will now show up to 6 devices in the Setup window.
	 * The first 4 devices will be transmitted to the robot.
	 * The additional devices are shown to allow teams to use one component of a composite device,
	 * such as the TI Launchpad with FRC software without having to sacrifice one of the 4 transmitted devices. 
	 */
	public static final int numUSB = 3;
	
	// TODO I might have to do something about device 5 and 6 if we do something with the custom Driver Station. 
	
	// Joystick to have PID buttons on, and the buttons to use
	public static final int pidStick = 0, // Note: This will crash if the Joystick doesn't exist, I recommend only making it controller two (1) if you know you're going to use tank drive
							pidDisableButton = 1,
							pidEnableButton = 8;
	
	public static final int gamepadLYaxis = 1;
	
	public static final int gamepadLTrigger = 2;
	public static final int gamepadRTrigger = 3;
	
	/** Exit codes for Team1721 **/
	public static enum roboError {
	    SUCCESS(0),
	    FAILURE(-1),
	    BtnErr(8); // Because this is the button that always broke, lol

	    private int returnCode;

	    private roboError(int returnCode) {
	        this.returnCode = returnCode;
	    }

	    public int getExitCode() {
	        return returnCode;
	    }
	}
	
}
