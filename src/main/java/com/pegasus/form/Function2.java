package com.pegasus.form;

import java.util.List;
import java.util.Optional;

import com.azure.ai.formrecognizer.FormRecognizerClient;
import com.azure.ai.formrecognizer.FormRecognizerClientBuilder;
import com.azure.ai.formrecognizer.models.FormRecognizerOperationResult;
import com.azure.ai.formrecognizer.models.RecognizedForm;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.polling.SyncPoller;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.pegasus.form.processor.FormProcessorFactory;
import com.pegasus.form.processor.FormProcessorV2;
import com.pegasus.form.property.PropertyManager;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function2 {
   
    private static ObjectMapper mapper = null;
    
    static {
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        PropertyManager.load();
    }
    
    @FunctionName("form")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        FormRecognizerClient formRecognizerClient = new FormRecognizerClientBuilder()
                .credential(new AzureKeyCredential(Configuration.getFormApiKey()))
                .endpoint(Configuration.getFormEndPoint())
                .buildClient();
        
        final String filename = request.getQueryParameters().get("filename");
        final String companyCode = request.getQueryParameters().get("company_code");
        if (filename == null || companyCode == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Either filename or company_code query parameter is missing")
                    .build();
        }

        String formUrl = Configuration.getFormBlobContainerUrl() + "/analyze/" + companyCode.toLowerCase() + "/" + filename;
        String modelId = Configuration.getModelId(companyCode.toLowerCase());
        
        SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> recognizeFormPoller =
            formRecognizerClient.beginRecognizeCustomFormsFromUrl(modelId, formUrl);

        List<RecognizedForm> recognizedForms = recognizeFormPoller.getFinalResult();

        FormProcessorV2 processor2 = FormProcessorFactory.getInstance(companyCode);
        String entity = null;

        processor2.process(recognizedForms);
        try {
            entity = mapper.writeValueAsString(processor2.getContainer());
        } catch (Exception e) {
            System.out.println("Error while converting to json");
        }

        if (entity != null) {
            return request.createResponseBuilder(HttpStatus.OK)
                .body(entity)
                .build();
        } else {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Encountered Error")
                    .build();
        }
    }
}
