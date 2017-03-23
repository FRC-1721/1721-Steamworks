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
public class DriveToGearTarget extends DriveToCoordinates {
	boolean	targetAcquired	= true;
	double	approachDistance;

	public DriveToGearTarget(double dist, double speed, double distTol) {
		super(0.0, 0.0, speed, distTol, 5);
		approachDistance = dist;
	}

	protected void initialize() {
		targetAcquired = Robot.positionEstimator.setBestGearTarget();
		if (!targetAcquired)
			return;
		double[] point = Robot.positionEstimator.getTargetApproachPoint(approachDistance);
		targetX = point[0];
		targetY = point[1];
		super.initialize();
	}


	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		if (!targetAcquired)
			return;
		super.execute();
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		if (!targetAcquired) {
			return true;
		} else {
			return super.isFinished();
		}
	}

}
