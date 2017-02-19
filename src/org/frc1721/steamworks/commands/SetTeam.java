package org.frc1721.steamworks.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.frc1721.steamworks.Robot;
import org.frc1721.steamworks.RobotMap;
/**
 *
 */
public class SetTeam extends Command {

	protected boolean complete = false;
	protected int m_team;


	
    public SetTeam(int team) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	m_team = team;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	complete = false;
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	double flipY = 1.0;
    	if (m_team == RobotMap.blueTeam) flipY = -1.0;
    	RobotMap.hopperY = flipY*Math.abs(RobotMap.hopperY);	
    	complete = true;
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return complete;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
    
    
}
