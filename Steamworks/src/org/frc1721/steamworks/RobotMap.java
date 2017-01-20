package org.frc1721.steamworks;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.interfaces.Gyro;

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
	public static final double dtP = 2.0, dtI = 0.0, dtD = 0.0, dtF = .02;
	
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
	
	
	
	
}
