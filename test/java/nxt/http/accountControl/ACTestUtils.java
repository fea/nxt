package nxt.http.accountControl;

import nxt.Constants;
import nxt.http.APICall;
import nxt.http.monetarysystem.TestCurrencyIssuance;
import nxt.util.Logger;

import org.json.simple.JSONObject;
import org.junit.Assert;

public class ACTestUtils {

    public static class Builder extends APICall.Builder {

        public Builder(String requestType, String secretPhrase) {
            super(requestType);
            secretPhrase(secretPhrase);
            feeNQT(0);
        }
    }
    
    public static class CurrencyBuilder extends TestCurrencyIssuance.Builder {
        public CurrencyBuilder() {
            params.remove("minReservePerUnitNQT");
            params.remove("minDifficulty");
            params.remove("maxDifficulty");
            params.remove("algorithm");
        }
    }

    public static class CurrencyExchangeBuilder extends APICall.Builder {

        public CurrencyExchangeBuilder(String currencyId, String secretPhrase, int height) {
            super("publishExchangeOffer");
            param("currency", currencyId);
            param("buyRateNQT", 10 * Constants.ONE_NXT);
            param("sellRateNQT", 10 * Constants.ONE_NXT);
            param("totalBuyLimit", 0);
            param("totalSellLimit", 50);
            param("initialBuySupply", 0);
            param("initialSellSupply", 5);
            param("expirationHeight", height);
            secretPhrase(secretPhrase);
            feeNQT(0);
        }
    }
    
    public static class AssetBuilder extends APICall.Builder {

        public AssetBuilder(String secretPhrase) {
            super("issueAsset");
            param("name", "TestAsset");
            param("description", "Unit tests asset");
            param("quantityQNT", 10000);
            param("decimals", 4);
            secretPhrase(secretPhrase);
            feeNQT(0);
        }

    }
    
    public static JSONObject assertTransactionSuccess(APICall.Builder builder) {
        JSONObject response = builder.build().invoke();
        
        Logger.logMessage(builder.getParam("requestType") + " response: " + response.toJSONString());
        Assert.assertNull(response.get("error"));
        String result = (String) response.get("transaction");
        Assert.assertNotNull(result);
        return response;
    }
    
    public static void assertTransactionBlocked(APICall.Builder builder) {
        JSONObject response = builder.build().invoke();
        
        Logger.logMessage(builder.getParam("requestType") + " response: " + response.toJSONString());
        
        //Assert.assertNotNull("Transaction wasn't even created", response.get("transaction"));
        
        String errorMsg = (String) response.get("error");
        Assert.assertNotNull("Transaction should fail, but didn't", errorMsg);
        Assert.assertTrue(errorMsg.contains("nxt.NxtException$AccountControlException"));
    }
}
