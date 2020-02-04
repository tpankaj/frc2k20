/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Robot;
import frc.robot.Robot.RobotState;
import frc.robot.subsystems.Intake.IntakeState;

public class IntakeCommand extends CommandBase 
{
  /**
   * Creates a new IntakeCommand.
   */
  private double timeout;
  private double startTime;

  public IntakeCommand(double _timeout) 
  {
    addRequirements(Robot.intake);
    timeout = _timeout;
    startTime =0;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize(){
    startTime = Timer.getFPGATimestamp();
    Robot.intake.setIntakeState(IntakeState.DEPLOYING);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {

    if(Robot.intake.getIntakeState() == IntakeState.DEPLOYED)
      Robot.intake.setIntakeState(IntakeState.INTAKING);

  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) 
  {
    Robot.intake.setIntakeState(IntakeState.STOWING);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() 
  {
    if(Robot.getRobotState() == RobotState.TELEOP){
      return !Robot.oi.isIntakeButtonPressed();
    }else{
      return Timer.getFPGATimestamp() - startTime > timeout;
    }
  }
}
