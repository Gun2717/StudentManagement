package fit.se.ui;

import fit.se.model.Student;
import fit.se.service.StudentService;

import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Dialog for adding/editing student information
 */
public class StudentDialog extends JDialog {
    private StudentService service;
    private Student student;
    private boolean confirmed = false;

    // Form fields
    private JTextField idField;
    private JTextField nameField;
    private JDateChooser dobChooser;
    private JComboBox<String> genderCombo;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextField majorField;
    private JSpinner gpaSpinner;

    public StudentDialog(Frame parent, StudentService service, Student student) {
        super(parent, student == null ? "Thêm sinh viên mới" : "Sửa thông tin sinh viên", true);
        this.service = service;
        this.student = student;

        initComponents();
        if (student != null) {
            loadStudentData();
        }
    }

    private void initComponents() {
        setSize(500, 600);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        // Form Panel
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // ESC to close
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Row 0: ID
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Mã sinh viên: *"), gbc);

        gbc.gridx = 1;
        idField = new JTextField(20);
        if (student != null) {
            idField.setEditable(false);
            idField.setBackground(Color.LIGHT_GRAY);
        }
        panel.add(idField, gbc);

        // Row 1: Full Name
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Họ và tên: *"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(20);
        panel.add(nameField, gbc);

        // Row 2: Date of Birth
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Ngày sinh: *"), gbc);

        gbc.gridx = 1;
        dobChooser = new JDateChooser();
        dobChooser.setDateFormatString("dd/MM/yyyy");
        dobChooser.setPreferredSize(new Dimension(200, 25));
        panel.add(dobChooser, gbc);

        // Row 3: Gender
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Giới tính: *"), gbc);

        gbc.gridx = 1;
        genderCombo = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        panel.add(genderCombo, gbc);

        // Row 4: Email
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        panel.add(emailField, gbc);

        // Row 5: Phone
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Điện thoại:"), gbc);

        gbc.gridx = 1;
        phoneField = new JTextField(20);
        panel.add(phoneField, gbc);

        // Row 6: Address
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Địa chỉ:"), gbc);

        gbc.gridx = 1;
        addressField = new JTextField(20);
        panel.add(addressField, gbc);

        // Row 7: Major
        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(new JLabel("Ngành học:"), gbc);

        gbc.gridx = 1;
        majorField = new JTextField(20);
        panel.add(majorField, gbc);

        // Row 8: GPA
        gbc.gridx = 0; gbc.gridy = 8;
        panel.add(new JLabel("GPA: *"), gbc);

        gbc.gridx = 1;
        SpinnerNumberModel gpaModel = new SpinnerNumberModel(0.0, 0.0, 4.0, 0.01);
        gpaSpinner = new JSpinner(gpaModel);
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(gpaSpinner, "0.00");
        gpaSpinner.setEditor(editor);
        panel.add(gpaSpinner, gbc);

        // Row 9: Note
        gbc.gridx = 0; gbc.gridy = 9;
        gbc.gridwidth = 2;
        JLabel noteLabel = new JLabel("* Trường bắt buộc");
        noteLabel.setFont(noteLabel.getFont().deriveFont(Font.ITALIC));
        noteLabel.setForeground(Color.RED);
        panel.add(noteLabel, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton saveBtn = new JButton("Lưu");
        saveBtn.setPreferredSize(new Dimension(100, 30));
        saveBtn.addActionListener(e -> saveStudent());

        JButton cancelBtn = new JButton("Hủy");
        cancelBtn.setPreferredSize(new Dimension(100, 30));
        cancelBtn.addActionListener(e -> dispose());

        panel.add(saveBtn);
        panel.add(cancelBtn);

        // Enter to save
        getRootPane().setDefaultButton(saveBtn);

        return panel;
    }

    private void loadStudentData() {
        idField.setText(student.getId());
        nameField.setText(student.getFullName());

        // Convert LocalDate to Date
        Date date = Date.from(student.getDateOfBirth()
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
        dobChooser.setDate(date);

        genderCombo.setSelectedItem(student.getGender().getDisplayName());
        emailField.setText(student.getEmail());
        phoneField.setText(student.getPhone());
        addressField.setText(student.getAddress());
        majorField.setText(student.getMajor());
        gpaSpinner.setValue(student.getGpa());
    }

    private void saveStudent() {
        try {
            // Validate required fields
            if (idField.getText().trim().isEmpty()) {
                showWarning("Vui lòng nhập mã sinh viên!");
                idField.requestFocus();
                return;
            }

            if (nameField.getText().trim().isEmpty()) {
                showWarning("Vui lòng nhập họ tên!");
                nameField.requestFocus();
                return;
            }

            if (dobChooser.getDate() == null) {
                showWarning("Vui lòng chọn ngày sinh!");
                dobChooser.requestFocus();
                return;
            }

            // Create or update student object
            Student studentData = (student == null) ? new Student() : student;

            studentData.setId(idField.getText().trim());
            studentData.setFullName(nameField.getText().trim());

            // Convert Date to LocalDate
            LocalDate dob = dobChooser.getDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            studentData.setDateOfBirth(dob);

            studentData.setGender(Student.Gender.fromString(
                    (String) genderCombo.getSelectedItem()));
            studentData.setEmail(emailField.getText().trim());
            studentData.setPhone(phoneField.getText().trim());
            studentData.setAddress(addressField.getText().trim());
            studentData.setMajor(majorField.getText().trim());
            studentData.setGpa((Double) gpaSpinner.getValue());

            // Save to database/file
            if (student == null) {
                service.addStudent(studentData);
                JOptionPane.showMessageDialog(this,
                        "Thêm sinh viên thành công!",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                service.updateStudent(studentData);
                JOptionPane.showMessageDialog(this,
                        "Cập nhật thông tin thành công!",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            confirmed = true;
            dispose();

        } catch (IllegalArgumentException e) {
            showWarning(e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Cảnh báo",
                JOptionPane.WARNING_MESSAGE);
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
