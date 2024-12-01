package steamTables;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class DataBase {
    private final double[][] compressedLiquid = new double[114][6];
    private final double[][] saturatedTableT = new double[77][13];
    private final double[][] saturatedTableP = new double[75][13];
    private final double[][] superHeatedTable = new double[523][6];

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
        String excelCompressedLiquid = "CompressedLiquid.xlsx";
        readExcelFile(excelCompressedLiquid, compressedLiquid, 0, 0);
    }

    private void setSaturatedTableT() {
        String excelSatT = "Saturated.xlsx";
        readExcelFile(excelSatT, saturatedTableT, 79, 0); // From row 79 (index 78)
    }

    private void setSaturatedTableP() {
        String excelSatP = "Saturated.xlsx";
        readExcelFile(excelSatP, saturatedTableP, 0, 77); // Up to row 77 (index 76 inclusive)
    }

    private void setSuperHeatedTable() {
        String excelSuperHeated = "SuperHeated.xlsx";
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
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (is == null) {
                throw new FileNotFoundException("File not found in resources: " + filePath);
            }

            try (Workbook workbook = new XSSFWorkbook(is)) {
                Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
                int rowCount = sheet.getPhysicalNumberOfRows();

                if (endRow == 0 || endRow > rowCount) {
                    endRow = rowCount; // Read till the end if endRow is not specified
                }

                for (int i = startRow; i < endRow; i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) break;

                    for (int j = 0; j < table[i - startRow].length; j++) {
                        Cell cell = row.getCell(j);
                        if (cell == null) continue;

                        table[i - startRow][j] = getNumericCellValue(cell);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading Excel file: " + filePath);
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Table array size does not match the rows/columns in the Excel file.");
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

    public double[][] getCompressedLiquidTable() {
        return compressedLiquid;
    }

    public double[][] getSaturatedTableT() {
        return saturatedTableT;
    }

    public double[][] getSaturatedTableP() {
        return saturatedTableP;
    }

    public double[][] getSuperHeatedTable() {
        return superHeatedTable;
    }
}
