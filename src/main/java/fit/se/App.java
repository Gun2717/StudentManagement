package fit.se;

import fit.se.dao.*;
import fit.se.service.StudentService;
import fit.se.ui.MainFrame;
import fit.se.util.DatabaseConnection;

import javax.swing.*;

/**
 * Main application class
 */
public class App {

    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Show storage selection dialog
        SwingUtilities.invokeLater(() -> {
            String[] options = {"File (Serialization)", "Database (MariaDB/MySQL)"};
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Chọn phương thức lưu trữ dữ liệu:",
                    "Cấu hình hệ thống",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            IStudentDAO dao;

            if (choice == 1) {
                // Database mode
                if (testDatabaseConnection()) {
                    dao = new StudentDatabaseDAO();
                    JOptionPane.showMessageDialog(null,
                            "Hệ thống sử dụng Database (MariaDB/MySQL)",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Không thể kết nối Database!\nChuyển sang chế độ File.",
                            "Cảnh báo",
                            JOptionPane.WARNING_MESSAGE);
                    dao = new StudentFileDAO();
                }
            } else {
                // File mode (default)
                dao = new StudentFileDAO();
                JOptionPane.showMessageDialog(null,
                        "Hệ thống sử dụng File (Serialization)",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            // Create service and show main frame
            StudentService service = new StudentService(dao);
            MainFrame frame = new MainFrame(service);
            frame.setVisible(true);
        });
    }

    /**
     * Test database connection
     */
    private static boolean testDatabaseConnection() {
        try {
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            return dbConn.testConnection();
        } catch (Exception e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }
}