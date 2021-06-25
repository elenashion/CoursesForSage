package sage.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateExchangeRecordRequestJSON {
    @JsonProperty("s2cor__UID__c")
    private String s2corUIDC;
    @JsonProperty("s2cor__Type__c")
    private String s2corTypeC;
    @JsonProperty("s2cor__Rate__c")
    private String s2corRateC;
    @JsonProperty("s2cor__To_Currency__c")
    private String s2corToCurrencyC;
    @JsonProperty("s2cor__Label__c")
    private String s2corLabelC;
    @JsonProperty("s2cor__Effective_From__c")
    private String s2corEffectiveFromC;
    @JsonProperty("s2cor__Currency__c")
    private String s2corCurrencyC;


    @JsonCreator
    public CreateExchangeRecordRequestJSON(@JsonProperty("s2cor__UID__c") String s2corUIDC, @JsonProperty("s2cor__Type__c") String s2corTypeC, @JsonProperty("s2cor__Rate__c") String s2corRateC,
                                           @JsonProperty("s2cor__To_Currency__c") String s2corToCurrencyC, @JsonProperty("s2cor__Label__c") String s2corLabelC,
                                           @JsonProperty("s2cor__Effective_From__c") String s2corEffectiveFromC, @JsonProperty("s2cor__Currency__c") String s2corCurrencyC) {
        this.s2corUIDC = s2corUIDC;
        this.s2corTypeC = s2corTypeC;
        this.s2corRateC = s2corRateC;
        this.s2corToCurrencyC = s2corToCurrencyC;
        this.s2corLabelC = s2corLabelC;
        this.s2corEffectiveFromC = s2corEffectiveFromC;
        this.s2corCurrencyC = s2corCurrencyC;
    }
}