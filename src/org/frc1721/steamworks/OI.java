package org.frc1721.steamworks;


import org.frc1721.steamworks.commands.DisableDrivePIDCommand;

import org.frc1721.steamworks.commands.EnableDrivePIDCommand;
import org.frc1721.steamworks.commands.IsGear;
import org.frc1721.steamworks.subsystems.DriveTrain;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

import static java.lang.System.out;
import static java.lang.System.err;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
	//// CREATING BUTTONS
	// One type of button is a joystick button which is any button on a
	//// joystick.
	// You create one by telling it which joystick it's on and which button
	// number it is.
	// Joystick stick = new Joystick(port);
	// Button button = new JoystickButton(stick, buttonNumber);

	// There are a few additional built in buttons you can use. Additionally,
	// by subclassing Button you can create custom triggers and bind those to
	// commands the same as any other Button.

	//// TRIGGERING COMMANDS WITH BUTTONS
	// Once you have a button, it's trivial to bind it to a button in one of
	// three ways:

	// Start the command when the button is pressed and let it run the command
	// until it is finished as determined by it's isFinished method.
	// button.whenPressed(new ExampleCommand());

	// Run the command while the button is being held down and interrupt it once
	// the button is released.
	// button.whileHeld(new ExampleCommand());

	// Start the command when the button is released and let it run the command
	// until it is finished as determined by it's isFinished method.
	// button.whenReleased(new ExampleCommand());
	
	public static Joystick[] jsticks;
	public static Joystick jOp;
	
	// Drive controls
    public static JoystickButton enableDrivePIDButton;
    public static JoystickButton disableDrivePIDButton;
    
    // Print buttons
    public static JoystickButton printLimitSwitch;
    
	private int 	jsOne = -1,
					jsTwo = -1;
	
	public OI ()
	{

//    	out.printf("First %d\n", jsOne);
//    	out.printf("Second %d\n", jsTwo);

		
		for (int i = 0; i < RobotMap.numUSB; i++)
		{
			Joystick controller = new Joystick(i);
			
			if(controller.getName().equals(RobotMap.jstick)) {
				if (jsOne == -1)
					jsOne = controller.getPort();
				else
					jsTwo = controller.getPort();	
    		}
		}
			
		
		// Find RobotMap.jstick sticks
    	    	
		
//    	out.printf("First %d\n", jsOne);
//    	out.printf("Second %d\n", jsTwo);
    	
//		out.println("THIS NEEDS TO BE SEEN" + foo);
		
    	
    	if(jsTwo == -1)
    		jsticks = new Joystick[1];
    	else
    		jsticks = new Joystick[2];
    	

    	// always create the first Joystick, if we have a second Joystick create it as well
   		jsticks[0] = new Joystick(jsOne);
    	if(jsticks.length == 2)
    		jsticks[1] = new Joystick(jsTwo);	
	
    	
    	
    	out.printf("Number of Joysticks: %d\n", jsticks.length);
    	
//    	out.printf("First %d\n", jsOne);
//    	out.printf("Second %d\n", jsTwo);
    	
		
		
		// Drive commands
    	disableDrivePIDButton = new JoystickButton(jsticks[RobotMap.pidStick], RobotMap.pidDisableButton);
    	disableDrivePIDButton.whenPressed(new DisableDrivePIDCommand());
    	enableDrivePIDButton = new JoystickButton(jsticks[RobotMap.pidStick], RobotMap.pidEnableButton);
    	enableDrivePIDButton.whenPressed(new EnableDrivePIDCommand());
    	
//		for (int i = 0; i < jsticks.length; i++)
//			out.print(joystickInfo(jsticks[i]));
	}
	
	private String joystickInfo(Joystick jstick)
	{
		return String.format(
				"The number of buttons this joystick has is %d\n" +
    			"The name of the joystick is %s\n" +
				"The port of this joystick is %s\n",
				jstick.getButtonCount(), jstick.getName(), jstick.getPort());
	}
	
}
