# 2017-Steamworks - FRC 1721
FRC Team 1721's 2017 code repository - FIRST Steamworks!

### What?

[FIRST](http://www.firstinspires.org) is a global non-profit organization focused on getting kids involved in STEM activities and after-school programs.

We are some of those kids who just happen to live in and arround Concord NH.


### Still, what?

This is the code that makes a robot run. It's written primarily in Java. Anything used by this code is done so with permission, and those softwares will be mentioned bellow:

- WPILib for Java (provided by FIRST for use by any FRC team). Written by WPI, and National Instruments. Open source.

### The maintainers

This code is maintained and written by the following people

- Jim Forsythe
- Zachary Samenfeld
- Brennan Macaig

### Competition notes:
- pull the vision branch.  Several bug fixes are in it.
- make a backup of main.py on the pi.  scp the new main.py into the home directory of the pi.
- You may want to re-name the host name of the pi since other's may have a raspberry pi.  If you do, replace it in main.py
- May want to setup main.py to run on startup.  
- To kill main.py, find the process id and do a "kill -9 pid", or, control-c it if running from the command line.
- the website for the mjepg server is raspberrypi.local:8080/index.html
- if bandwidth is too high on the stream, you can increase the sleep time on main.py line 208.  
- I have reduced the number of processing threads to 6 to hopefully remove the lag in the video.  If you have lag, try reducing BUF_SIZE in main.py.  3-4 should still give good performance (>20fps for processing). 

- Calibration:  
-- The code was passing area rather than rawDist (1/sqrt(area)).  I have fixed this, so the curve fit should get better.  There were bugs in the angle calibration as well so hopefully that will get a better rSquared value.  If the rSq value is close to 1, it should be a good calibration.  The dist rSq should be good.  If the angle rSq is > 0.5 we can consider that as decent. 
-- After the calibration runs, before turning off the robot, write down the distM,distC,angleM,angleC - these are the curve fit constants.  Put those into the code, line 28/29 of CameraSystem.java, and re-load the code.  I.e. these are not saved, so make sure to manually over-write the current values and load the code again onto the robot.
-- Testing the update method.  After autoCalibration, the motors should now be off for a few minutes, so you should be able to manually move the robot around.  If you do, watch the video feed for green rectangles, i.e. good data, and look at realDist, realAngle in SmartDashboard(SD).  The dist and angle are supposed to be to the gear target (from the center of the robot).  Make sure these values look reasonable.  Then look at the posEstimatorX/Y.  This is the position estimate of where the robot is - normally by integrating the encoders, but the camera should now influence/update these values.  The gear target is at (9.5,0), so you expect (9.5 - posEstimatorX) to be the distance to the robot, i.e. realDist.  If you were to pick up the robot and move it (i.e. move it without moving the encoders), any change you get in posEstimatorX should be from vision.  So that's a good test, pick it up and move it a few feet, and then put it down and let vision take samples, and see if posEstimatorX get's updated.  
-- if you can't get vision calibrated, turn cameraSystem.calibrated to false to prevent the vision tracking from messing up the coordinates.

- Testing AutoDepositGear
-- Make sure this still works ok with vision active. I changed it to automatically find the closest gear target and use the geometry of the gear target to do the approach (see command DriveToGearTarget).  If this doesn't work, the old code is commented out below the new stuff, so you can try that. If this does work, we could write a similar set of commands to activate in teleop.


