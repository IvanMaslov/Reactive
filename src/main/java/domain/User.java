package domain;

import org.bson.Document;

public class User {
    private final int id;
    private final String nickname;
    private final Currency currency;

    public User(int id, String nickname, Currency currency) {
        this.id = id;
        this.nickname = nickname;
        this.currency = currency;
    }

    public User(Document document) {
        this(document.getInteger("id"),
                document.getString("nickname"),
                Currency.getFromString(document.getString("currency")));
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "UserId = " + id
                + " | UserNickname = " + nickname
                + " | UserCurrency = " + currency.toString()
                + System.lineSeparator();
    }
}
