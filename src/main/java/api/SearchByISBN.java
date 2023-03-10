package api;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
// For request
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Servlet implementation class SearchByISBN
 */
public class SearchByISBN extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchByISBN() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Search using Google Books API for title
		String isbn = request.getParameter("isbn");
		String encodedAuthor = URLEncoder.encode(isbn, "UTF-8");
		URL url = new URL("https://www.googleapis.com/books/v1/volumes?q=isbn:" + encodedAuthor);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/json");

		if (connection.getResponseCode() != 200) {
		    throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
		}
		        
		BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
		        
		// Write the response
		response.setContentType("application/json");
		String output;
		while ((output = br.readLine()) != null) {
		    response.getWriter().write(output);
		}

		connection.disconnect();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
