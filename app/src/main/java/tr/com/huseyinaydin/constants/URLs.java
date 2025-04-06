package tr.com.huseyinaydin.constants;

public class URLs {
    private static final String LAST_ONE_HOUR_AFAD = "https://deprem.afad.gov.tr/apiv2/event/filter?start=2024-03-01&end=2024-03-02";

    public URLs() {
    }

    public static String getLastOneHourAfad(){
        return LAST_ONE_HOUR_AFAD;
    }
}