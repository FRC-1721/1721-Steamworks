package org.frc1721.steamworks.subsystems;

import org.frc1721.steamworks.CustomPIDController;
import org.frc1721.steamworks.CustomRobotDrive;
import org.frc1721.steamworks.CustomRobotDrive.GyroMode;
import org.frc1721.steamworks.RobotMap;
import org.frc1721.steamworks.commands.DriveInTeleop;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.command.Subsystem;

public class DriveTrain extends Subsystem {

	protected CustomRobotDrive m_robotDrive;
	protected NavxController m_navController;
	protected GyroMode gyroMode = GyroMode.off;
	protected double m_rateScale = 10.0;
	protected double m_turnRateScale = 180.0;
	protected double m_reversed = 1.0;
	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		// setDefaultCommand(new MySpecialCommand());

		setDefaultCommand(new DriveInTeleop());
	}

	public DriveTrain(CustomRobotDrive robotDrive, NavxController navController) {
		super("DriveTrain");
		m_robotDrive = robotDrive;
		m_navController = navController;
	}

	public void setGyroMode(GyroMode gMode) {
		if (gyroMode != gMode) {
			gyroMode = gMode;
			if (gyroMode == GyroMode.off) {
				m_navController.disable();
			} else if (gyroMode == GyroMode.heading) {
				m_navController.reset();
				m_navController.enable();
				m_navController.setPIDSourceType(PIDSourceType.kDisplacement);
				CustomPIDController gyroController = m_navController.getPIDController();
				gyroController.setPID(RobotMap.navP, RobotMap.navI, RobotMap.navD, RobotMap.navF);
				gyroController.setOutputRange(-0.3, 0.3);
				DriverStation.reportWarning("GyroMode is heading!", false);

			} else {
				m_navController.reset();
				m_navController.enable();
				m_navController.setPIDSourceType(PIDSourceType.kRate);
				CustomPIDController gyroController = m_navController.getPIDController();
				gyroController.setPID(RobotMap.navRateP, RobotMap.navRateI, RobotMap.navRateD, RobotMap.navRateF);
				gyroController.setOutputRange(-0.8, 0.8);
				DriverStation.reportWarning("GyroMode is not heading!", false);
			}
		}
		m_robotDrive.setGyroMode(gMode);
	}

	// Drive using a dimensional speed and turn rate
	public void rateDrive(double forward, double turn) {
		// Use Arcade drive, but first turn rates into non-dimensional values
		m_robotDrive.arcadeDrive(m_reversed*forward / m_rateScale, turn / m_turnRateScale, false, false);
	}

	public void jInput(Joystick stick) {
		m_robotDrive.arcadeDrive(-m_reversed*stick.getY(), -stick.getTwist(), true, false);
	}

	public boolean setDriveReversed (double reversed) {
		boolean newDirection = false;
		if (reversed*m_reversed < 0.0) newDirection = true;
		m_reversed = reversed;
		return newDirection;
	}
	
	
	public void jInput(Joystick left, Joystick right) {
		m_robotDrive.tankDrive(left, right, false);
	}

	public void stop() {
		m_robotDrive.drive(0, 0);
	}

	public double getDistance() {
		return m_robotDrive.getDistance();
	}

	// Changes the scaling of raw inputs into a drive rate and turn rate
	public void setDriveScale(double driveRate, double turnRate) {
		m_rateScale = driveRate;
		m_turnRateScale = turnRate;
	}

}
