/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.astek.internal.service;

import fr.astek.internal.bean.Achat;
import fr.astek.internal.bean.Client;
import fr.astek.internal.bean.User;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

/**
 *
 * @author dlebert
 */
public class InvoiceGenerator {

        private static final HSSFWorkbook wb = new HSSFWorkbook();
        private static final HSSFSheet sheet = wb.createSheet("invoice");
    
    public static HSSFWorkbook generateInvoice(User user, Client client, List<Achat> achats) {

        
        
        HSSFCellStyle cellStyle = wb.createCellStyle();
        HSSFFont bold = wb.createFont();
        bold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        

        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("Facture de " + user.getLogin());
        sheet.addMergedRegion(new CellRangeAddress(0,0,0,10));
       
        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue("A l'intention de  " + client.getRaisonSociale());
        sheet.addMergedRegion(new CellRangeAddress(1,1,0,10));
        
        cellStyle.setFont(bold);
        row = sheet.createRow(3);
        cell = row.createCell(2);
        cell.setCellValue("Produit");
        CellRangeAddress region = CellRangeAddress.valueOf("C4:E4");
        sheet.addMergedRegion(region); 
        cell.setCellStyle(cellStyle);
        cell = row.createCell(5);

        cellStyle = wb.createCellStyle();
        cellStyle.setFont(bold);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cell.setCellValue("Quantité");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(6);
        cell.setCellValue("Prix Unitaire");
        cell.setCellStyle(cellStyle);
        sheet.addMergedRegion(new CellRangeAddress(3,3,6,7));
        cell = row.createCell(8);
        cell.setCellValue("Total");
        cell.setCellStyle(cellStyle);
        sheet.addMergedRegion(new CellRangeAddress(3,3,8,9));
        
        int i = 5;
        for (Achat achat : achats){
            
            row = sheet.createRow(i);
            cell = row.createCell(2);
            cell.setCellValue(achat.getProduit());
            sheet.addMergedRegion(new CellRangeAddress(i,i,2,4));
            cell = row.createCell(5);
            cell.setCellValue(achat.getQuantite());
            cell = row.createCell(6);
            cell.setCellValue(achat.getPrixUnitaire());
            sheet.addMergedRegion(new CellRangeAddress(i,i,6,7));
            cell = row.createCell(8);
            cell.setCellFormula("F"+ (i+1) + "*G" + (i+1) );
            sheet.addMergedRegion(new CellRangeAddress(i,i,8,9));
            
            i++;
        }
        i+= 2;
        row = sheet.createRow(i);
        cell = row.createCell(7);
        cellStyle = wb.createCellStyle();
        cellStyle.setFont(bold);
        cell.setCellValue("Total : ");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(8);
        cell.setCellFormula("SUM(I5:I" + (i) +")" );
        sheet.addMergedRegion(new CellRangeAddress(i,i,8,9));
        
        return wb;
    }
    
    private static void createBorder(CellRangeAddress region, boolean top, boolean right, boolean bottom, boolean left){
        
        if (top){
            RegionUtil.setBorderTop( CellStyle.BORDER_MEDIUM, region, sheet, wb );
        }
        
        if (right){
            RegionUtil.setBorderRight( CellStyle.BORDER_MEDIUM,region, sheet, wb );
        }
        if (bottom){
            RegionUtil.setBorderBottom( CellStyle.BORDER_MEDIUM,region, sheet, wb );
        }
        if (left){
            RegionUtil.setBorderLeft( CellStyle.BORDER_MEDIUM,region, sheet, wb );
        }

        
    }
    
    
}
