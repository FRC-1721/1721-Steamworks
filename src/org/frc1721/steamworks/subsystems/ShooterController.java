package org.frc1721.steamworks.subsystems;

import org.frc1721.steamworks.RobotMap;
import org.frc1721.steamworks.commands.Shooter;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.command.PIDSubsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */

// TODO MAKE WORK
public class ShooterController extends PIDSubsystem {

	public boolean spinning = false;

	public ShooterController() {
		super("Shooter", 1.0, 0.0, 0.0);
		setAbsoluteTolerance(RobotMap.shooterErrorPercent);
		RobotMap.shooterEnc.setDistancePerPulse(RobotMap.sDPP);
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
		SmartDashboard.putNumber("Scaled Right Joystick Axis", ((operator.getRawAxis(RobotMap.gamepadRYaxis) + 1.0d) / 2.0d));
		
	}

	/**
	 * Sets the shooter,
	 * limits range from -1.0 to +1.0 using the limit method.
	 * 
	 * @param set The value to set the motor to.
	 * @return The current shooter speed.
	 */
	public double setShooter(double set) {

		RobotMap.sShooter.set(limit(set));
		return RobotMap.sShooter.get();
	}

	/**
	 * Limit motor values to the -1.0 to +1.0 range.
	 */
	private static double limit(double num) {
		if (num > 1.0) {
			return 1.0;
		}
		if (num < -1.0) {
			return -1.0;
		}
		return num;
	}


}
