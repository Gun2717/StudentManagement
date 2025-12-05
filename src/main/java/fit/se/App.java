package fit.se;

import fit.se.api.ApiServer;
import fit.se.dao.*;
import fit.se.service.AuthService;
import fit.se.service.StudentService;
import fit.se.ui.LoginDialog;
import fit.se.ui.MainFrame;
import fit.se.util.DatabaseConnection;

import javax.swing.*;

/**
 * Complete Application with all features:
 * - File/Database storage
 * - Authentication & Authorization
 * - REST API Server
 * - Excel Import/Export
 * - PDF Reports
 * - Grade Management
 * - Web Interface
 */
public class App {

    private static ApiServer apiServer;

    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            try {
                // Step 1: Choose storage type
                String[] storageOptions = {"File (Serialization)", "Database (MariaDB/MySQL)"};
                int storageChoice = JOptionPane.showOptionDialog(
                        null,
                        "Chọn phương thức lưu trữ dữ liệu:",
                        "Cấu hình hệ thống",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        storageOptions,
                        storageOptions[1]
                );

                IStudentDAO studentDAO;
                IUserDAO userDAO = null;

                if (storageChoice == 1) {
                    // Database mode
                    if (testDatabaseConnection()) {
                        studentDAO = new StudentDatabaseDAO();
                        userDAO = new UserDatabaseDAO();
                        JOptionPane.showMessageDialog(null,
                                "✅ Hệ thống sử dụng Database (MariaDB/MySQL)",
                                "Thông báo",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "⚠️ Không thể kết nối Database!\nChuyển sang chế độ File.",
                                "Cảnh báo",
                                JOptionPane.WARNING_MESSAGE);
                        studentDAO = new StudentFileDAO();
                    }
                } else {
                    // File mode (default)
                    studentDAO = new StudentFileDAO();
                    JOptionPane.showMessageDialog(null,
                            "✅ Hệ thống sử dụng File (Serialization)",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                }

                // Step 2: Authentication (if database available)
                AuthService authService = null;
                if (userDAO != null) {
                    String[] authOptions = {"Đăng nhập", "Bỏ qua (Demo mode)"};
                    int authChoice = JOptionPane.showOptionDialog(
                            null,
                            "Bạn có muốn đăng nhập vào hệ thống?",
                            "Xác thực",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            authOptions,
                            authOptions[0]
                    );

                    if (authChoice == 0) {
                        authService = new AuthService(userDAO);
                        LoginDialog loginDialog = new LoginDialog(null, authService);
                        loginDialog.setVisible(true);

                        if (!loginDialog.isAuthenticated()) {
                            System.exit(0);
                        }
                    }
                }

                // Step 3: Create services
                StudentService studentService = new StudentService(studentDAO);

                // Step 4: Start REST API Server (optional)
                String[] apiOptions = {"Có", "Không"};
                int apiChoice = JOptionPane.showOptionDialog(
                        null,
                        "Bạn có muốn khởi động REST API Server?\n" +
                                "(Cho phép truy cập qua Web Interface và API calls)",
                        "REST API Server",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        apiOptions,
                        apiOptions[1]
                );

                if (apiChoice == 0) {
                    try {
                        apiServer = new ApiServer(studentService);
                        apiServer.start();

                        JOptionPane.showMessageDialog(null,
                                "🚀 REST API Server đã khởi động!\n\n" +
                                        "API Base URL: http://localhost:8080/api\n" +
                                        "Web Interface: Mở file index.html trong trình duyệt\n\n" +
                                        "Endpoints:\n" +
                                        "  GET    /api/students\n" +
                                        "  GET    /api/students/{id}\n" +
                                        "  POST   /api/students\n" +
                                        "  PUT    /api/students/{id}\n" +
                                        "  DELETE /api/students/{id}\n" +
                                        "  GET    /api/students/search\n" +
                                        "  GET    /api/students/statistics",
                                "API Server",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null,
                                "❌ Không thể khởi động API Server: " + e.getMessage(),
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }

                // Step 5: Show main application
                MainFrame mainFrame = new MainFrame(studentService);

                // Display current user info if logged in
                if (authService != null && authService.isLoggedIn()) {
                    mainFrame.setTitle("🎓 Student Management - " +
                            authService.getCurrentUser().getFullName() +
                            " (" + authService.getCurrentUser().getRole().getDisplayName() + ")");
                }

                mainFrame.setVisible(true);

                // Add shutdown hook for API server
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    if (apiServer != null) {
                        try {
                            apiServer.stop();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    studentService.shutdown();
                }));

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "❌ Lỗi khởi động ứng dụng: " + e.getMessage(),
                        "Lỗi nghiêm trọng",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                System.exit(1);
            }
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
