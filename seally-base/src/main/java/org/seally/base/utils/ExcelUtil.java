package org.seally.base.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSON;

/**
 * @Date 2018年12月04日
 * @author 邓宁城
 * @Description excel常用处理工具类
 * @return
 */
public class ExcelUtil {
	
	/**默认列宽**/
	public static final int DEFAULT_COLUMN_WIDTH = 3000;
	/**默认日期格式化格式**/
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * @Date 2018年12月5日
	 * @author 邓宁城
	 * @Description 设置指定列号的列宽
	 * @param sheet 当前工作簿
	 * @param content 当前列内容（改参数与固定列宽width二选一传递，优先取固定列宽width）
	 * @param colIndex 当前列序号
	 * @param width 列宽（如果不传递则默认根据content参数自适应列宽，如果content未传递或是传递为空串则默认ExcelUtil.DEFAULT_COLUMN_WIDTH）
	 */
	public static void setColumnWidth(Sheet sheet,String content,int colIndex,Integer width){
		sheet.setColumnWidth(colIndex, null == width ? ((null == content || content.trim().length() == 0) ? ExcelUtil.DEFAULT_COLUMN_WIDTH : content.getBytes().length*256) : width);
	}
	
	/**
	 * @Date 2018年12月5日
	 * @author 邓宁城
	 * @Description 获取一个指定参数的简单富文本对象（一般用于列标题*号标红着重显示）
	 * @param workbook 当前excel对象
	 * @param markFont 特殊标记内容区域字体
	 * @param otherFont 剩余不标记内容区域字体
	 * @param cellContent 内容文本
	 * @param markStart 特殊标记内容区域开始字符序号（含该序号边界）
	 * @param markEnd 特殊标记内容区域结束字符序号（不含该序号边界）
	 * @return 
	 */
	public static RichTextString initCellRichTextString(Workbook workbook,Font markFont,Font otherFont,String cellContent,int markStart,int markEnd){
		RichTextString richString =(workbook instanceof HSSFWorkbook) ? new HSSFRichTextString(cellContent) : new XSSFRichTextString(cellContent);
		richString.applyFont(markStart,markEnd, markFont); 
		richString.applyFont(markEnd,cellContent.length(), otherFont);
		return richString;
	}
	
	/**
	 * @Date 2018年12月5日
	 * @author 邓宁城
	 * @Description 保存excel文件到本地磁盘
	 * @param workbook	当前excel对象
	 * @param localPath 本地磁盘路径
	 * @param fileName 保存文件名（带完整后缀名.xls/.xlsx）
	 * @throws IOException
	 */
	public static void saveExcelToLocal(Workbook workbook,String localPath,String fileName) throws IOException{
		FileOutputStream fileOut = new FileOutputStream(String.format("%s%s%s", localPath,java.io.File.separator,fileName));
		workbook.write(fileOut);
		fileOut.flush();
		fileOut.close();
	}
	
	/**
	 * @Date 2018年12月6日
	 * @author 邓宁城
	 * @Description 读取本地磁盘的excel文件到Excel文件对象
	 * @param localPath 本地路径
	 * @param fileName 本地excel文件名
	 * @return
	 * @throws IOException
	 */
	public static Workbook readLocalExcel(String localPath,String fileName) throws IOException{
		
		InputStream inputStream = new FileInputStream(new File(String.format("%s%s%s", localPath,java.io.File.separator,fileName)));
		
		return WorkbookFactory.create(inputStream);
	}
	
	 
	/**
	 * @Date 2018年12月5日
	 * @author 邓宁城
	 * @Description 设置某列所填值的可选下拉框选项
	 * @param workbook 当前excel对象
	 * @param sheet	当前工作簿
	 * @param colIndex 出现下拉选项列序号（与开始行序号配合规定出现下拉选项的单元格范围）
	 * @param rowIndex 出现下拉选项列的开始行序号（与列序号配合规定出现下拉选项的单元格范围）
	 * @param options 下拉选项
	 * @remark 使用此方法设置区域目前最多为0-20000 行 options最多20000个
	 */
	public static void setColumOptions(Workbook workbook,Sheet sheet,int colIndex,int rowIndex,List<String> options){
		
		if(null == options || options.isEmpty())	return;
		
		//为列提供下拉选项数目超过253时必须采用特殊方式处理，否则报错
		if(options.size() < 253){//小数目，直接设置
			CellRangeAddressList addressList = new CellRangeAddressList(rowIndex, SpreadsheetVersion.EXCEL97.getLastRowIndex(), colIndex, colIndex);
			DataValidation dataValidation = null;
			if(sheet instanceof HSSFSheet) {//低版本
				dataValidation = new HSSFDataValidation(addressList, DVConstraint.createExplicitListConstraint(options.stream().toArray(String[]::new)));
	        }else {//高版本
	            dataValidation = sheet.getDataValidationHelper().createValidation(new  XSSFDataValidationConstraint(options.stream().toArray(String[]::new)), addressList);  
	            dataValidation.setSuppressDropDownArrow(true);  
	        }  
			dataValidation.setShowErrorBox(true);
			sheet.addValidationData(dataValidation); 
		}else{//数目过多，特殊处理(以隐藏工作簿提供支持)设置
			String hiddenSheet = String.format("HIDE_SHEET_%d", colIndex);
			int endRow = 20000; // 结束行
			Sheet category1Hidden = workbook.createSheet(hiddenSheet); // 创建下拉选项隐藏域
			for (int i = 0, length = options.size(); i < length; i++) { // 循环赋值（为了防止下拉框的行数与隐藏域的行数有重叠导致只能获取到>=选中行数的隐藏域中的选项，将隐藏域加到结束行之后）
				category1Hidden.createRow(endRow + i).createCell(colIndex).setCellValue(options.get(i));
			} 
			Name  name = workbook.createName(); 
			name.setNameName(hiddenSheet); 
			name.setRefersToFormula(hiddenSheet + "!A1:A" + (options.size() + endRow)); // A1:A代表隐藏域创建第?列createCell(?)时。以A1列开始A行数据获取下拉数组
			CellRangeAddressList addressList = new CellRangeAddressList(rowIndex, SpreadsheetVersion.EXCEL2007.getLastRowIndex(), colIndex, colIndex); 
			if(sheet instanceof HSSFSheet) {//低版本
		        DVConstraint constraint = DVConstraint.createFormulaListConstraint(hiddenSheet); 
		        DataValidation validation = new HSSFDataValidation(addressList, constraint); 
		        sheet.addValidationData(validation);
	        }else {//高版本
		      	DataValidationHelper dataValidationHelper = sheet.getDataValidationHelper();
				DataValidationConstraint constraint = dataValidationHelper.createFormulaListConstraint(hiddenSheet);
		        DataValidation validation = dataValidationHelper.createValidation(constraint, addressList);  
		        validation.setSuppressDropDownArrow(true);  
		        validation.setShowErrorBox(true);   
				sheet.addValidationData(validation);
	        }  
			workbook.setSheetHidden(workbook.getSheetIndex(category1Hidden), true);
		}
	}
	
	/**
	 * @Date 2018年12月5日
	 * @author 邓宁城
	 * @Description 获取一个指定参数的单元格备注（一般用于列标题对该列内容进行说明）
	 * @param sheet 当前工作簿
	 * @param content 备注内容
	 * @param lColIndex 备注提示框左上角开始列序号相当于x1
	 * @param lRowIndex 备注提示框左上角开始行序号相当于y1
	 * @param rColIndex 备注提示框右下角开始列序号相当于x2
	 * @param rRowIndex 备注提示框右下角开始行序号相当于y2
	 * @return
	 */
	public static Comment initCellComment(Sheet sheet,String content,short lColIndex,int lRowIndex,short rColIndex,int rRowIndex){
		Comment cellComment = null;
		if(sheet instanceof HSSFSheet){
			cellComment = ((HSSFSheet)sheet).createDrawingPatriarch().createComment(new HSSFClientAnchor(0,0,0,0,lColIndex,lRowIndex,rColIndex,rRowIndex));//创建批注
			cellComment.setString(new HSSFRichTextString(content));//设置批注内容
		}else{
			cellComment = sheet.createDrawingPatriarch().createCellComment(new XSSFClientAnchor(0,0,0,0,lColIndex,lRowIndex,rColIndex,rRowIndex));
			cellComment.setString(new XSSFRichTextString(content));//设置批注内容
		}
		return cellComment;
	}
	
	/**
	 * @Date 2018年12月5日
	 * @author 邓宁城
	 * @Description 设置合并格式的主标题单元格
	 * @param workbook 当前excel对象
	 * @param sheet 当前工作簿
	 * @param titleFont 标题字体
	 * @param rowIndex 主标题所在行号
	 * @param mergeColums 合并列数（一般取正文列数）
	 * @param titleContent 主标题内容
	 */
	public static void setSheetTitleMergeRow(Workbook workbook,Sheet sheet,Font titleFont,int rowIndex, int mergeColums,String titleContent){
		//创建主标题信息行
		Row sheetTitleRow = sheet.createRow(rowIndex);
		//行高
		sheetTitleRow.setHeightInPoints((short)30);
		//合并单元格
		CellRangeAddress titleCellRegion = new CellRangeAddress(rowIndex,rowIndex,0,mergeColums-1);
		sheet.addMergedRegion(titleCellRegion);
		Cell titleCell = sheetTitleRow.createCell(0);
		titleCell.setCellStyle(ExcelUtil.initCellStyle(workbook,HorizontalAlignment.CENTER,VerticalAlignment.CENTER,"@"));
		titleCell.setCellValue(ExcelUtil.initCellRichTextString(workbook, titleFont, titleFont, titleContent, 0, 0));
	}
	
	/**
	 * @Date 2018年12月5日
	 * @author 邓宁城
	 * @Description 获取一个指定参数的字体
	 * @param workbook	当前excel对象
	 * @param colorNo 字体颜色代号
	 * @param fontHeight 字体高度
	 * @param bold 是否加粗
	 * @param fontName 字体名称 ：宋体/楷体
	 * @return
	 */
	public static Font initFont(Workbook workbook,short colorNo,short fontHeight,boolean bold,String fontName){
		Font font = workbook.createFont();
		font.setColor(colorNo);//HSSFFont.COLOR_RED
		font.setFontName(fontName);
		font.setBold(bold);
		font.setFontHeightInPoints(fontHeight);
		return font;
	}
	
	/**
	 * @Date 2018年12月5日
	 * @author 邓宁城
	 * @Description 获取一个指定参数的单元格样式
	 * @param workbook	当前excel对象
	 * @param hAlign 水平对齐方式
	 * @param vAlign 垂直对齐方式
	 * @param format 单元格内容格式
	 * @return
	 */
	public static CellStyle initCellStyle(Workbook workbook,HorizontalAlignment hAlign,VerticalAlignment vAlign,String format){
		CellStyle cellStyle = workbook.createCellStyle();
		DataFormat dataFormat = workbook.createDataFormat();
		cellStyle.setDataFormat(dataFormat.getFormat(format));
		cellStyle.setAlignment(hAlign);
		cellStyle.setVerticalAlignment(vAlign);
		return cellStyle;
	}
	
	/**
	 * @Date 2018年12月6日
	 * @author 邓宁城
	 * @Description 写入数据到excel文件中
	 * @param workbook	当前操作excel对象
	 * @param rowDatas 行数据
	 * @param colKeys 行数据中每行对应的列名，输出文件中数据列顺序和列信息由此决定
	 * @param dateFormat 遇到日期类型的数据，输出String类型的日期格式，不提供使用默认：yyyy-MM-dd HH:mm:ss
	 * @param sheetIndex 写入的工作簿序号，默认0
	 * @param beginRowIndex 写入数据的开始行序号，默认0，如果workbook事先有行标题等数据请指定防止覆盖掉标题行
	 * @param beginColIndex 写入数据的开始列，默认0，如果workbook每列事先有列头等数据请指定防止覆盖掉列头
	 */
	public static void writeDataToExcel(Workbook workbook,List<Map<String,Object>> rowDatas,List<String> colKeys,String dateFormat,Integer sheetIndex,Integer beginRowIndex,Integer beginColIndex){
		
		if(null == rowDatas || rowDatas.isEmpty() || null == colKeys || colKeys.isEmpty())	return;
		
		//校验并设置序号合法性
		sheetIndex = null == sheetIndex ? 0 : (sheetIndex >= workbook.getNumberOfSheets() ? workbook.getNumberOfSheets() : sheetIndex);
		beginColIndex = null == beginColIndex ? 0 : beginColIndex;
		dateFormat = (null == dateFormat || dateFormat.trim().isEmpty()) ? DEFAULT_DATE_FORMAT : dateFormat.trim();
		
		//获取目标工作表
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		Integer rowIndex = null == beginRowIndex ? 0 : beginRowIndex;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		for(Map<String,Object> rowData : rowDatas){
			int colIndex = beginColIndex;
			Row row = sheet.createRow(rowIndex);
			for(String key : colKeys){
				Object value = rowData.get(key);
				Cell cell = row.createCell(colIndex);
				if(null != value){
					cell.setCellValue( (value instanceof Date) ? LocalDateTime.ofInstant(((Date)value).toInstant(), ZoneId.systemDefault()).format(formatter) : value.toString());
				}
				colIndex ++ ;
			}
			rowIndex ++ ;
		}
	}
	
	/**
	 * @Date 2018年12月6日
	 * @author 邓宁城
	 * @Description 读取excel对象中的数据
	 * @param workbook 当前excel对象
	 * @param colKeys 数据列返回键名，顺序与excel中列顺序对应
	 * @param rowIndexKey 行数据Map对应excel中的行号的键名，传递则返回行号，不传递则不返回
	 * @param sheetIndex 读取工作簿索引，默认0
	 * @param beginRowIndex 读取正文数据开始行，默认0 防止有标题行等，因此如果存在标题行时需要传递以便于定位到数据行
	 * @return
	 */
	public static List<Map<String,String>> readExcelToData(Workbook workbook,List<String> colKeys,String rowIndexKey,Integer sheetIndex,Integer beginRowIndex){
		 
		List<Map<String,String>> rowDatas = new ArrayList<>();
		if(null == workbook)	return rowDatas;
		 
		sheetIndex = null == sheetIndex ? 0 : (sheetIndex >= workbook.getNumberOfSheets() ? workbook.getNumberOfSheets() : sheetIndex);
		beginRowIndex = null == beginRowIndex ? 0 : beginRowIndex;
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		
        for(int rowIndex = beginRowIndex;rowIndex <= sheet.getLastRowNum(); rowIndex++){
        	Row row = sheet.getRow(rowIndex);
        	if(null == row){
        		continue;//空行不解析
        	}
        	boolean emptyRow = true;
        	Map<String,String> rowData = new HashMap<>();
        	for(int colIndex = 0;colIndex < colKeys.size() ; colIndex++){
        		Cell cell = row.getCell(colIndex);
        		if(null == cell)	continue;//空单元格不解析
        		String stringCellValue = cell.getStringCellValue();
        		if(null == stringCellValue || stringCellValue.trim().length() == 0)	continue;
        		emptyRow = false;
        		rowData.put(colKeys.get(colIndex), stringCellValue);
        	}
        	if(emptyRow)	continue;//防止不是空行，但是没有一个数据也不添加到
        	if(null != rowIndexKey){//如果传递行号键名则保存行号，以便于后面业务处理数据时能够对应到文件行
        		rowData.put(rowIndexKey, rowIndex+"");
        	}
        	rowDatas.add(rowData);
        }
        return rowDatas;
	}
	
	
	
	/**
	 * @Date 2018年12月5日
	 * @author 邓宁城
	 * @Description 创建一个excel模板示例代码 isHVersion=true则创建高版本.xlsx，否则创建低版本.xls
	 * @param sheetName 工作簿名称
	 * @param sheetTitle 主标题
	 * @param colTitles 列标题
	 * @param isHVersion 是否是高版本 true=是  false=不是
	 * @return
	 */
	public static Workbook createTemplateExcelExample(String sheetName,String sheetTitle,List<String> colTitles,boolean isHVersion){
		
		//创建excel对象，需用调用方自己确定
		Workbook workbook = isHVersion ? new XSSFWorkbook() : new HSSFWorkbook();
		
		//创建工作簿示例
		Sheet sheet = workbook.createSheet(sheetName);
		
		//设置工作簿标题行示例
		ExcelUtil.setSheetTitleMergeRow(workbook, sheet,ExcelUtil.initFont(workbook,Font.COLOR_NORMAL, (short)16,true,"宋体"),0, colTitles.size(), sheetTitle);
		
		//创建内容标题行示例
		Row titleRow = sheet.createRow(1);
		
		//创建内容标题行示例
		for(int colIndex = 0 ;colIndex < colTitles.size() ; colIndex++){
			//创建标题行每列上的单元格
			Cell cell = titleRow.createCell(colIndex);
			
			//获取标题内容
			String titleContent = colTitles.get(colIndex);
			
			//设置标题内容
			if(titleContent.startsWith("*")){//设置标题着重符号（红*）示例
				cell.setCellValue(ExcelUtil.initCellRichTextString(workbook, ExcelUtil.initFont(workbook,Font.COLOR_RED, (short)14,true,"宋体"), ExcelUtil.initFont(workbook,HSSFFont.COLOR_NORMAL, (short)14,true,"宋体"), titleContent, 0, 1));
			}else{
				cell.setCellValue(ExcelUtil.initCellRichTextString(workbook, ExcelUtil.initFont(workbook,Font.COLOR_RED, (short)14,true,"宋体"), ExcelUtil.initFont(workbook,HSSFFont.COLOR_NORMAL, (short)14,true,"宋体"), titleContent, 0, 0));
			}
			
			//对第一列标题设置批注示例
			if(colIndex == 0){
				cell.setCellComment(ExcelUtil.initCellComment(sheet, "这是第一列的注释，请认真阅读", (short)0, 1,(short)4, 6));
			}
			
			//对第一列标题设置固定列宽其它列设置自动列宽示例
			if(colIndex == 0){
				ExcelUtil.setColumnWidth(sheet, null, colIndex,3000);
			}else{//设置自动列宽示例
				ExcelUtil.setColumnWidth(sheet, titleContent, colIndex, null);
			}
			
			//对第每一列标题设置居中样式、且设置为字符格式示例
			cell.setCellStyle(ExcelUtil.initCellStyle(workbook,HorizontalAlignment.CENTER,VerticalAlignment.CENTER,"@"));
			
			//对其他列设置超长下拉项示例（setColumOptions方法内部已经处理下拉项过多的问题，调用方无需关心）
			List<String> options = new ArrayList<>(java.util.Arrays.asList("广东","广西","湖北"));
			ExcelUtil.setColumOptions(workbook, sheet, colIndex,titleRow.getRowNum()+1,options);
			
		}
		return workbook;
	}
	
	
	public static void main(String[] args) throws IOException {
		//数据列键
		List<String> colKeys = Arrays.asList("province","city","money","date");
		
		//创建低版本普通模板示例
		Workbook xlsTemplateExcel = ExcelUtil.createTemplateExcelExample("财务调查汇总表","XXX区域财务调查",new ArrayList<>(Arrays.asList("*省份（必填）","市区（选填）","*年度收益额汇总（必填）","统计日期（选填）")),false);
		//创建低版本普通模板示例
		Workbook xlsxTemplateExcel = ExcelUtil.createTemplateExcelExample("财务调查汇总表","XXX区域财务调查",new ArrayList<>(Arrays.asList("*省份（必填）","市区（选填）","*年度收益额汇总（必填）","统计日期（选填）")),true);
		
		//测试数据
		List<Map<String,Object>> rowDatas = new ArrayList<>();
		Map<String,Object> row1 = new HashMap<>();row1.put("province", "广东");row1.put("city", "深圳市");row1.put("money", 40000000);row1.put("date", new Date());rowDatas.add(row1);
		Map<String,Object> row2 = new HashMap<>();row2.put("province", "湖北");row2.put("city", "武汉市");row2.put("money", 30000000);row2.put("date", new Date());rowDatas.add(row2);
		Map<String,Object> row3 = new HashMap<>();row3.put("province", "湖南");row3.put("city", "长沙市");row3.put("money", 20000000);row3.put("date", new Date());rowDatas.add(row3);
		
		//写数据到示例
		ExcelUtil.writeDataToExcel(xlsTemplateExcel, rowDatas, colKeys,DEFAULT_DATE_FORMAT, 0, 2, 0);
		ExcelUtil.writeDataToExcel(xlsxTemplateExcel, rowDatas, colKeys,DEFAULT_DATE_FORMAT, 0, 2, 0);
		
		//保存数据到本地
		ExcelUtil.saveExcelToLocal(xlsTemplateExcel, "D:/", "xlsTest.xls");
		ExcelUtil.saveExcelToLocal(xlsxTemplateExcel, "D:/", "xlsxTest.xlsx");
		
		//由本地读取excel读数据示例
		Workbook xlsWorkbook = ExcelUtil.readLocalExcel("D:/", "xlsTest.xls");
		Workbook xlsxWorkbook = ExcelUtil.readLocalExcel("D:/", "xlsxTest.xlsx");
		
		//解析excel数据示例
		List<Map<String, String>> readExcelToData1 = ExcelUtil.readExcelToData(xlsWorkbook, colKeys,"rk",0, 2);
		System.out.println(JSON.toJSONString(readExcelToData1));
		List<Map<String, String>> readExcelToData2 = ExcelUtil.readExcelToData(xlsxWorkbook, colKeys,"rk",0, 2);
		System.out.println(JSON.toJSONString(readExcelToData2));
		
	}
}
