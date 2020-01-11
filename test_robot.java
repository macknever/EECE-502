package Spurs;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class test_robot extends AdvancedRobot {

    /*--------variables--------*/
    double enemyX;
    double enemyY;
    /*--------variables--------*/

    public void run(){
        while (true){
            turnGunLeft(360);
            //goTo(getBattleFieldWidth() - enemyX, enemyY);
            //goTo(enemyX, getBattleFieldHeight() - enemyY);
            //goTo(getBattleFieldWidth() - enemyX, getBattleFieldHeight() - enemyY);
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        stop();
        setTurnRightRadians(Math.cos(e.getBearingRadians()));
        setAhead(4 * e.getVelocity());

        // TODO
    }

    private void goTo(double x, double y) {
        /* Transform our coordinates into a vector */
        x -= getX();
        y -= getY();

        /* Calculate the angle to the target position */
        double angleToTarget = Math.atan2(x, y);

        /* Calculate the turn required get there */
        double targetAngle = Utils.normalRelativeAngle(angleToTarget - getHeadingRadians());

        /*
         * The Java Hypot method is a quick way of getting the length
         * of a vector. Which in this case is also the distance between
         * our robot and the target location.
         */
        double distance = Math.hypot(x, y);

        /* This is a simple method of performing set front as back */
        double turnAngle = Math.atan(Math.tan(targetAngle));
        setTurnRightRadians(turnAngle);
        if(targetAngle == turnAngle) {
            setAhead(distance);
        } else {
            setBack(distance);
        }
    }
}
