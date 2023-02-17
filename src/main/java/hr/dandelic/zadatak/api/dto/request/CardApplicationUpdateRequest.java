package hr.dandelic.zadatak.api.dto.request;

import hr.dandelic.zadatak.persistence.model.CardApplication;
import lombok.Data;

@Data
public class CardApplicationUpdateRequest {
    private CardApplication.CardApplicationStatus status;
}
