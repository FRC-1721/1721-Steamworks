package org.frc1721.steamworks.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.frc1721.steamworks.Robot;
import org.frc1721.steamworks.RobotMap;
import org.frc1721.steamworks.CustomRobotDrive.GyroMode;
/**
 *
 */
public class DriveToCoordinates extends Command {
	protected double targetX;
	protected double targetY;
	protected double heading;
	protected double headingErr;
	protected double distance;
	protected double m_startDistance;
	protected double mSpeed;
	protected static final double kRad2Deg = 57.3;
	protected static double kDistTol = 1.0;
	protected static double angleTol = 5.0;
	static int kToleranceIterations = 5;
	protected boolean onHeading = false;
	protected Timer collisionTimer;
	protected boolean collision = false;
	protected static double collisionRecoverTime = 0.2;
	protected static double collisionRecoverSpeed = 2.0;
	
	
    public DriveToCoordinates(double x, double y, double speed, double distTol, int toleranceIterations) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	this(x,y,speed);
    	kDistTol = distTol;
    	kToleranceIterations = toleranceIterations;
    }
	
    public DriveToCoordinates(double x, double y, double speed) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.driveTrain);
    	mSpeed = speed;
    	targetX = x;
    	targetY = y;
    	collisionTimer = new Timer();
    }
    
    
    protected void getDistanceAndHeading() {

    	double delX = targetX - Robot.positionEstimator.getDisplacementX();
    	double delY = targetY - Robot.positionEstimator.getDisplacementY();
    	heading = Math.atan2(delY, delX)*kRad2Deg + RobotMap.yawOffset;
    	distance = Math.sqrt(delX*delX + delY*delY);
    	if (mSpeed < 0 ) {
    		// Driving backwards
    		heading = heading + 180.0;
    		distance = -distance;
    	}
		if (heading > 180.0) {
			heading -= 360.0;
		} else if (heading < -180.0) {
			heading += 360.0;
		}
    }
    

    // Called just before this Command runs the first time
    protected void initialize() {
    	onHeading = false;
    	getDistanceAndHeading();
    	Robot.driveTrain.setGyroMode(GyroMode.heading);
    	Robot.navController.setSetpoint(heading);
    	Robot.navController.setAbsoluteTolerance(angleTol);
    	Robot.navController.setToleranceBuffer(5);
    	Robot.distanceController.setOutputRange(-Math.abs(mSpeed), Math.abs(mSpeed));
    	Robot.distanceController.setOutputRange(-Math.abs(mSpeed), Math.abs(mSpeed));
		Robot.distanceController.setToleranceBuffer(kToleranceIterations);
		Robot.distanceController.setAbsoluteTolerance(kDistTol);

    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	SmartDashboard.putNumber("dtcDistance", distance);
    	SmartDashboard.putNumber("dtcHeading", heading);
    	SmartDashboard.putBoolean("dtcOnHeading", onHeading);
    	getDistanceAndHeading();
    	if (! onHeading) {
    		Robot.driveTrain.rateDrive(0.0, 0.0);
    		if (Robot.navController.onTargetDuringTime()) {
    			// Start the distance controller
    			onHeading = true;
    			m_startDistance = Robot.driveTrain.getDistance();
        		Robot.distanceController.reset();
        		Robot.distanceController.enable();
        		Robot.distanceController.setSetpointRelative(distance);
    		}
    	} 
    	if (onHeading) {
    		if (!collision) {
    			collision = Robot.positionEstimator.checkCollision();
    			// ToDo Double check this and re-enable
    			collision = false;
    			if (collision) {
    				collisionTimer.start();
    			}
    		}
    		if (collision) {
    			if (collisionTimer.hasPeriodPassed(collisionRecoverTime)) {
    				// Start over, finding a heading
    				onHeading = false;
    				Robot.driveTrain.stop();
    				collision = false;
    				return;
    			}
    			// Drive 'backwards' at the recover speed
    			if (mSpeed > 0) {
    				Robot.driveTrain.rateDrive(-collisionRecoverSpeed,0);
    			} else {
    				Robot.driveTrain.rateDrive(collisionRecoverSpeed,0);
    			}	
    		}
    		// Update the set points
    		if (Math.abs(distance) > 2.0) {
    			Robot.navController.setSetpoint(heading);
    			Robot.distanceController.setSetpointRelative(distance);
    		}
    		double speed = Robot.distanceController.getPIDOutput();
    		Robot.driveTrain.rateDrive(speed,0);
    	}
    	
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
      if (onHeading) {
    	  return Robot.distanceController.onTargetDuringTime();
      } else {
    	  return false;
      }
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
