package tk.mybatis.springboot;

import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by ltao on 2017/8/10.
 */
public class SetTest {

    @Test
    public void testSet() {
        Set set = new HashSet<>();
        set.add(1);
        set.add(2);
        set.add(3);
        System.out.println(set);
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

    @Test
    public void test2() {
        System.out.println(System.getProperty("user.dir"));
        System.out.println(this.getClass().getClassLoader().getResource("banner.txt"));
    }

    @Test
    public void test3() {
        int a = -32;
        System.out.println((byte)a);
    }
}
