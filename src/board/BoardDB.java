package board;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.sql.*;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import java.io.File;
import java.io.IOException;

public class BoardDB {
	private Connection con;
	
	public BoardDB() {
		try {
			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/mysql");
			con = ds.getConnection();
		} catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {if(con != null) con.close();} catch (SQLException e) {e.printStackTrace();}
	}
	
	public ArrayList<PostEntity> getPosts(int page) {
		ArrayList<PostEntity> list = new ArrayList<PostEntity>();
		Statement stmt = null;
		ResultSet rs;
		String sql;
		try {
			sql = "select * from board order by id desc limit " + (page-1)*10
					+ ", 10";
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				PostEntity be = new PostEntity();
				be.setId(rs.getInt("id"));
				be.setUserId(rs.getString("user_id"));
				be.setPassword(rs.getString("password"));
				be.setTitle(rs.getString("title"));
				be.setContent(rs.getString("content"));
				be.setDate(rs.getTimestamp("date"));
				be.setFileName(rs.getString("file_name"));
				be.setFileURL(rs.getString("file_URL"));
				be.setViews(rs.getInt("views"));
				be.setLogon(rs.getBoolean("logon"));
				list.add(be);
			}
		} catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(stmt != null) {try {stmt.close();} catch (SQLException e) {e.printStackTrace();}}
		}
		return list;
	}
	
	public ArrayList<PostEntity> getPosts(int page, String word) {
		ArrayList<PostEntity> list = new ArrayList<PostEntity>();
		Statement stmt = null;
		ResultSet rs;
		String sql = "select * from board where title like '%" + word + "%' order by id desc limit " + (page-1)*10 + ", 10";
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				PostEntity be = new PostEntity();
				be.setId(rs.getInt("id"));
				be.setUserId(rs.getString("user_id"));
				be.setPassword(rs.getString("password"));
				be.setTitle(rs.getString("title"));
				be.setContent(rs.getString("content"));
				be.setDate(rs.getTimestamp("date"));
				be.setFileName(rs.getString("file_name"));
				be.setFileURL(rs.getString("file_URL"));
				be.setViews(rs.getInt("views"));
				be.setLogon(rs.getBoolean("logon"));
				list.add(be);
			}
			return list;
		} catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(stmt != null) {try {stmt.close();} catch (SQLException e) {e.printStackTrace();}}
		}
		return null;
	}
	
	public PostEntity getPost(int id) {
		PostEntity be = new PostEntity();
		Statement stmt = null;
		ResultSet rs;
		String sql;
		sql = "select * from board where id=" + id;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			be.setId(rs.getInt("id"));
			be.setUserId(rs.getString("user_id"));
			be.setPassword(rs.getString("password"));
			be.setTitle(rs.getString("title"));
			be.setContent(rs.getString("content"));
			be.setDate(rs.getTimestamp("date"));
			be.setFileName(rs.getString("file_name"));
			be.setFileURL(rs.getString("file_URL"));
			be.setViews(rs.getInt("views"));
			be.setLogon(rs.getBoolean("logon"));
			return be;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(stmt != null) {try {stmt.close();} catch (SQLException e) {e.printStackTrace();}}
		}
		return null;
	}
	public int getListCount(String keyword) {
		Statement stmt = null;
		String sql = "select count(*) from board where title like '%" + keyword + "%'";
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			return rs.getInt("count(*)");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(stmt != null) {try {stmt.close();} catch (SQLException e) {e.printStackTrace();}}
		}
		return 0;
	}
	
	public boolean insertPost(HttpServletRequest request) {
		PreparedStatement pstmt = null;
		String userId = request.getParameter("user_id");
		String password = request.getParameter("password");
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String fileName = (String)request.getAttribute("fileName");
		String fileURL = (String)request.getAttribute("fileURL");
		String sql;
		boolean logon = request.getSession().getAttribute("login") != null;
		if(userId.isBlank() || password.isBlank() || title.isBlank() || content.isBlank()) {
			return false;
		}
		sql = "insert into board values(0, ?, ?, ?, ?, now(), ?, ?, 0, ?)";
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
			pstmt.setString(2, password);
			pstmt.setString(3, title);
			pstmt.setString(4, content);
			pstmt.setString(5, fileName);
			pstmt.setString(6, fileURL);
			pstmt.setBoolean(7, logon);
			return pstmt.executeUpdate() != 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(pstmt != null) {try {pstmt.close();} catch (SQLException e) {e.printStackTrace();}}
		}
		return false;
	}
	
	public boolean updatePost(HttpServletRequest request) {
		PreparedStatement pstmt = null;
		String userId = request.getParameter("user_id");
		String password = request.getParameter("password");
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String id = request.getParameter("id");
		String sql;
		if(userId.isBlank() || password.isBlank() || title.isBlank() || content.isBlank()) {
			return false;
		}
		sql = "update board set user_id=?, password=?, title=?, content=? where id=?";
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
			pstmt.setString(2, password);
			pstmt.setString(3, title);
			pstmt.setString(4, content);
			pstmt.setString(5, id);
			return pstmt.executeUpdate() != 0;
		} catch (SQLException e) {
			// 
			e.printStackTrace();
		} finally {
			if(pstmt != null) {try {pstmt.close();} catch (SQLException e) {e.printStackTrace();}}
		}
		return false;
	}
	
	public boolean deletePost(HttpServletRequest request) {
		PreparedStatement pstmt = null;
		int id = Integer.parseInt(request.getParameter("id"));
		String sql = "delete from board where id=?";
		if(getPost(id).getFileName() != null) {
			String fileDir = request.getServletContext().getInitParameter("uploadDir");
			new File(fileDir, getPost(id).getFileURL()).delete();
		}
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, id);
			return (pstmt.executeUpdate() != 0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(pstmt != null) {try {pstmt.close();} catch (SQLException e) {e.printStackTrace();}}
		}
		return false;
	}
	
	public void viewPlus(int id) {
		Statement stmt = null;
		String sql = "update board set views = views+1 where id=" + id;
		try {
			stmt = con.createStatement();
			stmt.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(stmt != null) {try {stmt.close();} catch (SQLException e) {e.printStackTrace();}}
		}
	}
	
	public boolean checkId(String userId) {
		Statement stmt = null;
		ResultSet rs;
		String sql = "select count(*) from member where user_id='" + userId + "'";
		if(userId == null || userId.isBlank()) {
			return false;
		}
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			return (rs.getInt("count(*)") != 0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(stmt != null) {try {stmt.close();}catch (SQLException e) {e.printStackTrace();}}
		}
		return false;
	}
	
	public boolean checkPostPassword(int id, String password) {
		Statement stmt = null;
		ResultSet rs;
		String sql = "select * from board where id='" + id + "'";
		if(password == null || password.isBlank()) {
			return false;
		}
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			if(rs.getString("password").equals(password)) {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(stmt != null) {try {stmt.close();}catch (SQLException e) {e.printStackTrace();}}
		}
		return false;
	}
	
	public boolean checkMemberPassword(String userId, String password) {
		Statement stmt = null;
		ResultSet rs;
		String sql = "select * from member where user_id='" + userId + "'";
		if(userId == null || userId.isBlank() || password == null || password.isBlank()) {
			return false;
		}
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			if(rs.getString("password").equals(password)) {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(stmt != null) {try {stmt.close();} catch (SQLException e) {e.printStackTrace();}}
		}
		return false;
	}
	
	public boolean join(HttpServletRequest request) {
		PreparedStatement pstmt = null;
		String userId = request.getParameter("user_id");
		String password = request.getParameter("password");
		String sql;
		if(userId == null || userId.isBlank() || password == null || password.isBlank()) {
			return false;
		}
		sql = "insert into member values(0, ?, ?)";
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
			pstmt.setString(2, password);
			return (pstmt.executeUpdate() != 0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(pstmt != null) {try {pstmt.close();} catch (SQLException e) {e.printStackTrace();}}
		}
		return false;
	}
	
	public String uploadFile(HttpServletRequest request) {
		String location = request.getServletContext().getInitParameter("uploadDir");
		System.out.println(location);
		try {
			Part part = request.getPart("file");
			String fileHeader = part.getHeader("content-disposition");
			String fileName = fileHeader.substring(fileHeader.indexOf("filename=")+9).replace("\"", "");
			String randomString;
			do {randomString = UUID.randomUUID().toString().substring(0, 14);} 
			while(new File(location, randomString + fileName).exists());
			part.write(randomString + fileName);
			return (randomString + fileName);
		} catch (IOException | ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<CommentEntity> getComments(int post_id) {
		ArrayList<CommentEntity> commentList = new ArrayList<CommentEntity>(); 
		PreparedStatement pstmt = null;
		ResultSet rs;
		String sql = "select * from comment where post_id = ? order by step";
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, post_id);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				CommentEntity ce = new CommentEntity();
				ce.setId(rs.getInt("id"));
				ce.setPostId(rs.getInt("post_id"));
				ce.setStep(rs.getInt("step"));
				ce.setDepth(rs.getInt("depth"));
				ce.setUserId(rs.getString("user_id"));
				ce.setPassword(rs.getString("password"));
				ce.setContent(rs.getString("content"));
				ce.setDate(rs.getTimestamp("date"));
				ce.setLogon(rs.getBoolean("logon"));
				commentList.add(ce);
			}
			return commentList;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(pstmt != null) {try {pstmt.close();} catch (SQLException e) {e.printStackTrace();}}
		}
		return null;
	}
	
	public CommentEntity getComment(int id) {
		CommentEntity ce = new CommentEntity();
		Statement stmt = null;
		ResultSet rs;
		String sql = "select * from comment where id=" + id;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			ce.setId(rs.getInt("id"));
			ce.setPostId(rs.getInt("post_id"));
			ce.setStep(rs.getInt("step"));
			ce.setDepth(rs.getInt("depth"));
			ce.setUserId(rs.getString("user_id"));
			ce.setPassword(rs.getString("password"));
			ce.setContent(rs.getString("content"));
			ce.setDate(rs.getTimestamp("date"));
			ce.setLogon(rs.getBoolean("logon"));
			return ce;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(stmt != null) {try {stmt.close();} catch (SQLException e) {e.printStackTrace();}}
		}
		return null;
	}
	
	public boolean insertComment(HttpServletRequest request) {
		Statement stmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs;
		int postId = Integer.parseInt(request.getParameter("post_id"));
		int replyId = Integer.parseInt(request.getParameter("target_id"));
		int step;
		int depth = Integer.parseInt(request.getParameter("depth"));;
		String userId = request.getParameter("user_id");
		String password = request.getParameter("password");
		String content = request.getParameter("content");
		String sql;
		boolean logon = request.getSession().getAttribute("login") != null;
		if(userId == null || userId.isBlank() || password == null || password.isBlank() || content == null || content.isBlank()) {
			return false;
		}
		try {
			con.setAutoCommit(false);
			stmt = con.createStatement();
			sql = "select step from comment where step=(select min(step) from comment where step>(select step from comment where id=" + replyId + ") and depth<=(select depth from comment where id=" + replyId + ") and post_id=" + postId + ") and post_id=" + postId;
			rs = stmt.executeQuery(sql);
			if(rs.next()) {
				step = rs.getInt("step");
				sql = "update comment set step=step+1 where step>=" + step + " and post_id=" + postId;
				stmt.executeUpdate(sql);
			} else {
				sql = "select max(step) from comment where post_id=" + postId;
				rs = stmt.executeQuery(sql);
				rs.next();
				step = rs.getInt("max(step)")+1;
			}
			sql = "insert into comment value(0, ?, ?, ?, ?, ?, ?, now(), ?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, postId);
			pstmt.setInt(2, step);
			pstmt.setInt(3, depth);
			pstmt.setString(4, userId);
			pstmt.setString(5, password);
			pstmt.setString(6, content);
			pstmt.setBoolean(7, logon);
			int result = pstmt.executeUpdate();
			con.commit();
			return result != 0;
		} catch (SQLException e) {
			try {con.rollback();} catch (SQLException e1) {e1.printStackTrace();}
			e.printStackTrace();
		} finally {
			try {con.setAutoCommit(true);} catch (SQLException e1) {e1.printStackTrace();}
			if(pstmt != null) {try {stmt.close(); pstmt.close();} catch (SQLException e) {e.printStackTrace();}}
		}
		return false;
	}
	
	public boolean deleteComment(HttpServletRequest request) {
		Statement stmt = null;
		String id = request.getParameter("id");
		String postId = request.getParameter("post_id");
		String sql;
		try {
			stmt = con.createStatement();
			sql = "update comment set step=step-1 where step>(select * from (select step from comment where id=" + id + ") a) and post_id=" + postId;
			stmt.executeUpdate(sql);
			sql = "delete from comment where id=" + id;
			return stmt.executeUpdate(sql) != 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(stmt != null) {try {stmt.close();} catch (SQLException e) {e.printStackTrace();}}
		}
		return false;
	}
	
	public boolean updateComment(HttpServletRequest request) {
		PreparedStatement pstmt = null;
		int id = Integer.parseInt(request.getParameter("target_id"));
		String content = request.getParameter("content");
		String sql = "update comment set content=?, date=now() where id=?";
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, content);
			pstmt.setInt(2, id);
			return pstmt.executeUpdate() != 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(pstmt != null) {try {pstmt.close();} catch (SQLException e) {e.printStackTrace();}}
		}
		return false;
	}
	
	public boolean checkCommentPassword(int id, String password) {
		Statement stmt = null;
		ResultSet rs;
		String sql = "select * from comment where id=" + id;
		if(password == null || password.isBlank()) {
			return false;
		}
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			if(rs.getString("password").equals(password)) {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(stmt != null) {try {stmt.close();} catch (SQLException e) {e.printStackTrace();}}
		}
		return false;
	}
	
	public boolean isImage(String URL) {
		if(URL != null ) {
			String location = "C:\\Users\\user0\\Documents\\eclipse-workspace\\board\\WebContent\\uploadStorage\\";
			try {
				String mimetype = Files.probeContentType(Paths.get(location + URL));
				for(String str:mimetype.split("/")) {
					if(str.equalsIgnoreCase("image")) {return true;}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
}
