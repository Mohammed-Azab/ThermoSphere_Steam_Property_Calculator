package steamTables;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DataBase {
    private double[][] compressedLiquid = new double[114][6];
    private double[][] saturatedTableT = new double[77][13];
    private double[][] saturatedTableP = new double[75][13];
    private double[][] superHeatedTable = new double[523][6];
    private final String excelSatT = "Saturated.xlsx";
    private final String excelSatP = "Saturated.xlsx";
    private final String excelSuperHeated = "SuperHeated.xlsx";
    private final String excelCompressedLiquid = "CompressedLiquid.xlsx";

    public DataBase() {
        setTables();
    }

    private void setTables() {
        setCompressedLiquidTable();
        setSaturatedTableT();
        setSaturatedTableP();
        setSuperHeatedTable();
    }

    private void setCompressedLiquidTable() {
        readExcelFile(excelCompressedLiquid, compressedLiquid, 0, 0);
    }

    private void setSaturatedTableT() {
        readExcelFile(excelSatT, saturatedTableT, 78, 0); // From row 79 (index 78)
    }

    private void setSaturatedTableP() {
        readExcelFile(excelSatP, saturatedTableP, 0, 77); // Up to row 77 (index 76 inclusive)
    }

    private void setSuperHeatedTable() {
        readExcelFile(excelSuperHeated, superHeatedTable, 0, 0);
    }

    /**
     * Reads data from an Excel file and fills the given table.
     *
     * @param filePath  Path to the Excel file
     * @param table     Target 2D array to fill
     * @param startRow  Start row index (inclusive)
     * @param endRow    End row index (exclusive), 0 means read all rows
     */
    private void readExcelFile(String filePath, double[][] table, int startRow, int endRow) {
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
            int rowCount = sheet.getPhysicalNumberOfRows();

            if (endRow == 0 || endRow > rowCount) {
                endRow = rowCount; // Read till the end if endRow is not specified
            }

            for (int i = startRow; i < endRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                for (int j = 0; j < table[i - startRow].length; j++) {
                    Cell cell = row.getCell(j);
                    if (cell == null) continue;

                    table[i - startRow][j] = getNumericCellValue(cell);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Safely retrieves a numeric value from a cell, handling different cell types.
     *
     * @param cell The Excel cell
     * @return Numeric value of the cell
     */
    private double getNumericCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            default:
                return 0.0;
        }
    }
}
