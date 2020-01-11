package Spurs;




import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class excel_test {

    File Win_rate = new File("/Users/Lawrence Li/robocode/robots/Spurs/LUT_robot.data/Win_rate.txt");
    static int[] rate_array=new int[1000];

    public excel_test() throws IOException {
    }

    public void write_excel(){


        //Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("Win_rate");

        //This data needs to be written (Object[])
        Map<String, Object[]> data = new TreeMap<String, Object[]>();
        data.put("1", new Object[]{"Rounds", "Win", "Win_rate"});
        for (int i = 2; i < rate_array.length; i++) {
            data.put(Integer.toString(i), new Object[]{i-1, rate_array[i], rate1(rate_array,i+1)});
        }


        //Iterate over data and write to sheet
        Set<String> keyset = data.keySet();

        int rownum = 0;
        for (String key : keyset)
        {
            //create a row of excelsheet
            XSSFRow row = sheet.createRow(rownum++);

            //get object array of prerticuler key
            Object[] objArr = data.get(key);

            int cellnum = 0;

            for (Object obj : objArr)
            {
                XSSFCell cell = row.createCell(cellnum++);
                if (obj instanceof String)
                {
                    cell.setCellValue((String) obj);
                }
                else if (obj instanceof Integer)
                {
                    cell.setCellValue((Integer) obj);
                }
                else if (obj instanceof Double)
                {
                    cell.setCellValue((Double) obj);
                }

            }
        }
        try
        {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File("/Users/Lawrence Li/robocode/robots/Spurs/LUT_robot.data/Win.xlsx"));
            workbook.write(out);
            out.close();
            System.out.println("howtodoinjava_demo.xlsx written successfully on disk.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void write_excel(int[] r){


        //Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("Win_rate");

        //This data needs to be written (Object[])
        Map<String, Object[]> data = new TreeMap<String, Object[]>();
        data.put("1", new Object[]{"Rounds", "Win", "Win_rate"});
        for (int i = 0; i < r.length; i++) {
            data.put(Integer.toString(i+2), new Object[]{i+1, r[i], rate1(r,i+1)});
        }


        //Iterate over data and write to sheet
        Set<String> keyset = data.keySet();

        int rownum = 0;
        for (String key : keyset)
        {
            //create a row of excelsheet
            XSSFRow row = sheet.createRow(rownum++);

            //get object array of prerticuler key
            Object[] objArr = data.get(key);

            int cellnum = 0;

            for (Object obj : objArr)
            {
                XSSFCell cell = row.createCell(cellnum++);
                if (obj instanceof String)
                {
                    cell.setCellValue((String) obj);
                }
                else if (obj instanceof Integer)
                {
                    cell.setCellValue((Integer) obj);
                }
                else if (obj instanceof Double)
                {
                    cell.setCellValue((Double) obj);
                }

            }
        }
        try
        {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File("/Users/Lawrence Li/robocode/robots/Spurs/LUT_robot.data/Win.xlsx"));
            workbook.write(out);
            out.close();
            System.out.println("howtodoinjava_demo.xlsx written successfully on disk.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private String rate1(int[] wr,int k){
        int count = 0;
        double rate=0;
        String rates;
        for (int i = 0; i < k; i++) {
            if(wr[i]!=0){count++;}
        }
        rate = 100*count/k;
        rates= Double.toString(rate);
        return (rates+"%");
    }

    public void load() throws IOException {
        BufferedReader reader = new BufferedReader(new
                FileReader(Win_rate));
        String line = reader.readLine();
        int st=0;
        try {

            while (line != null) {

                rate_array[st] = Integer.parseInt(line.trim());
                System.out.println(rate_array[st]);
                st++;
                line = reader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            reader.close();
        }
    }

    public static void main(String[] args) throws IOException {
    excel_test n = new excel_test();
    n.load();
    int[] r = {0,1,1,0,0,0,1,1,1,0,1,0,0,1,0,1,1,1,1,0,0,0,1,1,1,1};
    n.write_excel(r);
}
}
