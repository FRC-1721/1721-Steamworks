package org.frc1721.steamworks.subsystems;

import static java.lang.System.out;
import org.frc1721.steamworks.Robot;
import org.frc1721.steamworks.RobotMap;
import org.frc1721.steamworks.commands.Lift;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class LiftController extends Subsystem {

	private boolean canUp = true, canDown = true;

	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		// setDefaultCommand(new MySpecialCommand());

		setDefaultCommand(new Lift());
	}

	// TODO, make better
	
	public void jInput(Joystick operator) {
		//out.printf("'%s.jInput()' ran!", this.getClass().getName());

		// If the top limit switch is true and the left motor or the gamepad
		// axis is positive stop the motor!!
		if (Robot.topLimitSwitch.get()
				&& ((RobotMap.lLift.get() > 0.0) || (operator.getRawAxis(RobotMap.gamepadLYaxis) > 0.0))) {
			RobotMap.lLift.stopMotor();
			canUp = false;
		}

		if (Robot.bottomLimitSwitch.get()
				&& ((RobotMap.lLift.get() < 0.0) || (operator.getRawAxis(RobotMap.gamepadLYaxis) < 0.0))) {
			RobotMap.lLift.stopMotor();
			canDown = false;
		}

		if (canUp && canDown) {
			RobotMap.lLift.set(operator.getRawAxis(RobotMap.gamepadLYaxis));
		} else {
			if (canUp) {
				// Force up
				if (operator.getRawAxis(RobotMap.gamepadLYaxis) > 0.0)
					RobotMap.lLift.set(operator.getRawAxis(RobotMap.gamepadLYaxis));
			}

			if (canDown) {
				// Force down
				if (operator.getRawAxis(RobotMap.gamepadLYaxis) < 0.0)
					RobotMap.lLift.set(operator.getRawAxis(RobotMap.gamepadLYaxis));
			}
		}

		canUp = true;
		canDown = true;
	}
}
