import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import model.Employer;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class SearchTest {

    final String BASE_URL = "jdbc:sqlite:./JBApp.db";
    private OkHttpClient client;

    @BeforeAll
    public void setUpAll() {
        client = new OkHttpClient();
    }

    @Test
    public void testSearch1() throws IOException {

        String endpoint = BASE_URL + "/search/key=Nestle";
        Request request = new Request.Builder()
                .url(endpoint)
                .build();
        Response response = client.newCall(request).execute();
        //assert if the result is as expected
        result = response.body().string();
    }

    @Test
    public void testInsert1() throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/search")
                .build();
        Response response = client.newCall(request).execute();
        assertEquals(200, response.code());
    }

    @Test
    public void testInsert2() throws IOException {
        Request request = new Request.Builder()
                .url("http://localhost:7000/authors")
                .build();
        Response response = client.newCall(request).execute();
        assertEquals(200, response.code());
    }

    @Test
    public void testInsert3() throws IOException {
        Request request = new Request.Builder()
                .url("http://localhost:7000/authors")
                .build();
        Response response = client.newCall(request).execute();
        assertEquals(200, response.code());
    }





}
