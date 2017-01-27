package org.frc1721.steamworks;

import edu.wpi.first.wpilibj.*;
import org.frc1721.steamworks.CustomPIDController;
import com.kauailabs.navx.frc.AHRS;
/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
	
	// --------- DRIVE TRAIN ----------
	
	/** VictorSPs for drive (left, right) **/
	public static Victor dtLeft, dtRight;
	
	/** PWM ports for drive motor controllers **/
	public static final int dtlPWM = 0, dtrPWM = 1;
	
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
	public static final int lsLsPA = 9;
	
	
	
	
	// ---------> DRIVE TRAIN: PID AND GYRO <----------
	
	/** PID Control Variables **/
	public static final double dtP = 1.0, dtI = 0.0, dtD = 0.0, dtF = .1;
	
	/** Encoder reversals **/
	public static final boolean dtlEncR = true, dtrEncR = false;
	
	/** Gyro **/
	public static AHRS navx; 
	
	/** Update Speed (Hz) **/
	public static final byte navUpdateHz = 20;
	
	/** NavX PID Controller **/
	public static final double navP = 0.05, navI = 0.0, navD = 0.0, navF = 0.0;
	
	/** Rate Controller for the NavX **/
	public static final double navRateP = 0.001, navRateI = 0.0, navRateD = 0.0, navRateF = 0.001;
	
	/** Drive Train PID Rate controllers **/
	public static CustomPIDController dtLeftController;
	public static CustomPIDController dtRightController;
	
	// ---------> OI <----------
	
	/** Joysticks, Input, and Buttons **/
	
	// Name the Logitech Extreme Pro controllers identify with
	public static final String jstick = "Logitech Extreme 3D";
	
	// Name the Logitech F310 Gamepad identifies with
	public static final String gamepad = "Controller (Gamepad F310)";
	
	// Number of usb ports to scan
	// We should never use more then 3 USB ports but I'm scanning 6 because that seems to be the max Driver Station will allow
	public static final int numUSB = 6;
	
	// Joystick to have PID buttons on, and the buttons to use
	public static final int pidStick = 0, // Note: This will crash if the Joystick doesn't exist, I recommend only making it controller two (1) if you know you're going to use tank drive
							pidDisableButton = 1,
							pidEnableButton = 8;
	
	
}
