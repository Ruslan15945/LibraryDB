import java.sql.*;
import java.util.Locale;

public class Session {

    private Connection connection = null;

    public Session (String user, String pwd, String host, int port, String sid) throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("JDBC not found");
        }
        String url = String.format("jdbc:oracle:thin:@%s:%d:%s", host, port, sid);
        Locale def_locale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
        this.connection = DriverManager.getConnection(url, user, pwd);
        Locale.setDefault(def_locale);
    }

    public void close(){
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Closed connection to database");
            } catch (SQLException e) {
                System.out.println("Cannot close connection to database");
            }
        }

    }

    public ResultSet executeQuery(String query) throws SQLException {
        Statement statement = connection.createStatement();

        return statement.executeQuery(query);

    }
    public int executeUpdate(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeUpdate(query);

    }
    public long executeRetQuery(String query) throws SQLException{
        PreparedStatement pstmt = connection.prepareStatement(query,
                new String[]{ "ID" });
        pstmt.executeUpdate();
        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                System.out.println(generatedKeys.getString(1));
                return generatedKeys.getLong(1);
            }
            else {
                throw new SQLException("Creating failed, no ID obtained.");
            }
        }
    }
}















