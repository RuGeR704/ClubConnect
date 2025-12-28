package Storage;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.TimeZone;

public class ConPool {

    private static volatile DataSource datasource;

    public static Connection getConnection() throws SQLException {
        if (datasource == null) {
            synchronized (ConPool.class) {
                if (datasource == null) {
                    PoolProperties p = new PoolProperties();
                    p.setUrl("jdbc:mysql://avnadmin:AVNS__TRf8_zf0Qwhep82k5B@mysql-20c49e1a-gerardorusso7704-edea.g.aivencloud.com:10470/defaultdb?ssl-mode=REQUIRED");
                    p.setDriverClassName("com.mysql.cj.jdbc.Driver");
                    p.setUsername("avnadmin");
                    p.setPassword("AVNS__TRf8_zf0Qwhep82k5B");
                    p.setMaxActive(100);
                    p.setInitialSize(10);
                    p.setMinIdle(10);
                    p.setRemoveAbandonedTimeout(60);
                    p.setRemoveAbandoned(true);
                    p.setLogAbandoned(true); // facoltativo ma utile
                    p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                            + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");

                    datasource = new DataSource();
                    datasource.setPoolProperties(p);
                }
            }
        }
        return datasource.getConnection();
    }

}