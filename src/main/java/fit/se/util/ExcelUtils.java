package fit.se.util;

import fit.se.model.Student;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Utility class for Excel Import/Export operations
 */
public class ExcelUtils {

    /**
     * Export students to Excel file
     */
    public static void exportToExcel(List<Student> students, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Students");

        // Create header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        // Create data style
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        // Create date style
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.cloneStyleFrom(dataStyle);
        dateStyle.setDataFormat(workbook.createDataFormat().getFormat("dd/MM/yyyy"));

        // Create number style
        CellStyle numberStyle = workbook.createCellStyle();
        numberStyle.cloneStyleFrom(dataStyle);
        numberStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] columns = {"Mã SV", "Họ tên", "Ngày sinh", "Giới tính",
                "Email", "Điện thoại", "Địa chỉ", "Ngành học", "GPA", "Xếp loại"};

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // Fill data rows
        int rowNum = 1;
        for (Student student : students) {
            Row row = sheet.createRow(rowNum++);

            Cell cell0 = row.createCell(0);
            cell0.setCellValue(student.getId());
            cell0.setCellStyle(dataStyle);

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(student.getFullName());
            cell1.setCellStyle(dataStyle);

            Cell cell2 = row.createCell(2);
            Date date = Date.from(student.getDateOfBirth().atStartOfDay(ZoneId.systemDefault()).toInstant());
            cell2.setCellValue(date);
            cell2.setCellStyle(dateStyle);

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(student.getGender().getDisplayName());
            cell3.setCellStyle(dataStyle);

            Cell cell4 = row.createCell(4);
            cell4.setCellValue(student.getEmail());
            cell4.setCellStyle(dataStyle);

            Cell cell5 = row.createCell(5);
            cell5.setCellValue(student.getPhone());
            cell5.setCellStyle(dataStyle);

            Cell cell6 = row.createCell(6);
            cell6.setCellValue(student.getAddress());
            cell6.setCellStyle(dataStyle);

            Cell cell7 = row.createCell(7);
            cell7.setCellValue(student.getMajor());
            cell7.setCellStyle(dataStyle);

            Cell cell8 = row.createCell(8);
            cell8.setCellValue(student.getGpa());
            cell8.setCellStyle(numberStyle);

            Cell cell9 = row.createCell(9);
            cell9.setCellValue(student.getGradeClassification());
            cell9.setCellStyle(dataStyle);
        }

        // Auto-size columns
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }

    /**
     * Import students from Excel file
     */
    public static List<Student> importFromExcel(String filePath) throws IOException {
        List<Student> students = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            // Skip header row
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    Student student = new Student();

                    // ID
                    Cell cell0 = row.getCell(0);
                    if (cell0 != null) {
                        student.setId(formatter.formatCellValue(cell0).trim());
                    }

                    // Full Name
                    Cell cell1 = row.getCell(1);
                    if (cell1 != null) {
                        student.setFullName(formatter.formatCellValue(cell1).trim());
                    }

                    // Date of Birth
                    Cell cell2 = row.getCell(2);
                    if (cell2 != null && cell2.getCellType() == CellType.NUMERIC) {
                        if (DateUtil.isCellDateFormatted(cell2)) {
                            Date date = cell2.getDateCellValue();
                            LocalDate dob = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            student.setDateOfBirth(dob);
                        }
                    }

                    // Gender
                    Cell cell3 = row.getCell(3);
                    if (cell3 != null) {
                        student.setGender(Student.Gender.fromString(formatter.formatCellValue(cell3)));
                    }

                    // Email
                    Cell cell4 = row.getCell(4);
                    if (cell4 != null) {
                        student.setEmail(formatter.formatCellValue(cell4).trim());
                    }

                    // Phone
                    Cell cell5 = row.getCell(5);
                    if (cell5 != null) {
                        student.setPhone(formatter.formatCellValue(cell5).trim());
                    }

                    // Address
                    Cell cell6 = row.getCell(6);
                    if (cell6 != null) {
                        student.setAddress(formatter.formatCellValue(cell6).trim());
                    }

                    // Major
                    Cell cell7 = row.getCell(7);
                    if (cell7 != null) {
                        student.setMajor(formatter.formatCellValue(cell7).trim());
                    }

                    // GPA
                    Cell cell8 = row.getCell(8);
                    if (cell8 != null) {
                        if (cell8.getCellType() == CellType.NUMERIC) {
                            student.setGpa(cell8.getNumericCellValue());
                        } else {
                            String gpaStr = formatter.formatCellValue(cell8);
                            student.setGpa(Double.parseDouble(gpaStr));
                        }
                    }

                    // Validate basic fields
                    if (student.getId() != null && !student.getId().isEmpty() &&
                            student.getFullName() != null && !student.getFullName().isEmpty()) {
                        students.add(student);
                    }

                } catch (Exception e) {
                    System.err.println("Error parsing row " + i + ": " + e.getMessage());
                }
            }
        }

        return students;
    }

    /**
     * Create Excel template for importing
     */
    public static void createTemplate(String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Students");

        // Create header
        Row headerRow = sheet.createRow(0);
        String[] columns = {"Mã SV", "Họ tên", "Ngày sinh", "Giới tính",
                "Email", "Điện thoại", "Địa chỉ", "Ngành học", "GPA"};

        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }

        // Add sample row
        Row sampleRow = sheet.createRow(1);
        sampleRow.createCell(0).setCellValue("SV001");
        sampleRow.createCell(1).setCellValue("Nguyễn Văn A");
        sampleRow.createCell(2).setCellValue("15/05/2003");
        sampleRow.createCell(3).setCellValue("Nam");
        sampleRow.createCell(4).setCellValue("nva@email.com");
        sampleRow.createCell(5).setCellValue("0912345678");
        sampleRow.createCell(6).setCellValue("Hà Nội");
        sampleRow.createCell(7).setCellValue("Công nghệ thông tin");
        sampleRow.createCell(8).setCellValue(3.50);

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }
}
