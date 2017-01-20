
package org.frc1721.steamworks;

import org.frc1721.steamworks.subsystems.Climber;
import org.frc1721.steamworks.subsystems.DriveTrain;
import org.frc1721.steamworks.subsystems.Shooter;

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
	}
}
