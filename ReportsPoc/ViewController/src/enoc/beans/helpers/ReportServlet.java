package enoc.beans.helpers;


import java.io.IOException;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.fill.JRTemplatePrintText;


/**
 * Servlet implementation class testServlet
 */
public class ReportServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        performAction(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        performAction(request, response);
    }

    protected void performAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        JasperPrint fillReport = (JasperPrint)request.getSession().getAttribute("fillRep");
        String reportName = (String)request.getSession().getAttribute("repName");

        String format = request.getParameter("format");

        OutputStream out = response.getOutputStream();

        try {
            if (format != null && format.equals("docx")) {
                response.setHeader("Content-Disposition", "attachment; filename=\"" + reportName + "\"");
                response.setContentType("application/msword");
                JRDocxExporter exporter = new JRDocxExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, fillReport);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
                exporter.exportReport();
            } else if (format != null && format.equals("xlsx")) {
                response.setHeader("Content-Disposition", "attachment; filename=\"" + reportName + "\"");
                //                response.setHeader("Content-Disposition", "inline; filename=\"" + reportName + "\"");
                response.setContentType("application/vnd.ms-excel");
                //                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                //                response.setHeader("Content-Disposition", "inline; filename=\"report.xlsx\"");
                //                response.setHeader("Access-Control-Allow-Origin", "*");
                //                response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");

                JRXlsExporter exporter = new JRXlsExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, fillReport);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
                exporter.exportReport();
            } else {
                response.setHeader("Content-Disposition", "inline; filename=\"" + reportName + "\"");
                response.setContentType("application/pdf");
                JasperExportManager.exportReportToPdfStream(fillReport, out);
            }
        } catch (JRException e) {

            e.printStackTrace();
        } catch (NoClassDefFoundError err) {
            System.out.println("Err msg = " + err.getMessage());
            System.out.println("Err Cause = " + err.getCause());
        }
        out.flush();
    }


}
