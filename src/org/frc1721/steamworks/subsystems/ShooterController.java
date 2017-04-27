package org.frc1721.steamworks.subsystems;

import java.text.DecimalFormat;

import org.frc1721.steamworks.OI;
import org.frc1721.steamworks.RobotMap;
import org.frc1721.steamworks.commands.Shooter;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.PIDSubsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */

public class ShooterController extends PIDSubsystem {

	public static final double		originalSpin	= 0.83d;						// TODO TEMP, I'm keeping this out of
																					// RobotMap.java to edit quick.
	private static double			spin			= originalSpin;
	private static DecimalFormat	dFormat			= new DecimalFormat("#.###");
	private static boolean			enabled			= false;						// I'm using enabled so that I can turn
																					// the motors on and off but preserve the
																					// value of spin
	private static double			servoValue		= RobotMap.servoDown;

	public ShooterController() {
		super("Shooter", RobotMap.sP, RobotMap.sI, RobotMap.sD);
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
			enabled = true;
		}

		if (operator.getRawButton(RobotMap.spinUpButton)) {
			enabled = true;
		}

		if (operator.getRawButton(RobotMap.spinDownButton)) {
			enabled = false;
		}

		//servoValue -= Double.valueOf(dFormat.format(0.001d * operator.getRawAxis(RobotMap.gamepadLYaxis)));
		servoValue = OI.limit(servoValue, Math.min(RobotMap.servoDown, RobotMap.servoUp), Math.max(RobotMap.servoDown, RobotMap.servoUp));


		spin -= Double.valueOf(dFormat.format(0.001d * operator.getRawAxis(RobotMap.gamepadRYaxis)));
		spin = OI.limit(spin);

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
		if (enabled)
			RobotMap.sShooter.set(set);
		else
			RobotMap.sShooter.set(0.0d); // TODO Learn if .stopMotor would work better here

		return RobotMap.sShooter.get();
	}

	/**
	 * Only to be used in SpinDownShooter.java and SpinUpShooter.java.
	 * 
	 * @param isSpin
	 */
	public static void setSpin(boolean enable) {
		ShooterController.enabled = enable;
	}

	public static void updateSmartDashboard() {
		SmartDashboard.putNumber("Shooter Encoder", RobotMap.sShooter.get());
		SmartDashboard.putNumber("Spin Value", spin);
		SmartDashboard.putNumber("Rate", RobotMap.shooterEnc.getRate());
		SmartDashboard.getBoolean("isSpin", enabled);
		SmartDashboard.putNumber("servo", servoValue);

	}

}
