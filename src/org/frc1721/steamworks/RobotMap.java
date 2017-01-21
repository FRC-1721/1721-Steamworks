package org.frc1721.steamworks;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import org.frc1721.steamworks.CustomPIDController;
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
	
	/** Encoder DIO Ports **/
	public static final int dtlEncPA = 0,
							dtlEncPB = 1,
							dtrEncPA = 2,
							dtrEncPB = 3;
	
	
	
	
	// ---------> DRIVE TRAIN: PID AND GYRO <----------
	
	/** PID Control Variables **/
	public static final double dtP = 0.5, dtI = 0.0, dtD = 0.0, dtF = .0;
	
	/** Encoder reversals **/
	public static final boolean dtlEncR = true, dtrEncR = false;
	
	/** Gyro **/
	public static Gyro navx; // TODO: Import the NavX libraries and correct this.
	
	/** Update Speed (Hz) **/
	public static final byte navUpdateHz = 20;
	
	/** NavX PID Controller **/
	public static final double navP = 0.05, navI = 0.01, navD = 0.0, navF = 0.0;
	
	/** Rate Controller for the NavX **/
	public static final double navRateP = 0.05, navRateI = 0.0, navRateD = 0.0, navRateF = 0.0;
	
	/* Drive Train PID Rate controllers */
	public static CustomPIDController dtLeftController;
	public static CustomPIDController dtRightController;
	
	/**
	 *Joysticks, Input, and Buttons.
	 */
	public static final int jStickPort = 1;
	
}
