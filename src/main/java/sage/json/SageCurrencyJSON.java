package sage.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SageCurrencyJSON {
    @JsonProperty("Id")
    private String id;
    @JsonProperty("s2cor__Currency_Code__c")
    private String code;
}
