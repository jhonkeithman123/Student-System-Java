import java.sql.*;

public class DB {
    private static final String URL = "jdbc:sqlite:studentinfo.db";
    private static Connection conn;

    static { init(); }

    public static synchronized Connection getConnection() throws SQLException {
        try { Class.forName("org.sqlite.JDBC"); } catch (ClassNotFoundException ignored) {}
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL);
            conn.setAutoCommit(true);
        }
        return conn;
    }

    public static void init() {
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS users(id TEXT PRIMARY KEY, password TEXT, role TEXT, name TEXT, email TEXT)");
            s.execute("CREATE TABLE IF NOT EXISTS students(student_id TEXT PRIMARY KEY, name TEXT, course TEXT, year TEXT, email TEXT, contact TEXT, FOREIGN KEY(student_id) REFERENCES users(id))");
            s.execute("CREATE TABLE IF NOT EXISTS enrollments(student_id TEXT, subject TEXT, PRIMARY KEY(student_id,subject))");
            s.execute("CREATE TABLE IF NOT EXISTS schedules(student_id TEXT, subject TEXT, day TEXT, time TEXT, room TEXT)");
            s.execute("CREATE TABLE IF NOT EXISTS grades(student_id TEXT, subject TEXT, grade REAL, term TEXT)");
            try ( // seed admin
                    PreparedStatement ps = c.prepareStatement("INSERT OR IGNORE INTO users(id,password,role,name,email) VALUES(?,?,?,?,?)")) {
                ps.setString(1, "admin");
                ps.setString(2, "Admin123!");
                ps.setString(3, "admin");
                ps.setString(4, "Administrator");
                ps.setString(5, "admin@system.local");
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
