package org.frc1721.steamworks.commands;

import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.frc1721.steamworks.Robot;
import org.frc1721.steamworks.RobotMap;
import org.frc1721.steamworks.CustomRobotDrive.GyroMode;

/**
 *
 */
public class DriveToGearTarget extends DriveToCoordinates {
	private boolean	targetAcquired	= true;
	private double[]	approachDistance;
	int iDist = 0;


	public DriveToGearTarget(double[] dist, double speed, double distTol) {
		super(0.0, 0.0, speed, distTol, 5);
		approachDistance = dist;
	}

	protected void initialize() {
		targetAcquired = Robot.positionEstimator.setBestGearTarget();
		if (!targetAcquired)
			return;
		getTargetPoint();
		super.initialize();
	}

	protected void getTargetPoint(){
		double[] point = Robot.positionEstimator.getTargetApproachPoint(approachDistance[iDist]);
		targetX = point[0];
		targetY = point[1];
	}
	
	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		updateSmartDashboard();
		if (!targetAcquired)
			return;
		super.execute();
		if ( (iDist < 1) || (distance < 0.5)) {
			// Get a new target
			getTargetPoint();		
		}
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		if (!targetAcquired) {
			return true;
		} else if (iDist > 0) {
			return super.isFinished();
		} else {
			return false;
		}
	}

	public void updateSmartDashboard() {
		SmartDashboard.putBoolean("targetAcquired", targetAcquired);
		super.updateSmartDashboard();
		
	}
	
}
