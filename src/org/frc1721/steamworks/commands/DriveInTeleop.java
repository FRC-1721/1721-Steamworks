package org.frc1721.steamworks.commands;

import org.frc1721.steamworks.OI;

import org.frc1721.steamworks.Robot;
import org.frc1721.steamworks.RobotMap;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;

import static java.lang.System.out;

public class DriveInTeleop extends Command {
	
	public DriveInTeleop() {
		requires(Robot.driveTrain);
	}

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
		if (OI.jsticks.length == 1) // TODO Make a robot mode so this looks better
    		Robot.driveTrain.jInput(OI.jsticks[0]);
		else if (OI.jsticks.length == 2)
			Robot.driveTrain.jInput(OI.jsticks[0], OI.jsticks[1]);
		else
			out.printf("Tell Zachary to look in '%s.java'\n", this.getClass().getName());
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.driveTrain.stop();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
	
}
