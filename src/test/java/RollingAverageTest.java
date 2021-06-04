/*----------------------------------------------------------------------------*/
/* Copyright (c) 2021 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

import static org.junit.Assert.*;

import org.junit.*;

import frc.robot.util.RollingAverage;

public class RollingAverageTest {
    public static final double DELTA = 1e-5;

    @Before
    public void setup() {

    }

    @After
    public void shutdown() {

    }

    @Test
    public void averageOfEmpty() {
        RollingAverage avg = new RollingAverage(3);
        assertEquals(avg.getAverage(), 0, DELTA);
    }

    @Test
    public void averageWithoutEviction() {
        RollingAverage avg = new RollingAverage(3);
        avg.add(1);
        avg.add(2);
        avg.add(3);
        assertEquals(avg.getAverage(), 2, DELTA);
    }
    
    @Test
    public void averageWithEviction() {
        RollingAverage avg = new RollingAverage(3);
        avg.add(1);
        avg.add(2);
        avg.add(3);
        avg.add(4);
        assertEquals(avg.getAverage(), 3, DELTA);
    }

    @Test
    public void averageWithReset() {
        RollingAverage avg = new RollingAverage(3);
        avg.add(1);
        avg.add(2);
        avg.add(3);
        avg.reset();
        assertEquals(avg.getAverage(), 0, DELTA);
        avg.add(4);
        avg.add(5);
        avg.add(6);
        assertEquals(avg.getAverage(), 5, DELTA);
    }

    @Test
    public void allWithinError() {
        RollingAverage avg = new RollingAverage(3);
        avg.add(1);
        avg.add(1.01);
        avg.add(0.99);
        assertTrue(avg.allWithinError(1, 0.02));
        assertFalse(avg.allWithinError(1, 0.0002));
    }
}
