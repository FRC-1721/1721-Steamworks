package org.frc1721.steamworks.commands;


import org.frc1721.steamworks.Robot;
import org.frc1721.steamworks.RobotMap;
import org.frc1721.steamworks.subsystems.DriveTrain;
import edu.wpi.first.wpilibj.command.Command;
/**
 *
 */
public class SetDistanceToTarget extends Command {
	double m_distance;
	static int kToleranceIterations = 5;
	protected double mSpeed = 0.0; // speed in feet/sec
	protected double m_deltaDistance;

	

    public SetDistanceToTarget(double distance, double speed) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.driveTrain);
    	mSpeed = speed;
    	m_distance=distance;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	m_deltaDistance = Robot.cameraSystem.getTargetDistance() - m_distance;
    		Robot.distanceController.enable();
    		Robot.distanceController.setSetpointRelative(m_deltaDistance);
    		Robot.distanceController.setOutputRange(-Math.abs(mSpeed), Math.abs(mSpeed));
    		Robot.distanceController.setToleranceBuffer(kToleranceIterations);
    		Robot.distanceController.setAbsoluteTolerance(1.0);
		
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	m_deltaDistance = Robot.cameraSystem.getTargetDistance() - m_distance;
    	Robot.distanceController.setSetpointRelative(m_deltaDistance);
    	double speed = Robot.distanceController.getPIDOutput();;
    	Robot.driveTrain.rateDrive(speed,0);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	
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
