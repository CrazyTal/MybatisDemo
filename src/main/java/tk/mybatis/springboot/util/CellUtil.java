package tk.mybatis.springboot.util;

/**
 * Created by ltao on 2017/8/14.
 */
public class CellUtil {
    public static String getCellVal(int cellCol) {
        String[] arr = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        return getCellVal(cellCol, arr);
    }

    private static String getCellVal(int cellCol, String[] arr) {
        if (cellCol / arr.length > 0) {
            return getCellVal(cellCol / arr.length - 1, arr) + arr[cellCol % arr.length];
        } else {
            return arr[cellCol % arr.length];
        }
    }

//    @Test
//    public void test1() {
//        System.out.println(getCellVal(999));//ALL
//    }
}
