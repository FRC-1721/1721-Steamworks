package org.frc1721.steamworks.commands;

import org.frc1721.steamworks.OI;

import org.frc1721.steamworks.Robot;
import org.frc1721.steamworks.RobotMap;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.Timer;

import static java.lang.System.out;

public class DrivePause extends Command {
	Timer pauseTimer;
	double m_pauseTime;
	
	public DrivePause(double pauseTime) {
		requires(Robot.driveTrain);
		m_pauseTime = pauseTime;
		pauseTimer = new Timer();
	}

    // Called just before this Command runs the first time
    protected void initialize() {
    	pauseTimer.start();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.driveTrain.rateDrive(0, 0);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return pauseTimer.hasPeriodPassed(m_pauseTime);
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
