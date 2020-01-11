package Spurs;



import robocode.*;

import java.awt.*;
import java.io.*;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import Spurs.neuralNet_law;

//This is a Robot_Classifier, try to classify the enemy
public class Robot_Classifier extends AdvancedRobot {

    /*--------variables-------*/


    /*--------temp-parameter static-------*/
    static int[] winsRate =new int[1001];
    static double[] distance_diff = new double [1001];
    static double[] bearing_diff = new double [1001];
    static double[] energys = new double [1001];
    /*--------temp-parameter robot-------*/
    double bearing;
    double distance = 0;
    double energy = 0;
    static int shoot_count;
    int count = 0;



    /*--------FILE-------*//*--------FILE-------*/

    File dd = new File("/Users/Lawrence Li/Documents/data_file/dd.txt"); // distance diff
    File bd = new File("/Users/Lawrence Li/Documents/data_file/bd.txt"); // bearing diff
    File fd = new File("/Users/Lawrence Li/Documents/data_file/fd.txt"); // bearing diff






    /*--------variables-------*/

    /*--------Methods-------*//*--------Methods-------*//*--------Methods-------*/
    /*--------Methods-------*//*--------Methods-------*//*--------Methods-------*/

    /*--------save and load-------*/

    //save the value of (State, Action) in to a file






    public void savedd(){// save distance difference
        PrintStream w = null;
        try
        {
            w = new PrintStream(new FileOutputStream(dd));
            w.println(av(distance_diff));
        }
        catch (IOException e) {
            e.printStackTrace();
        }finally {
            w.flush();
            w.close();
        }
    }

    public void savebd(){// save distance difference
        PrintStream w = null;
        try
        {
            w = new PrintStream(new FileOutputStream(bd));
            w.println(av(bearing_diff));
        }
        catch (IOException e) {
            e.printStackTrace();
        }finally {
            w.flush();
            w.close();
        }
    }

    public void savefd(){// save fire data
        PrintStream w = null;
        try
        {
            w = new PrintStream(new FileOutputStream(fd));
            w.println(av(energys));
            w.println(shoot_count/cou(distance_diff));

        }
        catch (IOException e) {
            e.printStackTrace();
        }finally {
            w.flush();
            w.close();
        }
    }

    /*--------OnRobots Methods-------*/
    public void onBattleEnded(BattleEndedEvent e) {


        savedd();
        savebd();
        savefd();

    }




    public void onWin(WinEvent event){

        winsRate[getRoundNum()] = 1;
    }
    public void onHitWall(HitWallEvent e){
        setBack(100);
    }
    public void onDeath(DeathEvent event){

        winsRate[getRoundNum()] = 0;

    } //on series

    public void onScannedRobot(ScannedRobotEvent e) {


        bearing = e.getBearing();
        distance = e.getDistance();
        energy = e.getEnergy();
        if(count < 200) {
            count++;
            System.out.println("count = "+ count);
            if(count == 0){
                distance_diff[0] = distance;
                bearing_diff[0] = bearing;
                energys[0]  = energy;
            }
            else {
                distance_diff[count] = distance - distance_diff[count-1];
                bearing_diff[count] = bearing- distance_diff[count-1];
                energys[count] = energy - energys[count-1];

                if(energys[count]  == 0) shoot_count++;
            }
        }
        if(Math.random()<0.5) {

            setAhead((e.getDistance() / 4 + 25) * 1);
        }


    }

    public double av(double[] ar){
        double av = 0;
        int c = 0;
        for (int i = 0; i < ar.length; i++) {
            if(ar[i] != 0) {c++;
            av += ar[i];}
        }
        av = av/c;
        return av;
    }

    public int cou(double[] ar){
        int c = 0;
        for (int i = 0; i < ar.length; i++) {
            if(ar[i]!=0)c++;
        }
        return c;
    }








    /*--------Main Part RUN-------*/
    public void run(){

        // Set colors
        setBodyColor(new Color(221, 175, 19));
        setGunColor(new Color(11,77,113));
        setRadarColor(new Color(99,228,199));
        setBulletColor(new Color(255,238,0));
        setScanColor(new Color(255,241,46));

        // Every part of the robot moves freely from the others.
        setAdjustRadarForRobotTurn(true);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);


        turnRadarLeft(360);
        while (true) {
            //turnRadarLeft(360);
        turnRadarLeft(360);

        }
    }
}
