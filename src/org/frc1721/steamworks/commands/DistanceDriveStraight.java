package org.frc1721.steamworks.commands;


import org.frc1721.steamworks.Robot;
import org.frc1721.steamworks.RobotMap;
import org.frc1721.steamworks.subsystems.DriveTrain;
import edu.wpi.first.wpilibj.command.Command;
/**
 *
 */
public class DistanceDriveStraight extends Command {
	double m_distance;
	static int kToleranceIterations = 200;
	protected double mSpeed = 0.0; // speed in feet/sec
	protected double m_startDistance;
	protected boolean m_usePID = false;

	

    public DistanceDriveStraight(double distance, double speed, boolean usePID) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.driveTrain);
    	mSpeed = speed;
    	m_distance=distance;
    	m_usePID = usePID;
    }

    public DistanceDriveStraight(double distance, double speed) {
        this(distance,speed,false);
    }
    
    // Called just before this Command runs the first time
    protected void initialize() {
    	m_startDistance = Robot.driveTrain.getDistance();

    	if (m_usePID) {
    		Robot.distanceController.enable();
    		Robot.distanceController.setSetpointRelative(m_distance);
    		Robot.distanceController.setOutputRange(-Math.abs(mSpeed), Math.abs(mSpeed));
    		Robot.distanceController.setToleranceBuffer(kToleranceIterations);
    		Robot.distanceController.setAbsoluteTolerance(1.0);
    	}
		
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	double speed = mSpeed;
    	if (m_usePID) {
    		speed = Robot.distanceController.getPIDOutput();
    	}
    	Robot.driveTrain.rateDrive(speed,0);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	if (m_usePID) {
    		return Robot.distanceController.onTargetDuringTime();
    	} 
    	// Without PID, just make sure it made it past the point
    	if (m_distance > 0) {
    		if (Robot.driveTrain.getDistance() > m_startDistance + m_distance) {
    			return true;
    		}
    	} else {
    		if (Robot.driveTrain.getDistance() < m_startDistance + m_distance) {
    			return true;
    		}
    	}
    	return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	//Robot.distanceDrivePID.disable();
    	Robot.driveTrain.stop();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	//Robot.distanceDrivePID.disable();
    	Robot.driveTrain.stop();
    }
}
