package api;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
// For Request
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
// For storing objects and comparing
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
// For JSON parsing
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servlet implementation class SearchByTitle
 */
public class SearchByTitle extends HttpServlet {
	private static final long serialVersionUID = 1L;

    static class Book {
        String title;
        String publishedDate;
        String publisher;

        public Book(String title, String publishedDate, String publisher) {
            this.title = title;
            this.publishedDate = publishedDate;
            this.publisher = publisher;
        }
    }
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchByTitle() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		// Search using Google Books API for title
		String titleParam = request.getParameter("title");
        String encodedAuthor = URLEncoder.encode(titleParam, "UTF-8");
        URL url = new URL("https://www.googleapis.com/books/v1/volumes?q=intitle:" + encodedAuthor);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        if (connection.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
        }
        
        BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
        
        StringBuilder res = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            res.append(line);
        }
        br.close();

        JSONObject jsonObject = new JSONObject(res.toString());
        JSONArray items = jsonObject.getJSONArray("items");

        ArrayList<Book> books = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            JSONObject volumeInfo = item.getJSONObject("volumeInfo");

            String title = volumeInfo.getString("title");
            String publishedDate = volumeInfo.optString("publishedDate", "");
            String publisher = volumeInfo.optString("publisher", "");

            Book book = new Book(title, publishedDate, publisher);
            books.add(book);
        }

        // Sort the books by year published (in descending order)
        Collections.sort(books, new Comparator<Book>() {
            @Override
            public int compare(Book b1, Book b2) {
                if (b1.publishedDate == null && b2.publishedDate == null) {
                    return 0;
                } else if (b1.publishedDate == null) {
                    return 1;
                } else if (b2.publishedDate == null) {
                    return -1;
                } else {
                    return b2.publishedDate.compareTo(b1.publishedDate);
                }
            }
        });

        // Convert it into JSON again
        JSONArray resultArray = new JSONArray();
        for (Book book : books) {
            JSONObject bookObj = new JSONObject();
            bookObj.put("title", book.title);
            bookObj.put("publishedDate", book.publishedDate);
            bookObj.put("publisher", book.publisher);
            resultArray.put(bookObj);
        }

        JSONObject result = new JSONObject();
        result.put("books", resultArray);
                
        // Set response type
        response.setContentType("application/json");
        response.getWriter().write(result.toString());

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
