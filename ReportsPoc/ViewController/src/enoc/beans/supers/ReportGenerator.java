package enoc.beans.supers;


import enoc.beans.common.ApplicationBean;
import enoc.beans.enums.JasperReportFormat;
import enoc.beans.factory.ResourceManager;
import enoc.beans.helpers.Constants;
import enoc.beans.utils.JSFUtils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.OutputStream;

import java.sql.Connection;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


import javax.annotation.PostConstruct;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import javax.servlet.http.HttpServletRequest;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.myfaces.trinidad.render.ExtendedRenderKitService;
import org.apache.myfaces.trinidad.util.Service;


public abstract class ReportGenerator {

    protected String reportName;
    protected String reportFolder;
    protected String format;
    private ApplicationBean application;
    private String jasperFile;
    private String jrxmlFile;
    private String path;
    private boolean isArabicMode;
    private String fullContextPath;
    private JasperPrint fillReport;
    protected String servletUrl;
    protected String contentType;
    protected String printableReportName;
    protected String currentDate;

    public ReportGenerator(String reportFolder, String reportName) {
        checkArabicMode();
        this.reportFolder = reportFolder;
        this.reportName = reportName;
        format = JasperReportFormat.PDF.getFormat();

    }

    private void checkArabicMode() {
        String lang = (String)JSFUtils.getSessionAttribute(Constants.SESSION_LANG);
        if (lang == null || lang.equalsIgnoreCase(Constants.ARABIC_FLAG)) {
            isArabicMode = true;
        }
    }

@PostConstruct
    public void constructReport() {
        path = String.format("%sreports%s%s%s", application.getREAL_PATH(), File.separator, reportFolder, File.separator);
        constructJasperFile();
    }

    public void constructJasperFile() {
        jasperFile = String.format("%s%s.jasper", path, reportName);
        jrxmlFile = String.format("%s%s.jrxml", path, reportName);
    }

    public HashMap getParamsMap() {
        HashMap paramsMap = new HashMap();
        generateParamsMap(paramsMap);
        return paramsMap;
    }

    public abstract void generateParamsMap(HashMap parametersMap);

    private Connection prepareReport() throws Exception {
        Connection conn = ResourceManager.getConnection(false);
        Locale locale = JSFUtils.getFacesContext().getViewRoot().getLocale();
        HttpServletRequest request = JSFUtils.getRequest();
        fullContextPath = createContextPath(request).toString();
        HashMap parameterMap = getParamsMap();
        parameterMap.put("REPORT_LOCALE", locale);
        parameterMap.put("CONTEXT_PATH", fullContextPath);
        fillReport = fillReport(conn, parameterMap);
        JSFUtils.setSessionAttribute("fillRep", fillReport);
        currentDate = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new Date());
        printableReportName = reportName + currentDate + "." + format;
        System.err.println(printableReportName);
        JSFUtils.setSessionAttribute("repName", printableReportName);
        return conn;
    }

    private void setReportFormat(ActionEvent e, String buttonId) {

        if ((buttonId == null)) {
            buttonId = e.getComponent().getId();
        }
        if (buttonId.contains(JasperReportFormat.DOCX.getFormat())) {
            format = JasperReportFormat.DOCX.getFormat();
            contentType = Constants.DOC_CONTNENT;
        } else if (buttonId.contains(JasperReportFormat.XLSX.getFormat())) {
            format = JasperReportFormat.XLSX.getFormat();
            contentType = Constants.XLSX_CONTNENT;
        } else {
            format = JasperReportFormat.PDF.getFormat();
            contentType = Constants.PDF_CONTNENT;
        }

    }

    public void printReport(ActionEvent e) {
        setReportFormat(e, null);
        printReport();
    }


    public void printReport() {
        Connection conn = null;
        try {
            conn = prepareReport();
            FacesContext context = JSFUtils.getFacesContext();
            servletUrl = fullContextPath + "/reportServlet?format=" + format;
            System.out.println("servletUrl  >>   " + servletUrl);
            ExtendedRenderKitService erks = Service.getService(context.getRenderKit(), ExtendedRenderKitService.class);
            erks.addScript(context, "doPrint(\"" + servletUrl + "\");");
        } catch (Exception e) {
            handleException(e);
        } finally {
            closeConnection(conn);
        }
    }


    public void printReportAsPopup(ActionEvent e) {
        setReportFormat(e, null);
        printReportAsPopup();
    }


    public void printReportAsPopup() {
        Connection conn = null;
        try {
            conn = prepareReport();
            servletUrl = fullContextPath + "/reportServlet?format=" + format;
            System.out.println("servletUrl   " + servletUrl);
        } catch (Exception e) {
            handleException(e);
        } finally {
            closeConnection(conn);
        }
    }


    public void downloadReport(OutputStream outputStream, String buttonId) {
        setReportFormat(null, buttonId);
        downloadReportAction(outputStream);
    }


    public void downloadReportAction(OutputStream outputStream) {
        Connection conn = null;
        try {
            conn = prepareReport();
            servletUrl = fullContextPath + "/reportServlet?format=" + format;
            System.out.println("servletUrl  >>   " + servletUrl);

            if (format.equals(JasperReportFormat.XLSX.getFormat())) {
                generateXlsxReport(outputStream);
            } else if (format.equals(JasperReportFormat.DOCX.getFormat())) {
                generateDocxReport(outputStream);
            } else {
                generatePdfReport(outputStream);
            }
        } catch (Exception e) {
            handleException(e);
        } finally {
            closeConnection(conn);
        }
    }

    private void generateXlsxReport(OutputStream outputStream) throws JRException {
        JRXlsxExporter xlsExporter = new JRXlsxExporter();
        xlsExporter.setParameter(JRExporterParameter.JASPER_PRINT, fillReport);
        xlsExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
        xlsExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        xlsExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
        xlsExporter.exportReport();

    }

    private void generateDocxReport(OutputStream outputStream) throws JRException {
        JRDocxExporter docxExporter = new JRDocxExporter();
        docxExporter.setParameter(JRExporterParameter.JASPER_PRINT, fillReport);
        docxExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
        docxExporter.exportReport();

    }

    private void generatePdfReport(OutputStream outputStream) throws JRException {
        JRPdfExporter pdfExporter = new JRPdfExporter();
        pdfExporter.setParameter(JRExporterParameter.JASPER_PRINT, fillReport);
        pdfExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
        pdfExporter.exportReport();

    }

    private JasperPrint fillReport(Connection conn, HashMap parameterMap) throws JRException, FileNotFoundException {
        JasperPrint fillReport = null;
        FileInputStream reportStream = new FileInputStream(jasperFile);
        fillReport = JasperFillManager.fillReport(reportStream, parameterMap, conn);
        return fillReport;
    }


    private static StringBuilder createContextPath(HttpServletRequest request) {
        String serverName = request.getServerName();
        String port = request.getServerPort() + "";
        String contextPath = request.getContextPath();
        String protocol = "http://";
        StringBuilder url = new StringBuilder(protocol).append(serverName).append(":").append(port).append(contextPath);
        return url;
    }

    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                handleException(e);
            }
        }
    }

    private void handleException(Exception e) {
        e.printStackTrace();

    }

    public void setApplication(ApplicationBean application) {
        this.application = application;
    }

    public ApplicationBean getApplication() {
        return application;
    }

    public void setReportFolder(String reportFolder) {
        this.reportFolder = reportFolder;
    }

    public String getReportFolder() {
        return reportFolder;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getServletUrl() {
        return servletUrl;
    }

    public String getContectType() {
        return contentType;
    }

    public String getPrintableReportName() {
        return printableReportName;
    }

 
}
