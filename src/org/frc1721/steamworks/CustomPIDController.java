package org.frc1721.steamworks;

import java.util.LinkedList;
import java.util.TimerTask;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.VictorSP;

public class CustomPIDController extends PIDController {

    private double 	m_Pdt,
				    m_Idt,
				    m_Ddt;
    
    private double m_prevDxDt = 0.0;
    private double m_prevOutput = 0.0;
    private double m_prevPosition = 0.0;
    
    protected double m_absTolerance = 0.0;
    
    protected boolean m_onTarget = false;
    protected int m_iterOnTarget = 0;
	
    public CustomPIDController(double Kp, double Ki, double Kd, double Kf, PIDSource source, PIDOutput output, double period) {
    	super(Kp, Ki, Kd, Kf, source, output, period);
    	DriverStation.reportWarning(String.format("Before .cancel(): %s", m_controlLoop.toString()), false);
    	m_controlLoop.cancel();
    	DriverStation.reportWarning(String.format("After .cancel(): %s", m_controlLoop.toString()), false);
    	// TODO see what this gives me.
    }
    
	public CustomPIDController(double dtp, double dti, double dtd, double dtf, Encoder dtlEnc, VictorSP dtLeft, double d) {
		super(dtp, dti, dtd, dtf, dtlEnc, dtLeft, d);

        scalePID();

        m_onTarget = false;
        m_iterOnTarget = 0;
	}

	public CustomPIDController(double p, double i, double d, double f, PIDSource m_source, PIDOutput m_output) {
		super(p, i, d, f, m_source, m_output);
	}

	// I left all of this here so it's easy to quickly edit.
	@Override
	protected void calculate() { 
       boolean enabled;
       PIDSource pidInput;

       synchronized (this) {
           if (m_pidInput == null) {
               return;
           }
           if (m_pidOutput == null) {
               return;
           }
           enabled = m_enabled; // take snapshot of these values...
           pidInput = m_pidInput;
       }

       if (enabled) {
           boolean rateControl = (m_pidInput.getPIDSourceType() == PIDSourceType.kRate);
           double input;
           double newOutput;
           boolean t_onTarget;
           double dsdt;
           double dxdt;
           double ddxdt;
           // feed forward term
           double ff = 0.0;

           PIDOutput pidOutput = null;
           synchronized (this) {
               input = pidInput.pidGet();
           }
           synchronized (this) {
               m_error = m_setpoint - input;

               dsdt = m_setpoint - m_prevSetpoint;
               if (m_continuous) {
                   if (Math.abs(m_error) > (m_maximumInput - m_minimumInput) / 2) {
                       if (m_error > 0) {
                           m_error = m_error - m_maximumInput + m_minimumInput;
                       } else {
                           m_error = m_error + m_maximumInput - m_minimumInput;
                       }
                   }
                   if (Math.abs(dsdt) > (m_maximumInput - m_minimumInput) / 2) {
                       if (dsdt > 0) {
                           dsdt = dsdt - m_maximumInput + m_minimumInput;
                       } else {
                           dsdt = dsdt + m_maximumInput - m_minimumInput;
                       }
                   }
               }
               if (rateControl) {
                   dsdt = m_setpoint;
                   dxdt = input;
                   ff = m_F*(dsdt - m_prevSetpoint);
               } else {
                   dsdt = dsdt/m_period;
                   dxdt = (input - m_prevPosition);
                   if (m_continuous) {
                       if (Math.abs(dxdt) > (m_maximumInput - m_minimumInput) / 2) {
                           if (dxdt > 0) {
                               dxdt = dxdt - m_maximumInput + m_minimumInput;
                           } else {
                               dxdt = dxdt + m_maximumInput - m_minimumInput;
                           }
                       }
                   }
                   dxdt = dxdt/m_period;
               }
               ddxdt = (dxdt - m_prevDxDt);
               newOutput = m_prevOutput + m_Pdt*(dsdt - dxdt) - m_D*ddxdt + ff;
               // Don't include the i term for rate control
               if (!rateControl) {
                   newOutput = newOutput + m_I*m_error;
               }
               if (newOutput > m_maximumOutput) {
                   newOutput = m_maximumOutput;
               } else if (newOutput < m_minimumOutput) {
                   newOutput = m_minimumOutput;
               }
               m_prevSetpoint = m_setpoint;
               m_prevPosition = input;
               m_result = newOutput;
               m_prevError = m_error;
               m_prevOutput = newOutput;
               pidOutput = m_pidOutput;
               m_prevDxDt = dxdt;
               // Update the buffer.
               m_buf.add(m_error);
               m_bufTotal += m_error;
               // Remove old elements when the buffer is full.
               if (m_buf.size() > m_bufLength) {
                   m_bufTotal -= m_buf.remove();
               }
               if ( (m_absTolerance > 0.0) && (Math.abs(m_error) < m_absTolerance) ) {
                   t_onTarget = true;
               } else {
                   t_onTarget = false;
               }
               //  Check if on target and start the timer
               if (m_onTarget == false) {
                   // Wasn't on target, but is now, so start the timer
                   if (t_onTarget) {
                       m_onTarget = true;
                       m_iterOnTarget = 1;
                   }
               } else {
                   // Was on target, make sure it still is
                   if (  t_onTarget == false) {
                       m_onTarget = false;
                       m_iterOnTarget = 0;
                   } else {
                       // Stayed on target, increment the timer
                       m_iterOnTarget++;
                   }
               }
           }

           pidOutput.pidWrite(newOutput);

       	}
	}
	
	protected class CustomPIDTask extends PIDTask {

	    private CustomPIDController m_controller;
	    
	    public CustomPIDTask(CustomPIDController controller) {
	    	super(controller);
	    }

	    @Override
	    public void run() {
	      m_controller.calculate();
	    }
	  }
	
	@Override
    public synchronized void setSetpoint(double setpoint) {
    	super.setSetpoint(setpoint);
        m_onTarget = false;
        m_iterOnTarget = 0;
    }
    
    public synchronized void setAbsoluteTolerance(double absvalue) {
    	super.setAbsoluteTolerance(absvalue);
        m_absTolerance = absvalue;
    }
    
    @Override
    public synchronized void enable() {
    	super.enable();
        m_prevSetpoint = m_pidInput.pidGet();
        m_prevDxDt = 0.0;
    }
    
    @Override
    public synchronized void disable() {
    	super.disable();
        m_prevOutput = 0.0;
    }

    @Override
    public synchronized void reset() {
        m_bufTotal = 0.0;
        m_buf.clear();	
    }
    
    public void zeroOutput () {
        m_result = 0.0;
        m_prevOutput = 0.0;
        m_pidOutput.pidWrite(0.0);
    }
    
    public void setOutput(double output) {
        m_pidOutput.pidWrite(output);
    }
    
    public boolean onTargetDuringTime () {
        if (m_bufLength <= 1) return false;
        if (m_onTarget && (m_iterOnTarget >= m_bufLength) ) {
            return true;
        } else {
            return false;
        }
    }

    public int getIterOnTarget () {
        return m_iterOnTarget;
    }
	
    public synchronized PIDSource getPIDSource() {
        return m_pidInput;
    }
    
    private void scalePID() {
        m_Pdt = m_P*m_period;
        m_Idt = m_I*m_period;
        m_Ddt = m_D*m_period;
    }
    
    @Override
    public synchronized void setPID(double p, double i, double d) {
    	super.setPID(p, i, d);
    	scalePID();
    }
    
    @Override
    public synchronized void setPID(double p, double i, double d, double f) {
    	super.setPID(p, i, d, f);
    	scalePID();
    }
}