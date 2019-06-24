import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import board.BoardDB;

/**
 * Servlet implementation class serv
 */
@WebServlet("/serv")
@MultipartConfig(maxFileSize = 1024*1024*50, location = "C:\\Users\\user0\\Documents\\eclipse-workspace\\board\\WebContent\\uploadStorage")
public class serv extends HttpServlet {
	private static final long serialVersionUID = 1L;
		
    /**
     * @see HttpServlet#HttpServlet()
     */
    public serv() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		BoardDB db = new BoardDB();
		HttpSession session = request.getSession();
		String action = request.getParameter("action");
		if(action.equals("insertPost")) {
			if(request.getPart("file").getSize() > 0) {
				String fileInfo = db.uploadFile(request);
				request.setAttribute("fileName", fileInfo.substring(14));
				request.setAttribute("fileURL", fileInfo);
				if(fileInfo != null) {
					if(db.insertPost(request)) {
						response.sendRedirect("boardList.jsp");
					} else {
						response.getWriter().append("에러가 발생했습니다.");
					}
				}
			} else {
				if(db.insertPost(request)) {
					response.sendRedirect("boardList.jsp");
				} else {
					response.getWriter().append("에러가 발생했습니다.");
				}
			}
		} else if(action.equals("updatePost")) {
			int id = Integer.parseInt(request.getParameter("id"));
			if(db.getPost(id).isLogon()) {
				if(session.getAttribute("login").equals(db.getPost(id).getUserId())) {
					if(db.updatePost(request)) {
						response.sendRedirect("boardList.jsp");
					} else {
						response.getWriter().append("에러가 발생했습니다.");
					} 
				}
			} else {
				if(db.checkPostPassword(id, request.getParameter("password"))) {
					if(db.updatePost(request)) {
						response.sendRedirect("boardList.jsp");
					} else {
						response.getWriter().append("에러가 발생했습니다.");
					} 
				} else {
					response.getWriter().append("에러가 발생했습니다.");
				}
			}
		} else if(action.equals("login")) {
			String userId = request.getParameter("user_id");
			if(db.checkMemberPassword(userId, request.getParameter("password"))) {
				session.setAttribute("login", userId);
				response.sendRedirect("boardList.jsp");
			} else {
				response.getWriter().append("잘못된 아이디 또는 비밀번호입니다.");
			}
		} else if(action.equals("join")) {
			if(!db.checkId(request.getParameter("user_id"))) {
				if(db.join(request)) {
					response.getWriter().append("가입되었습니다.<p>5초후 로그인화면으로 이동합니다.").append("<script>setTimeout('location.href=\"boardLogin.jsp\";', 5000)</script>");
				} else {
					response.getWriter().append("에러가 발생했습니다.");
				}
			} else {
				response.getWriter().append("중복된 아이디입니다.");
			}
		} else if(action.equals("logout")) {
			session.invalidate();
			response.sendRedirect("boardList.jsp");
		} else if(action.equals("editPost")) {
			int id = Integer.parseInt(request.getParameter("id"));
			if(request.getParameter("password") == null) { // 패스워드 입력 전
				if(db.getPost(id).isLogon()) {
					if(session.getAttribute("login") != null && session.getAttribute("login").equals(db.getPost(id).getUserId())) {
						request.getRequestDispatcher("boardWrite.jsp").forward(request, response);
					} else {
						response.getWriter().append("권한이 없습니다.");
					}
				} else {
					if(session.getAttribute("login") != null) {response.getWriter().append("권한이 없습니다.");}
					else {request.getRequestDispatcher("boardPassword.jsp").forward(request, response);}
				}
			} else { // 패스워드 입력 후
				if(db.checkPostPassword(id, request.getParameter("password"))) {
					request.getRequestDispatcher("boardWrite.jsp").forward(request, response);
				} else {
					response.getWriter().append("잘못된 비밀번호입니다.");
				}
			}
		} else if(action.equals("deletePost")) {
			int id = Integer.parseInt(request.getParameter("id"));
			if(request.getParameter("password") == null) { // 패스워드 입력 전
				if(db.getPost(id).isLogon()) {
					if(session.getAttribute("login") != null && session.getAttribute("login").equals(db.getPost(id).getUserId())) {
						if(db.deletePost(request)) {response.sendRedirect("boardList.jsp");} 
						else {response.getWriter().append("에러가 발생했습니다.");}
					} else {
						response.getWriter().append("권한이 없습니다.");
					}
				} else {
					if(session.getAttribute("login") != null) {response.getWriter().append("권한이 없습니다.");}
					else {request.getRequestDispatcher("boardPassword.jsp").forward(request, response);}
				}
			} else { // 패스워드 입력 후
				if(db.checkPostPassword(id, request.getParameter("password"))) {
					if(db.deletePost(request)) {response.sendRedirect("boardList.jsp");}
					else {response.getWriter().append("에러가 발생했습니다.");}
				} else {
					response.getWriter().append("잘못된 비밀번호입니다.");
				}
			}
		} else if(action.equals("insertComment")) {
			if(db.insertComment(request)) {response.getWriter().append("location.reload();");}
			else {response.getWriter().append("alert('에러가 발생했습니다.');");}
		} else if(action.equals("updateComment")) {
			int id = Integer.parseInt(request.getParameter("target_id"));
			if(db.getComment(id).isLogon()) {
				if(db.getComment(id).getUserId().equals(session.getAttribute("login"))) {
					if(db.updateComment(request)) {response.getWriter().append("location.reload();");} 
					else {response.getWriter().append("alert('에러가 발생했습니다.');");}
				} else {
					response.getWriter().append("alert('권한이 없습니다.');");
				}
			} else {
				if(db.checkCommentPassword(id, request.getParameter("password"))) {
					if(db.updateComment(request)) {response.getWriter().append("location.reload();");} 
					else {response.getWriter().append("alert('에러가 발생했습니다.');");}
				} else {
					response.getWriter().append("alert('잘못된 비밀번호입니다.');");
				}
			}
		} else if(action.equals("deleteComment")) {
			int id = Integer.parseInt(request.getParameter("id"));
			if(request.getParameter("password") == null) { // 패스워드 입력 전
				if(db.getComment(id).isLogon())  {
					if(session.getAttribute("login") != null && session.getAttribute("login").equals(db.getComment(id).getUserId())) {
						if(db.deleteComment(request)) {response.sendRedirect("boardView.jsp?id=" + request.getParameter("post_id"));} 
						else {response.getWriter().append("에러가 발생했습니다.");}
					} else {
						response.getWriter().append("권한이 없습니다.");
					}
				} else {
					if(session.getAttribute("login") != null) {response.getWriter().append("권한이 없습니다.");}
					else {request.getRequestDispatcher("boardPassword.jsp").forward(request, response);}
				}
			} else { // 패스워드 입력 후
				if(db.checkCommentPassword(id, request.getParameter("password"))) {
					if(db.deleteComment(request)) {response.sendRedirect("boardView.jsp?id=" + request.getParameter("post_id"));}
					else {response.getWriter().append("에러가 발생했습니다.");}
				} else {
					response.getWriter().append("잘못된 비밀번호입니다.");
				}
			}
		}
		db.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
