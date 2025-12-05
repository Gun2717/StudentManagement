package fit.se.ui;

import fit.se.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Modern Login Dialog
 */
public class LoginDialog extends JDialog {
    private AuthService authService;
    private boolean authenticated = false;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox rememberCheckBox;

    private static final Color PRIMARY = new Color(33, 150, 243);
    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);

    public LoginDialog(Frame parent, AuthService authService) {
        super(parent, "Đăng nhập hệ thống", true);
        this.authService = authService;
        initComponents();
    }

    private void initComponents() {
        setSize(450, 500);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());

        // Main panel with gradient
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, Color.WHITE,
                        0, getHeight(), BACKGROUND);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        // Logo/Title
        JLabel titleLabel = new JLabel("🎓 Student Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.insets = new Insets(0, 0, 30, 0);
        mainPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Đăng nhập để tiếp tục");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_PRIMARY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.insets = new Insets(0, 0, 30, 0);
        mainPanel.add(subtitleLabel, gbc);

        // Username panel
        gbc.insets = new Insets(8, 0, 8, 0);
        mainPanel.add(createFieldPanel("👤 Tên đăng nhập", usernameField = createTextField()), gbc);

        // Password panel
        mainPanel.add(createFieldPanel("🔒 Mật khẩu", passwordField = createPasswordField()), gbc);

        // Remember me
        rememberCheckBox = new JCheckBox("Ghi nhớ đăng nhập");
        rememberCheckBox.setOpaque(false);
        rememberCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.insets = new Insets(5, 0, 15, 0);
        mainPanel.add(rememberCheckBox, gbc);

        // Login button
        JButton loginBtn = createStyledButton("Đăng nhập", PRIMARY);
        loginBtn.addActionListener(e -> performLogin());
        gbc.insets = new Insets(10, 0, 10, 0);
        mainPanel.add(loginBtn, gbc);

        // Links panel
        JPanel linksPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        linksPanel.setOpaque(false);

        JLabel forgotLabel = createLinkLabel("Quên mật khẩu?");
        forgotLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(LoginDialog.this,
                        "Vui lòng liên hệ quản trị viên để khôi phục mật khẩu.",
                        "Quên mật khẩu", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        linksPanel.add(forgotLabel);

        linksPanel.add(new JLabel("|"));

        JLabel registerLabel = createLinkLabel("Đăng ký");
        registerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showRegisterDialog();
            }
        });
        linksPanel.add(registerLabel);

        gbc.insets = new Insets(15, 0, 0, 0);
        mainPanel.add(linksPanel, gbc);

        // Footer
        JLabel footerLabel = new JLabel("Version 2.0 | © 2024 Student Management System");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footerLabel.setForeground(Color.GRAY);
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.insets = new Insets(30, 0, 0, 0);
        mainPanel.add(footerLabel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Enter to login
        getRootPane().setDefaultButton(loginBtn);

        // ESC to close
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        usernameField.requestFocusInWindow();
    }

    private JPanel createFieldPanel(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelComp.setForeground(TEXT_PRIMARY);
        panel.add(labelComp, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(300, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY, 2, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });

        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(300, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY, 2, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });

        return field;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setPreferredSize(new Dimension(300, 45));
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

    private JLabel createLinkLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(PRIMARY);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setForeground(PRIMARY.darker());
                label.setText("<html><u>" + text + "</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setForeground(PRIMARY);
                label.setText(text);
            }
        });

        return label;
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        try {
            if (authService.login(username, password)) {
                authenticated = true;
                JOptionPane.showMessageDialog(this,
                        "Đăng nhập thành công!\nChào mừng " + authService.getCurrentUser().getFullName(),
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                showError("Tên đăng nhập hoặc mật khẩu không đúng!");
                passwordField.setText("");
                passwordField.requestFocus();
            }
        } catch (IllegalStateException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Lỗi hệ thống: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showRegisterDialog() {
        JOptionPane.showMessageDialog(this,
                "Tính năng đăng ký đang được phát triển.\nVui lòng liên hệ quản trị viên.",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
