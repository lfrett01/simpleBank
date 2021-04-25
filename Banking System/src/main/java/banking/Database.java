package banking;

import java.sql.*;

public class Database {
    Connection conn = null;
    private final String url;

    public Database(String filename) {
        // SQLite connection string
        String url = "jdbc:sqlite:" + filename;
        this.url = url;
        Connection conn = null;
        try {
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }

    }

    private Connection connect() {
        // SQLite connection string
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void createNewTable() {
        // SQL statement for creating a new table
        String sql = """
                CREATE TABLE IF NOT EXISTS card (
                	id integer PRIMARY KEY,
                	number TEXT NOT NULL UNIQUE,
                	pin TEXT NOT NULL,
                	balance INTEGER DEFAULT 0
                );""";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insert(String number, String pin) {
        String sql = "INSERT INTO card(number, pin) VALUES(?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, number);
            pstmt.setString(2, pin);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Boolean checkAccountExists(String number, String pin){
        String sql = "SELECT * "
                + "FROM card WHERE number = ? AND pin =?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){

            // set the value
            pstmt.setString(1,number);
            pstmt.setString(2, pin);
            //
            ResultSet rs  = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public Integer getBalance(String number){
        String sql = "SELECT balance "
                + "FROM card WHERE number = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){

            // set the value
            pstmt.setString(1,number);
            ResultSet rs  = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                return rs.getInt("balance");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public void updateBalance(String number, int income){
        String sql = "UPDATE card SET balance = balance + ? "
                + "WHERE number = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){

            // set the value
            pstmt.setInt(1,income);
            pstmt.setString(2, number);
            // update
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void transfer(String fromAccount, String toAccount, int transferAmount) {
        String sql = "UPDATE card SET balance = balance + ? "
                + "WHERE number = ?";

        Connection con;
        PreparedStatement pstmt1, pstmt2;

        try {
            con = this.connect();
            if (con == null) {
                return;
            }
            // set auto-commit to false
            con.setAutoCommit(false);
            // take money from first account
            pstmt1 = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt1.setInt(1, transferAmount * -1);
            pstmt1.setString(2, fromAccount);
            pstmt2 = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt2.setInt(1, transferAmount);
            pstmt2.setString(2, toAccount);
            int firstRowAffected = pstmt1.executeUpdate();
            int secondRowAffected = pstmt2.executeUpdate();
            if (firstRowAffected != 1 && secondRowAffected != 1) {
                con.rollback();
            }
            con.commit();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public boolean checkAccountExists(String number) {
        String sql = "SELECT * "
                + "FROM card WHERE number = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){
            // set the value
            pstmt.setString(1, number);
            //execute
            ResultSet rs  = pstmt.executeQuery();
            // loop through the result set
            while (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        // if number not in table return false
        return false;
    }

    public void delete(String accountNumber) {
        String sql = "DELETE FROM card WHERE number = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // set the corresponding param
            pstmt.setString(1, accountNumber);
            // execute the delete statement
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
