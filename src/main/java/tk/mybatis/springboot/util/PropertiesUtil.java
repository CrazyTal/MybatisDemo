package tk.mybatis.springboot.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * properties文件工具类 注意：使用此类，属性文件必须由Spring管理才可以直接获取到属性值
 *
 * @author JinChao
 */
public class PropertiesUtil {
    public static Map<String, String> propertiesMap = null;

    /**
     * 取得属性值
     *
     * @param perpertyName 属性名
     * @return
     */
    public static String getPropertyValue(String perpertyName) {
        if (propertiesMap == null) {
            return "属性文件未初始化成功！";
        } else {
            return propertiesMap.get(perpertyName);
        }
    }

    /**
     * 初始化
     */
    public static void init() {
        Properties properties = new Properties();
        propertiesMap = new HashMap<String, String>();
        try {
            InputStream is = PropertiesUtil.class.getResourceAsStream("/config.properties");
            properties.load(new InputStreamReader(is, "utf-8"));
            Enumeration propertyName = properties.propertyNames();
            while (propertyName.hasMoreElements()) {
                String key = (String) propertyName.nextElement();
                String value = properties.getProperty(key);
                propertiesMap.put(key, value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Int值
     *
     * @param perpertyName
     * @return
     */
    public static int getIntValue(String perpertyName) {
        if (propertiesMap == null) {
            return 0;
        } else {
            return Integer.parseInt(propertiesMap.get(perpertyName));
        }
    }

    /**
     * 获取静态参数
     *
     * @param key
     * @return
     */
    public static String getStrVal(String key) {
        if (propertiesMap == null) {
            init();
        }
        return propertiesMap.get(key);
    }

}
