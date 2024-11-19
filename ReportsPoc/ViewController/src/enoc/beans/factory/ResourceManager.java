package enoc.beans.factory;

import java.sql.*;

import java.util.Hashtable;
import java.util.ResourceBundle;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;

public class ResourceManager {

    private static ResourceBundle bundle;
    static {
        bundle = ResourceBundle.getBundle("enoc.beans.config.jdbc");
    }

    public static synchronized Connection getConnection(boolean autoCommit) {
        Connection conn = null;
        Hashtable ht = new Hashtable();
        ht.put(Context.INITIAL_CONTEXT_FACTORY,
               "weblogic.jndi.WLInitialContextFactory");
        ht.put(Context.PROVIDER_URL, bundle.getString("provider_url"));
        try {
            Context initContext = new InitialContext();
            DataSource ds = (DataSource)initContext.lookup(bundle.getString("datasource_jndi"));
            conn = ds.getConnection();
            conn.setAutoCommit(autoCommit);
            
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void close(Connection conn) {
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }


    public static void commit(Connection conn) {
        try {
            if (conn != null)
                conn.commit();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public static void close(Statement stmt) {
        try {
            if (stmt != null)
                stmt.close();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public static void close(PreparedStatement stmt) {
        try {
            if (stmt != null)
                stmt.close();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public static void close(ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
}
