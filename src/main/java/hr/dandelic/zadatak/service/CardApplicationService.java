package hr.dandelic.zadatak.service;

import hr.dandelic.zadatak.api.dto.request.CardApplicationCreateRequest;
import hr.dandelic.zadatak.api.dto.request.CardApplicationUpdateRequest;
import hr.dandelic.zadatak.api.dto.response.CardApplicationResponse;
import hr.dandelic.zadatak.mapper.CardApplicationMapper;
import hr.dandelic.zadatak.persistence.model.CardApplication;
import hr.dandelic.zadatak.persistence.repository.CardApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardApplicationService {
    private final CardApplicationMapper cardApplicationMapper;
    private final CardApplicationRepository cardApplicationRepository;

    private static final String ONE_APPLICATION_PER_OIB_ERROR = "validation.oneApplicationPerOib";

    @Transactional(readOnly = true)
    public List<CardApplicationResponse> findAllByOib(Long oib) {
        return cardApplicationMapper.map(cardApplicationRepository.findAllByOib(oib));
    }

    @Transactional(readOnly = true)
    public CardApplication getActiveByOib(Long oib) {
        return findActiveByOib(oib)
                .orElseThrow(() -> new EntityNotFoundException("Active card application not found"));
    }

    @Transactional(readOnly = true)
    public Optional<CardApplication> findActiveByOib(Long oib) {
        List<CardApplication.CardApplicationStatus> activeStatuses = List.of(
                CardApplication.CardApplicationStatus.NEW,
                CardApplication.CardApplicationStatus.IN_PROGRESS
        );
        return cardApplicationRepository
                .findByOibAndStatusIsIn(oib, activeStatuses);
    }

    @Transactional
    public void create(CardApplicationCreateRequest createRequest) {
        if (findActiveByOib(createRequest.getOib()).isPresent()) {
            throw new ValidationException(ONE_APPLICATION_PER_OIB_ERROR);
        }

        CardApplication cardApplication = cardApplicationMapper.map(createRequest);
        cardApplication.setStatus(CardApplication.CardApplicationStatus.NEW);

        cardApplicationRepository.save(cardApplication);
    }

    @Transactional
    public void generateFile(Long oib) {
        CardApplication cardApplication = getActiveByOib(oib);

        String fileName = String.format("%s-%s.txt", oib, cardApplication.getCreatedAt().getEpochSecond());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(cardApplication.toStringForFile());
            log.info("Successfully generated file for OIB: " + cardApplication.getOib());
        } catch (IOException e){
            log.error("An error has occurred while generating file: " + e.getMessage());
        }

    }


    @Transactional
    public CardApplicationResponse updateStatus(Long oib, CardApplicationUpdateRequest updateRequest) {
        CardApplication persisted = getActiveByOib(oib);
        cardApplicationMapper.update(persisted, updateRequest);

        // Make sure there's only one active file per active card application
        if(persisted.getStatus().equals(CardApplication.CardApplicationStatus.CANCELLED) ||
        persisted.getStatus().equals(CardApplication.CardApplicationStatus.COMPLETED)) {
            markFileAsInactive(persisted);
        }

        return cardApplicationMapper.map(persisted);
    }

    @Transactional
    public void delete(Long oib) {
        CardApplication persisted = getActiveByOib(oib);

        // If file exists for given active CardApplication, mark as inactive
        markFileAsInactive(persisted);
        cardApplicationRepository.delete(persisted);
    }

    private void markFileAsInactive(CardApplication persisted) {
        try {
            File originalFile = new File(getFileName(persisted));
            if(originalFile.exists()) {
                Files.move(originalFile.toPath(),
                        new File("inactive-" + getFileName(persisted)).toPath());
            } else {
                log.info("No active file associated with given OIB: " + persisted.getOib());
            }
        } catch (IOException e) {
            log.error("Cannot delete file associated with given OIB: " + persisted.getOib());
        }
    }


    private String getFileName(CardApplication cardApplication) {
        return String
                .format("%s-%s.txt", cardApplication.getOib(), cardApplication.getCreatedAt().getEpochSecond());
    }


}
