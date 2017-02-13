/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008-2016. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.frc1721.steamworks.commands;

import org.frc1721.steamworks.CustomPIDController;

import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.command.PIDSubsystem;
import edu.wpi.first.wpilibj.command.Subsystem;

public abstract class CustomPIDSubsystem extends PIDSubsystem {

	protected PIDSourceType m_pidSourceType = PIDSourceType.kDisplacement;
	
	
    public CustomPIDSubsystem(String name, double p, double i, double d, double f) {
		super(name, p, i, d, f);
	}

	public PIDSourceType getPIDSourceType() {
        return m_pidSourceType;
    }
    
    public void setToleranceBuffer(int bufLength) {
        m_controller.setToleranceBuffer(bufLength);
    }

    protected void setPIDSourceType(PIDSourceType pidSourceType) {
        m_pidSourceType = pidSourceType;
        m_controller.setPIDSourceType(m_pidSourceType);
    }
    
    public boolean onTargetDuringTime () {
        if( m_controller.onTarget()) {
            return true;
        } else {
            return false;
        }
    }
}
