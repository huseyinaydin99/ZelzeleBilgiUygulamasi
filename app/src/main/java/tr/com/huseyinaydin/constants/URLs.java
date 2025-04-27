package tr.com.huseyinaydin.constants;

public class URLs {
    private static final String LAST_ONE_HOUR_AFAD = "https://servisnet.afad.gov.tr/apigateway/deprem/apiv2/event/filter?";
    private static final String BASE_URL = "https://deprem.afad.gov.tr/";

    public URLs() {
    }

    public static String getLastOneHourAfad() {
        return LAST_ONE_HOUR_AFAD;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }
}