package org.frc1721.steamworks.subsystems;

import java.text.DecimalFormat;

import org.frc1721.steamworks.RobotMap;
import org.frc1721.steamworks.commands.Shooter;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.PIDSubsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */

// TODO MAKE WORK
public class ShooterController extends PIDSubsystem {

	protected static final double	originalSpin	= 0.83d;						// TODO TEMP, I'm keeping this out of
																					// RobotMap.java to edit quick.
	protected static double			spin			= originalSpin;
	protected static DecimalFormat	dFormat			= new DecimalFormat("#.###");
	private static boolean			isSpin			= false;
	protected static double			servoValue		= RobotMap.servoDown;

	public ShooterController() {
		super("Shooter", RobotMap.sD, RobotMap.sI, RobotMap.sP);
		setAbsoluteTolerance(RobotMap.shooterErrorPercent);
		getPIDController().setContinuous();
	}

	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(new Shooter());
	}

	@Override
	protected double returnPIDInput() {
		// returns the sensor value that is providing the feedback for the system
		return RobotMap.shooterEnc.getRate();
	}

	@Override
	protected void usePIDOutput(double output) {
		// this is where the computed output value from the PIDController is applied to the motor
		RobotMap.sShooter.pidWrite(output);

	}

	public static void jstick(Joystick operator) {

		switch (operator.getPOV()) {
			case RobotMap.gamepadPovTop:
				servoValue = RobotMap.servoUp;
				break;
			case RobotMap.gamepadPovBottom:
				servoValue = RobotMap.servoDown;
				break;

		}

		if (operator.getRawButton(RobotMap.resetSpinButton)) {
			spin = originalSpin;
			isSpin = true;
		}

		if (operator.getRawButton(RobotMap.spinUpButton)) {
			isSpin = true;
		}

		if (operator.getRawButton(RobotMap.spinDownButton)) {
			isSpin = false;
		}

		servoValue -= Double.valueOf(dFormat.format(0.001d * operator.getRawAxis(RobotMap.gamepadLYaxis)));
		servoValue = limit(servoValue);


		spin -= Double.valueOf(dFormat.format(0.001d * operator.getRawAxis(RobotMap.gamepadRYaxis)));
		spin = limit(spin);

		// System.out.println("foo");
		setShooter(spin);
		setServo(servoValue);
	}

	private static double setServo(double servo) {
		RobotMap.shooterServo.set(servo);
		return RobotMap.shooterServo.get();
	}


	/**
	 * Sets the shooter,
	 * limits range from -1.0 to +1.0 using the limit method.
	 * 
	 * @param set The value to set the motor to.
	 * @return The current shooter speed.
	 */
	private static double setShooter(double set) {
		if (isSpin)
			RobotMap.sShooter.set(-set);
		else
			RobotMap.sShooter.set(0.0d);

		return RobotMap.sShooter.get();
	}

	public static void setSpin(boolean isSpin) {
		ShooterController.isSpin = isSpin;
	}

	/**
	 * Limit values. default -1.0 to +1.0.
	 */
	private static double limit(double num) {
		if (num < -1.0) {
			return -1.0;
		}
		if (num > 1.0) {
			return 1.0;
		}
		return num;
	}

	/**
	 * Limit values. from min to max.
	 */
	private static double limit(double num, double min, double max) {
		if (num < min) {
			return min;
		}
		if (num > max) {
			return max;
		}
		return num;
	}

	public static void updateSmartDashboard() {
		SmartDashboard.putNumber("Spin Value", spin);
		SmartDashboard.putNumber("Rate", RobotMap.shooterEnc.getRate());
		SmartDashboard.getBoolean("isSpin", isSpin);
		SmartDashboard.putNumber("servo", servoValue);

	}

}
