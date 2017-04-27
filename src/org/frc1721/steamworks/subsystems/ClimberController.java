package org.frc1721.steamworks.subsystems;

import org.frc1721.steamworks.OI;
import org.frc1721.steamworks.RobotMap;
import org.frc1721.steamworks.commands.Climber;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.MotorSafety;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class ClimberController extends Subsystem {

	private static double spin = 0.0d;

	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		// setDefaultCommand(new MySpecialCommand());

		setDefaultCommand(new Climber());
	}

	public void jInput(Joystick operator) {
		if (operator.getRawButton(RobotMap.fullClimb)) {
			// If the fullClimb button is pushed set the spin value to full power.
			spin = 1.0d;
		} else if (operator.getRawAxis(RobotMap.gamepadRTrigger) > 0.001d) {
			// If the fullClimb button is not pushed, and the trigger is bigger then a near zero value,
			// set the spin to the value of the trigger.
			spin = operator.getRawAxis(RobotMap.gamepadRTrigger);
		} else {
			// If the fullClimb button is not pushed, and the trigger is a near zero value, set the spin value to zero
			spin = 0.0d;
		}
		
		spin = OI.limit(spin, 0.0d, 1.0d);

		// Set the climber to the spin value.
		setClimber(spin);
	}

	/**
	 * Give a number between 0 and 1 to run the climber motor
	 * @param spin
	 */
	private static void setClimber(double spin) {
		// Take a given spin value and take the absolute value of it, then limit that number between 0 and 1
		// (no back drive).
		spin = OI.limit(Math.abs(spin), 0.0d, 1.0d);
		RobotMap.cClimb.set(spin);
	}

	public static void updateSmartDashboard() {
		SmartDashboard.putNumber("Climber Motor", RobotMap.cClimb.get());
	}
}
