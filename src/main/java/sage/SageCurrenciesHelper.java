package sage;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import sage.entities.Pair;
import sage.entities.RequestType;
import sage.entities.SageException;
import sage.json.*;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SageCurrenciesHelper {

    private final String DATA_CURRENCY_SERVICE = "/services/data/v44.0/queryAll";
    private final String DATA_CURRENCY_SQL_GET = "SELECT Id, s2cor__Currency_Code__c FROM s2cor__Sage_COR_Currency__c";
    private final String DATA_SERVICE = "/services/data/v44.0/sobjects/s2cor__Sage_COR_Currency_Exchange_Rate__c";
    private String createRecordRequest;
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final Map<String, String> currencyIdByName = new HashMap<>();
    private final SageAPI api;

    public SageCurrenciesHelper(SageAPI api) {
        this.api = api;
        try {
            createRecordRequest = new URIBuilder().setScheme("https").setHost(api.getLoginUrl()).setPath(DATA_SERVICE).build().toString();
        } catch (Exception e) {
            createRecordRequest = String.format("https://%s%s", api.getLoginUrl(), DATA_SERVICE);
        }
        loadCurrencies(api.getLoginUrl());
    }

    private void loadCurrencies(String loginUrl) {
        if (!api.isConnected())
            return;
        try {
            URI uri = new URIBuilder().setScheme("https").setHost(loginUrl).setPath(DATA_CURRENCY_SERVICE).addParameter("q", DATA_CURRENCY_SQL_GET).build();
            Pair<Integer, String> getCurrenciesResult = api.sendRequest(uri.toString(), RequestType.GET);
            if (getCurrenciesResult.getKey() != HttpStatus.SC_OK) {
                log.error("Can't get currencies from Saleforce, error={}", getCurrenciesResult.getValue());
                throwExceptionWithCheckCode(jsonMapper.readValue(getCurrenciesResult.getValue(), ErrorResultFromSaleforceJSON.class), null);
            }
            SageGetCurrenciesResultJSON currencies = jsonMapper.readValue(getCurrenciesResult.getValue(), SageGetCurrenciesResultJSON.class);
            if (currencies.getCurrencyJSONS() == null || currencies.getCurrencyJSONS().length < 1) {
                log.error("Currency array from Salesforce is empty");
                return;
            }
            for (SageCurrencyJSON currency : currencies.getCurrencyJSONS()) {
                currencyIdByName.put(currency.getCode(), currency.getId());
            }
            log.info("Loaded {} currencies, [{}]", currencies.getTotalSize(), String.join(", ", currencyIdByName.keySet()));
        } catch (Exception e) {
            log.error("Failed to get currencies from Saleforce, error={}", e.getMessage());
        }
    }

    public Map<String, String> getCurrencyIdByNameMap() {
        return Collections.unmodifiableMap(currencyIdByName);
    }

    public boolean createCurrencyExchangeRecord(String label, String effectiveFrom, String currency, String rate, String toCurrency, String uid) {
        if (api.isConnected() && !currencyIdByName.isEmpty()) {
            try {
                CreateExchangeRecordRequestJSON createRecord = new CreateExchangeRecordRequestJSON(uid, "Spot", rate, currencyIdByName.get(toCurrency), label, effectiveFrom, currencyIdByName.get(currency));
                StringWriter w = new StringWriter();
                jsonMapper.writeValue(w, createRecord);
                Pair<Integer, String> result = api.sendRequest(createRecordRequest, w.toString(), RequestType.POST);
                if (result.getKey() != HttpStatus.SC_CREATED) {
                    ErrorResultFromSaleforceJSON[] jsonResult = jsonMapper.readValue(result.getValue(), ErrorResultFromSaleforceJSON[].class);
                    throwExceptionWithCheckCode(jsonResult[0], createRecord);
                }
                return true;
            } catch (IOException e) {
                log.error("Failed to create CurrencyExchangeRecord, error={}", e.getMessage());
            }
        }
        return false;
    }

    public ExchangeRecordJSON getCurrencyExchangeRecordById(String id) {
        if (api.isConnected()) {
            String getRecordRequest = String.format("%s/%s", createRecordRequest, id);
            try {
                Pair<Integer, String> result = api.sendRequest(getRecordRequest, RequestType.GET);
                if (result.getKey() != HttpStatus.SC_OK) {
                    log.error("Error get CurrencyExchangeRecord, recordId={}, error={}\n", id, result.getValue());
                    return null;
                }
                return jsonMapper.readValue(result.getValue(), ExchangeRecordJSON.class);
            } catch (IOException e) {
                log.error("Failed to get CurrencyExchangeRecord, recordId={} error={}\n", id, e.getMessage());
            }
        }
        return null;
    }

    private void throwExceptionWithCheckCode(ErrorResultFromSaleforceJSON jsonResult, CreateExchangeRecordRequestJSON createRecord) {
        try {
            switch (jsonResult.getErrorCode()) {
                case "DUPLICATE_VALUE":
                    ExchangeRecordJSON duplicateRecord = getCurrencyExchangeRecordById(jsonResult.getMessage().split("id: ")[1]);
                    if (duplicateRecord != null && new BigDecimal(duplicateRecord.getS2corRateC()).compareTo(new BigDecimal(createRecord.getS2corRateC())) == 0)
                        return;
                    if (duplicateRecord == null)
                        throw new SageException(String.format("Duplicate value for record with id=%s. New record rate=%s\n",
                                jsonResult.getMessage().split("id: ")[1], createRecord.getS2corRateC()));
                    else
                        throw new SageException(String.format("Duplicate value for record [uid=%s, name=%s, id=%s, rate=%s]. New record rate=%s\n",
                                duplicateRecord.getS2corUIDC(), duplicateRecord.getName(), duplicateRecord.getId(), duplicateRecord.getS2corRateC(),
                                createRecord.getS2corRateC()));
                case "MALFORMED_ID":
                    throw new SageException(String.format("%s. Wrong fields: %s\n", jsonResult.getMessage(), Arrays.toString(jsonResult.getFields())));
                case "INVALID_SESSION_ID":
                    api.connectionLost("INVALID_SESSION_ID");
                    throw new SageException(String.format("%s. Failed to create records.", jsonResult.getMessage()));
                case "INVALID_CROSS_REFERENCE_KEY":
                    throw new SageException(String.format("%s. You doesn't have access to some of id (currency or record) or they doesn't exist", jsonResult.getMessage()));
                case "REQUIRED_FIELD_MISSING":
                    throw new SageException(String.format("Required field missing. Fields=%s, new ExchangeRecord=%s",
                            Arrays.toString(jsonResult.getFields()), createRecord == null ? null : createRecord.toString()));
                case "MALFORMED_QUERY":
                    throw new SageException(String.format("Can't get currencies from Saleforce, error=%s", jsonResult.getMessage()));

            }
            throw new SageException(String.format("Can't create CurrencyExchangeRecord. ErrorCode=%s, errorMessage=%s, fields=%s", jsonResult.getErrorCode(),
                    jsonResult.getMessage(), Arrays.toString(jsonResult.getFields())));
        } catch (Exception e) {
            throw new SageException(String.format("Failed writeMessageAboutError. Error=%s, jsonObject=%s", e.getMessage(), jsonResult.toString()));
        }
    }
}
