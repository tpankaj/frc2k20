/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Hardware;
import frc.robot.Robot;
import frc.robot.Robot.RobotState;
import frc.robot.subsystems.Flywheel.FlywheelState;
import frc.robot.util.RollingAverage;
import frc.robot.util.Limelight.LED_MODE;
import frc.robot.util.Limelight.PIPELINE_STATE;

public class AutoShoot extends CommandBase {
  /**
   * Creates a new autoShoot.
   */

  private double timeout;
  private double RPM;
  private double startTime;
  private RollingAverage horizontalOffset;
  private RollingAverage verticalOffset;

  public AutoShoot(double _timeoutSeconds, double _RPM) {
    timeout = _timeoutSeconds;
    RPM = _RPM;
    horizontalOffset = new RollingAverage(5);
    verticalOffset = new RollingAverage(5);
    startTime = 0;

    addRequirements(Robot.drivetrain);
    addRequirements(Robot.flywheel);
    addRequirements(Robot.hopper);

  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    startTime = Timer.getFPGATimestamp();
    horizontalOffset.reset();
    verticalOffset.reset();
    Robot.flywheel.setFlywheelState(FlywheelState.SPINNINGUP);
    Hardware.limelight.setPipeline(PIPELINE_STATE.VISION_WIDE);
    Hardware.limelight.setLED(LED_MODE.ON);
    
    if(RPM != 0){
      Robot.flywheel.setTargetVelocity(RPM);
    }else{
      Robot.flywheel.setTargetVelocity(3000);
    }
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    horizontalOffset.add(Hardware.limelight.getHorizontalAngle());
    verticalOffset.add(Hardware.limelight.getHorizontalAngle());

    Robot.drivetrain.alignToTarget(horizontalOffset.getAverage());
    double avgDistance = Hardware.limelight.getDistanceFromTarget(verticalOffset.getAverage());
    Robot.flywheel.updateTargetVelocity(Hardware.limelight.getRPMFromDistance(avgDistance));

    boolean canShoot;
    if (Hardware.limelight.hasTarget() && horizontalOffset.allWithinError(0, .7) && Robot.flywheel.getFlywheelState() == FlywheelState.ATSPEED) {
      Robot.hopper.runHopper(0.5, .5);
      canShoot = true;
    }else{
      if(!Robot.hopper.getTopBreakbeam()){
          Robot.hopper.runHopper(.3, .3);
      }
      canShoot = false;
    }

    SmartDashboard.putBoolean("can shoot", canShoot);

  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    Robot.drivetrain.stop();
    Robot.hopper.stopMotors();
    Robot.flywheel.setFlywheelState(FlywheelState.OFF);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {

    if (Robot.getRobotState() == RobotState.TELEOP) {
      return !Robot.oi.getAlignButton();
    } else {
      return Timer.getFPGATimestamp() - startTime > timeout;
    }

  }
}
