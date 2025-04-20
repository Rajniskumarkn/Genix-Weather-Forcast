package Package;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


/**
 * Servlet implementation class MyServlet
 */


@WebServlet("/MyServlet")
public class MyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public MyServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // API Key
        String apiKey = "1469fffa0f2e827082812487432968d9";

        // Get the city from the form input
        String city = request.getParameter("city");

        // Encode the city name for URL
        String encodedCity = URLEncoder.encode(city, "UTF-8");

        // Create the URL for the OpenWeatherMap API request (with units=metric for Celsius)
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + encodedCity + "&appid=" + apiKey + "&units=metric";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);

            Scanner scanner = new Scanner(reader);
            StringBuilder responseContent = new StringBuilder();

            while (scanner.hasNext()) {
                responseContent.append(scanner.nextLine());
            }
            scanner.close();

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);

            // Extract data from JSON
            long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
            Date date = new Date(dateTimestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = sdf.format(date);

            // Temperature (already in Celsius now)
            int temperatureCelsius = (int) jsonObject.getAsJsonObject("main").get("temp").getAsDouble();

            // Humidity
            int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();

            // Wind Speed
            double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();

            // Weather Condition
            String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();

            // Set the data as request attributes (for sending to the JSP page)
            request.setAttribute("date", formattedDate);
            request.setAttribute("city", city);
            request.setAttribute("temperature", temperatureCelsius);
            request.setAttribute("weatherCondition", weatherCondition);
            request.setAttribute("humidity", humidity);
            request.setAttribute("windSpeed", windSpeed);
            request.setAttribute("weatherData", responseContent.toString());

            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Forward to JSP page
        request.getRequestDispatcher("index.jsp").forward(request, response);
        
    }
}
