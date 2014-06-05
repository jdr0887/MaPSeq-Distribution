package edu.unc.mapseq.workflow.exporter;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.unc.mapseq.workflow.model.WorkflowEntity;
import edu.unc.mapseq.workflow.model.WorkflowEntityAttribute;
import edu.unc.mapseq.workflow.model.WorkflowMessage;

public class JsonTest {

    @Test
    public void parse() {
        String json = "{\"accountName\": \"asdfadsf\",\"entities\": [{\"attributes\": [{\"name\": \"GATKDepthOfCoverage.intervalList\",\"value\": \"%s\"},{\"name\": \"GATKDepthOfCoverage.prefix\",\"value\": \"%s\"},{\"name\": \"GATKDepthOfCoverage.summaryCoverageThreshold\",\"value\": \"%s\"}],\"entityType\": \"HTSFSample\",\"guid\": \"6\"},{\"attributes\": [{\"name\": \"GATKDepthOfCoverage.intervalList\",\"value\": \"%s\"},{\"name\": \"GATKDepthOfCoverage.prefix\",\"value\": \"%s\"},{\"name\": \"GATKDepthOfCoverage.summaryCoverageThreshold\",\"value\": \"%s\"}],\"entityType\": \"WorkflowRun\",\"name\": \"%s\"}]}";
        ObjectMapper mapper = new ObjectMapper();
        try {
            WorkflowMessage workflowMessage = mapper.readValue(json, WorkflowMessage.class);
            System.out.println(workflowMessage.toString());

            for (WorkflowEntity entity : workflowMessage.getEntities()) {
                System.out.println(entity.toString());

                if (entity.getAttributes() != null && entity.getAttributes().size() > 0) {
                    for (WorkflowEntityAttribute attribute : entity.getAttributes()) {
                        System.out.println(attribute.toString());
                    }
                }
                
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
