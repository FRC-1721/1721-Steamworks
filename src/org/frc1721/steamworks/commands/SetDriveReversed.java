package org.frc1721.steamworks.commands;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import org.frc1721.steamworks.Robot;
import org.frc1721.steamworks.RobotMap;
import org.frc1721.steamworks.subsystems.DriveTrain;
/**
 *
 */
public class SetDriveReversed extends Command {
	protected boolean newDirection = true;
	protected double m_reversed;
	protected Timer newDirectionTimer;
	protected static double kStopTime = 0.2;
	
    public SetDriveReversed(double reversed) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.driveTrain);
    	m_reversed = reversed;
    	newDirection = true;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	newDirection = Robot.driveTrain.setDriveReversed(m_reversed);
    	if (m_reversed > 0) {
    		CameraServer.getInstance().startAutomaticCapture(0);
    	} else {
    		CameraServer.getInstance().startAutomaticCapture(1);
    	}
		newDirectionTimer = new Timer();
		newDirectionTimer.start();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.driveTrain.stop();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
      if (! newDirection) {
    	  return true;
      } else if (newDirectionTimer.get() > kStopTime) {
    	  return true;
      } else {
    	  return false;
      }
    }

    // Called once after isFinished returns true
    protected void end() {
 
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
