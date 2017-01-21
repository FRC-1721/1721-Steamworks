package org.frc1721.steamworks;

import edu.wpi.first.wpilibj.*;
import org.frc1721.steamworks.CustomPIDController;

public class CustomRobotDrive extends RobotDrive {

	// Default PID parameters
	protected CustomPIDController m_leftController;
	protected CustomPIDController m_rightController;
	protected boolean m_PIDPresent = false;
	protected boolean m_PIDEnabled = false; 
	// Output from -1 to 1 scaled to give rate in ft/s for PID Controller
	protected double m_rateScale = 10.0;
	
	public CustomRobotDrive(int leftMotorChannel, int rightMotorChannel) {
		super(leftMotorChannel, rightMotorChannel);
		// TODO Auto-generated constructor stub
	}

	public CustomRobotDrive(SpeedController leftMotor, SpeedController rightMotor) {
		super(leftMotor, rightMotor);
		// TODO Auto-generated constructor stub
	}

	/* Initialize with PID Controls */
	public CustomRobotDrive(SpeedController leftMotor, SpeedController rightMotor,
			CustomPIDController leftController,  CustomPIDController rightController) {
		super(leftMotor, rightMotor);
		m_leftController = leftController;
		m_rightController = rightController;
		m_leftController.disable();
		m_rightController.disable();
		m_leftController.setPIDSourceType(PIDSourceType.kRate);
		m_rightController.setPIDSourceType(PIDSourceType.kRate);
		m_PIDPresent = true;
	}
	
	public CustomRobotDrive(int frontLeftMotor, int rearLeftMotor, int frontRightMotor, int rearRightMotor) {
		super(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor);
		// TODO Auto-generated constructor stub
	}

	public CustomRobotDrive(SpeedController frontLeftMotor, SpeedController rearLeftMotor,
			SpeedController frontRightMotor, SpeedController rearRightMotor) {
		super(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor);
		// TODO Auto-generated constructor stub
	}

}
