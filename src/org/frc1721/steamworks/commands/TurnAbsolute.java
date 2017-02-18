package org.frc1721.steamworks.commands;

import org.frc1721.steamworks.RobotMap;
import org.frc1721.steamworks.Robot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.frc1721.steamworks.CustomRobotDrive.GyroMode;

public class TurnAbsolute extends Command{
	double m_targetHeading;
	protected int kToleranceIterations = 1;
	
	public TurnAbsolute(double turnAngle, int tolIter) {
		requires(Robot.driveTrain);
		kToleranceIterations = tolIter;
		if (kToleranceIterations <= 1) kToleranceIterations = 2;
		m_targetHeading =  turnAngle;
	}
	
	protected void initialize() { 
		Robot.driveTrain.setGyroMode(GyroMode.heading);
		Robot.navController.reset();
		Robot.navController.enable();
		Robot.navController.setSetpoint(m_targetHeading);
		Robot.navController.setAbsoluteTolerance(10.0);
		Robot.navController.setToleranceBuffer(kToleranceIterations);
		
	}
	protected void execute() { 
		Robot.driveTrain.rateDrive(0, 0); 
		SmartDashboard.putNumber("TargetHeading", m_targetHeading);
		//SmartDashboard.putNumber("IterOnTarget", Robot.navController.getIterOnTarget());
		}
	// Just set to run tank.
	protected void end() { 
		Robot.driveTrain.stop(); 
		} // Just set to tank.
	
	protected void interrupted() { end(); }
	
	/* Unused, required methods. Pfffft */
	protected boolean isFinished() {
		//return finished;
		if ( Robot.navController.onTargetDuringTime()) {
			return true;
		} else {
			return false;
		}
	}
	
}
