package sage.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRecordJSON {
    @JsonProperty("Id")
    private String id;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("IsDeleted")
    private Boolean isDeleted;
    @JsonProperty("CreatedDate")
    private String createdDate;
    @JsonProperty("CreatedById")
    private String createdById;
    @JsonProperty("LastModifiedDate")
    private String lastModifiedDate;
    @JsonProperty("LastModifiedById")
    private String lastModifiedById;
    @JsonProperty("SystemModstamp")
    private String systemModstamp;
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
    @JsonProperty("s2cor__Effective_To__c")
    private String s2corEffectiveToC;
    @JsonProperty("s2cor__Effective_From__c")
    private String s2corEffectiveFromC;
    @JsonProperty("s2cor__Currency__c")
    private String s2corCurrencyC;
    @JsonProperty("s2cor__Summary__c")
    private String s2corSummaryC;

    @JsonCreator
    public ExchangeRecordJSON(@JsonProperty("Id") String id, @JsonProperty("Name") String name, @JsonProperty("IsDeleted") Boolean isDeleted, @JsonProperty("CreatedDate") String createdDate,
                              @JsonProperty("CreatedById") String createdById, @JsonProperty("LastModifiedDate") String lastModifiedDate, @JsonProperty("LastModifiedById") String lastModifiedById,
                              @JsonProperty("SystemModstamp") String systemModstamp, @JsonProperty("s2cor__UID__c") String s2corUIDC, @JsonProperty("s2cor__Type__c") String s2corTypeC,
                              @JsonProperty("s2cor__Rate__c") String s2corRateC, @JsonProperty("s2cor__To_Currency__c") String s2corToCurrencyC, @JsonProperty("s2cor__Label__c") String s2corLabelC,
                              @JsonProperty("s2cor__Effective_To__c") String s2corEffectiveToC, @JsonProperty("s2cor__Effective_From__c") String s2corEffectiveFromC,
                              @JsonProperty("s2cor__Currency__c") String s2corCurrencyC, @JsonProperty("s2cor__Summary__c") String s2corSummaryC) {
        this.id = id;
        this.name = name;
        this.isDeleted = isDeleted;
        this.createdDate = createdDate;
        this.createdById = createdById;
        this.lastModifiedDate = lastModifiedDate;
        this.lastModifiedById = lastModifiedById;
        this.systemModstamp = systemModstamp;
        this.s2corUIDC = s2corUIDC;
        this.s2corTypeC = s2corTypeC;
        this.s2corRateC = s2corRateC;
        this.s2corToCurrencyC = s2corToCurrencyC;
        this.s2corLabelC = s2corLabelC;
        this.s2corEffectiveToC = s2corEffectiveToC;
        this.s2corEffectiveFromC = s2corEffectiveFromC;
        this.s2corCurrencyC = s2corCurrencyC;
        this.s2corSummaryC = s2corSummaryC;
    }
}
