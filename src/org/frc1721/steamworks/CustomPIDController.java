package org.frc1721.steamworks;

import java.util.LinkedList;

/* Copyright (c) FIRST 2008-2012. All Rights Reserved. */
/* Open Source Software - may be modified and shared by FRC teams. The code */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project. */
/*----------------------------------------------------------------------------*/

import java.util.TimerTask;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.HLUsageReporting;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDInterface;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;
import edu.wpi.first.wpilibj.util.BoundaryException;

/**
 * Class implements a PID Control Loop.
 *
 * Creates a separate thread which reads the given PIDSource and takes care of
 * the integral calculations, as well as writing the given PIDOutput
 */
public class CustomPIDController extends PIDController {

	public CustomPIDController(double dtp, double dti, double dtd, double dtf, Encoder dtlEnc, VictorSP dtLeft, double d) {
		super(dtp, dti, dtd, dtf, dtlEnc, dtLeft, d);
		
		scalePID();
	}

	  private void scalePID() {
	        m_Pdt = m_P*m_period;
	        m_Idt = m_I*m_period;
	        m_Ddt = m_D*m_period;
	    }
	
}
