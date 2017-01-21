package org.frc1721.steamworks.subsystems;

import org.frc1721.steamworks.CustomRobotDrive;
import org.frc1721.steamworks.commands.DriveInTeleop;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.*;
/**
 *
 */
public class DriveTrain extends Subsystem {
	
	
	protected static CustomRobotDrive m_robotDrive;

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    	
		setDefaultCommand(new DriveInTeleop());
    }
    
    
	public DriveTrain(CustomRobotDrive robotDrive) {
		super("DriveTrain");
		m_robotDrive = robotDrive;
	}
	
	public void jInput(Joystick stick) {
		m_robotDrive.arcadeDrive(-stick.getY(), -stick.getTwist());
	}
	
	public void stop() {
		m_robotDrive.drive(0, 0);
	}
	
    
}

