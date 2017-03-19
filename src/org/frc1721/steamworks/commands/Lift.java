package org.frc1721.steamworks.commands;

import org.frc1721.steamworks.OI;
import org.frc1721.steamworks.Robot;
import org.frc1721.steamworks.RobotMap;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

public class Lift extends Command {

  protected boolean complete = false;
  private Timer liftTimer;
  private boolean canUp = true, canDown = true;

  public Lift() {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    requires(Robot.lift);
    liftTimer = new Timer();
  }

  // Called just before this Command runs the first time
  protected void initialize() {}

  // Called repeatedly when this Command is scheduled to run
  protected void execute() {
    // out.printf("'%s.execute()' ran!", this.getClass().getName());

    if (Robot.topLimitSwitch.get()) {
      // half speed pulse
      // 100ms
      liftTimer.start();
      RobotMap.lLift.set(0.5);
      try {
        liftTimer.wait(100);
      } catch (InterruptedException e) {
        DriverStation.reportWarning(String.format("'%s' just had an '%s' ", this.getClass().getName(), e.getLocalizedMessage()), false);
//        e.printStackTrace();
      }
      RobotMap.lLift.set(0.0);
    }

    // If the top limit switch is true and the left motor or the gamepad
    // axis is positive stop the motor!!
    if (Robot.topLimitSwitch.get() && ((RobotMap.lLift.get() < 0.0)
        || (OI.jOp.getRawAxis(RobotMap.gamepadLYaxis) < 0.0))) {
      RobotMap.lLift.stopMotor();
      canUp = false;
    }

    if (Robot.bottomLimitSwitch.get() && ((RobotMap.lLift.get() > 0.0)
        || (OI.jOp.getRawAxis(RobotMap.gamepadLYaxis) > 0.0))) {
      RobotMap.lLift.stopMotor();
      canDown = false;
    }

    if (canUp && canDown) {
      RobotMap.lLift.set(OI.jOp.getRawAxis(RobotMap.gamepadLYaxis));
    } else {
      if (canUp) {
        // Force up
        if (OI.jOp.getRawAxis(RobotMap.gamepadLYaxis) < 0.0)
          RobotMap.lLift.set(OI.jOp.getRawAxis(RobotMap.gamepadLYaxis));
      }

      if (canDown) {
        // Force down
        if (OI.jOp.getRawAxis(RobotMap.gamepadLYaxis) > 0.0)
          RobotMap.lLift.set(OI.jOp.getRawAxis(RobotMap.gamepadLYaxis));
      }
    }

    canUp = true;
    canDown = true;
  }

  // Make this return true when this Command no longer needs to run execute()
  protected boolean isFinished() {
    return complete;
  }

  // Called once after isFinished returns true
  protected void end() {}

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  protected void interrupted() {
    complete = true;
  }
}
