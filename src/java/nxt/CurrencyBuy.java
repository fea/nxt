package nxt;

import nxt.db.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class CurrencyBuy extends CurrencyOffer {

    private static final DbKey.LongKeyFactory<CurrencyOffer> buyOfferDbKeyFactory = new DbKey.LongKeyFactory<CurrencyOffer>("id") {

        @Override
        public DbKey newKey(CurrencyOffer offer) {
            return offer.dbKey;
        }

    };

    private static final VersionedEntityDbTable<CurrencyOffer> buyOfferTable = new VersionedEntityDbTable<CurrencyOffer>("buy_offer", buyOfferDbKeyFactory) {

        @Override
        protected CurrencyBuy load(Connection con, ResultSet rs) throws SQLException {
            return new CurrencyBuy(rs);
        }

        @Override
        protected void save(Connection con, CurrencyOffer buy) throws SQLException {
            buy.save(con, table);
        }

    };

    public static int getCount() {
        return buyOfferTable.getCount();
    }

    public static CurrencyOffer getBuyOffer(long offerId) {
        return buyOfferTable.get(buyOfferDbKeyFactory.newKey(offerId));
    }

    public static DbIterator<CurrencyOffer> getAll(int from, int to) {
        return buyOfferTable.getAll(from, to);
    }

    static void init() {}

    CurrencyBuy(Transaction transaction, Attachment.MonetarySystemPublishExchangeOffer attachment) {
        super(transaction.getId(), attachment.getCurrencyId(), transaction.getSenderId(), attachment.getBuyRateNQT(),
                attachment.getTotalBuyLimit(), attachment.getInitialBuySupply(), attachment.getExpirationHeight(), transaction.getHeight());
        this.dbKey = buyOfferDbKeyFactory.newKey(id);
    }

    private CurrencyBuy(ResultSet rs) throws SQLException {
        super(rs);
        this.dbKey = buyOfferDbKeyFactory.newKey(super.id);
    }

    protected void save(Connection con, String table) throws SQLException {
        super.save(con, table);
    }

    @Override
    public CurrencyOffer getCounterOffer() {
        return CurrencySell.getSellOffer(id);
    }

    static void addOffer(CurrencyOffer buyOffer) {
        buyOfferTable.insert(buyOffer);
    }

    static void remove(CurrencyOffer buyOffer) {
        buyOfferTable.delete(buyOffer);
    }

    public static DbIterator<CurrencyOffer> getCurrencyOffers(long currencyId) {
        return buyOfferTable.getManyBy(new DbClause.LongClause("currency_id", currencyId), 0, -1, " ORDER BY rate DESC, height ASC, id ASC ");
    }

    void increaseSupply(long delta) {
        super.increaseSupply(delta);
        buyOfferTable.insert(this);
    }

    void decreaseLimitAndSupply(long delta) {
        super.decreaseLimitAndSupply(delta);
        buyOfferTable.insert(this);
    }

}
