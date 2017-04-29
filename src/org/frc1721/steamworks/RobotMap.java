package org.frc1721.steamworks;

import org.frc1721.steamworks.OI.ControllerMode;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into to a variable name. This provides
 * flexibility changing wiring, makes checking the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */

// TODO CLEAN ALL THE THINGS UP
// TODO Remove all magic numbers from everything
// TODO Why do we have VictorSP objects and encoder objects
// TODO Make everything an enum, I like enums.
public class RobotMap {

	// Variables to change Robot
	public static boolean practiceBot = false;

	// --------- DRIVE TRAIN ----------

	/** VictorSPs for drive (left, right) **/
	public static VictorSP dtLeft, dtRight;

	/** VictorSP for the climbing **/
	public static VictorSP		cClimb;
	public static final boolean	climbInverted	= true;

	/** VictorSP for the shooter **/
	public static VictorSP		sShooter;
	public static final boolean	shooterInverted	= true;

	/** PWM ports for drive motor controllers **/
	public static final int dtlPWM = 0, dtrPWM = 1;

	/** PWN port for the climbing motor controller **/
	public static final int climbPWM = 2;

	/** PWM port for the shooter motor controller **/
	public static final int shooterPWM = 3;

	/** Servo Channel **/
	public static final int shooterServoPWN = 4;

	/** Servo **/
	public static Servo shooterServo;

	/** Shooter rate **/
	public static final double	shooterTargetRate	= 1500;
	public static final double	shooterErrorPercent	= 0.01;	// 1% Error?


	/** Encoders for drive **/
	public static Encoder	dtlEnc, dtrEnc;
	public static Encoder	shooterEnc;
	public static boolean	leftEncoderDisabled		= false;
	public static boolean	rightEncoderDisabled	= false;
	public static boolean	shooterEncoderDisabled	= false;	// This doesn't do anything..

	/** Encoder DIO Ports **/
	public static final int	dtlEncPA	= 0;
	public static final int	dtlEncPB	= 1;
	public static final int	dtrEncPA	= 2;
	public static final int	dtrEncPB	= 3;

	public static final int	sEncPA	= 4;
	public static final int	sEncPB	= 5;

	/** Limit Switch DIO Port **/
	public static final int gearLs = 4, topLs = 5, bottomLs = 6;

	public static NetworkTable cameraTable;

	public static I2C lcd;

	// ---------> DRIVE TRAIN: PID AND GYRO <----------

	/** Drive PID Control Variables **/
	public static double dtP = 1.0, dtI = 0.0, dtD = 0.0, dtF = 0.1;

	/** Shooter PID Control Variables **/
	public static double sP = 8.0, sI = 0.0, sD = 0.1; // TODO Figure out what the F term is

	// Distance controller
	public static double	distTu	= 0.2;
	public static double	distP	= 2.0;
	public static double	distI	= 0.1 * distP * 2.0 / distTu;
	public static double	distD	= distP * distTu / 8.0;

	/** Encoder reversals **/
	public static final boolean dtlEncR = false, dtrEncL = false, sEncR = false; // TODO FIX

	/** Gyro **/
	public static AHRS navx;

	/** Update Speed (Hz) **/
	public static final byte	navUpdateHz	= 20;
	public static double		yawOffset	= 0.0;
	public static double		xStart		= 0.0, yStart = 0.0;

	/** Some field positions **/
	// Take y = 0 as centerline of field, and x as away from driver team
	public static double	centerStartX		= 1.5, centerStartY = 0.0;
	public static double	sideStartX			= 1.5, sideStartY = 5.5;
	public static double	cGearDepositX		= 9.5, cGearDepositY = 0.0;
	public static double	sideGearDepositX	= cGearDepositX + 1.47;
	public static double	sideGearDepositY	= 2.55;

	public static double		fieldWidth			= 27.0;
	public static double		hopperX				= 1.5, hopperY = fieldWidth / 2.0 - 1.5;
	public static double		quarterFieldWidth	= fieldWidth / 4.0;
	/** NavX PID Controller **/
	// Time scale of oscillations when using only P term
	private static final double	navTu				= 0.5;


	// Settings from https://en.wikipedia.org/wiki/Ziegler%E2%80%93Nichols_method

	public static double	navP	= 0.8 * 0.006;
	public static double	navI	= 0.0 * navP / (0.5 * navTu);
	public static double	navD	= navP * navTu / 3.0;
	public static double	navF	= 0.0;

	/*
	 * STEPS TO CALIBRATE THE DPP (Distance Pure Pulse) VALUES:
	 * 
	 * (TODO Jim please make sure the steps are correct)
	 * 
	 * 1. Check Directions of the motors and the encoders. left motor should move forward with a positive value, and the left
	 * encoder should grow (+). right motor should move backwards with a positive value, and the right encoder should grow
	 * (+).
	 * 
	 * 2. Put robot in test mode in Driver Station, move robot forward a few feet.
	 * 
	 * 3. 'desired' is the number of feet (exactly) you moved the robot 'actual' is the number you got 'current' is the
	 * current DPP values Use the formula '(desired/actual)*current' distance pure pulse to calculate the new DPP
	 * 
	 */

	public static double	rDPP	= 0.0074536447630841d;
	public static double	lDPP	= 0.0074536447630841d;
	public static double	sDPP	= 1d;

	/** Whether we are using a raspberry pi for vision processing **/
	public static final boolean visionPi = true;

	/** Rate Controller for the NavX **/
	public static final double navRateP = 0.0,
			navRateI = 0.0,
			navRateD = 0.0,
			navRateF = 0.001;

	// public static final double navRateP = 0.001,
	// navRateI = 0.0,
	// navRateD = 0.0,
	// navRateF = 0.001;

	// public static final double navRateP = 0.0005,
	// navRateI = 0.0,
	// navRateD = 0.0005,
	// navRateF = 0.0;


	/** Drive Train PID Rate controllers **/
	public static CustomPIDController	dtLeftController;
	public static CustomPIDController	dtRightController;

	/** Rate Conversion for drive train **/
	public static double	driveRateScale	= 6.0;		// feet per second
	public static double	turnRateScale	= 600.0;	// Degrees per second

	// ---------> OI <----------

	/* Joysticks, Input, and Buttons */

	public static ControllerMode controllerMode;

	// Name the Logitech Extreme Pro controllers identify with
	// Put in order of likelihood to get a hit for faster load times
	public static final String[] jsticks = {"Logitech Extreme 3D"};

	// Names the Logitech F310 Gamepad identifies with
	// Put in order of likelihood to get a hit for faster load times
	public static final String[] gamepads = {"Gamepad F310 (Controller)", "Controller (Gamepad F310)"};


	/*
	 * The Driver Station will now show up to 6 devices in the Setup window. The first 4 devices will be transmitted to the
	 * robot. The additional devices are shown to allow teams to use one component of a composite device, such as the TI
	 * Launchpad with FRC software without having to sacrifice one of the 4 transmitted devices.
	 */
	/** Number of USB ports to scan **/
	public static final int numUSB = 3;

	// TODO I might have to do something about device 5 and 6 if we do something with the custom Driver Station.

	/*
	 * Joystick to have PID buttons on, and the buttons to use Note: This will crash if the Joystick doesn't exist, I
	 * recommend only making it controller two (1) if you know you're going to use tank drive
	 */
	public static final int pidStick = 0,
			pidDisableButton = 1,
			pidEnableButton = 8,
			forwardDriveButton = 6,
			reverseDriveButton = 4,
			runTeleopDepositGearButton = 3,
			joystickSliderAxis = 3;

	public static final int	spinUpButton	= 1;
	public static final int	spinDownButton	= 2;
	public static final int	resetSpinButton	= 3;
	public static final int	fullClimb		= 4;


	// public static final AxisType gamepadLYaxis = AxisType.
	// TODO Use ButtonType and AxisType instead of using the raw numbers.

	public static final int	gamepadLYaxis	= 1;
	public static final int	gamepadRYaxis	= 5;

	public static final int	gamepadLTrigger	= 2;
	public static final int	gamepadRTrigger	= 3;

	public static final int	gamepadPovTop		= 0;
	public static final int	gamepadPovBottom	= 180;

	/*
	 * Rough Position to put the servo in, I'm aware this is getto but this servo is getto too so fuck it.
	 */
	public static final double	servoDown	= 0.80d;
	public static final double	servoUp		= 1.0d;

	public static final int redTeam = 0, blueTeam = 1;

	/*
	 * Update some parameters based on what has been setup Disable p term if encoders are not working.
	 * Feedfoward term is essentially manual mode.
	 */

	static {
		if (leftEncoderDisabled || rightEncoderDisabled) {
			dtP = 0.0;
		}
		// Practice bot parameters
		if (practiceBot) {
			lDPP = lDPP * 1.26;
			rDPP = rDPP * 1.26;
		}
	}

	/** Exit codes for Team1721 **/
	public static enum roboError {
		SUCCESS(0), FAILURE(-1), BtnErr(8); // Because this is the button that always broke, lol

		private int returnCode;

		private roboError(int returnCode) {
			this.returnCode = returnCode;
		}

		public int getExitCode() {
			return returnCode;
		}
	}

}
