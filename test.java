package Spurs;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.Color;

import java.awt.geom.Point2D;
import java.io.*;

//import com.sun.javafx.geom.Point2D;

import robocode.*;

import java.util.Random;
import robocode.control.RobotSetup;

public class test extends AdvancedRobot{

    File LUT_law =new File("/Users/Lawrence Li/robocode/robots/Spurs/LUT_robot.data/LUT_law22.txt");
    double[][] in = new double[9*4*3*6*4][5];
    double[][] out = new double[9*4*3*6*4][1];
    public static Object[][] LUT = new Object[9*4*3*6*4][2];
    static String state="011000";
    public  void save() {

        PrintStream w = null;

        try {
            w = new PrintStream(new RobocodeFileOutputStream(getDataFile("LUT_law.txt")));
            for (int i=0;i<13;i++) {
                w.println(1+","+1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            w.flush();
            w.close();
        }

    }//save






    public static int[] quantize(double a, double b){
        int a1 = (int)(a/100)+1;
        int b1 = (int)(b/100)+1;
        int[] r ={a1,b1};
        return r;
    }
    public static void ini_table(){
        int count = 0;
        PrintStream fout =null;
        FileOutputStream pout =null;
        //x,y,distance,bearing,blood_me,blood_rival,action
        try{
            pout = new FileOutputStream("LUT_law.txt",true);
            fout = new PrintStream(pout,true);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    for (int k = 0; k < 3; k++) {
                        for (int l = 0; l < 4; l++) {


                            fout.println(Integer.toString(i) +
                                    Integer.toString(j) +
                                    Integer.toString(k) +
                                    Integer.toString(l) +
                                    "," + 0.5);
                        }//l
                    }//k
                }//j
            }//i
        }//try
        catch(IOException e){e.printStackTrace();}
        finally {
            fout.close();
        }
        //LUT = new Object[count][2];

    }

    public static void ini_table21(){
        int count = 0;
        PrintStream fout =null;
        FileOutputStream pout =null;
        //x,y,distance,bearing,blood_me,blood_rival,action
        try{
            pout = new FileOutputStream("LUT_law21.txt",true);
            fout = new PrintStream(pout,true);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    for (int k = 0; k < 3; k++) {
                        for (int l = 1; l <= 4; l++) {
                            for (int m = 1; m <= 2; m++) {


                                fout.println(Integer.toString(i) +
                                        Integer.toString(j) +
                                        Integer.toString(k) +
                                        Integer.toString(l) +
                                        Integer.toString(m) +
                                        "," + 0.5);
                            }//m
                        }//l
                    }//k
                }//j
            }//i
        }//try
        catch(IOException e){e.printStackTrace();}
        finally {
            fout.close();
        }
        //LUT = new Object[count][2];

    }

    public static void ini_table22(){
        int count = 0;
        PrintStream fout =null;
        FileOutputStream pout =null;
        //x,y,distance,bearing,blood_me,blood_rival,action
        try{
            pout = new FileOutputStream("LUT_law22.txt",true);
            fout = new PrintStream(pout,true);
            for (int i = 1; i <= 9; i++) {
                for (int j = 1; j <= 4; j++) {
                    for (int k = 1; k <= 3; k++) {
                        for (int l = 1; l <= 6; l++) {
                            for (int m = 1; m <= 4; m++) {


                                fout.println(Integer.toString(i) +
                                        Integer.toString(j) +
                                        Integer.toString(k) +
                                        Integer.toString(l) +
                                        Integer.toString(m) +
                                        "," + 0.5);
                            }//m
                        }//l
                    }//k
                }//j
            }//i
        }//try
        catch(IOException e){e.printStackTrace();}
        finally {
            fout.close();
        }
        //LUT = new Object[count][2];

    }
    public static void ini_table23(){
        int count = 0;
        PrintStream fout =null;
        FileOutputStream pout =null;
        //x,y,distance,bearing,blood_me,blood_rival,action
        try{
            pout = new FileOutputStream("LUT_law24.txt",true);
            fout = new PrintStream(pout,true);
            for (int i = 1; i <= 9; i++) {
                for (int j = 1; j <= 4; j++) {
                    for (int k = 1; k <= 3; k++) {
                        for (int l = 1; l <= 6; l++) {
                            for (int m = 1; m <= 4; m++) {


                                fout.println(Integer.toString(i) +
                                        Integer.toString(j) +
                                        Integer.toString(k) +
                                        Integer.toString(l) +
                                        Integer.toString(m) +
                                        "," + 0.5);
                            }//m
                        }//l
                    }//k
                }//j
            }//i
        }//try
        catch(IOException e){e.printStackTrace();}
        finally {
            fout.close();
        }
        //LUT = new Object[count][2];

    }

        public static void  update(String state,double value) throws IOException {
        BufferedReader R_u = null;
        FileReader r_u = null;
        try {
            File file;
            r_u = new FileReader("LUT_law.txt");
            R_u = new BufferedReader(r_u);
            String line = R_u.readLine();
            while (line != null) {
                String sp[] = line.split(",");
                if (sp[0] == state) {
                    CharArrayWriter tempStream = new CharArrayWriter();
                    line = line.replace(line, state + "," + Double.toString(value));
                    tempStream.write(line);

                    tempStream.writeTo(new FileWriter("LUT_law.txt"));

                    break;
                } else {
                    line = R_u.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            R_u.close();
        }
    }

    public static int greedy_action(){
        double value_tmp = 0;
        int action = 0;
        for (int i = 0; i < 4; i++) {
            String state_action = state+Integer.toString(i);
            for (int j = 0; j < LUT.length; j++) {
                if(state_action.equals(LUT[j][0])){
                    if(value_tmp < Double.valueOf((String)LUT[j][1]) ){
                        value_tmp = Double.valueOf((String)LUT[j][1]);
                        action = i+1;
                        break;
                    }
                }

            }
        }
        return action;

    }


    public void load_nn() throws IOException {
        BufferedReader reader = new BufferedReader(new
                FileReader(LUT_law));
        String line = reader.readLine();
        try {
            int st=0;
            while (line != null) {

                String splitLine[] = line.split(",");
                LUT[st][0] = splitLine[0];
                for (int i = 0; i < splitLine[0].length(); i++) {
                    in[st][i] = Character.getNumericValue(splitLine[0].charAt(i));
                }
                LUT[st][1] = splitLine[1];
                out[st][0] = Double.parseDouble(splitLine[1]);
                if(st<LUT.length-1){st++;}
                line = reader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            reader.close();
        }
    }//load

    public static double[][] transform_in(Object[][] lut){
        int l1 = lut.length;
        String tmp = lut[0][0].toString();
        int l2 = tmp.length();
        double[][] in= new double[l1][l2];
        for (int i = 0; i < l1; i++) {
            for (int j = 0; j < l2; j++) {
                String tmp1 = lut[i][0].toString();
                in[i][j] = Character.getNumericValue(tmp1.charAt(j));
            }
        }
        return in;
    }
    public static double[][] transform_out(Object[][] lut){
        int l1 = lut.length;
        double[][] out= new double[l1][1];
        for (int i = 0; i < l1; i++) {
            String tmp1 = lut[i][1].toString();
            out[i][0] = Double.parseDouble(tmp1);
        }
        return out;
    }



   public static int exploratory_action21() {

       int action=0;
       int action1=0;
       int action2=0;
       action1 = 1+(int)(Math.random()*(4));
       action2 = 1+(int)(Math.random()*(2));
       action = action1*10+action2;
       return action;
   }

    public static int quantize_energy_pro(double e1,double e2) {
        int e=1+(int)Math.random()*6;
        if(e1<e2 && e1<4){e=1;}
        if(e1<e2 && e1>3 && e1<7){e=2;}
        if(e1<e2 && e1>6){e=3;}
        if(e1>e2 && e1<4){e=4;}
        if(e1>e2 && e1>4 && e1<7){e=5;}
        if(e1>e2 && e1>6){e=6;}
        return e;
    }



    public static void main(String args[]) throws IOException {



        //ini_table23();
        //System.out.println(quantize_energy_pro(4,2));
        test t = new test();
        t.load_nn();
        System.out.println(t.in[1][1]+","+t.out[1][0]);
        /*t.in = transform_in(LUT);
        t.out = transform_out(LUT);
*/


    }


}
