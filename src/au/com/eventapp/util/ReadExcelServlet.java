package au.com.eventapp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @see http://docs.oracle.com/javaee/6/tutorial/doc/glraq.html
 * @see http://stackoverflow.com/questions/4929646/how-to-get-an-excel-blank-cell-value-in-apache-poi
 * 
 * CREATE TABLE `database_name`.`speaker` (
  `id` INT NOT NULL,
  `first_name` VARCHAR(50) NULL,
  `last_name` VARCHAR(50) NULL,
  `position` VARCHAR(100) NULL,
  `company` VARCHAR(100) NULL,
  `description` VARCHAR(5000) NULL,
  PRIMARY KEY (`id`));
  
  CREATE TABLE `database_name`.`program` (
  `id` INT NOT NULL,
  `start_date` DATETIME NULL,
  `end_date` DATETIME NULL,
  `category1` VARCHAR(100) NULL,
  `category2` VARCHAR(200) NULL,
  `title` VARCHAR(1000) NULL,
  `description` VARCHAR(1000) NULL,
  `venue` VARCHAR(150) NULL,
  `speaker` VARCHAR(100) NULL,
  PRIMARY KEY (`id`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;
 * 
 */
@WebServlet(name = "ReadExcelServlet", urlPatterns = {"/upload"})
@MultipartConfig
public class ReadExcelServlet extends HttpServlet {

	private static final long serialVersionUID = -1175618610286400116L;
	
	private int COLUMN_COUNT = 0;
	
	private Connection getDBConnection(String url, String database, String id, String password) {
		Connection conn = null;

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			conn = DriverManager.getConnection(url + database, id, password);
			
			if (conn != null) {
				System.out.println("Succeeded to make connection!");
			} else {
				System.out.println("Failed to make connection!");
			}
			
			return conn;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return conn;
	}
	
	private String getFileName(final Part part) {
	    final String partHeader = part.getHeader("content-disposition");
	    
	    for (String content : part.getHeader("content-disposition").split(";")) {
	        if (content.trim().startsWith("filename")) {
	            return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
	        }
	    }
	    
	    return null;
	}
	
	/**
	 * Insert speaker data
	 */
	private void insertSpeaker(HttpServletRequest request, String filepath, String filename, Connection conn, Statement statement) {
		try {
			File file = new File(filepath + File.separator + filename);
			System.out.println(file.getPath());
			
			FileInputStream fis = new FileInputStream(file);
			
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			
			String cellValue = "";
			String insertValue = "";
			
			int count = 0;
			String sql = "";
			
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				
				count += 1;
				insertValue = "";
				
				COLUMN_COUNT = 5;
				sql = "insert into speaker (first_name, last_name, position, company, description, id) values (";
				
				for (int i = 0; i < COLUMN_COUNT; i++) {
					cellValue = row.getCell(i) == null ? "" : row.getCell(i).toString();
					
					cellValue = cellValue.replace("'", "\\'");
					
					insertValue += "'" + cellValue + "',";
				}
				
				sql = sql + insertValue + count + ")";
				System.out.println(sql);
				
				statement.execute(sql);
			}
			
			workbook.close();
			
			request.setAttribute("message", count + " rows inserted Successfully");
		} catch (Exception ex) {
			request.setAttribute("message", "Parsing Failed due to " + ex);
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Insert program data
	 */
	private void insertProgram(HttpServletRequest request, String filepath, String filename, Connection conn, Statement statement) {
		try {
			File file = new File(filepath + File.separator + filename);
			System.out.println(file.getPath());
			
			FileInputStream fis = new FileInputStream(file);
			
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			
			String cellValue = "";
			String insertValue = "";
			
			int count = 0;
			String sql = "";
			String sDay = "";
			String sTime = "";
			String eDay = "";
			String eTime = "";
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				
				if (row.getCell(0) == null || row.getCell(0).toString().trim().equals("")) {
					break;
				}
				
				count += 1;
				insertValue = "";
				
				COLUMN_COUNT = 10;
				sql = "insert into program (start_date, end_date, category1, category2, venue, title, description, speaker, id) values (";
				
				for (int i = 0; i < COLUMN_COUNT; i++) {
					cellValue = row.getCell(i) == null ? "" : row.getCell(i).toString();
					
					if (i == 0) {	// Start Date
						if (HSSFDateUtil.isCellDateFormatted(row.getCell(i))) {
							sDay = dateFormat.format(row.getCell(i).getDateCellValue());
							sDay = sDay.substring(0, 10);
						}
						
						continue;
					} else if (i == 1) {	// Start Time
						if (HSSFDateUtil.isCellDateFormatted(row.getCell(i))) {
							sTime = dateFormat.format(row.getCell(i).getDateCellValue());
							sTime = sTime.substring(11, sTime.length());
						}
						
						cellValue = "STR_TO_DATE('" + sDay + " " + sTime + "', '%d/%m/%Y %H:%i')";
					} else if (i == 2) {	// End Date
						if (HSSFDateUtil.isCellDateFormatted(row.getCell(i))) {
							eDay = dateFormat.format(row.getCell(i).getDateCellValue());
							eDay = eDay.substring(0, 10);
						}
						
						continue;
					} else if (i == 3) {	// End Time
						if (HSSFDateUtil.isCellDateFormatted(row.getCell(i))) {
							eTime = dateFormat.format(row.getCell(i).getDateCellValue());
							eTime = eTime.substring(11, eTime.length());
						}
						
						cellValue = "STR_TO_DATE('" + eDay + " " + eTime + "', '%d/%m/%Y %H:%i')";
					}
					
					if (i < 4) {
						insertValue += cellValue + ",";
					} else {
						cellValue = cellValue.replace("'", "\\'");
						
						insertValue += "'" + cellValue + "',";
					}
				}
				
				sql = sql + insertValue + count + ")";
				System.out.println(sql);
				
				statement.execute(sql);
			}
			
			workbook.close();
			
			request.setAttribute("message", count + " rows inserted Successfully");
		} catch (Exception ex) {
			request.setAttribute("message", "Parsing Failed due to " + ex);
			ex.printStackTrace();
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		
		final String filepath = request.getParameter("filepath");
		final String url = request.getParameter("url");
		final String database = request.getParameter("database");
		final String id = request.getParameter("id");
		final String password = request.getParameter("password");
		
		// DB connection
		Connection conn = null;
		Statement statement = null;
		
		try {
			conn = getDBConnection(url, database, id, password);
			statement = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// Parse Excel file
		// process only if its multipart content
		String filename = getFileName(request.getPart("file"));
		
		if (ServletFileUpload.isMultipartContent(request)) {
			if (filename.toLowerCase().startsWith("speaker")) {
				insertSpeaker(request, filepath, filename, conn, statement);
			} else if (filename.toLowerCase().startsWith("program")) {
				insertProgram(request, filepath, filename, conn, statement);
			}
		} else {
			request.setAttribute("message", "Sorry this Servlet only handles file upload request");
		}

		request.getRequestDispatcher("/result.jsp").forward(request, response);
	}

}
