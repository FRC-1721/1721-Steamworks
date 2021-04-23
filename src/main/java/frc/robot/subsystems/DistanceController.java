/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008-2016. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.frc1721.steamworks.subsystems;

import org.frc1721.steamworks.CustomPIDSubsystem;
import org.frc1721.steamworks.subsystems.DriveTrain;


import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/**
 * This class is designed to handle the case where there is a {@link Subsystem}
 * which uses a single {@link PIDController} almost constantly (for instance, an
 * elevator which attempts to stay at a constant height).
 *
 * <p>
 * It provides some convenience methods to run an internal {@link PIDController}
 * . It also allows access to the internal {@link PIDController} in order to
 * give total control to the programmer.
 * </p>
 *
 * @author Joe Grinstead
 */
public class DistanceController extends CustomPIDSubsystem  {

  double pidOut = 0.0;
  private DriveTrain m_distSystem; // The system used to measure distance, must have "getDistance" method


  /**
   * Instantiates a {@link DistanceController} that will use the given p, i and d
   * values.
   *$
   * @param name the name
   * @param p the proportional value
   * @param i the integral value
   * @param d the derivative value
   */
  public DistanceController(String name, double p, double i, double d, DriveTrain subSystem) {
    super(name, p, i, d, 0);
    m_distSystem = subSystem;
    setPIDSourceType(PIDSourceType.kDisplacement);
  }

  
  public void initDefaultCommand() {
      // Set the default command for a subsystem here.
      //setDefaultCommand(new MySpecialCommand());
  }
  
  protected double returnPIDInput() {
      // Return your input value for the PID loop
      // e.g. a sensor, like a potentiometer:
      // yourPot.getAverageVoltage() / kYourMaxVoltage;
  	return m_distSystem.getDistance();
  }
  
 public void zeroOutput() {
	 m_controller.zeroOutput();
	pidOut = 0.0;
 }
  
  public void setSetpointRelative(double deltaSetpoint) {
	    setSetpoint(m_distSystem.getDistance() + deltaSetpoint);
	  }
  
  protected void usePIDOutput(double output) {
      // Use output to drive your system, like a motor
      // e.g. yourMotor.set(output);
	  // Just hold the value in the class for someone else to grab
  	pidOut = output;
  }
  
  public void reset() {
	  m_controller.reset();	
  }
  
  public double getPIDOutput() {
  	return pidOut;
  }
  
  
  public void updateSmartDashboard() {
	  SmartDashboard.putNumber("DistanceControllerDistance", returnPIDInput());
	  SmartDashboard.putNumber("DistanceControllerSetpoint", m_controller.getSetpoint());
	  SmartDashboard.putNumber("DistanceControllerOutput", pidOut);
  }
}
