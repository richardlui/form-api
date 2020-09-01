package com.pegasus.form;

import java.util.List;
import java.util.Optional;

import com.azure.ai.formrecognizer.FormRecognizerClient;
import com.azure.ai.formrecognizer.FormRecognizerClientBuilder;
import com.azure.ai.formrecognizer.models.FormRecognizerOperationResult;
import com.azure.ai.formrecognizer.models.FormTable;
import com.azure.ai.formrecognizer.models.FormTableCell;
import com.azure.ai.formrecognizer.models.RecognizedForm;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.polling.SyncPoller;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

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

        String formUrl = "https://scan5354.blob.core.windows.net/1466/" + filename;
        String modelId = Configuration.getModelId(filename);
        
        SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> recognizeFormPoller =
            formRecognizerClient.beginRecognizeCustomFormsFromUrl(modelId, formUrl);

        List<RecognizedForm> recognizedForms = recognizeFormPoller.getFinalResult();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < recognizedForms.size(); i++) {
            RecognizedForm form = recognizedForms.get(i);
            sb.append("Page " + (i+1)).append("\n");
            System.out.printf("----------- Recognized custom form info for page %d -----------%n", i);
            //System.out.printf("Form type: %s%n", form.getFormType());
            form.getFields().forEach((label, formField) -> {
                System.out.printf("Field %s has value %s with confidence score of %f.%n", label,
                    formField.getValueData().getText(),
                    formField.getConfidence());
                sb.append(label).append(": ").append(formField.getValueData().getText()).append("\n");
            });
            // print table details
            form.getPages().forEach((page)-> {
                for (FormTable table : page.getTables()) {
                    int rowCount = table.getRowCount();
                    int currentRow = 0;
                    int currentColumn = 0;
                    Boolean newRow = true;
                    int skipRowIndex = -1;
                    for (FormTableCell cell : table.getCells()) {
                        if (currentRow != cell.getRowIndex()) {
                            newRow = true;
                            currentColumn = 0;
                        } else {
                            newRow =false;
                        }
                        currentRow = cell.getRowIndex();
                        currentColumn = cell.getColumnIndex();
                        
                        // skip first line which is the header
                        if (cell.getRowIndex() == 0) {
                            continue;
                        }
                        // skip any blank row
                        if (newRow && cell.getColumnIndex() != 0) {
                            skipRowIndex = currentRow;
                            continue;
                        }
                        if (cell.getRowIndex() == skipRowIndex) {
                            continue;
                        }
                        
                        // now get the first 6 columns data
                        processCell(sb, cell);
                        
                    }
                }
            });
            
        }
        return request.createResponseBuilder(HttpStatus.OK)
                .body(sb.toString())
                .build();
        
    }
    
    private void splitPurchaseOrder(StringBuilder sb, String text) {
        int index = text.indexOf(" ");
        String po = text.substring(0, index);
        String desc = text.substring(index+1, text.length());
        sb.append("================================================================\n");
        sb.append("PO: ").append(po).append("\n");
        sb.append("Description: ").append(desc).append("\n");
    }

    private void processCell(StringBuilder sb, FormTableCell cell) {
        switch(cell.getColumnIndex()) {
            case 0:
                splitPurchaseOrder(sb, cell.getText());
                
                break;
            
            case 1:
                sb.append("Item No: ").append(cell.getText()).append("\n");
                break;
            case 2:
                sb.append("Color: ").append(cell.getText()).append("\n");
                break;
            case 3:
                sb.append("Quantity: ").append(cell.getText()).append("\n");
                break;
            case 5:
                sb.append("Net Weight: ").append(cell.getText()).append("\n");
                break;
            default:
                
        }
    }
}
