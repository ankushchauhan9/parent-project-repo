package com.example.fileProcessor.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

@Component
public class CsvParser implements FileParser {

    private static final Logger log = LoggerFactory.getLogger(CsvParser.class);
    private Map<String, String> output = new HashMap<>();

    @Autowired
    private ArtifactTaskExecutor artifactTaskExecutorImpl;

    @Override
    public void parse(File file) {

        try(Reader reader = new FileReader(file)){

            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build();

            Iterable<CSVRecord> csvRecords = format.parse(reader);
            for(CSVRecord record : csvRecords) {
                Map<String, String> map = record.toMap();
                for(Map.Entry<String, String> entry: map.entrySet()) {
                    output.put(entry.getKey(), entry.getValue());
                }
                String groupId = String.format("%s-%s","UPLOAD-" ,System.nanoTime());
                this.artifactTaskExecutorImpl.onNewTask(output.toString(), groupId);
            }
        }catch(Exception e) {
            log.error("Csv Parser : {}", e.getMessage());
        }

    }
}
