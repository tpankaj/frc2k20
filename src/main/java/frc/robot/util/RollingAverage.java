/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.util;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Add your docs here.
 */
public class RollingAverage {

    private int size;
    private double sum;
    private Queue<Double> queue;

    public RollingAverage(int _size){
        size = _size;
        queue = new LinkedList<>();
        sum = 0;
    }

    public void add(double x){
        if (queue.size() >= size) {
            sum -= queue.remove();
        }
        queue.add(x);
        sum+=x;
    }

    public double getAverage(){
        return sum/size;
    }

    public void reset(){
        queue = new LinkedList<>();
        sum = 0;
    }

    public boolean allWithinError(double target, double acceptableError){
        for (Double val : queue) {
            if(Math.abs(val - target) > acceptableError)
                return false; 
         }

        return true;
    }

    
}