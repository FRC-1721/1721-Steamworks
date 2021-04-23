package org.frc1721.steamworks.commands;

import org.frc1721.steamworks.Robot;
import org.frc1721.steamworks.RobotMap;

import edu.wpi.first.wpilibj.command.Command;

public class ProcessCameraData extends Command {

	public ProcessCameraData() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
		requires(Robot.cameraSystem);
    }

	// Called just before this Command runs the first time
	protected void initialize() {
		Robot.cameraSystem.start();
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		Robot.cameraSystem.processData();
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return false;
	}

	// Called once after isFinished returns true
	protected void end() {
		
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
		Robot.cameraSystem.stop();
	}
}
