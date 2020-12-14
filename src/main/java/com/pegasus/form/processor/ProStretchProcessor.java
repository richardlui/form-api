package com.pegasus.form.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.azure.ai.formrecognizer.models.FormPage;
import com.azure.ai.formrecognizer.models.FormTable;
import com.azure.ai.formrecognizer.models.FormTableCell;
import com.azure.ai.formrecognizer.models.RecognizedForm;
import com.pegasus.form.model.Container;
import com.pegasus.form.model.LineItem;
import com.pegasus.form.model.PackingList;

public class ProStretchProcessor extends FormProcessorV2 {

    private static final String ISSUE_DATE_KEY = "issueDate";
    private static final String SELLER_NAME_KEY = "sellerName";
    private static final String BUYER_NAME_KEY = "buyerName";
    private static final String INVOICE_NUM_KEY = "invoiceNumber";
    
    public ProStretchProcessor() {
        super();
    }

    public ProStretchProcessor(String result) {
        super(result);
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
    public Container extractLineItems(List<RecognizedForm> forms,
            Map<String, String> labels) {
        Container container = new Container();
        for (int i = 0; i < forms.size(); i++) {
            RecognizedForm form = forms.get(i);
            // Take the first page only
            FormPage page = form.getPages().get(0);
            // Scan thru the first table
            FormTable table = page.getTables().get(0);
            int currentRow = -1;
            Boolean newRow = true;
            int skipRowIndex = -1;
            PackingList lineItem = null;
            for (FormTableCell cell : table.getCells()) {
                // skip first line which is the header
                if (cell.getRowIndex() == 0) {
                    continue;
                }
                if (currentRow != cell.getRowIndex()) {
                    newRow = true;
                    // Add previous item
                    if (lineItem != null) {
                        container.getPackingList().add(lineItem);
                    }
                    // Create a new item for the new row
                    lineItem = new PackingList();
                    lineItem.setIssueDate(labels.get(ISSUE_DATE_KEY));
                    lineItem.setBuyerName(labels.get(BUYER_NAME_KEY));
                    lineItem.setSellerName(labels.get(SELLER_NAME_KEY));
                    lineItem.setInvoiceDate(labels.get(ISSUE_DATE_KEY));
                    lineItem.setInvoiceNumber(labels.get(INVOICE_NUM_KEY));
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
            container.getPackingList().add(lineItem);
        }
        return container;
    }
    
    private void splitPurchaseOrder(PackingList lineItem, String text) {
        StringBuilder sb = new StringBuilder();
        int index = text.indexOf(" ");
        String po = text.substring(0, index);
        String desc = text.substring(index+1, text.length());
        sb.append("================================================================\n");
        sb.append("PO: ").append(po).append("\n");
        sb.append("Item No: ").append(desc).append("\n");
        lineItem.setOrderNumber(po);
        lineItem.setMaterialReferenceNumber(desc);
        //lineItem.withPoNumber(po)
        //    .withItemNumber(desc);
        System.out.println(sb.toString());
    }
    
    private void processCell(PackingList lineItem, FormTableCell cell) {
        switch(cell.getColumnIndex()) {
            case 0:
                lineItem.setOrderNumber(cell.getText());
                break;
            case 1:
                lineItem.setMaterialReferenceNumber(cell.getText());
                break;
            case 2:
                lineItem.setCountryOfOrigin(cell.getText());
                break;
            case 5:
                lineItem.setMaterialDescription(cell.getText());
                break;
            case 6:
                lineItem.setMaterialColor(cell.getText());
                break;
            case 10:
                lineItem.setShippedQuantity(cell.getText());
                break;
            case 12:
                lineItem.setShippedUOM(cell.getText());
                break;
            case 14:
                lineItem.setNetWeight(cell.getText());
                break;
            case 15:
                lineItem.setGrossWeight(cell.getText());
                break;
            case 16:
                lineItem.setLength(cell.getText());
                lineItem.setLengthUOM("cm");
                break;
            case 17:
                lineItem.setWidth(cell.getText());
                lineItem.setWidthUOM("cm");
                break;
            case 18:
                lineItem.setHeight(cell.getText());
                lineItem.setHeightUOM("cm");
                break;
            default:
                break;
        }
    }
}
