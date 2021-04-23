/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008-2016. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.frc1721.steamworks.subsystems;

import org.frc1721.steamworks.CustomPIDController;
import org.frc1721.steamworks.CustomPIDSubsystem;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class NavxController extends CustomPIDSubsystem  {

  double pidOut = 0.0;
  private AHRS mGyro;
  private double lastHeading = 0.0;
  static final double kToleranceDegrees = 2.0f;
  private Timer gyroTimer;
  private final double kMinPeriod = 0.01;
  private double gyroRate = 0.0;

  public NavxController(String name, double p, double i, double d, double f, AHRS gyro, PIDSourceType pidSourceType) {
    super(name, p, i, d, f);
    mGyro = gyro;
    setPIDSourceType(pidSourceType);
    gyroTimer = new Timer();
    gyroTimer.start();
  }

  public void setPIDSourceType (PIDSourceType pidSourceType) {
	  super.setPIDSourceType(pidSourceType);
	  m_controller.setOutputRange(-0.8, 0.8);
	  m_controller.setContinuous(true);
	  if (pidSourceType == PIDSourceType.kDisplacement) {
		  m_controller.setInputRange(-180.0, 180.0);
	  } else {
		  m_controller.setInputRange(0.0, 0.0);
		  lastHeading = mGyro.getYaw();
		  gyroTimer.reset();
		  gyroRate = 0.0;
	  }	  
  } 
  
  public void initDefaultCommand() {
      // Set the default command for a subsystem here.
      //setDefaultCommand(new MySpecialCommand());
  }
  
  protected double returnPIDInput() {
      // Return your input value for the PID loop
      // e.g. a sensor, like a potentiometer:
      // yourPot.getAverageVoltage() / kYourMaxVoltage;
  	if (m_pidSourceType == PIDSourceType.kDisplacement) {
  		return mGyro.getYaw();
  	} else {
  		double deltaT = gyroTimer.get();
  		if (deltaT > kMinPeriod ) {
  			gyroTimer.reset();
  			double heading = mGyro.getYaw();
  			// Subtract/add 360 if the change in heading is larger than 180
  			gyroRate = heading - lastHeading;
  			if (gyroRate >= 180.0) {
  				gyroRate = gyroRate - 360.0;
  			} else if (gyroRate <= -180.0) {
  				gyroRate = gyroRate + 360.0;
  			}
  			// now divide change in heading by deltaT
  			gyroRate = gyroRate/deltaT;
  			lastHeading = heading;
  			gyroTimer.reset();
  		}
  		return gyroRate;
  	}
  }
  
 public void zeroOutput() {
	 m_controller.zeroOutput();
	 pidOut = 0.0;
 }
  
  public void setSetpointRelative(double deltaSetpoint) {
	    setSetpoint(getPosition() + deltaSetpoint);
	  }
  
  protected void usePIDOutput(double output) {
      // Use output to drive your system, like a motor
      // e.g. yourMotor.set(output);
  	pidOut = output;
  }
  
  public void reset() {
  	m_controller.reset();
  	lastHeading = mGyro.getYaw();
  	gyroTimer.reset();
  	
  }
  
  public double getPIDOutput() {
  	return pidOut;
  }
  
  
  public void updateSmartDashboard() {
	  if (m_pidSourceType == PIDSourceType.kDisplacement) {
		  SmartDashboard.putNumber("NavControllerHeading", mGyro.getYaw());
	  } else {
		  SmartDashboard.putNumber("NavControllerHeadinRate", gyroRate);
	  }
	  SmartDashboard.putNumber("NavControllerPIDSetPoint", m_controller.getSetpoint());
	  SmartDashboard.putNumber("NavControllerPIDOutput", pidOut);
	  SmartDashboard.putNumber("NavControllerAvgError", m_controller.getAvgError());
  }
  
}
