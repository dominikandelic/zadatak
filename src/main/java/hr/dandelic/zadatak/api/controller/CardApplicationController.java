package hr.dandelic.zadatak.api.controller;

import hr.dandelic.zadatak.api.dto.request.CardApplicationCreateRequest;
import hr.dandelic.zadatak.api.dto.request.CardApplicationUpdateRequest;
import hr.dandelic.zadatak.api.dto.response.CardApplicationResponse;
import hr.dandelic.zadatak.mapper.CardApplicationMapper;
import hr.dandelic.zadatak.service.CardApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/api/v1/card-applications")
@RequiredArgsConstructor
public class CardApplicationController {
    private final CardApplicationService cardApplicationService;
    private final CardApplicationMapper cardApplicationMapper;

    @GetMapping
    public List<CardApplicationResponse> getCardApplicationsByOib(@RequestParam Long oib) {
        return cardApplicationService.findAllByOib(oib);
    }

    @GetMapping("/{oib}")
    public CardApplicationResponse getActiveCardApplicationByOib(@PathVariable Long oib) {
        return cardApplicationMapper.map(cardApplicationService.getActiveByOib(oib));
    }

    @PostMapping
    public void createCardApplication(@RequestBody @Valid CardApplicationCreateRequest createRequest) {
        cardApplicationService.create(createRequest);
    }

    @PutMapping("/{oib}/generate-file")
    public void generateFile(@PathVariable Long oib) {
        cardApplicationService.generateFile(oib);
    }

    @PatchMapping("/{oib}")
    public CardApplicationResponse updateCardApplication(@PathVariable Long oib,
                                                         @RequestBody @Valid
                                                         CardApplicationUpdateRequest updateRequest) {
        return cardApplicationService.updateStatus(oib, updateRequest);
    }

    @DeleteMapping("/{oib}")
    public void deleteCardApplication(@PathVariable Long oib) {
        cardApplicationService.delete(oib);
    }

}
