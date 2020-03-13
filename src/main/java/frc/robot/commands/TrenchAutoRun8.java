/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import java.util.List;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Robot;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/latest/docs/software/commandbased/convenience-features.html
public class TrenchAutoRun8 extends SequentialCommandGroup {
  /**
   * Creates a new TrenchAutoRun.
   */
  public TrenchAutoRun8() {
    addCommands(
      new ParallelRaceGroup(    
        new SequentialCommandGroup(
          new ParallelCommandGroup(
            getTrajectory1(),
            new IntakeCommand().withTimeout(3)
          ),
          new ParallelRaceGroup(
            getTrajectory2(),
            new IntakeCommand()
        )
      ),
        new AutoHopper().withTimeout(15)
      ),
      new AutoShoot(6000).withTimeout(4),
      new ParallelRaceGroup(  
        new SequentialCommandGroup(    
          new ParallelRaceGroup(
            getTrajectory3(),
            new IntakeCommand().withTimeout(5.5)
          ),
          new ParallelRaceGroup(
            getTrajectory4(),
            new IntakeCommand()
          )
        ),
          new AutoHopper()
      )
    );
  }

  public Command getTrajectory1(){
    Robot.drivetrain.setOdometry(new Pose2d(12.946, -6.747, new Rotation2d()) );
    Robot.drivetrain.invertPathDirection(true);
    
    Trajectory traj = TrajectoryGenerator.generateTrajectory(List.of(
    new Pose2d(12.946, -6.747, new Rotation2d()),
    new Pose2d(10.27, -5.99, new Rotation2d(.346,-.532))
    ), Robot.drivetrain.getTrajectoryConfig());
        
    return Robot.generatePath(traj);
  }


  public Command getTrajectory2(){
    Robot.drivetrain.invertPathDirection(false);
    
    Trajectory traj = TrajectoryGenerator.generateTrajectory(List.of(
      new Pose2d(10.27, -5.99, new Rotation2d(.346,-.532)),
      new Pose2d(12.13, -6.807, new Rotation2d(1.8,.334))
    ), Robot.drivetrain.getTrajectoryConfig());
    
    
    return Robot.generatePath(traj);
  }

  public Command getTrajectory3(){
    Robot.drivetrain.invertPathDirection(true);
    
    Trajectory traj = TrajectoryGenerator.generateTrajectory(List.of(
      new Pose2d(12.13, -6.807, new Rotation2d(1.8,.334)),
      new Pose2d(10.68, -7.514, new Rotation2d()),
      new Pose2d(8.68, -7.514, new Rotation2d())
    ), Robot.drivetrain.getTrajectoryConfig());
    
    
    return Robot.generatePath(traj);
  }

  public Command getTrajectory4(){
    Robot.drivetrain.invertPathDirection(false);
    
    Trajectory traj = TrajectoryGenerator.generateTrajectory(List.of(
      new Pose2d(8.68, -7.514, new Rotation2d()),
      new Pose2d(10.68, -7.514, new Rotation2d())
    ), Robot.drivetrain.getTrajectoryConfig());
    
    
    return Robot.generatePath(traj);
  }


  public String toString(){
    return "TrenchAutoRun8";
  }
}