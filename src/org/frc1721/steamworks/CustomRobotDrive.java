package org.frc1721.steamworks;

import edu.wpi.first.wpilibj.*;

import org.frc1721.steamworks.CustomPIDController;
import org.frc1721.steamworks.subsystems.NavxController;

public class CustomRobotDrive extends RobotDrive {

	// Enumerated class to hold gyro mode
	public enum GyroMode {
		off, rate, heading
	}
	
	// Default PID parameters
	protected CustomPIDController m_leftController;
	protected CustomPIDController m_rightController;
	protected boolean m_PIDPresent = false;
	protected boolean m_PIDEnabled = false; 
	// Output from -1 to 1 scaled to give rate in ft/s for PID Controller
	protected double m_rateScale = 10.0;
	
	// Gyro parameters
	protected NavxController m_turnController;
	protected double m_turnDeadzone = 0.02;
	public double turnRateScale = 30.0;
	protected GyroMode gyroMode = GyroMode.off;
	
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
			CustomPIDController leftController,  CustomPIDController rightController, 
			NavxController navController) {
		super(leftMotor, rightMotor);
		m_leftController = leftController;
		m_rightController = rightController;
		m_leftController.disable();
		m_rightController.disable();
		m_leftController.setPIDSourceType(PIDSourceType.kRate);
		m_rightController.setPIDSourceType(PIDSourceType.kRate);
		m_PIDPresent = true;
		m_turnController = navController;
		
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


/* Over-ridden functions */
public void setLeftRightMotorOutputs(double leftOutput, double rightOutput) {
	
    if (gyroMode != GyroMode.off) {
    	/* First find the requested avgOutput (forward/reverse) and differential 
    	 * output (twist).  
    	 */
    	double avgOutput = 0.5*(leftOutput + rightOutput);
    	double diffOutput = leftOutput - rightOutput;
    	if (gyroMode == GyroMode.rate) {
    		// If in rate control mode need to set the controller target based
    		// on the requested turn rate
    		m_turnController.setSetpoint(diffOutput*turnRateScale);
    	}
    	// Replace the differential output with the commanded turn rate from the 
    	// controller.
    	diffOutput = m_turnController.getPIDOutput();
    	leftOutput = limit(avgOutput + diffOutput);
    	rightOutput = limit(avgOutput - diffOutput);
    		
    }
	
	
    if (m_PIDEnabled) {
    	m_leftController.setSetpoint(limit(leftOutput) * m_maxOutput * m_rateScale);
    	if (Math.abs(leftOutput) < 0.001) m_leftController.zeroOutput();
    	m_rightController.setSetpoint(-limit(rightOutput) * m_maxOutput * m_rateScale);
    	if (Math.abs(rightOutput) < 0.001) m_rightController.zeroOutput();
    	
    	/* Safety updates normally done in super class */
        if (this.m_syncGroup != 0) {
            CANJaguar.updateSyncGroup(m_syncGroup);
          }

          if (m_safetyHelper != null)
            m_safetyHelper.feed();
    	
    } else {
    	super.setLeftRightMotorOutputs(leftOutput, rightOutput);
    }

  }



/* New Functions */
public void enablePID() {
	  m_PIDEnabled = true;

	  m_leftController.reset();
	  m_rightController.reset();
	  m_leftController.enable();
	  m_rightController.enable();
}

public void disablePID() {
	  m_PIDEnabled = false;
	  m_leftController.disable();
	  m_rightController.disable();
}

public void setDriveRate(double rate) {
	  m_rateScale = rate;
}


public void setGyroMode(GyroMode gMode) {
	  
	  gyroMode = gMode;
	  // Set the setpoint to the current heading
	  if (gyroMode == GyroMode.rate) {
		  m_turnController.setSetpoint(0.0);
	  } else if (gyroMode == GyroMode.heading) {
		  m_turnController.setSetpointRelative(0.0);
	  }  
}

}