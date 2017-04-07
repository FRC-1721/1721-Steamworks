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

/**
 *
 */
public class ClimberController extends Subsystem {

	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		// setDefaultCommand(new MySpecialCommand());

		setDefaultCommand(new Climber());
	}

	public void jInput(Joystick operator) {
		if (operator.getRawButton(RobotMap.fullClimb)) {
			RobotMap.cClimb.set(-1.0d);
		} else if ((operator.getRawAxis(RobotMap.gamepadLTrigger) + operator.getRawAxis(RobotMap.gamepadRTrigger)) > 0) {
			RobotMap.cClimb.set((operator.getRawAxis(RobotMap.gamepadLTrigger)) - (operator.getRawAxis(RobotMap.gamepadRTrigger)));
		} else {
			RobotMap.cClimb.set(0.0d);
		}

	}
}
