import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.concurrent.TimeUnit;

public class CrptApi {
    private TimeUnit timeUnit;
    private int requestLimit;
    private long lastResetTime;
    private int currentRequests;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
        this.lastResetTime = System.currentTimeMillis();
        this.currentRequests = 0;
    }

    public void createDocument(Object document, String signature) {
        // Логика выполнения запроса к API
        if (allowRequest()) {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost request = new HttpPost("https://ismp.crpt.ru/api/v3/lk/documents/create");
                // Подготовка запроса
                ObjectMapper mapper = new ObjectMapper();
                String jsonDocument = mapper.writeValueAsString(document);
                StringEntity entity = new StringEntity(jsonDocument);
                request.setEntity(entity);
                // Выполнение запроса
                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    // Обработка ответа
                    HttpEntity responseEntity = response.getEntity();
                    // Чтение ответа, обработка и т.д.
                }
            } catch (Exception e) {
                // Обработка ошибок
            }
        } else {
            // Запрос заблокирован из-за превышения лимита
        }
    }

    private synchronized boolean allowRequest() {
        // Проверка превышения лимита
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastResetTime > timeUnit.toMillis(1)) {
            currentRequests = 0;
            lastResetTime = currentTime;
        }
        if (currentRequests < requestLimit) {
            currentRequests++;
            return true;
        }
        return false;
    }
}
