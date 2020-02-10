/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Hardware;
import frc.robot.Robot;
import frc.robot.subsystems.Flywheel.FlywheelState;
import frc.robot.subsystems.Intake.IntakeState;

public class AutoHopper extends CommandBase {
  /**
   * Creates a new AutoHopper.
   */
  private double startTime;
  private int lastBall;
  public AutoHopper() {
    addRequirements(Robot.hopper);
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    startTime = 0;
    lastBall = 0;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    int balls = Robot.hopper.getBalls();


    if(balls == 3 && lastBall == 2){
      startTime = Timer.getFPGATimestamp();
    }
    if(balls == 4 && lastBall == 3){
      startTime = Timer.getFPGATimestamp();
    }

    if( Robot.flywheel.getFlywheelState() == FlywheelState.SPINNINGUP || Robot.flywheel.getFlywheelState() == FlywheelState.ATSPEED){
      Robot.hopper.runHopper(.3, .3);
    }
    else if(balls ==1){
      Robot.hopper.runHopper(0, 0.3);
    }else if(balls == 2){
      if(!Robot.hopper.getTopBreakbeam())
        Robot.hopper.runHopper(0.1, 0.2);
      else
        Robot.hopper.runHopper(0, 0.2);
    }else if(balls == 3){
      if(Timer.getFPGATimestamp() - startTime > .8)
        Robot.hopper.runHopper(0, 0);
      else 
        Robot.hopper.runHopper(0, .2);
    }else if(balls >= 4){
     // Do nothing
    }else{
      if(Robot.intake.getIntakeState() == IntakeState.INTAKING)  
        Robot.hopper.runHopper(0, .3);
      else 
        Robot.hopper.runHopper(0, 0);
    
    }

    lastBall = balls;
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    Robot.hopper.stopMotors();
    Hardware.intakeFunnel.set(ControlMode.PercentOutput, 0);

  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}