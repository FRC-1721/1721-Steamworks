package org.frc1721.steamworks.commands;

import org.frc1721.steamworks.RobotMap;
import org.frc1721.steamworks.commands.*;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoCalibrateVision extends CommandGroup {
  public AutoCalibrateVision() {

    // Set the position - use a coordinate system centered on gear drop-off. Robot center is at -1.5
    // ft.
    double targetX = RobotMap.centerStartX - 1.5;
    addSequential(new SetCoordinates(targetX, RobotMap.centerStartY));
    addSequential(new SetYawOffset(180.0));
    addSequential(new EnableDrivePIDCommand());
    targetX -= 1.0; // Move backwards a foot first before taking data
    addSequential(new DriveToCoordinates(targetX, 0.0, 0.2, 0.1, 5));
    // addSequential(new DistanceDriveStraight(1.5, 0.2, 0.1));
    addParallel(new CalibrateVision());
    for (int j = 0; j <1; j++) {
      for (int i = 0; i < 3; i++) {
        targetX -= 1.0; // Move backwards in 1 foot increments.
        addSequential(new DriveToCoordinates(targetX, 0.0, 0.1, 0.1, 5));
        // addSequential(new DistanceDriveStraight(1.0, 0.2, 0.1));
        addSequential(new TurnAbsolute(175.0, 5, 2));
        addSequential(new TurnAbsolute(-175.0, 5, 2));
      }
      for (int i = 0; i < 3; i++) {
        targetX += 1.0; // Move backwards in 1 foot increments.
        addSequential(new DriveToCoordinates(targetX, 0.0, -0.1, 0.1, 5));
        // addSequential(new DistanceDriveStraight(1.0, 0.2, 0.1));
        addSequential(new TurnAbsolute(175.0, 5, 2));
        addSequential(new TurnAbsolute(-175.0, 5, 2));
      }
    }
    addParallel(new DriveInTeleop());
    addSequential(new ProcessCameraData());
    // Drive to a point in line with the gear deposit

  }
}
