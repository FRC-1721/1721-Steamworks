package org.frc1721.steamworks.commands;

import org.frc1721.steamworks.Robot;
import org.frc1721.steamworks.subsystems.ShooterController;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class SpinDownShooter extends Command {

	public SpinDownShooter() {
		// Use requires() here to declare subsystem dependencies
		// eg. requires(chassis);
		requires(Robot.shooter);
	}

	// Called just before this Command runs the first time
	protected void initialize() {
		ShooterController.setSpin(false);
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return true;
	}

	// Called once after isFinished returns true
	protected void end() {}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {}
}
