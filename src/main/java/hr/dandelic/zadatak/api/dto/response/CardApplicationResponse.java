package hr.dandelic.zadatak.api.dto.response;

import hr.dandelic.zadatak.persistence.model.CardApplication;
import lombok.Data;

@Data
public class CardApplicationResponse {
    private String firstName;
    private String lastName;
    private long oib;
    private CardApplication.CardApplicationStatus status;
}
