/* 机器人功能试验场地
在这里，将初步实验Robot 的各种函数。
2019.09.24
Author: Lawrence
 */



package Spurs;
/*
Here, the most work for the robot is imitate others by copying their code.
And test the result.
 */

import robocode.AdvancedRobot;

/*public class Intelli_robot extends AdvancedRobot {
    int turnDirection = 1; // Clockwise or counterclockwise

}
*/
import robocode.*;

import java.sql.SQLOutput;

public class Intelli_robot extends AdvancedRobot{
        double previousEnergy = 100;
        int movementDirection = 1;
        int gunDirection = 1;
        public void run() {
            setTurnGunRight(99999);
        }
        public void onScannedRobot(ScannedRobotEvent e) {
            // Stay at right angles to the opponent
            setTurnRight(e.getBearing()+90-
                    30*movementDirection);

            // If the bot has small energy drop,
            // assume it fired
            double changeInEnergy = previousEnergy-e.getEnergy();
            if (changeInEnergy>0 &&
                    changeInEnergy<=3) {
                // Dodge!
                movementDirection =
                        -movementDirection;
                setAhead((e.getDistance()/4+25)*movementDirection);

            }
            // When a bot is spotted,
            // sweep the gun and radar
            gunDirection = -gunDirection;
            setTurnGunRight(99*gunDirection);

            // Fire directly at target
            fire ( 2 ) ;

            // Track the energy level
            previousEnergy = e.getEnergy();

        }
}