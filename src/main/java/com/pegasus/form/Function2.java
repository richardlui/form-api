package com.pegasus.form;

import java.util.List;
import java.util.Optional;

import com.azure.ai.formrecognizer.FormRecognizerClient;
import com.azure.ai.formrecognizer.FormRecognizerClientBuilder;
import com.azure.ai.formrecognizer.models.FormRecognizerOperationResult;
import com.azure.ai.formrecognizer.models.RecognizedForm;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.polling.SyncPoller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.pegasus.form.processor.ProStretchProcessor;
import com.pegasus.form.processor.WilsonGarmentProcessor;
import com.pegasus.form.processor.FormProcessor;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function2 {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/form?filename=1466_002.pdf"
     */
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
                .credential(new AzureKeyCredential(Configuration.FORM_API_KEY))
                .endpoint(Configuration.FORM_ENDPOINT)
                .buildClient();
        
        final String query = request.getQueryParameters().get("filename");
        final String filename = request.getBody().orElse(query);

        String formUrl = "https://intelliform.blob.core.windows.net/analyze/" + filename;
        String modelId = Configuration.getModelId(filename);
        
        SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> recognizeFormPoller =
            formRecognizerClient.beginRecognizeCustomFormsFromUrl(modelId, formUrl);

        List<RecognizedForm> recognizedForms = recognizeFormPoller.getFinalResult();

        FormProcessor processor = null;
        if (filename.startsWith("1464")) {
            processor = new ProStretchProcessor(recognizedForms);
            processor.process();
        } else  if (filename.startsWith("1466")) {
            processor = new WilsonGarmentProcessor(recognizedForms);
            processor.process();
        }
        
        String entity = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            entity = mapper.writeValueAsString(processor.getPackingList());
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
