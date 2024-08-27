import javax.swing.*;
import java.awt.*;

public class Test extends JFrame {

    private static void printInfo(String s){
        System.out.println(s);
    }
/*
    private int indexPosition(int x, int y) {
        int rows = primaryRules.nRows();
        int cols = primaryRules.nCols();
        int i;
        if (rows % 2 == 1 &&  x % 2 == 1 || rows % 2 == 0 &&  x % 2 == 0)
            i = ((rows - x) * cols) - y;
        else
            i = ((rows - x - 1) * cols) + y + 1;
        return i;
    }

    private int xPosition(int i) {
        int rows = primaryRules.nRows();
        int cols = primaryRules.nCols();
        return rows - (i - 1) / cols - 1;
    }

    private int yPosition(int i) {
        int rows = primaryRules.nRows();
        int cols = primaryRules.nCols();
        int x = xPosition(i);
        if (rows % 2 == 1 &&  x % 2 == 1 || rows % 2 == 0 &&  x % 2 == 0)
            return (i - 1) % cols;
        else
            return cols - ((i - 1) % cols) - 1;
    }
*/
    public static void main(String[] args) {
        int rows = 10;
        int cols = 10;
        for(int i = 1; i<=rows*cols; i+=9) {
            int x = rows - (i - 1) / cols - 1;
            if (rows % 2 == 1 &&  x % 2 == 1 || rows % 2 == 0 &&  x % 2 == 0)
                printInfo("i="+i+" y="+ ((i - 1) % cols) +" x="+x);
            else
                printInfo("i="+i+" y="+ (cols - ((i - 1) % cols) - 1) +" x="+x);
        }
    }

}
