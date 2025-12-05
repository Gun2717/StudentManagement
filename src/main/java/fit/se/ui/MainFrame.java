package fit.se.ui;

import fit.se.model.Student;
import fit.se.service.StudentService;
import fit.se.service.StudentService.StudentStatistics;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;

import fit.se.model.Student;
import fit.se.service.StudentService;
import fit.se.service.StudentService.StudentStatistics;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Main application frame - Swing GUI
 */
public class MainFrame extends JFrame {
    private StudentService service;

    // UI Components
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;
    private JLabel statusLabel;
    private JProgressBar progressBar;

    // Column names
    private static final String[] COLUMN_NAMES = {
            "Mã SV", "Họ tên", "Ngày sinh", "Giới tính",
            "Email", "Điện thoại", "Ngành học", "GPA", "Xếp loại"
    };

    public MainFrame(StudentService service) {
        this.service = service;
        initComponents();
        loadStudentData();
    }

    private void initComponents() {
        setTitle("Hệ Thống Quản Lý Sinh Viên");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        // Menu Bar
        createMenuBar();

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel - Search
        mainPanel.add(createSearchPanel(), BorderLayout.NORTH);

        // Center Panel - Table
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);

        // Bottom Panel - Status
        mainPanel.add(createStatusPanel(), BorderLayout.SOUTH);

        // Right Panel - Actions
        mainPanel.add(createActionPanel(), BorderLayout.EAST);

        add(mainPanel);

        // Window closing handler
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                service.shutdown();
            }
        });
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem refreshItem = new JMenuItem("Làm mới", KeyEvent.VK_R);
        refreshItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        refreshItem.addActionListener(e -> loadStudentData());

        JMenuItem statsItem = new JMenuItem("Thống kê", KeyEvent.VK_S);
        statsItem.addActionListener(e -> showStatistics());

        JMenuItem exitItem = new JMenuItem("Thoát", KeyEvent.VK_X);
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(refreshItem);
        fileMenu.add(statsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm"));

        panel.add(new JLabel("Loại:"));
        searchTypeCombo = new JComboBox<>(new String[]{"Tên", "Ngành học", "GPA >= "});
        panel.add(searchTypeCombo);

        searchField = new JTextField(20);
        panel.add(searchField);

        JButton searchBtn = new JButton("Tìm kiếm");
        searchBtn.addActionListener(e -> performSearch());
        panel.add(searchBtn);

        JButton clearBtn = new JButton("Xóa bộ lọc");
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            loadStudentData();
        });
        panel.add(clearBtn);

        // Enter key support
        searchField.addActionListener(e -> performSearch());

        return panel;
    }

    private JScrollPane createTablePanel() {
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setRowHeight(25);
        studentTable.getTableHeader().setReorderingAllowed(false);

        // Column widths
        int[] columnWidths = {80, 150, 100, 80, 180, 100, 150, 60, 100};
        for (int i = 0; i < columnWidths.length; i++) {
            studentTable.getColumnModel().getColumn(i)
                    .setPreferredWidth(columnWidths[i]);
        }

        // Double click to edit
        studentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editStudent();
                }
            }
        });

        return new JScrollPane(studentTable);
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        Dimension btnSize = new Dimension(120, 30);

        JButton addBtn = createButton("Thêm mới", "icons/add.png", btnSize);
        addBtn.addActionListener(e -> addStudent());

        JButton editBtn = createButton("Sửa", "icons/edit.png", btnSize);
        editBtn.addActionListener(e -> editStudent());

        JButton deleteBtn = createButton("Xóa", "icons/delete.png", btnSize);
        deleteBtn.addActionListener(e -> deleteStudent());

        JButton viewBtn = createButton("Xem chi tiết", "icons/view.png", btnSize);
        viewBtn.addActionListener(e -> viewStudent());

        panel.add(addBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(editBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(deleteBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(viewBtn);

        return panel;
    }

    private JButton createButton(String text, String iconPath, Dimension size) {
        JButton button = new JButton(text);
        button.setMaximumSize(size);
        button.setMinimumSize(size);
        button.setPreferredSize(size);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        statusLabel = new JLabel("Sẵn sàng");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);

        panel.add(statusLabel, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.EAST);

        return panel;
    }

    // Action methods
    private void addStudent() {
        StudentDialog dialog = new StudentDialog(this, service, null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadStudentData();
        }
    }

    private void editStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn sinh viên cần sửa!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String studentId = (String) tableModel.getValueAt(selectedRow, 0);
        try {
            Student student = service.findStudentById(studentId);
            StudentDialog dialog = new StudentDialog(this, service, student);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                loadStudentData();
            }
        } catch (Exception e) {
            showError("Lỗi khi tải thông tin sinh viên", e);
        }
    }

    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn sinh viên cần xóa!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String studentId = (String) tableModel.getValueAt(selectedRow, 0);
        String studentName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa sinh viên:\n" + studentId + " - " + studentName + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.deleteStudent(studentId);
                loadStudentData();
                showStatus("Đã xóa sinh viên: " + studentName);
            } catch (Exception e) {
                showError("Lỗi khi xóa sinh viên", e);
            }
        }
    }

    private void viewStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn sinh viên!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String studentId = (String) tableModel.getValueAt(selectedRow, 0);
        try {
            Student student = service.findStudentById(studentId);
            showStudentDetails(student);
        } catch (Exception e) {
            showError("Lỗi khi tải thông tin sinh viên", e);
        }
    }

    private void performSearch() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            loadStudentData();
            return;
        }

        String searchType = (String) searchTypeCombo.getSelectedItem();

        // Use async search with SwingWorker
        SwingWorker<List<Student>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Student> doInBackground() throws Exception {
                showProgress("Đang tìm kiếm...");

                if ("Tên".equals(searchType)) {
                    return service.searchByName(searchText);
                } else if ("Ngành học".equals(searchType)) {
                    return service.searchByMajor(searchText);
                } else { // GPA
                    double minGpa = Double.parseDouble(searchText);
                    return service.getTopStudents(minGpa);
                }
            }

            @Override
            protected void done() {
                try {
                    List<Student> results = get();
                    updateTable(results);
                    showStatus("Tìm thấy " + results.size() + " kết quả");
                } catch (Exception e) {
                    showError("Lỗi khi tìm kiếm", e);
                }
                hideProgress();
            }
        };

        worker.execute();
    }

    private void loadStudentData() {
        SwingWorker<List<Student>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Student> doInBackground() throws Exception {
                showProgress("Đang tải dữ liệu...");
                return service.getAllStudents();
            }

            @Override
            protected void done() {
                try {
                    List<Student> students = get();
                    updateTable(students);
                    showStatus("Đã tải " + students.size() + " sinh viên");
                } catch (Exception e) {
                    showError("Lỗi khi tải dữ liệu", e);
                }
                hideProgress();
            }
        };

        worker.execute();
    }

    private void updateTable(List<Student> students) {
        tableModel.setRowCount(0);
        for (Student s : students) {
            tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getFullName(),
                    s.getFormattedDateOfBirth(),
                    s.getGender().getDisplayName(),
                    s.getEmail(),
                    s.getPhone(),
                    s.getMajor(),
                    String.format("%.2f", s.getGpa()),
                    s.getGradeClassification()
            });
        }
    }

    private void showStatistics() {
        try {
            StudentStatistics stats = service.calculateStatistics();
            JOptionPane.showMessageDialog(this,
                    stats.toString(),
                    "Thống kê sinh viên",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            showError("Lỗi khi tính toán thống kê", e);
        }
    }

    private void showStudentDetails(Student s) {
        String details = String.format(
                "Mã SV: %s\nHọ tên: %s\nNgày sinh: %s (Tuổi: %d)\n" +
                        "Giới tính: %s\nEmail: %s\nĐiện thoại: %s\n" +
                        "Địa chỉ: %s\nNgành học: %s\nGPA: %.2f\nXếp loại: %s",
                s.getId(), s.getFullName(), s.getFormattedDateOfBirth(),
                s.getAge(), s.getGender().getDisplayName(), s.getEmail(),
                s.getPhone(), s.getAddress(), s.getMajor(),
                s.getGpa(), s.getGradeClassification()
        );

        JOptionPane.showMessageDialog(this, details,
                "Chi tiết sinh viên", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                "Hệ Thống Quản Lý Sinh Viên\n" +
                        "Version 1.0\n\n" +
                        "Công nghệ:\n" +
                        "- Java Swing GUI\n" +
                        "- File & Database (MariaDB/MySQL)\n" +
                        "- Multi-threading\n\n" +
                        "© 2024",
                "About",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Utility methods
    private void showStatus(String message) {
        statusLabel.setText(message);
    }

    private void showProgress(String message) {
        statusLabel.setText(message);
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
    }

    private void hideProgress() {
        progressBar.setVisible(false);
        progressBar.setIndeterminate(false);
    }

    private void showError(String message, Exception e) {
        JOptionPane.showMessageDialog(this,
                message + ":\n" + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
