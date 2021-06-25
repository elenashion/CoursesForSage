package sage.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResultFromSaleforceJSON {
    @JsonProperty("errorCode")
    private String errorCode;
    @JsonProperty("message")
    private String message;
    @JsonProperty("fields")
    private String[] fields;

    @JsonCreator
    public ErrorResultFromSaleforceJSON(@JsonProperty("errorCode") String errorCode, @JsonProperty("message") String message, @JsonProperty("fields") String[] fields) {
        this.errorCode = errorCode;
        this.message = message;
        this.fields = fields;
    }
}
