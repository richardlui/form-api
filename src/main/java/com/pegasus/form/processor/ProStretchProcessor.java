package com.pegasus.form.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.azure.ai.formrecognizer.models.FormPage;
import com.azure.ai.formrecognizer.models.FormTable;
import com.azure.ai.formrecognizer.models.FormTableCell;
import com.azure.ai.formrecognizer.models.RecognizedForm;
import com.pegasus.form.model.LineItem;

public class ProStretchProcessor extends FormProcessor {

    public ProStretchProcessor(String result) {
        super(result);
    }
    
    public ProStretchProcessor(List<RecognizedForm> forms) {
        super(forms);
    }
    
    @Override
    public Map<String, String> extractLabel(List<RecognizedForm> forms) {
        StringBuilder sb = new StringBuilder();
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < forms.size(); i++) {
            RecognizedForm form = forms.get(i);
            sb.append("Page " + (i+1)).append("\n");
            System.out.printf("----------- Recognized custom form info for page %d -----------%n", i);
            //System.out.printf("Form type: %s%n", form.getFormType());
            form.getFields().forEach((label, formField) -> {
                System.out.printf("Field %s has value %s with confidence score of %f.%n", label,
                    formField.getValueData().getText(),
                    formField.getConfidence());
                map.put(label, formField.getValueData().getText());
                sb.append(label).append(": ").append(formField.getValueData().getText()).append("\n");
            });
        }
        System.out.println("Extracted labels: " + sb.toString());
        return map;
    }
    
    @Override
    public List<LineItem> extractLineItems(List<RecognizedForm> forms) {
        List<LineItem> list = new ArrayList<>();
        for (int i = 0; i < forms.size(); i++) {
            RecognizedForm form = forms.get(i);
            // print table details
            for (FormPage page : form.getPages()) {
                // Ignore first table which is shipping info
                // read second table only
                if (page.getTables().size() > 1) {
                    FormTable table = page.getTables().get(1);
                    int currentRow = -1;
                    Boolean newRow = true;
                    int skipRowIndex = -1;
                    LineItem lineItem = null;
                    for (FormTableCell cell : table.getCells()) {
                        if (currentRow != cell.getRowIndex()) {
                            newRow = true;
                            // Add previous item
                            if (lineItem != null) {
                                list.add(lineItem);
                            }
                            // Create a new item for the new row
                            lineItem = new LineItem();
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
                        
                        processCell(lineItem, cell);
                        
                    }
                    // add the last item
                    list.add(lineItem);
                }
            }
        }
        return list;
    }
    
    private void splitPurchaseOrder(LineItem lineItem, String text) {
        StringBuilder sb = new StringBuilder();
        int index = text.indexOf(" ");
        String po = text.substring(0, index);
        String desc = text.substring(index+1, text.length());
        sb.append("================================================================\n");
        sb.append("PO: ").append(po).append("\n");
        sb.append("Item No: ").append(desc).append("\n");
        lineItem.withPoNumber(po)
            .withItemNumber(desc);
        System.out.println(sb.toString());
    }
    
    private void processCell(LineItem lineItem, FormTableCell cell) {
        StringBuilder sb = new StringBuilder();
        switch(cell.getColumnIndex()) {
            case 0:
                splitPurchaseOrder(lineItem, cell.getText());
                break;
            case 3:
                sb.append("Description: ").append(cell.getText()).append("\n");
                lineItem.setDescription(cell.getText());
                break;
            case 4:
                lineItem.setQuantity(cell.getText());
                sb.append("Quantity: ").append(cell.getText()).append("\n");
                break;
            case 8:
                lineItem.setNetWeight(cell.getText());
                sb.append("Net Weight: ").append(cell.getText()).append("\n");
                break;
            default:
                break;
        }
        System.out.println(sb.toString());
    }
}
