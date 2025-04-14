package tr.com.huseyinaydin.constants;

public class URLs {
    private static final String LAST_ONE_HOUR_AFAD = "https://deprem.afad.gov.tr/apiv2/event/filter?";

    public URLs() {
    }

    public static String getLastOneHourAfad(){
        return LAST_ONE_HOUR_AFAD;
    }
}