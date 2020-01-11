package Spurs;

import robocode.*;

import java.awt.*;
import java.io.*;
import java.util.Random;

import static robocode.util.Utils.normalRelativeAngleDegrees;

public class LUT_robot extends AdvancedRobot {

    private final double alpha = 0.3;//learning rate
    private final double gamma = 0.9;//discount
    private final double exploratory_rate = 0.3;

    private String state_lut;
    private String[] state_action_pre = new String[2];
    private String[] state_action_for = new String[2];
    double reward=0;
    static int[] winsRate =new int[500];

    int next_Action = 1;
    double bearing;
    int turnDirection;
    double energy_Dif =0;
    double[] energy_Tmp = new double[2];


    double absoluteBearing ;
    double bearingFromGun ;
    double bearingFromRadar ;

    boolean movingForward;
    boolean inWall;
    excel_test n = new excel_test();
    /*the robot built by this class will action follow the look up table.*/
    /*reward set by function
     */

    String tmp_v ="";//store temporary score

    double distance = 0;
    int[] enemy_location;


    public static Object LUT[][] = new Object[9*4*3*6*4][2];

    //File LUT_law =new File("/Users/Lawrence Li/robocode/robots/Spurs/LUT_robot.data/LUT_law.txt");
    //File LUT_law =new File("/Users/Lawrence Li/robocode/robots/LUT_law.txt");
    File LUT_law =new File("/Users/Lawrence Li/robocode/robots/LUT_law22.txt");
    File Win_rate = new File("/Users/Lawrence Li/robocode/robots/Spurs/LUT_robot.data/Win_rate.txt");

    public LUT_robot() throws IOException {
    }

    public void onBattleEnded(BattleEndedEvent e) {

        n.write_excel(winsRate);
        saveWinRate();

    }
    public void onHitRobot(HitRobotEvent e){
        reward -= 1;

    } //our robot hit by enemy robot
    public void onBulletHit(BulletHitEvent event){reward += 2;} //one of our bullet hits enemy robot
    public void onHitByBullet(HitByBulletEvent event){reward -=1;} //when our robot is hit by a bullet
    public void onHitWall(HitWallEvent e){
        reward-= 2;//earlier it was -2
    }
    public void onRoundEnded(RoundEndedEvent e){
        save();
    }
    public void onWin(WinEvent event){
        reward += 3;
        winsRate[getRoundNum()] = 1;
    }
    public void onDeath(DeathEvent event){
        reward -= 3;
        winsRate[getRoundNum()] = 0;

    } //on series


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

        if (getX() <= 50 || getY() <= 50 || getBattleFieldWidth() - getX() <= 50 || getBattleFieldHeight() - getY() <= 50) {
            inWall = true;
        } else {
            inWall = false;
        }

        movingForward = true;
            //save();

            try {
                load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            save();

        while (true) {
            setTurnRadarRight(360);
            //load command
            try {
                load();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            if (getX() > 50 && getY() > 50 && getBattleFieldWidth() - getX() > 50 && getBattleFieldHeight() - getY() > 50 && inWall == true) {
                inWall = false;
            }
            if (getX() <= 50 || getY() <= 50 || getBattleFieldWidth() - getX() <= 50 || getBattleFieldHeight() - getY() <= 50 ) {
                if ( inWall == false){
                    reverseDirection();
                    inWall = true;
                }
            }


            if(Math.random()<exploratory_rate) {
                next_Action = exploratory_action();
            }else{
                next_Action = greedy_action();
            }
            state_action_pre[0] = state_lut + Integer.toString(next_Action);

            state_action_pre[1] = action_value_LUT(state_action_pre[0]);

            reward=0;
            energy_Tmp[0] = energy_Dif;

            action4(next_Action);

            energy_Tmp[1] = energy_Dif;
            if(energy_Tmp[0]-energy_Tmp[1]>0){
                reward +=1;
            }else if(energy_Tmp[0]-energy_Tmp[1]==0){reward +=0;}
            else{reward -= 1;}

            if(Math.random()<exploratory_rate) {
                next_Action = exploratory_action();
            }else{
                next_Action = greedy_action();
            }
            state_action_for[0] = state_lut + Integer.toString(next_Action);

            state_action_for[1] = action_value_LUT(state_action_for[0]);




            tmp_v = learn_from_LUT(state_action_pre[1],state_action_for[1]);
            state_action_pre[1] = tmp_v;
            update(state_action_pre);



            save();
        }
    }

    //save the value of (State, Action) in to a file
    public void save() {

        PrintStream w = null;

        try {
            w = new PrintStream(new FileOutputStream(LUT_law));
            for (int i=0;i<LUT.length;i++) {
                w.println(LUT[i][0]+","+LUT[i][1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            w.flush();
            w.close();
        }

    }//save
    public void saveWinRate(){
        PrintStream w = null;
        try
        {
            w = new PrintStream(new FileOutputStream(Win_rate));
            for(int i=0; i<winsRate.length; i++)
                w.println(winsRate[i]);
        }
        catch (IOException e) {
            e.printStackTrace();
        }finally {
            w.flush();
            w.close();
        }
    }

    public void action4(int c){
        switch (c){
            case 1:{
                retrieve();//retrieve;
                break;}
            case 2:{
                Yajigigi();//toward enemy
                break;}
            case 3:{
                circle();
                break;
            }
            case 4:{
                circle_left();
                break;
            }
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {

        absoluteBearing = getHeading() + e.getBearing();
        bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
        bearingFromRadar = normalRelativeAngleDegrees(absoluteBearing - getRadarHeading());


        smartFire(e.getDistance());
        state_lut = quantize_state(getX(),getY(),e.getBearing(),e.getDistance(),getEnergy(),e.getEnergy());
        System.out.println(getX()+" , "+getY());
        //state_lut = quantize_state(e.getDistance(),getEnergy(),e.getEnergy());

        bearing = e.getBearing();
        distance = e.getDistance();
        energy_Dif = getEnergy()-e.getEnergy();

    }

    public int greedy_action() {

        double value_tmp = -500;
        int action = 1;
        int[] f = {1,2,3,4};
        for (int j = 0; j < LUT.length; j++) {
            for (int i : f) {
                String state_action = state_lut + Integer.toString(i);
                if (LUT[j][0].equals(state_action)) {
                    if (value_tmp < Double.valueOf((String) LUT[j][1])) {
                        value_tmp = Double.valueOf((String) LUT[j][1]);
                        action = i;
                        break;
                    }
                }
            }
        }
        return action;
    }

    /*public int greedy_action41() {
        double value_tmp = -500;
        int action = 0;
        int [] f = {11,21,31,41,12,22,32,42};

        for (int j = 0; j < LUT.length; j++) {
            //System.out.println("this is 1st for"+LUT[0][1]);

            for (int i :f) {

                String state_action = state_lut + Integer.toString(i);

                //System.out.println(state_action);
                if (LUT[j][0].equals(state_action)) {
                    //System.out.println("found");
                    if (value_tmp < Double.valueOf((String) LUT[j][1])) {
                        value_tmp = Double.valueOf((String) LUT[j][1]);
                        action = i;
                        break;
                    }
                    //System.out.println("action is found:"+action);
                }
            }

        }
        return action;
    }*/

    //make a random int from 1-4
    public int exploratory_action() {

        int action=0;
        action = 1+(int)(Math.random()*(4));
        return action;
    }

   /* public int exploratory_action41() {

        int action=0;
        int action1=0;
        int action2=0;
        action1 = 1+(int)(Math.random()*(4));
        action2 = 1+(int)(Math.random()*(2));
        action = action1*10+action2;
        return action;
    }*/


    public void load() throws IOException {
        BufferedReader reader = new BufferedReader(new
                FileReader(LUT_law));
        String line = reader.readLine();
        try {
                int st=0;
                while (line != null) {

                    String splitLine[] = line.split(",");
                    LUT[st][0] = splitLine[0];
                    LUT[st][1] = splitLine[1];
                    if(st<LUT.length-1){st++;}
                    line = reader.readLine();
                }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            reader.close();
        }
    }

    //If take this action, what is the Q value
    public String action_value_LUT(String st){

        String value="";
        for (int j = 0; j < LUT.length; j++) {

            if (st.equals(LUT[j][0])) {
                value = (String)LUT[j][1];
                break;
            }

        }//for j
        return value;
    }

    //Use the next move to teach the present move, LookUpTable is the teacher.
    public String learn_from_LUT(String g,String punishment){
        double pain = Double.parseDouble(punishment);
        double gain = Double.parseDouble(g);
        double new_gain = gain+alpha*(reward+gamma*pain - gain);
        String gg = Double.toString(new_gain);
        return gg;
    }

    //Update method, write the new node to the LookUpTable
    private void update(String [] sa){
        for (int j = 0; j < LUT.length; j++) {

            if (sa[0].equals(LUT[j][0])) {
                LUT[j][1] = sa[1];
                break;
            }

        }//for j

    }

    private void retrieve(){
        setTurnLeft(bearing);
        setBack(distance);
    }

    private void Yajigigi(){
        setTurnLeft(bearing);
        setAhead(distance-5);
    }

    public void circle_left() {
        // switch directions if we've stopped
        if (getVelocity() == 0)turnDirection *= -1;

        // circle our enemy
        setTurnLeft(bearing + 90);
        setAhead(1000 * turnDirection);
    }



    public void reverseDirection() {
        if (movingForward) {
            setBack(40000);
            movingForward = false;
        } else {
            setAhead(40000);
            movingForward = true;
        }
    }




    public void smartFire(double robotDistance) {
        if (robotDistance <= 200.0D && this.getEnergy() >= 15.0D) {
            if (robotDistance > 50.0D) {
                this.fire(2.0D);
            } else {
                this.fire(3.0D);
            }
        } else {
            this.fire(1.0D);
        }

    }


    public int quantize_bearing(double bearing){
        int k = 0;
        if(0 <= bearing & bearing < 90 ){
            k = 1;
        }else if(90 <= bearing & bearing < 180 ){
            k = 2;
        }else if(-180 <= bearing & bearing < -90 ){
            k = 3;
        }else if(-90 <= bearing & bearing < 0 ){
            k = 4;
        }
        return k;
    }
    public int quantize_distance_lut(double distance){
        int dis = (int)(distance/96);//real distance
        if(dis<2){return 1;}
        if(dis>2 & dis<5){return 2;}
        else{return 3;}
    }
    public int quantize_energy(double energy) {
        int ene = (int) (energy / 10);
        return ene;
    }

    public int quantize_energy_pro(double e1,double e2) {
        int e=1+(int)Math.random()*6;
        if(e1<e2&e1<3){e=1;}
        if(e1<e2&e1>3&e1<6){e=2;}
        if(e1<e2&e1>6){e=3;}
        if(e1>e2&e1<3){e=4;}
        if(e1>e2&e1>3&e1<6){e=5;}
        if(e1>e2&e1>6){e=6;}
        return e;
    }

    public String quantize_state(double x,double y, double bearing, double distance, double energy_me,double energy_enemy){
        int loc = quantize_location(x,y);//location area
        int bea = quantize_bearing(bearing);//bearing
        int dis = quantize_distance_lut(distance);   //distance
        int e1 = quantize_energy(energy_me);
        int e2 = quantize_energy(energy_enemy);
        int ene = quantize_energy_pro(e1,e2); //quantized energy
        String state = Integer.toString(loc)
                +Integer.toString(bea)
                +Integer.toString(dis)
                +Integer.toString(ene);

        return state;
    }




    public static void ini_table(){
    }

    public int quantize_location(double a, double b){
        int a1 = (int)(a/270)+1;
        int b1 = (int)(b/200)+1;
        int l =0;
        String ab = Integer.toString(a1)+Integer.toString(b1);
        switch (ab){
            case "11":{l=1;break;}
            case "12":{l=2;break;}
            case "13":{l=3;break;}
            case "21":{l=4;break;}
            case "22":{l=5;break;}
            case "23":{l=6;break;}
            case "31":{l=7;break;}
            case "32":{l=8;break;}
            case "33":{l=9;break;}

        }
        return l;
    }

    public void circle(){
        if (movingForward){
            setTurnRight(normalRelativeAngleDegrees(bearing + 80));
        } else {
            setTurnRight(normalRelativeAngleDegrees(bearing + 100));
        }
        if (Math.abs(bearingFromGun) <= 4) {
            setTurnGunRight(bearingFromGun);
            setTurnRadarRight(bearingFromRadar);

            if (getGunHeat() == 0 && getEnergy() > .2) {
                fire(Math.min(4.5 - Math.abs(bearingFromGun) / 2 - distance / 250, getEnergy() - .1));
            }
        } // otherwise just set the gun to turn.
        //
        else {
            setTurnGunRight(bearingFromGun);
            setTurnRadarRight(bearingFromRadar);
        }
        if (bearingFromGun == 0) {
            scan();
        }
    }

}
