package org.frc1721.steamworks.commands;

import org.frc1721.steamworks.Robot;
import edu.wpi.first.wpilibj.command.*;

public class DriveInTeleop extends Command {

	public DriveInTeleop() {
		requires(Robot.driveTrain);
	}

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.driveTrain.jInput(Robot.oi.jstick);
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
