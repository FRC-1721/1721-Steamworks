package org.frc1721.steamworks.commands;

import org.frc1721.steamworks.Robot;
import org.frc1721.steamworks.RobotMap;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CalibrateVision extends Command {

	public class lsqFit {
		// Linear least squares method
		// Method from http://mathworld.wolfram.com/LeastSquaresFitting.html
		private double ssxx, ssyy, ssxy, xavg, yavg;
		private int nSamples;
		public double slope, constant, rSquared;
		
		public lsqFit () {
			reset();
		}
		
		public void reset() {
			ssxx = 0.0;
			ssyy = 0.0;
			ssxy = 0.0;
			xavg = 0.0;
			yavg = 0.0;
			nSamples = 0;
		}
		
		public void addSample(double x, double y) {
			ssxx += x*x;
			ssyy += y*y;
			ssxy += x*y;
			xavg += x;
			yavg += y;
			nSamples += 1;	
		}
		
		public void calculate() {
			double n = (double) nSamples;
			xavg = xavg/n;
			yavg = yavg/n;
			ssxx -= n*xavg*xavg;
			ssyy -= n*yavg*yavg;
			ssxy -= n*xavg*yavg;
			slope = ssxy/ssxx;
			constant = yavg - slope*xavg;
			rSquared = ssxy*ssxy/(ssxx*ssyy);
		}
	}
	
	private static lsqFit distFit;
	private static lsqFit angleFit;
	private Timer camTimer;
	
	public CalibrateVision() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
		requires(Robot.cameraSystem);
		distFit = new lsqFit();
		angleFit = new lsqFit();
		camTimer = new Timer();
    }

	// Called just before this Command runs the first time
	protected void initialize() {
		//Robot.cameraSystem.start();
	  camTimer.start();
		distFit.reset();
		angleFit.reset();
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		// Check if there is new data for the vision tracker
	  Robot.cameraSystem.updateSmartDashboard();
		if (Robot.cameraSystem.processData()) {
		 
			double dist = Robot.positionEstimator.getDistanceFromPoint(0.0,0.0);
			double relAngle = Robot.positionEstimator.getHeadingToPoint(0.0,0.0, true);
			distFit.addSample(Robot.cameraSystem.rawDist, dist);
			angleFit.addSample(Robot.cameraSystem.rawAngle, relAngle);
		}
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
	     return false;
		//return (camTimer.hasPeriodPassed(30.0));
	}

	// Called once after isFinished returns true
	protected void end() {
		distFit.calculate();
		angleFit.calculate();
		Robot.cameraSystem.distM = distFit.slope;
		Robot.cameraSystem.distC = distFit.constant;
		Robot.cameraSystem.angleM = angleFit.slope;
		Robot.cameraSystem.angleC = angleFit.constant;
		SmartDashboard.putNumber("distRSquared", distFit.rSquared);
		SmartDashboard.putNumber("angleRSquared", angleFit.rSquared);
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
		end();
	}
}
