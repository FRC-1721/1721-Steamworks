package org.frc1721.steamworks.subsystems;

import static java.lang.System.out;
import org.frc1721.steamworks.Robot;
import org.frc1721.steamworks.RobotMap;
import org.frc1721.steamworks.commands.Lift;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class LiftController extends Subsystem {

	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		// setDefaultCommand(new MySpecialCommand());

		setDefaultCommand(new Lift());
	}
}
