package au.com.eventapp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
		} catch (Exception ex) {
			request.setAttribute("message", "Parsing Failed due to " + ex);
			ex.printStackTrace();
		}
	}
	
	/**
	 * Insert venue data
	 */
	private void insertVenue(HttpServletRequest request, Connection conn, Statement statement) {
		int count = 0;
		String venue = "";
		String iSql = "";
		List<String> iSqlList = new ArrayList<String>();
		String sSql = "SELECT DISTINCT venue FROM program WHERE venue IS NOT null AND venue != ''";
		
		try {
			ResultSet rs = statement.executeQuery(sSql);
			
			while (rs.next()) {
				venue = rs.getString(1).trim();
				
				if (!venue.isEmpty()) {
					count += 1;
					iSql = "INSERT INTO venue (name, id) values('" + venue + "', " + count + ")";
					iSqlList.add(iSql);
				}
			}
			
			for (String query : iSqlList) {
				System.out.println(query);
				
				statement.execute(query);
			}
			
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Insert category data
	 */
	private void insertCategory(HttpServletRequest request, Connection conn, Statement statement) {
		int count = 0;
		String category1 = "";
		String category2 = "";
		String iSql = "";
		List<String> category1List = new ArrayList<String>();
		List<String> category2List = new ArrayList<String>();
		
		String c1sql = "SELECT DISTINCT category1 FROM program";
		String c2sql = "SELECT DISTINCT category2 FROM program WHERE TRIM(category2) != ''";
		
		try {
			// Category 1 (code : 100)
			ResultSet rs1 = statement.executeQuery(c1sql);
			
			while (rs1.next()) {
				category1 = rs1.getString(1).trim();
				
				if (!category1.isEmpty()) {
					count += 1;
					iSql = "INSERT INTO category (code, category, id) values(100, '" + category1 + "', " + count + ")";
					category1List.add(iSql);
				}
			}
			
			for (String query : category1List) {
				System.out.println(query);
				
				statement.execute(query);
			}
			
			// Category 2 (code : 200)
			ResultSet rs2 = statement.executeQuery(c2sql);
			
			while (rs2.next()) {
				category2 = rs2.getString(1).trim();
				
				if (!category2.isEmpty()) {
					count += 1;
					iSql = "INSERT INTO category (code, category, id) values(200, '" + category2 + "', " + count + ")";
					category2List.add(iSql);
				}
			}
			
			for (String query : category2List) {
				System.out.println(query);
				
				statement.execute(query);
			}
			
			if (rs1 != null) {
				rs1.close();
			}
			if (rs2 != null) {
				rs2.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Insert program_speaker
	 */
	private void insertProgramSpeaker(HttpServletRequest request, Connection conn) {
		Map<Integer, String> speakerMap = new HashMap<Integer, String>();
		Map<Integer, String> programMap = new HashMap<Integer, String>();
		
		String sql1 = "SELECT id, first_name, last_name FROM speaker";
		String sql2 = "SELECT id, speaker FROM program";
		String sql3 = "";
		
		try {
			Statement statement1 = conn.createStatement();
			Statement statement2 = conn.createStatement();
			
			ResultSet rs1 = statement1.executeQuery(sql1);
			ResultSet rs2 = statement2.executeQuery(sql2);
			
			while (rs1.next()) {
				speakerMap.put(rs1.getInt("id"), rs1.getString("first_name") + " " + rs1.getString("last_name"));
			}
			
			while (rs2.next()) {
				programMap.put(rs2.getInt("id"), rs2.getString("speaker"));
			}
			
			if (rs1 != null) {
				rs1.close();
			}
			if (rs2 != null) {
				rs2.close();
			}
			statement1.close();
			statement2.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			Statement statement3 = conn.createStatement();
			
			for (Entry<Integer, String> speaker : speakerMap.entrySet()) {
				System.out.println(speaker.getKey() + " - " + speaker.getValue());
				
				for (Entry<Integer, String> program : programMap.entrySet()) {
					System.out.println("program : " + program.getKey() + " - " + program.getValue());
					
					if (program.getValue().contains(speaker.getValue())) {
						sql3 = "INSERT program_speaker (program_id, speaker_id) VALUES (" + program.getKey() + "," + speaker.getKey() + ")";
						System.out.println(sql3);
						
						statement3.execute(sql3);
					}
				}
			}
			
			statement3.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void insertAbstracts(HttpServletRequest request, String filepath, Connection conn, String firstname, String lastname) {
		try {
			int speakerId = 0;
			int abstractsId = 0;
			Statement statement1 = conn.createStatement();
			Statement statement2 = conn.createStatement();
			Statement statement3 = conn.createStatement();
			Statement statement4 = conn.createStatement();
			
			String sql1 = "SELECT id FROM speaker WHERE first_name = '" + firstname + "' AND last_name = '" + lastname + "'";
			String sql2 = "SELECT MAX(id) AS id FROM abstracts";
			String sql3 = "";
			String sql4 = "";
			
			ResultSet rs1 = statement1.executeQuery(sql1);
			
			while (rs1.next()) {
				speakerId = rs1.getInt("id");
			}
			
			if (speakerId < 1) {
				return;
			}
			
			ResultSet rs2 = statement2.executeQuery(sql2);
			while (rs2.next()) {
				abstractsId = rs2.getInt("id");
			}
			abstractsId += 1;
			
			File folder = new File(filepath);
			File[] filelist = folder.listFiles();
			for (int i = 0; i < filelist.length; i++) {
				if (filelist[i].isFile()) {
					sql3 = "INSERT INTO abstracts (id, filename) VALUES (" + abstractsId + ", '" + filelist[i].getName() +"')";
					sql4 = "INSERT INTO speaker_abstracts (speaker_id, abstracts_id) VALUES (" + speakerId + ", " + abstractsId +")";
					
					System.out.println(sql3);
					System.out.println(sql4);
					
					statement3.execute(sql3);
					statement4.execute(sql4);
					
					abstractsId += 1;
				}
			}
			
			statement1.close();
			statement2.close();
			statement3.close();
			statement4.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		
		final String filepath = request.getParameter("filepath");
		final String filetype = request.getParameter("filetype");
		final String firstname = request.getParameter("firstname");
		final String lastname = request.getParameter("lastname");
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
			if (filetype.equalsIgnoreCase("speaker")) {
				insertSpeaker(request, filepath, filename, conn, statement);
			} else if (filetype.equalsIgnoreCase("program")) {
				insertProgram(request, filepath, filename, conn, statement);
				insertVenue(request, conn, statement);
				insertCategory(request, conn, statement);
				insertProgramSpeaker(request, conn);
			} else if (filetype.equalsIgnoreCase("abstracts")) {
				insertAbstracts(request, filepath, conn, firstname, lastname);
			} else {
				return;
			}
			
			request.setAttribute("message", "Program and Venue data has been inserted successfully.");
		} else {
			request.setAttribute("message", "Sorry this Servlet only handles file upload request");
		}
		
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		request.getRequestDispatcher("/result.jsp").forward(request, response);
	}

}
