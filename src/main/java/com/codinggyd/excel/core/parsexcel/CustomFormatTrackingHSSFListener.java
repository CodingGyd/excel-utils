package com.codinggyd.excel.core.parsexcel;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.record.FormatRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
 
/**
 * <pre>
 * 类名:  CustomFormatTrackingHSSFListener.java
 * 包名:  com.codinggyd.excel.core.parsexcel
 * 描述:  修改POI解析时,其中的日期格式指定为yyyy/mm/dd， 覆盖默认的m/d/y格式
 * 		 参考POI官网<a href="https://poi.apache.org/">https://poi.apache.org/</a>
 * 
 * 作者:  guoyd
 * 日期:  2017年11月26日
 *
 * Copyright @ 2017 Corpration Name
 * </pre>
 */
public class CustomFormatTrackingHSSFListener implements HSSFListener {
	private static POILogger logger = POILogFactory.getLogger(CustomFormatTrackingHSSFListener.class);
	private final HSSFListener _childListener;
	private final HSSFDataFormatter _formatter;
	private final NumberFormat _defaultFormat;
	private final Map<Integer, FormatRecord> _customFormatRecords = new HashMap<Integer, FormatRecord>();
	private final List<ExtendedFormatRecord> _xfRecords = new ArrayList<ExtendedFormatRecord>();


	public CustomFormatTrackingHSSFListener(HSSFListener childListener) {
		this(childListener, LocaleUtil.getUserLocale());
	}


	public CustomFormatTrackingHSSFListener(
			HSSFListener childListener, Locale locale) {
		_childListener = childListener;
		_formatter = new HSSFDataFormatter(locale);
		_defaultFormat = NumberFormat.getInstance(locale);
	}

	protected int getNumberOfCustomFormats() {
		return _customFormatRecords.size();
	}

	protected int getNumberOfExtendedFormats() {
		return _xfRecords.size();
	}


	@Override
    public void processRecord(Record record) {
		// Handle it ourselves
		processRecordInternally(record);

		// Now pass on to our child
		_childListener.processRecord(record);
	}

	
	public void processRecordInternally(Record record) {
		if (record instanceof FormatRecord) {
			FormatRecord fr = (FormatRecord) record;
			_customFormatRecords.put(Integer.valueOf(fr.getIndexCode()), fr);
		}
		if (record instanceof ExtendedFormatRecord) {
			ExtendedFormatRecord xr = (ExtendedFormatRecord) record;
			_xfRecords.add(xr);
		}
	}

	
	public String formatNumberDateCell(CellValueRecordInterface cell) {
		double value;
//		BigDecimal bigDecimal = null;
		if (cell instanceof NumberRecord) {
			value = ((NumberRecord) cell).getValue();
//			bigDecimal = new BigDecimal(((NumberRecord) cell).getValue());
		} else if (cell instanceof FormulaRecord) {
			value = ((FormulaRecord) cell).getValue();
		} else {
			throw new IllegalArgumentException("Unsupported CellValue Record passed in " + cell);
		}
		java.text.NumberFormat nf = java.text.NumberFormat.getInstance();   
		nf.setGroupingUsed(false);  
 //		String tem  = bigDecimal.toPlainString();
		// Get the built in format, if there is one
		int formatIndex = getFormatIndex(cell);
		String formatString = getFormatString(cell);

		if (formatString == null) {
			return _defaultFormat.format(value);
		}
		if(formatString.equals("m/d/yy")){
    	    formatString = "yyyy/mm/dd";
    	}
		
		
		// Format, using the nice new
		// HSSFDataFormatter to do the work for us
		return _formatter.formatRawCellContents(value, formatIndex, formatString);
	}

	
	public String getFormatString(int formatIndex) {
		String format = null;
		if (formatIndex >= HSSFDataFormat.getNumberOfBuiltinBuiltinFormats()) {
			FormatRecord tfr = _customFormatRecords.get(Integer.valueOf(formatIndex));
			if (tfr == null) {
				logger.log( POILogger.ERROR, "Requested format at index " + formatIndex
						+ ", but it wasn't found");
			} else {
				format = tfr.getFormatString();
			}
		} else {
			format = HSSFDataFormat.getBuiltinFormat((short) formatIndex);
		}
		return format;
	}

	
	public String getFormatString(CellValueRecordInterface cell) {
		int formatIndex = getFormatIndex(cell);
		if (formatIndex == -1) {
			// Not found
			return null;
		}
		return getFormatString(formatIndex);
	}

	
	public int getFormatIndex(CellValueRecordInterface cell) {
		ExtendedFormatRecord xfr = _xfRecords.get(cell.getXFIndex());
		if (xfr == null) {
			logger.log( POILogger.ERROR, "Cell " + cell.getRow() + "," + cell.getColumn()
					+ " uses XF with index " + cell.getXFIndex() + ", but we don't have that");
			return -1;
		}
		return xfr.getFormatIndex();
	}
}
