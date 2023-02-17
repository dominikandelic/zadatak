package hr.dandelic.zadatak.service;

import hr.dandelic.zadatak.persistence.model.CardApplication;
import liquibase.repackaged.org.apache.commons.lang3.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class CardApplicationServiceTest {

    @Test
    public void givenCardApplication_whenWrittenInFileStringRepresentation_thenReadsTheSameStatus() {
        CardApplication cardApplication = CardApplication.builder()
                .status(CardApplication.CardApplicationStatus.NEW)
                .firstName("Ivan")
                .lastName("Ivanic")
                .oib(12345678912L)
                .build();

        String fileName = String
                .format("test-%s-%s.txt", cardApplication.getOib(), cardApplication.getCreatedAt().getEpochSecond());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(cardApplication.toStringForFile());
        } catch (IOException e){
            log.error("An error has occurred while generating file: " + e.getMessage());
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String lineWithoutWhitespace = StringUtils.deleteWhitespace(reader.readLine());
            String[] csvApplication = lineWithoutWhitespace.split(",");
            if(csvApplication.length != 4) {
                fail("There must be only four values for this object!");
            }
            CardApplication applicationFromDisk = CardApplication.builder()
                    .firstName(csvApplication[0])
                    .lastName(csvApplication[1])
                    .oib(Long.valueOf(csvApplication[2]))
                    .status(CardApplication.CardApplicationStatus.valueOf(csvApplication[3]))
                    .build();
            assertEquals(cardApplication.getStatus(), applicationFromDisk.getStatus());
        }   catch (IOException e){
        log.error("An error has occurred while reading a file: " + e.getMessage());
    }
    }

}