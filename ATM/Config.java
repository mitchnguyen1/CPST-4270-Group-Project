
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
public class Config {
        private static final Properties props = new Properties();

        // Static block: runs ONCE when the class is loaded
        static {
            // Load db.properties using classloader
            try (InputStream is = DatabaseConnection.class.getClassLoader()
                    .getResourceAsStream("db.properties")) {

                if (is == null) {
                    throw new RuntimeException("db.properties not found in classpath");
                }

                props.load(is);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load db.properties", e);
            }
        }

        public static String get(String key) {
            return props.getProperty(key);
        }
    }

