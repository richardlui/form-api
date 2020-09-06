package com.pegasus.form.processor;

import java.util.List;

import com.azure.ai.formrecognizer.models.FormPage;
import com.azure.ai.formrecognizer.models.FormTable;
import com.azure.ai.formrecognizer.models.FormTableCell;
import com.azure.ai.formrecognizer.models.RecognizedForm;

public class ProStretchProcessor extends FormProcessor {

    public ProStretchProcessor(String result) {
        super(result);
    }
    
    public ProStretchProcessor(List<RecognizedForm> forms) {
        super(forms);
    }
    
    @Override
    public String extractLabel(List<RecognizedForm> forms) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < forms.size(); i++) {
            RecognizedForm form = forms.get(i);
            sb.append("Page " + (i+1)).append("\n");
            System.out.printf("----------- Recognized custom form info for page %d -----------%n", i);
            //System.out.printf("Form type: %s%n", form.getFormType());
            form.getFields().forEach((label, formField) -> {
                System.out.printf("Field %s has value %s with confidence score of %f.%n", label,
                    formField.getValueData().getText(),
                    formField.getConfidence());
                sb.append(label).append(": ").append(formField.getValueData().getText()).append("\n");
            });
        }
        return sb.toString();
    }
    
    @Override
    public String extractLineItems(List<RecognizedForm> forms) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < forms.size(); i++) {
            RecognizedForm form = forms.get(i);
            // print table details
            for (FormPage page : form.getPages()) {
                // Ignore first table which is shipping info
                // read second table only
                if (page.getTables().size() > 1) {
                    FormTable table = page.getTables().get(1);
                    int currentRow = 0;
                    Boolean newRow = true;
                    int skipRowIndex = -1;
                    for (FormTableCell cell : table.getCells()) {
                        if (currentRow != cell.getRowIndex()) {
                            newRow = true;
                        } else {
                            newRow =false;
                        }
                        currentRow = cell.getRowIndex();
                        
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
            }
        }
        return sb.toString();
    }
    
    private void splitPurchaseOrder(StringBuilder sb, String text) {
        int index = text.indexOf(" ");
        String po = text.substring(0, index);
        String desc = text.substring(index+1, text.length());
        sb.append("================================================================\n");
        sb.append("PO: ").append(po).append("\n");
        sb.append("Item No: ").append(desc).append("\n");
    }
    
    private void processCell(StringBuilder sb, FormTableCell cell) {
        switch(cell.getColumnIndex()) {
            case 0:
                splitPurchaseOrder(sb, cell.getText());
                break;
            case 3:
                sb.append("Description: ").append(cell.getText()).append("\n");
                break;
            case 4:
                sb.append("Quantity: ").append(cell.getText()).append("\n");
                break;
            case 8:
                sb.append("Net Weight: ").append(cell.getText()).append("\n");
                break;
            default:
                
        }
    }
}
