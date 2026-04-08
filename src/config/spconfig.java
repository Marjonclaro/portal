package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;

public class spconfig {

    // SINGLE CONNECTION METHOD
    public static Connection connectDB() {
        try {
            Class.forName("org.sqlite.JDBC"); 
            // Database file name: students.db
            return DriverManager.getConnection("jdbc:sqlite:students.db");
        } catch (Exception e) {
            System.out.println("Database Connection Error: " + e);
            return null;
        }
    }

    /**
     * Requirement #1: Authentication
     * Returns "Admin" or "Student" based on credentials
     */
    public String authenticate(String user, String pass) {
        String adminSql = "SELECT * FROM admin WHERE username = ? AND password = ?";
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(adminSql)) {
            pstmt.setString(1, user);
            pstmt.setString(2, pass);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return "Admin";
            }
        } catch (SQLException e) {
            System.out.println("Admin Login Error: " + e.getMessage());
        }

        String userSql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(userSql)) {
            pstmt.setString(1, user);
            pstmt.setString(2, pass);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return "Student";
            }
        } catch (SQLException e) {
            System.out.println("Student Login Error: " + e.getMessage());
        }
        return "Failed"; 
    }

    // --- REQUIREMENT #2: SHARED CRUD METHODS ---

    public void displayData(String sql, javax.swing.JTable table) {
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            table.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException e) {
            System.out.println("Error displaying data: " + e.getMessage());
        }
    }

    public void addRecord(String sql, Object... values) {
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Record Added Successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding record: " + e.getMessage());
        }
    }

    // --- REQUIREMENT #3: ADMIN MASTERLIST (Creation of Main Entity) ---
    
    public void addSubjectToMasterlist(String code, String title, int units) {
        String sql = "INSERT INTO subjects (code, title, units) VALUES (?, ?, ?)";
        try (Connection conn = connectDB(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            pstmt.setString(2, title);
            pstmt.setInt(3, units);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Subject successfully added to Masterlist!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Masterlist Error: " + e.getMessage());
        }
    }

    // --- REQUIREMENT #4: USER TRANSACTION (Creation of Transaction) ---
    
    public void createEnrollmentTransaction(String studentName, String subjectTitle) {
        // Uses CURRENT_TIMESTAMP to automatically record the time of the transaction
        String sql = "INSERT INTO enrollments (student_name, subject_title, date_enrolled) VALUES (?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = connectDB(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentName);
            pstmt.setString(2, subjectTitle);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Transaction Recorded: Enrollment Successful!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Transaction Failed: " + e.getMessage());
        }
    }

    // Method to fetch transactions for a specific user
    public void viewUserTransactions(DefaultTableModel model, String studentName) {
        model.setRowCount(0);
        String sql = "SELECT subject_title, date_enrolled FROM enrollments WHERE student_name = ?";
        try (Connection conn = connectDB(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentName);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Vector row = new Vector();
                row.add(rs.getString("subject_title"));
                row.add(rs.getString("date_enrolled"));
                model.addRow(row);
            }
        } catch (SQLException e) {
            System.err.println("Transaction Error: " + e.getMessage());
        }
        }
    public java.util.List<String> getAvailableSubjectTitles() {
    java.util.List<String> subjects = new java.util.ArrayList<>();
    // This query targets the 'subjects' table found in your DB 
    String sql = "SELECT title FROM subjects";
    
    try (java.sql.Connection conn = this.connect(); 
         java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
         java.sql.ResultSet rs = pstmt.executeQuery()) {
        
        while (rs.next()) {
            subjects.add(rs.getString("title"));
        }
    } catch (java.sql.SQLException e) {
        System.out.println("Database Error: " + e.getMessage());
    }
    return subjects;
}

    private Connection connect() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}