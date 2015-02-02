package nxt.http.twophased;


import junit.framework.Assert;
import nxt.BlockchainTest;
import nxt.Constants;
import nxt.http.APICall;
import nxt.util.Logger;
import org.json.simple.JSONObject;
import org.junit.Test;

public class TestGetPendingTransactionVotes extends BlockchainTest {

    @Test
    public void transactionVotes() {

        APICall apiCall = new TestCreateTwoPhased.TwoPhasedMoneyTransferBuilder()
                .quorum(3)
                .build();
        String transactionId = TestCreateTwoPhased.issueCreateTwoPhased(apiCall, false);

        generateBlock();

        long fee = Constants.ONE_NXT;
        apiCall = new APICall.Builder("approvePendingTransaction")
                .param("secretPhrase", secretPhrase3)
                .param("pendingTransaction", transactionId)
                .param("feeNQT", fee)
                .build();
        JSONObject response = apiCall.invoke();
        Logger.logMessage("approvePendingTransactionResponse:" + response.toJSONString());

        generateBlock();

        System.out.println("transactionId: " + transactionId);

        apiCall = new APICall.Builder("getPendingTransactionVotes")
                .param("pendingTransaction", transactionId)
                .build();
        response = apiCall.invoke();
        Logger.logMessage("getPendingTransactionVotesResponse:" + response.toJSONString());

        Assert.assertNull(response.get("errorCode"));
        Assert.assertEquals(1, ((Long) response.get("votes")).intValue());
    }

}