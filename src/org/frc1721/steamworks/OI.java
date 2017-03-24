
package org.frc1721.steamworks;

import static java.lang.System.out;

import org.frc1721.steamworks.commands.DisableDrivePIDCommand;
import org.frc1721.steamworks.commands.EnableDrivePIDCommand;
import org.frc1721.steamworks.commands.SetDriveReversed;
import org.frc1721.steamworks.commands.TeleopDepositGear;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class is the glue that binds the controls on the physical operator interface to the commands
 * and command groups that allow control of the robot.
 */
public class OI {

	/** Driver Joysticks **/
	public static Joystick[]	jsticks;	// Declares an array of Joystick(s)
	/** Operator Controller **/
	public static Joystick		jOp;

	/** Drive Buttons **/
	public static JoystickButton enableDrivePIDButton,
			disableDrivePIDButton,
			forwardDriveButton,
			reverseDriveButton,
			teleopDepositGearButton;

	/** Private DriverStation Instance **/
	private final DriverStation m_ds = DriverStation.getInstance();

	/* */
	private int jsOne = -1, jsTwo = -1, gpOp = -1;

	/** Controller mode **/
	public enum ControllerMode {
		tankMode("Tank Mode"), arcadeMode("Arcade Mode"), noMode("No Mode");

		private final String text;

		/**
		 * @param text
		 */
		private ControllerMode(final String text) {
			this.text = text;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return text;
		}
	}

	// TODO Find out why controllers don't init 100% of the time. ~FIXED, Kinda
	// TODO Find out why switching from tank to arcade is sketch sometimes. ~FIXED, Kinda
	// TODO Make code automatically detect deadzone and scale output for it.

	public OI() {
		for (int i = 0; i < RobotMap.numUSB; i++) {
			// out.println("ABC: " + i);

			Joystick controller = new Joystick(i);

			// for (int j = 0; j < jsticks.length; j++) {
			if (controller.getName().equals(RobotMap.jsticks[0])) {
				if (jsOne == -1) {
					jsOne = controller.getPort();
				} else {
					jsTwo = controller.getPort();
					// If both of the controllers get assigned break from the loop
					break;
				}
			}
			// }

			for (int j = 0; j < RobotMap.gamepads.length; j++) {
				if (controller.getName().equalsIgnoreCase(RobotMap.gamepads[j])) {
					gpOp = controller.getPort();
					break;
				}
			}
		}

		RobotMap.controllerMode = ControllerMode.arcadeMode;

		// /** Set Joystick mode **/
		// switch (jsticks.length) {
		// case 1:
		// RobotMap.controllerMode = ControllerMode.arcadeMode;
		// break;
		// case 2:
		// RobotMap.controllerMode = ControllerMode.tankMode;
		// break;
		//
		// default:
		// DriverStation.reportError("No controller mode selected!!!", false);
		// RobotMap.controllerMode = ControllerMode.noMode;
		// break;
		//
		// }

		// allocates memory for Joystick
		switch (RobotMap.controllerMode) {
			case arcadeMode:
				jsticks = new Joystick[1];
				jsticks[0] = new Joystick(jsOne);
				break;
			case tankMode:
				jsticks = new Joystick[2];
				jsticks[0] = new Joystick(jsOne);
				jsticks[1] = new Joystick(jsTwo);
				break;
			case noMode:
				// TODO Maybe do something with this?
				break;
		}

		out.println("Number of Joysticks: " + jsticks.length);

		jOp = new Joystick(gpOp);

		SmartDashboard.putString("Joystick Mode", RobotMap.controllerMode.toString());

		/** Driver Buttons **/
		try {
			disableDrivePIDButton = new JoystickButton(jsticks[RobotMap.pidStick], RobotMap.pidDisableButton);
			disableDrivePIDButton.whenPressed(new DisableDrivePIDCommand());
			enableDrivePIDButton = new JoystickButton(jsticks[RobotMap.pidStick], RobotMap.pidEnableButton);
			enableDrivePIDButton.whenPressed(new EnableDrivePIDCommand());
			forwardDriveButton = new JoystickButton(jsticks[RobotMap.pidStick], RobotMap.forwardDriveButton);
			forwardDriveButton.whenPressed(new SetDriveReversed(1.0));
			reverseDriveButton = new JoystickButton(jsticks[RobotMap.pidStick], RobotMap.reverseDriveButton);
			reverseDriveButton.whenPressed(new SetDriveReversed(-1.0));
			teleopDepositGearButton = new JoystickButton(jsticks[RobotMap.pidStick], RobotMap.runTeleopDepositGearButton);
			teleopDepositGearButton.whileHeld(new TeleopDepositGear()); // TODO Make this a two button system

		} catch (RuntimeException e) {
			DriverStation.reportError(String.format("The Driver Buttons in '%s.java' broke again.\n" + "RESTARTING ROBOT CODE!!!\n", this.getClass().getName()), true);
			// e.printStackTrace();
			System.exit(RobotMap.roboError.BtnErr.getExitCode());
		}

		// for (int i = 0; i < jsticks.length; i++)
		// out.print(joystickInfo(jsticks[i]));

		for (int i = 0; i < 3; i++) // TODO see how this works out.
			out.print(driverstationInfo(i));

	}

	private String joystickInfo(Joystick jstick) {
		return String.format("The number of buttons this joystick has is %d\n" + "The name of the joystick is %s\n" + "The port of this joystick is %s\n", jstick.getButtonCount(), jstick.getName(),
				jstick.getPort());
	}

	private String driverstationInfo(int stick) {
		return String.format("The driverstation joystick type is %d\n", m_ds.getJoystickType(stick));
	}

}
