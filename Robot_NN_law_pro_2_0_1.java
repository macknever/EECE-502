package Spurs;

import robocode.*;

import java.awt.*;
import java.io.*;
import java.util.Arrays;

import static robocode.util.Utils.normalRelativeAngleDegrees;
import Spurs.CircularDoubleArrayQueue;
import Spurs.neuralNet_law;

//This robot gonna use lookuptable as consultant to fight!
public class Robot_NN_law_pro_2_0_1 extends AdvancedRobot {

    /*--------variables-------*/

    /*--------hyper-parameter part-------*/
    private final double alpha = 0.4;//learning rate
    private final double gamma = 0.2;//discount
    private final double exploratory_rate = 0.2;//exploratory_rate

    /*--------temp-parameter String & double-------*/
    private double[] state_NN;
    private double[][] states ;
    private double[][] values ;
    private double[] state_action_pre = new double[13];     //7 input, 6 output
    private double[] state_action_for = new double[13];     //7 input, 6 output
    private double[] Q_values_pre = new double[6];
    private double[] Q_values_for = new double[6];
    private double q_Value_pre;
    private double q_Value_for;
    private double[] energy_Tmp = new double[2];
    double tmp_v;//store temporary score

    private int action_pre;

    public double[] total_reward_a = new double[9999];
    static double[] total_reward_r = new double[501];

    /*--------temp-parameter Double-------*/
    double reward=0;
    double energy_Dif =0;
    /*--------temp-parameter static-------*/
    static int[] winsRate =new int[1001];

    /*--------temp-parameter robot-------*/
    double bearing;
    double distance = 0;

    /*--------temp-parameter strategy-------*/
    int next_Action = 0;
    int turnDirection;

    /*--------temp-parameter circle-------*/
    double absoluteBearing;
    double bearingFromGun;
    double bearingFromRadar;
    boolean movingForward;
    boolean inWall;

    /*--------ON_OFF_POLICY-------*/
    boolean ON_OFF_POLICY=false; //true means on policy



    /*--------LOOKUPTABLE-------*//*--------LOOKUPTABLE-------*/
    /*public static Object LUT[][] = new Object[9*4*3*6*4][2];
    double[][] in = new double[LUT.length][5];
    double[][] out = new double[LUT.length][1];*/

    /*--------Neural Network-------*//*--------Neural Network-------*/

    neuralNet_law nn_Agent = new neuralNet_law(7,6);        //this is a Neural Net
    CircularDoubleArrayQueue Q = new CircularDoubleArrayQueue(100);     //this is a queue storing state and value

    /*--------FILE-------*//*--------FILE-------*/

    File Win_rate = new File("/Users/Lawrence Li/robocode/robots/Spurs/LUT_robot.data/Win_rate_pro_2.txt");

    File state_value_file = new File("/Users/Lawrence Li/robocode/robots/Spurs/LUT_robot.data/State_Values_pro_2.txt");
    File rewards = new File("/Users/Lawrence Li/robocode/robots/Spurs/LUT_robot.data/rewards.txt");





    public Robot_NN_law_pro_2_0_1() throws IOException {
    }




    /*--------variables part end-------*/

    /*--------Methods-------*//*--------Methods-------*//*--------Methods-------*/
    /*--------Methods-------*//*--------Methods-------*//*--------Methods-------*/

    /*--------save and load-------*/

    //save the value of (State, Action) in to a file
    public void save(CircularDoubleArrayQueue q) {

        Object[] le = q.toArray();
        PrintStream w = null;

        try {
            w = new PrintStream(new FileOutputStream(state_value_file));
            for (int i=0;i<le.length;i++) {
                w.println(q.get(i)[0]+","+
                        q.get(i)[1]+","+
                        q.get(i)[2]+","+
                        q.get(i)[3]+","+
                        q.get(i)[4]+","+
                        q.get(i)[5]+","+
                        q.get(i)[6]+","+
                        q.get(i)[7]+","+
                        q.get(i)[8]+","+
                        q.get(i)[9]+","+
                        q.get(i)[10]+","+
                        q.get(i)[11]+","+
                        q.get(i)[12]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            w.flush();
            w.close();
        }

    }//save
    public void load(CircularDoubleArrayQueue q) throws IOException {
        BufferedReader reader = new BufferedReader(new
                FileReader(state_value_file));
        String line = reader.readLine();
        try {
            int st=0;
            while (line != null) {

                String splitLine[] = line.split(",");
                double[] dv = Arrays.stream(splitLine).mapToDouble(Double::parseDouble).toArray();
                q.add(dv);
                line = reader.readLine();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            reader.close();
        }
    }//load*/

    public double countr(double[] r){
        int l = r.length;
        double count = 0.0;
        for (int i = 0; i < l; i++) {
            count += r[l];
        }
        return count;
    }

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
    public void saveReward(){
        PrintStream w = null;
        try
        {
            w = new PrintStream(new FileOutputStream(rewards));
            for(int i=0; i<total_reward_r.length; i++)
                w.println(total_reward_r[i]);
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

        //n.write_excel(winsRate);
        saveWinRate();
        save(Q);
    }
    public void onHitRobot(HitRobotEvent e){
        reward -= 4;

    } //our robot hit by enemy robot
    public void onBulletHit(BulletHitEvent event){reward += 3;} //one of our bullet hits enemy robot
    public void onHitByBullet(HitByBulletEvent event){reward -=2;} //when our robot is hit by a bullet
    public void onHitWall(HitWallEvent e){
        reward -= 10;

    }
    public void onRoundEnded(RoundEndedEvent e){
        saveWinRate();

        System.out.println("total reward of "+getRoundNum()+"is :"+total_reward_r[getRoundNum()]);
        saveReward();
    }
    public void onWin(WinEvent event){
        reward = 10;
        winsRate[getRoundNum()] = 1;

        Q_values_for = nn_Agent.outPut1(state_NN);

        q_Value_for = Q_values_for[next_Action-1];
        //state_action_for[1] = action_value_LUT(state_action_for[0]);


        tmp_v = learn_from_NN(q_Value_pre,q_Value_for);
        state_action_pre[7+action_pre] = tmp_v;
        Q.add(state_action_pre);
        states = queue_to_state(Q);
        values = queue_to_value(Q);
        nn_Agent.trainNet(states,values,1);
    }
    public void onDeath(DeathEvent event){
        reward = -5;
        winsRate[getRoundNum()] = 0;
        Q_values_for = nn_Agent.outPut1(state_NN);

        q_Value_for = Q_values_for[next_Action-1];
        //state_action_for[1] = action_value_LUT(state_action_for[0]);


        tmp_v = learn_from_NN(q_Value_pre,q_Value_for);
        state_action_pre[7+action_pre] = tmp_v;
        Q.add(state_action_pre);
        states = queue_to_state(Q);
        values = queue_to_value(Q);
        nn_Agent.trainNet(states,values,1);

    } //on series

    public void onScannedRobot(ScannedRobotEvent e) {

        bearing = e.getBearing();
        distance = e.getDistance();
        energy_Dif = getEnergy()-e.getEnergy();


        absoluteBearing = getHeading() + e.getBearing();
        bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
        bearingFromRadar = normalRelativeAngleDegrees(absoluteBearing - getRadarHeading());


        //smartFire(distance);
        state_NN = state_catcher(getX(),getY(),getHeading(),bearing,distance,getEnergy(),e.getEnergy());
        if (Math.abs(bearingFromGun) <= 4) {
            setTurnGunRight(bearingFromGun);


            if (getGunHeat() == 0 && getEnergy() > .2) {
                fire(Math.min(4.5 - Math.abs(bearingFromGun) / 2 - distance / 250, getEnergy() - .1));
            }
        }
        else {
            setTurnGunRight(bearingFromGun);

        }
        if (bearingFromGun == 0) {
            scan();
        }



    }

    /*--------Strategy Methods-------*/
    public void action6(int c){
        switch (c){
            case 1:{
                advance();
                break;}
            case 2:{
                retreat();
                break;}
            case 3:{
                Rainbow();
                break;
            }
            case 4:{
                Dragon();
                break;
            }
            case 5:{
                rainbow();
                break;
            }
            case 6:{
                dragon();
                break;
            }
        }
    }

    public int exploratory_action() {

        int action=0;
        action = 1+(int)(Math.random()*(6));
        return action;
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


    /*--------Data Process Methods-------*/

    public double learn_from_NN(double gain,double pain){ return gain+alpha*(reward+gamma*pain - gain);}


    /*--------Quantization Methods-------*/ //Using NN to compute the Q-value, don't need quantization

    /*--------Quantization Methods end-------*/

    public double[] state_catcher(double x,double y,double h,double b,double d,double e1,double e2){
        double[] state_now = new double[7];
        state_now[0] = x/getWidth();       //x-axis
        state_now[1] = y/getHeight();       //y-axis
        state_now[2] = h/360;       //heading
        state_now[3] = b/360;       //bearing
        state_now[4] = d/1000;      //distance
        state_now[5] = e1/100;      //my energy
        state_now[6] = e2/100;      //enemy's energy
        return state_now;
    }
    /*--------Actions Methods-------*/

    private void advance(){

        setAhead(100);
        execute();

    }
    private void retreat(){

        setBack(100);
        execute();
    }
    private void dragon(){

        setTurnRight(90);
        execute();
        setAhead(100);
        execute();
    }
    private void rainbow(){

        setTurnLeft(90);
        setAhead(100);
        execute();

    }
    private void Dragon(){

        setTurnRight(90);
        execute();
        setBack(100);
        execute();
    }
    private void Rainbow(){

        setTurnLeft(90);
        execute();
        setBack(100);
        execute();

    }




    public void reverseDirection() {
        if (movingForward) {
            setBack(200);
            movingForward = false;
        } else {
            setAhead(200);
            movingForward = true;
        }
        execute();
    }

    public void avoidwall(){
        if (getX() > 100 &&
                getY() > 100 &&
                getBattleFieldWidth() - getX() > 100 &&
                getBattleFieldHeight() - getY() > 100 &&
                inWall == true) {
            inWall = false;
        }
        if (getX() <= 100 ||
                getY() <= 100 ||
                getBattleFieldWidth() - getX() <= 100 ||
                getBattleFieldHeight() - getY() <= 100) {
            if ( inWall == false){
                reverseDirection();
                inWall = true;
            }
        }
    }




    /*--------Transform Look Up Table value to the input of Neural Network-------*/
    public double [][] queue_to_state(CircularDoubleArrayQueue q){
        Object[] le = q.toArray();
        double[][] s =new double[le.length][7];

        for (int i = 0; i < le.length; i++) {
            for (int j = 0; j < 7; j++) {
                s[i][j] = q.get(i)[j];
            }
        }
        return s;
    }

    public double [][] queue_to_value(CircularDoubleArrayQueue q){
        Object[] le = q.toArray();
        double[][] v =new double[le.length][6];

        for (int i = 0; i < le.length; i++) {
            for (int j = 7; j < 13; j++) {
                v[i][j-7] = q.get(i)[j];
            }
        }
        return v;
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

            try {

                load(Q);
            }
            catch (IOException e) {
                e.printStackTrace();
            }


            //explore or greedy!

                if (Math.random() < exploratory_rate) {
                    next_Action = exploratory_action();
                } else {
                    next_Action = nn_Agent.max_output(state_NN);
                }


            //this *_pre is the action  and Q_value at present
            Q_values_pre = nn_Agent.outPut1(state_NN);

            state_action_pre = nn_Agent.double_combine(state_NN,Q_values_pre);
            action_pre = next_Action-1;
            q_Value_pre = Q_values_pre[action_pre];
            //state_action_pre[1] = action_value_LUT(state_action_pre[0]);

            //Before action there is no reward!
            reward=0;

            //enery_tmp store the energy difference before and after action
            energy_Tmp[0] = energy_Dif;

            //Take action in the pre-set method
            action6(next_Action);
            //Check where I am
            turnRadarLeft(360);


            energy_Tmp[1] = energy_Dif;
            //If the energy difference is growing, it's great
            if(energy_Tmp[0]-energy_Tmp[1]>0){reward +=3;}
            else if(energy_Tmp[0]-energy_Tmp[1]==0){reward +=0;}
            else{reward -= 1;}

            //The next move is exploratory or greedy?
            //No matter what happen we use off-policy to learn

            total_reward_r[getRoundNum()] += reward;


            if(!ON_OFF_POLICY) {

                if (Math.random() < exploratory_rate) {
                    next_Action = exploratory_action();
                } else {
                    next_Action = nn_Agent.max_output(state_NN);
                }
            }else{
                next_Action = nn_Agent.max_output(state_NN);
            }

            Q_values_for = nn_Agent.outPut1(state_NN);

            q_Value_for = Q_values_for[next_Action-1];
            //state_action_for[1] = action_value_LUT(state_action_for[0]);

            System.out.println(reward);
            tmp_v = learn_from_NN(q_Value_pre,q_Value_for);
            state_action_pre[7+action_pre] = tmp_v;
            Q.add(state_action_pre);
            save(Q);
            states = queue_to_state(Q);
            values = queue_to_value(Q);
            nn_Agent.trainNet(states,values,1);
            //nn_Agent.trainNet();

            //store what i got.
            //save();
        }
    }
}
