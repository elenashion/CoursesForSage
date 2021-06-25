package sage;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import sage.entities.Pair;
import sage.entities.RequestType;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class SageAPI {

    private final String GRANT_SERVICE = "/services/oauth2/token";

    private boolean isConnected;
    private final String loginUrl;
    private final Map<String, String> headerParameters = new HashMap<>();
    // Method to send request. Object[] contains request, content, headerParameters and requestType
    // Return type must contains requestCode and result
    private Function<Object[], Pair<Integer, String>> sendHttpRequest;

    public SageAPI(String userName, String password, String loginUrl, String clientId, String clientSecret,
                   Function<Object[], Pair<Integer, String>> sendHttpRequest) {
        this.loginUrl = loginUrl;
        this.sendHttpRequest = sendHttpRequest;
        connectToSaleforce(userName, password, clientId, clientSecret, loginUrl);
    }

    private void connectToSaleforce(String userName, String password, String clientId, String clientSecret, String loginUrl) {
        headerParameters.put("Content-Type", "application/sage.json; charset=utf8");
        try {
            URI uri = new URIBuilder().setScheme("https").setHost(loginUrl).setPath(GRANT_SERVICE)
                    .addParameter("grant_type", "password")
                    .addParameter("client_id", clientId)
                    .addParameter("client_secret", clientSecret)
                    .addParameter("username", userName)
                    .addParameter("password", password)
                    .build();
            Pair<Integer, String> loginResult = sendRequest(uri.toString(),"", RequestType.POST);
            if (loginResult.getKey() != HttpStatus.SC_OK) {
                log.error("Error authenticating to SalesForce.com, error={}", loginResult.getValue());
                return;
            }
            JSONObject jsonObject = (JSONObject) new JSONTokener(loginResult.getValue()).nextValue();
            headerParameters.put("Authorization", String.format("Bearer %s", jsonObject.getString("access_token")));
            isConnected = true;
            log.debug("Connected to Salesforce successfully");
        } catch (Exception e) {
            log.error("Failed to connect to Saleforce, error={}", e.getMessage());
        }
    }

    public Pair<Integer, String> sendRequest(String request, RequestType type) {
        return sendRequest(request, "", type);
    }

    public Pair<Integer, String> sendRequest(String request, String content, RequestType type) {
        return sendHttpRequest.apply(new Object[]{request, content, headerParameters, type});
    }

    public boolean isConnected() {
        return isConnected;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void connectionLost(String error) {
        isConnected = false;
        log.error("Connection lost caused {}", error);
    }

    public boolean reconnect(String userName, String password, String clientId, String clientSecret) {
        connectToSaleforce(userName, password, clientId, clientSecret, loginUrl);
        return isConnected;
    }
}
