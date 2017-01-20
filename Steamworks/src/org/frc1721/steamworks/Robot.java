
package org.frc1721.steamworks;

import org.frc1721.steamworks.subsystems.Climber;
import org.frc1721.steamworks.subsystems.DriveTrain;
import org.frc1721.steamworks.subsystems.Shooter;

import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.Encoder;

import edu.wpi.first.wpilibj.IterativeRobot;


public class Robot extends IterativeRobot {

	public static OI oi;
	
	/** 
	 * Subsystems List
	 * In order to create a new subsystem this list must be appended.
	 * Also note that you must initialize your subsystem in robotInit() 
	 */
	public static Climber climber;
	public static Shooter shooter;
	public static DriveTrain driveTrain;
	
	
	@Override
	public void robotInit() {
		RobotMap.dtLeft = new Victor(RobotMap.dtlPWM);
		RobotMap.dtRight = new Victor(RobotMap.dtrPWM);
		RobotMap.dtRight.setInverted(true);
		LiveWindow.addActuator("LeftRobotDrive", "Victor", RobotMap.dtLeft);
		LiveWindow.addActuator("RightRobotDrive", "Victor", RobotMap.dtRight);
		RobotMap.dtlEnc = new Encoder(RobotMap.dtlEncPA, RobotMap.dtlEncPB);
		RobotMap.dtrEnc = new Encoder(RobotMap.dtrEncPA, RobotMap.dtrEncPB);
		LiveWindow.addSensor("LeftRobotDrive", "Encoder", RobotMap.dtlEnc);
	    LiveWindow.addSensor("RightRobotDrive", "Encoder", RobotMap.dtrEnc);
		RobotMap.dtlEnc.setDistancePerPulse(0.00727);
		RobotMap.dtrEnc.setDistancePerPulse(0.00727);
	}

	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
	}

	@Override
	public void autonomousInit() {
	}

	@Override
	public void autonomousPeriodic() {
	}

	@Override
	public void teleopInit() {
	}

	@Override
	public void teleopPeriodic() {
	}

	@Override
	public void testPeriodic() {
		LiveWindow.run();
	}
}
