package tk.mybatis.springboot;

import org.junit.Test;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by ltao on 2017/9/5.
 */
public class DateTest {
    @Test
    public void test1() {
        LocalDate date = LocalDate.now();
        LocalDate date1 = LocalDate.of(2017, 8, 3);
        System.out.println(date.toString());
        System.out.println(date1);
        System.out.println(DateTimeFormatter.ofPattern("yyyyMMdd").format(date));
        System.out.println(DateTimeFormatter.ofPattern("MM月dd日").format(date));
        System.out.println(date.getYear());
        System.out.println(date.getMonthValue());
        System.out.println(date.getDayOfMonth());

        System.out.println(new DecimalFormat("0.00").format(123.456));
        System.out.println(new DecimalFormat("0.00%").format(0.456789));
    }

    @Test
    public void test2() {
        String dateStr = "2017-08-03";
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        System.out.println(date);
        System.out.println(date.plusDays(-1));
//        File file = new File(this.getClass().getResource("/").getPath());
//        System.out.println(file.listFiles()[0]);
//        for (String a : file.list()) {
//            System.out.println(a);
//        }
    }

    @Test
    public void test3() {
        System.out.println(new DecimalFormat("0.00").format((double) (1449976 / 100) / 100));
        System.out.println((double) 1449976 / 10000);
        System.out.println(new DecimalFormat("0.00").format((double) 1449976 / 10000));
        System.out.println(new DecimalFormat("0.00").format(145.447));
    }
}
