package sage.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SageGetCurrenciesResultJSON {
    @JsonProperty("totalSize")
    private Integer totalSize;
    @JsonProperty("records")
    private SageCurrencyJSON[] currencyJSONS;
}
