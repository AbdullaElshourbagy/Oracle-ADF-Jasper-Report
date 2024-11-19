package enoc.beans.enums;

public enum JasperReportFormat {
    PDF("pdf"),
    DOCX("docx"),
    XLSX("xlsx");
    private static final long serialVersionUID = 1L;

    private final String format;

    JasperReportFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }
}
