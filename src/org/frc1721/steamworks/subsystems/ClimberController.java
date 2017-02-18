package org.frc1721.steamworks.subsystems;

import org.frc1721.steamworks.RobotMap;
import org.frc1721.steamworks.commands.Climber;

import edu.wpi.first.wpilibj.Joystick;
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
		RobotMap.cClimb.set(
				(operator.getRawAxis(RobotMap.gamepadLTrigger)) - (operator.getRawAxis(RobotMap.gamepadRTrigger)));

	}
}
