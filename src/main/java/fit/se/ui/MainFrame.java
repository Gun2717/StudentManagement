package fit.se.ui;

import fit.se.model.Student;
import fit.se.service.StudentService;
import fit.se.service.StudentService.StudentStatistics;
import fit.se.util.ExcelUtils;
import fit.se.util.PDFReportGenerator;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import static fit.se.util.ExcelUtils.importFromExcel;

/**
 * Modern Material Design Main Frame
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

    // Modern Color Scheme
    private static final Color PRIMARY_COLOR = new Color(33, 150, 243);      // Blue
    private static final Color PRIMARY_DARK = new Color(25, 118, 210);
    private static final Color ACCENT_COLOR = new Color(255, 87, 34);        // Orange
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);       // Green
    private static final Color BACKGROUND = new Color(250, 250, 250);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(117, 117, 117);
    private static final Color DIVIDER = new Color(224, 224, 224);

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
        setTitle("🎓 Hệ Thống Quản Lý Sinh Viên");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);

        // Set modern look
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Menu Bar
        createMenuBar();

        // Main Panel with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, BACKGROUND, 0, h, new Color(240, 245, 250));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Center Panel with table
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);

        // Bottom Panel
        mainPanel.add(createStatusPanel(), BorderLayout.SOUTH);

        // Right Panel - Actions
        mainPanel.add(createActionPanel(), BorderLayout.EAST);

        add(mainPanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                service.shutdown();
            }
        });
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(CARD_BACKGROUND);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, DIVIDER));

        JMenu fileMenu = createStyledMenu("📁 File");

        JMenuItem refreshItem = createStyledMenuItem("🔄 Làm mới", KeyEvent.VK_F5);
        refreshItem.addActionListener(e -> loadStudentData());

        JMenuItem statsItem = createStyledMenuItem("📊 Thống kê", 0);
        statsItem.addActionListener(e -> showStatistics());

        // NEW: Export submenu
        JMenu exportMenu = new JMenu("📤 Xuất dữ liệu");
        exportMenu.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JMenuItem exportExcelItem = createStyledMenuItem("📊 Xuất Excel", 0);
        exportExcelItem.addActionListener(e -> exportToExcel());

        JMenuItem exportPdfItem = createStyledMenuItem("📄 Xuất PDF", 0);
        exportPdfItem.addActionListener(e -> exportToPdf());

        JMenuItem exportStatsItem = createStyledMenuItem("📈 Báo cáo thống kê", 0);
        exportStatsItem.addActionListener(e -> exportStatisticsPdf());

        exportMenu.add(exportExcelItem);
        exportMenu.add(exportPdfItem);
        exportMenu.add(exportStatsItem);

        // NEW: Import item
        JMenuItem importItem = createStyledMenuItem("📥 Nhập từ Excel", 0);
        importItem.addActionListener(e -> importFromExcel());

        JMenuItem exitItem = createStyledMenuItem("🚪 Thoát", KeyEvent.VK_Q);
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(refreshItem);
        fileMenu.add(statsItem);
        fileMenu.addSeparator();
        fileMenu.add(exportMenu);
        fileMenu.add(importItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        menuBar.add(Box.createHorizontalGlue());


        JMenu helpMenu = createStyledMenu("❓ Help");
        JMenuItem aboutItem = createStyledMenuItem("ℹ️ About", 0);
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private JMenu createStyledMenu(String text) {
        JMenu menu = new JMenu(text);
        menu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        menu.setForeground(TEXT_PRIMARY);
        return menu;
    }

    private JMenuItem createStyledMenuItem(String text, int mnemonic) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        if (mnemonic != 0) item.setMnemonic(mnemonic);
        return item;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 15));
        headerPanel.setOpaque(false);

        // Title Section
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("🎓 QUẢN LÝ SINH VIÊN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        titlePanel.add(titleLabel);

        headerPanel.add(titlePanel, BorderLayout.NORTH);
        headerPanel.add(createSearchPanel(), BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setOpaque(true);
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(createModernBorder("🔍 Tìm kiếm nhanh"));

        JLabel typeLabel = new JLabel("Tìm theo:");
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        typeLabel.setForeground(TEXT_SECONDARY);
        panel.add(typeLabel);

        searchTypeCombo = new JComboBox<>(new String[]{"Tên", "Ngành học", "GPA >="});
        searchTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchTypeCombo.setPreferredSize(new Dimension(120, 35));
        styleComboBox(searchTypeCombo);
        panel.add(searchTypeCombo);

        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(300, 35));
        styleTextField(searchField, "Nhập từ khóa tìm kiếm...");
        searchField.addActionListener(e -> performSearch());
        panel.add(searchField);

        JButton searchBtn = createModernButton("🔍 Tìm", PRIMARY_COLOR, Color.WHITE);
        searchBtn.setPreferredSize(new Dimension(100, 35));
        searchBtn.addActionListener(e -> performSearch());
        panel.add(searchBtn);

        JButton clearBtn = createModernButton("✖️ Xóa", TEXT_SECONDARY, Color.WHITE);
        clearBtn.setPreferredSize(new Dimension(90, 35));
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            loadStudentData();
        });
        panel.add(clearBtn);

        return panel;
    }

    private JScrollPane createTablePanel() {
        // Custom table model
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentTable.setRowHeight(40);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setShowGrid(false);
        studentTable.setIntercellSpacing(new Dimension(0, 0));

        // Modern table header
        JTableHeader header = studentTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createEmptyBorder());

        // Alternating row colors
        studentTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    c.setBackground(new Color(227, 242, 253));
                    c.setForeground(TEXT_PRIMARY);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                    c.setForeground(TEXT_PRIMARY);
                }

                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                ((JLabel) c).setFont(new Font("Segoe UI", Font.PLAIN, 13));

                // Color code for GPA column
                if (column == 7 && value != null) {
                    try {
                        double gpa = Double.parseDouble(value.toString());
                        if (gpa >= 3.6) ((JLabel) c).setForeground(new Color(46, 125, 50));
                        else if (gpa >= 3.2) ((JLabel) c).setForeground(new Color(67, 160, 71));
                        else if (gpa >= 2.5) ((JLabel) c).setForeground(new Color(251, 140, 0));
                        else ((JLabel) c).setForeground(new Color(211, 47, 47));
                    } catch (Exception ignored) {
                    }
                }

                // Classification column
                if (column == 8) {
                    ((JLabel) c).setFont(new Font("Segoe UI", Font.BOLD, 12));
                    if (value != null) {
                        String val = value.toString();
                        if (val.equals("Xuất sắc")) ((JLabel) c).setForeground(new Color(46, 125, 50));
                        else if (val.equals("Giỏi")) ((JLabel) c).setForeground(new Color(67, 160, 71));
                        else if (val.equals("Khá")) ((JLabel) c).setForeground(new Color(251, 140, 0));
                        else ((JLabel) c).setForeground(new Color(211, 47, 47));
                    }
                }

                return c;
            }
        });

        // Column widths
        int[] widths = {90, 160, 110, 90, 200, 110, 160, 70, 110};
        for (int i = 0; i < widths.length; i++) {
            studentTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
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

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(createModernBorder("📋 Danh sách sinh viên"));
        scrollPane.getViewport().setBackground(Color.WHITE);

        return scrollPane;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(true);
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(createModernBorder("⚡ Thao tác"));
        panel.setPreferredSize(new Dimension(180, 0));

        Dimension btnSize = new Dimension(160, 45);
        int spacing = 12;

        JButton addBtn = createActionButton("➕ Thêm mới", SUCCESS_COLOR, btnSize);
        addBtn.addActionListener(e -> addStudent());

        JButton editBtn = createActionButton("✏️ Sửa", PRIMARY_COLOR, btnSize);
        editBtn.addActionListener(e -> editStudent());

        JButton deleteBtn = createActionButton("🗑️ Xóa", ACCENT_COLOR, btnSize);
        deleteBtn.addActionListener(e -> deleteStudent());

        JButton viewBtn = createActionButton("👁️ Chi tiết", new Color(156, 39, 176), btnSize);
        viewBtn.addActionListener(e -> viewStudent());

        JButton statsBtn = createActionButton("📊 Thống kê", new Color(255, 152, 0), btnSize);
        statsBtn.addActionListener(e -> showStatistics());

        panel.add(Box.createVerticalStrut(15));
        panel.add(addBtn);
        panel.add(Box.createVerticalStrut(spacing));
        panel.add(editBtn);
        panel.add(Box.createVerticalStrut(spacing));
        panel.add(deleteBtn);
        panel.add(Box.createVerticalStrut(spacing));
        panel.add(viewBtn);
        panel.add(Box.createVerticalStrut(spacing));
        panel.add(statsBtn);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(true);
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, DIVIDER),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        statusLabel = new JLabel("✅ Sẵn sàng");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(TEXT_SECONDARY);

        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(200, 20));
        progressBar.setForeground(PRIMARY_COLOR);

        panel.add(statusLabel, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.EAST);

        return panel;
    }

    // Utility methods for styling
    private Border createModernBorder(String title) {
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(DIVIDER, 1, true),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                PRIMARY_DARK
        );
        return BorderFactory.createCompoundBorder(
                titledBorder,
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );
    }

    private JButton createModernButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private JButton createActionButton(String text, Color color, Dimension size) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setMaximumSize(size);
        button.setMinimumSize(size);
        button.setPreferredSize(size);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void styleTextField(JTextField field, String placeholder) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DIVIDER, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(DIVIDER, 1, true),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
        });
    }

    private void styleComboBox(JComboBox<?> combo) {
        combo.setBorder(BorderFactory.createLineBorder(DIVIDER, 1, true));
        combo.setBackground(Color.WHITE);
    }

    // Action methods (keep existing logic, just update UI feedback)
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
            showModernMessage("⚠️ Vui lòng chọn sinh viên cần sửa!", "warning");
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
            showModernMessage("⚠️ Vui lòng chọn sinh viên cần xóa!", "warning");
            return;
        }

        String studentId = (String) tableModel.getValueAt(selectedRow, 0);
        String studentName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa sinh viên:\n" + studentId + " - " + studentName + "?",
                "⚠️ Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.deleteStudent(studentId);
                loadStudentData();
                showStatus("✅ Đã xóa sinh viên: " + studentName);
            } catch (Exception e) {
                showError("Lỗi khi xóa sinh viên", e);
            }
        }
    }

    private void viewStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            showModernMessage("⚠️ Vui lòng chọn sinh viên!", "warning");
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

        SwingWorker<List<Student>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Student> doInBackground() throws Exception {
                showProgress("🔍 Đang tìm kiếm...");

                if ("Tên".equals(searchType)) {
                    return service.searchByName(searchText);
                } else if ("Ngành học".equals(searchType)) {
                    return service.searchByMajor(searchText);
                } else {
                    double minGpa = Double.parseDouble(searchText);
                    return service.getTopStudents(minGpa);
                }
            }

            @Override
            protected void done() {
                try {
                    List<Student> results = get();
                    updateTable(results);
                    showStatus("✅ Tìm thấy " + results.size() + " kết quả");
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
                showProgress("⏳ Đang tải dữ liệu...");
                return service.getAllStudents();
            }

            @Override
            protected void done() {
                try {
                    List<Student> students = get();
                    updateTable(students);
                    showStatus("✅ Đã tải " + students.size() + " sinh viên");
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
            String message = String.format(
                    "<html><body style='width: 350px; font-family: Segoe UI;'>" +
                            "<h2 style='color: #1976D2; margin-bottom: 15px;'>📊 Thống kê sinh viên</h2>" +
                            "<table style='width: 100%%; border-collapse: collapse;'>" +
                            "<tr><td style='padding: 8px; background: #E3F2FD;'><b>Tổng số sinh viên:</b></td><td style='padding: 8px;'>%d</td></tr>" +
                            "<tr><td style='padding: 8px;'><b>GPA trung bình:</b></td><td style='padding: 8px;'>%.2f</td></tr>" +
                            "<tr><td style='padding: 8px; background: #E3F2FD;'><b>GPA cao nhất:</b></td><td style='padding: 8px; color: #4CAF50;'><b>%.2f</b></td></tr>" +
                            "<tr><td style='padding: 8px;'><b>GPA thấp nhất:</b></td><td style='padding: 8px; color: #F44336;'>%.2f</td></tr>" +
                            "<tr><td style='padding: 8px; background: #E3F2FD;'><b>Sinh viên nam:</b></td><td style='padding: 8px;'>%d</td></tr>" +
                            "<tr><td style='padding: 8px;'><b>Sinh viên nữ:</b></td><td style='padding: 8px;'>%d</td></tr>" +
                            "</table></body></html>",
                    stats.getTotalStudents(), stats.getAverageGpa(),
                    stats.getMaxGpa(), stats.getMinGpa(),
                    stats.getMaleCount(), stats.getFemaleCount()
            );

            JOptionPane.showMessageDialog(this, message,
                    "Thống kê", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            showError("Lỗi khi tính toán thống kê", e);
        }
    }

    private void showStudentDetails(Student s) {
        String details = String.format(
                "<html><body style='width: 400px; font-family: Segoe UI;'>" +
                        "<h2 style='color: #1976D2; margin-bottom: 15px;'>👤 Chi tiết sinh viên</h2>" +
                        "<table style='width: 100%%;'>" +
                        "<tr><td style='padding: 5px;'><b>Mã SV:</b></td><td style='padding: 5px;'>%s</td></tr>" +
                        "<tr><td style='padding: 5px;'><b>Họ tên:</b></td><td style='padding: 5px;'>%s</td></tr>" +
                        "<tr><td style='padding: 5px;'><b>Ngày sinh:</b></td><td style='padding: 5px;'>%s (Tuổi: %d)</td></tr>" +
                        "<tr><td style='padding: 5px;'><b>Giới tính:</b></td><td style='padding: 5px;'>%s</td></tr>" +
                        "<tr><td style='padding: 5px;'><b>Email:</b></td><td style='padding: 5px;'>%s</td></tr>" +
                        "<tr><td style='padding: 5px;'><b>Điện thoại:</b></td><td style='padding: 5px;'>%s</td></tr>" +
                        "<tr><td style='padding: 5px;'><b>Địa chỉ:</b></td><td style='padding: 5px;'>%s</td></tr>" +
                        "<tr><td style='padding: 5px;'><b>Ngành học:</b></td><td style='padding: 5px;'>%s</td></tr>" +
                        "<tr><td style='padding: 5px;'><b>GPA:</b></td><td style='padding: 5px; color: #4CAF50;'><b>%.2f</b></td></tr>" +
                        "<tr><td style='padding: 5px;'><b>Xếp loại:</b></td><td style='padding: 5px;'><b>%s</b></td></tr>" +
                        "</table></body></html>",
                s.getId(), s.getFullName(), s.getFormattedDateOfBirth(),
                s.getAge(), s.getGender().getDisplayName(), s.getEmail(),
                s.getPhone(), s.getAddress(), s.getMajor(),
                s.getGpa(), s.getGradeClassification()
        );

        JOptionPane.showMessageDialog(this, details,
                "Chi tiết sinh viên", JOptionPane.INFORMATION_MESSAGE);
    }


    private void showAbout() {
        String about = "<html><body style='font-family: Arial; font-size: 12px;'>"
                + "<h2 style='color:#2e7d32;'>Student Management System</h2>"
                + "<p>This application is built using <b>Java Swing</b>.</p>"
                + "<p>Features include:</p>"
                + "<ul>"
                + "<li>Add, Update, Delete Students</li>"
                + "<li>Search Student Information</li>"
                + "<li>Modern UI with Material Design style</li>"
                + "</ul>"
                + "<p style='margin-top:10px;'>Developer: <b>Cao Văn Bảo</b></p>"
                + "<p>Version: 2.0.0</p>"
                + "</body></html>";

        JOptionPane.showMessageDialog(this, about, "About", JOptionPane.INFORMATION_MESSAGE);
    }


    private void showModernMessage(String message, String type) {
        int messageType = type.equals("warning") ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE;
        JOptionPane.showMessageDialog(this, message, "Thông báo", messageType);
    }

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
        String errorMsg = String.format(
                "<html><body style='width: 350px;'>" +
                        "<h3 style='color: #F44336;'>❌ %s</h3>" +
                        "<p style='color: #666;'>%s</p>" +
                        "</body></html>",
                message, e.getMessage()
        );
        JOptionPane.showMessageDialog(this, errorMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

    private void exportToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Xuất danh sách sinh viên");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Excel Files (*.xlsx)", "xlsx"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.endsWith(".xlsx")) {
                filePath += ".xlsx";
            }

            String finalFilePath = filePath;
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    showProgress("📤 Đang xuất Excel...");
                    List<Student> students = service.getAllStudents();
                    ExcelUtils.exportToExcel(students, finalFilePath);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        showStatus("✅ Đã xuất " + finalFilePath);
                        int choice = JOptionPane.showConfirmDialog(MainFrame.this,
                                "Xuất Excel thành công!\nBạn có muốn mở file?",
                                "Thành công", JOptionPane.YES_NO_OPTION);

                        if (choice == JOptionPane.YES_OPTION) {
                            Desktop.getDesktop().open(new java.io.File(finalFilePath));
                        }
                    } catch (Exception e) {
                        showError("Lỗi khi xuất Excel", e);
                    }
                    hideProgress();
                }
            };
            worker.execute();
        }
    }

    // NEW METHODS: Import from Excel
    private void importFromExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Nhập danh sách sinh viên");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Excel Files (*.xlsx)", "xlsx"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();

            int choice = JOptionPane.showConfirmDialog(this,
                    "Nhập dữ liệu từ Excel sẽ thêm các sinh viên mới.\n" +
                            "Sinh viên trùng mã sẽ bị bỏ qua.\nTiếp tục?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);

            if (choice != JOptionPane.YES_OPTION) return;

            SwingWorker<Integer, Void> worker = new SwingWorker<>() {
                @Override
                protected Integer doInBackground() throws Exception {
                    showProgress("📥 Đang nhập Excel...");
                    List<Student> students = ExcelUtils.importFromExcel(filePath);

                    int successCount = 0;
                    for (Student student : students) {
                        try {
                            if (service.addStudent(student)) {
                                successCount++;
                            }
                        } catch (Exception e) {
                            System.err.println("Skip student " + student.getId() + ": " + e.getMessage());
                        }
                    }
                    return successCount;
                }

                @Override
                protected void done() {
                    try {
                        int count = get();
                        loadStudentData();
                        showStatus("✅ Đã nhập thành công " + count + " sinh viên");
                        JOptionPane.showMessageDialog(MainFrame.this,
                                "Nhập thành công " + count + " sinh viên!",
                                "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e) {
                        showError("Lỗi khi nhập Excel", e);
                    }
                    hideProgress();
                }
            };
            worker.execute();
        }
    }

    // NEW METHODS: Export to PDF
    private void exportToPdf() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Xuất danh sách PDF");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "PDF Files (*.pdf)", "pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.endsWith(".pdf")) {
                filePath += ".pdf";
            }

            String finalFilePath = filePath;
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    showProgress("📄 Đang tạo PDF...");
                    List<Student> students = service.getAllStudents();
                    PDFReportGenerator.generateStudentListReport(students, finalFilePath);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        showStatus("✅ Đã xuất " + finalFilePath);
                        int choice = JOptionPane.showConfirmDialog(MainFrame.this,
                                "Xuất PDF thành công!\nBạn có muốn mở file?",
                                "Thành công", JOptionPane.YES_NO_OPTION);

                        if (choice == JOptionPane.YES_OPTION) {
                            Desktop.getDesktop().open(new java.io.File(finalFilePath));
                        }
                    } catch (Exception e) {
                        showError("Lỗi khi xuất PDF", e);
                    }
                    hideProgress();
                }
            };
            worker.execute();
        }
    }

    // NEW METHODS: Export Statistics PDF
    private void exportStatisticsPdf() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Xuất báo cáo thống kê");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "PDF Files (*.pdf)", "pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.endsWith(".pdf")) {
                filePath += ".pdf";
            }

            String finalFilePath = filePath;
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    showProgress("📊 Đang tạo báo cáo...");
                    StudentStatistics stats = service.calculateStatistics();
                    List<Student> students = service.getAllStudents();
                    PDFReportGenerator.generateStatisticsReport(stats, students, finalFilePath);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        showStatus("✅ Đã xuất báo cáo " + finalFilePath);
                        int choice = JOptionPane.showConfirmDialog(MainFrame.this,
                                "Xuất báo cáo thành công!\nBạn có muốn mở file?",
                                "Thành công", JOptionPane.YES_NO_OPTION);

                        if (choice == JOptionPane.YES_OPTION) {
                            Desktop.getDesktop().open(new java.io.File(finalFilePath));
                        }
                    } catch (Exception e) {
                        showError("Lỗi khi xuất báo cáo", e);
                    }
                    hideProgress();
                }
            };
            worker.execute();
        }
    }
}
