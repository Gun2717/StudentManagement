package fit.se.util;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import fit.se.model.Student;
import fit.se.service.StudentService.StudentStatistics;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * PDF Report Generator using OpenPDF
 */
public class PDFReportGenerator {

    private static final Font TITLE_FONT = new Font(Font.TIMES_ROMAN, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.TIMES_ROMAN, 12, Font.NORMAL);
    private static final Font SMALL_FONT = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);

    /**
     * Generate student list report
     */
    public static void generateStudentListReport(List<Student> students, String filePath) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, new FileOutputStream(filePath));

        document.open();

        // Title
        Paragraph title = new Paragraph("DANH SACH SINH VIEN", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        Paragraph timeP = new Paragraph("Ngay xuat: " + timestamp, SMALL_FONT);
        timeP.setAlignment(Element.ALIGN_RIGHT);
        timeP.setSpacingAfter(10);
        document.add(timeP);

        // Create table
        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        float[] columnWidths = {1f, 2f, 1.5f, 1f, 2f, 1.5f, 2f, 1f, 1.2f};
        table.setWidths(columnWidths);

        // Header
        addTableHeader(table, new String[]{
                "Ma SV", "Ho ten", "Ngay sinh", "Gioi tinh",
                "Email", "Dien thoai", "Nganh hoc", "GPA", "Xep loai"
        });

        // Data rows
        for (Student s : students) {
            addTableCell(table, s.getId());
            addTableCell(table, s.getFullName());
            addTableCell(table, s.getFormattedDateOfBirth());
            addTableCell(table, s.getGender().getDisplayName());
            addTableCell(table, s.getEmail());
            addTableCell(table, s.getPhone());
            addTableCell(table, s.getMajor());
            addTableCell(table, String.format("%.2f", s.getGpa()));
            addTableCell(table, s.getGradeClassification());
        }

        document.add(table);

        // Footer
        Paragraph footer = new Paragraph("Tong so sinh vien: " + students.size(), SMALL_FONT);
        footer.setAlignment(Element.ALIGN_LEFT);
        footer.setSpacingBefore(10);
        document.add(footer);

        document.close();
    }

    /**
     * Generate statistics report
     */
    public static void generateStatisticsReport(StudentStatistics stats, List<Student> students, String filePath)
            throws IOException, DocumentException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));

        document.open();

        // Title
        Paragraph title = new Paragraph("BAO CAO THONG KE SINH VIEN", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(30);
        document.add(title);

        // Timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        Paragraph timeP = new Paragraph("Ngay xuat bao cao: " + timestamp, NORMAL_FONT);
        timeP.setSpacingAfter(20);
        document.add(timeP);

        // Statistics section
        Paragraph statsHeader = new Paragraph("I. THONG KE TONG QUAT", HEADER_FONT);
        statsHeader.setSpacingAfter(10);
        document.add(statsHeader);

        PdfPTable statsTable = new PdfPTable(2);
        statsTable.setWidthPercentage(70);
        statsTable.setSpacingBefore(10);
        statsTable.setSpacingAfter(20);

        addStatsRow(statsTable, "Tong so sinh vien:", String.valueOf(stats.getTotalStudents()));
        addStatsRow(statsTable, "So luong sinh vien nam:", String.valueOf(stats.getMaleCount()));
        addStatsRow(statsTable, "So luong sinh vien nu:", String.valueOf(stats.getFemaleCount()));
        addStatsRow(statsTable, "GPA trung binh:", String.format("%.2f", stats.getAverageGpa()));
        addStatsRow(statsTable, "GPA cao nhat:", String.format("%.2f", stats.getMaxGpa()));
        addStatsRow(statsTable, "GPA thap nhat:", String.format("%.2f", stats.getMinGpa()));

        document.add(statsTable);

        // Classification distribution
        Paragraph classHeader = new Paragraph("II. PHAN LOAI HOC LUC", HEADER_FONT);
        classHeader.setSpacingAfter(10);
        document.add(classHeader);

        PdfPTable classTable = new PdfPTable(2);
        classTable.setWidthPercentage(70);
        classTable.setSpacingBefore(10);
        classTable.setSpacingAfter(20);

        long excellent = students.stream().filter(s -> s.getGpa() >= 3.6).count();
        long good = students.stream().filter(s -> s.getGpa() >= 3.2 && s.getGpa() < 3.6).count();
        long fair = students.stream().filter(s -> s.getGpa() >= 2.5 && s.getGpa() < 3.2).count();
        long average = students.stream().filter(s -> s.getGpa() >= 2.0 && s.getGpa() < 2.5).count();
        long poor = students.stream().filter(s -> s.getGpa() < 2.0).count();

        addStatsRow(classTable, "Xuat sac (>= 3.6):", excellent + " SV");
        addStatsRow(classTable, "Gioi (3.2 - 3.6):", good + " SV");
        addStatsRow(classTable, "Kha (2.5 - 3.2):", fair + " SV");
        addStatsRow(classTable, "Trung binh (2.0 - 2.5):", average + " SV");
        addStatsRow(classTable, "Yeu (< 2.0):", poor + " SV");

        document.add(classTable);

        // Top students
        Paragraph topHeader = new Paragraph("III. DANH SACH SINH VIEN XUAT SAC (GPA >= 3.6)", HEADER_FONT);
        topHeader.setSpacingAfter(10);
        document.add(topHeader);

        PdfPTable topTable = new PdfPTable(4);
        topTable.setWidthPercentage(100);
        topTable.setSpacingBefore(10);

        addTableHeader(topTable, new String[]{"Ma SV", "Ho ten", "Nganh hoc", "GPA"});

        students.stream()
                .filter(s -> s.getGpa() >= 3.6)
                .sorted((a, b) -> Double.compare(b.getGpa(), a.getGpa()))
                .forEach(s -> {
                    addTableCell(topTable, s.getId());
                    addTableCell(topTable, s.getFullName());
                    addTableCell(topTable, s.getMajor());
                    addTableCell(topTable, String.format("%.2f", s.getGpa()));
                });

        document.add(topTable);

        document.close();
    }

    /**
     * Generate individual student transcript
     */
    public static void generateTranscript(Student student, String filePath)
            throws IOException, DocumentException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));

        document.open();

        // Title
        Paragraph title = new Paragraph("BANG DIEM SINH VIEN", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(30);
        document.add(title);

        // Student info
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(80);
        infoTable.setSpacingBefore(20);
        infoTable.setSpacingAfter(30);

        addStatsRow(infoTable, "Ma sinh vien:", student.getId());
        addStatsRow(infoTable, "Ho va ten:", student.getFullName());
        addStatsRow(infoTable, "Ngay sinh:", student.getFormattedDateOfBirth());
        addStatsRow(infoTable, "Gioi tinh:", student.getGender().getDisplayName());
        addStatsRow(infoTable, "Email:", student.getEmail());
        addStatsRow(infoTable, "Dien thoai:", student.getPhone());
        addStatsRow(infoTable, "Dia chi:", student.getAddress());
        addStatsRow(infoTable, "Nganh hoc:", student.getMajor());
        addStatsRow(infoTable, "GPA:", String.format("%.2f", student.getGpa()));
        addStatsRow(infoTable, "Xep loai:", student.getGradeClassification());

        document.add(infoTable);

        // Signature section
        Paragraph signature = new Paragraph(
                "\n\n\nNgay xuat: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                        "\n\n                                                    Truong khoa\n\n\n\n" +
                        "                                                    (Ky va ghi ro ho ten)",
                NORMAL_FONT
        );
        signature.setAlignment(Element.ALIGN_LEFT);
        document.add(signature);

        document.close();
    }

    // Helper methods
    private static void addTableHeader(PdfPTable table, String[] headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, NORMAL_FONT));
            cell.setBackgroundColor(new Color(200, 200, 200));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    private static void addTableCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, SMALL_FONT));
        cell.setPadding(4);
        table.addCell(cell);
    }

    private static void addStatsRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, NORMAL_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }
}
