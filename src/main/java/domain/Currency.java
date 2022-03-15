package domain;

public enum Currency {
    RUB, EUR, USD;

    public static Currency getFromString(String name) {
        switch (name) {
            case "RUB":
                return Currency.RUB;
            case "EUR":
                return Currency.EUR;
            case "USD":
                return Currency.USD;
            default:
                throw new IllegalArgumentException("Unknown currency");
        }
    }
}
