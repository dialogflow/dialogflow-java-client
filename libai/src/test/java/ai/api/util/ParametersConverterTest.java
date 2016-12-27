package ai.api.util;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class ParametersConverterTest {
	
	@Test(expected=ParseException.class)
	public void parseEmptyTimeTest() throws ParseException {
		ParametersConverter.parseTime("");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void parseNullTimeTest() throws ParseException {
		ParametersConverter.parseTime(null);
	}
	
    @Test
    public void parseTimeTest() throws ParseException {
        final String inputTime = "13:17:50";

        final Calendar date = Calendar.getInstance();
        date.setTime(ParametersConverter.parseTime(inputTime));
        final Calendar currentDate = Calendar.getInstance();

        assertEquals(currentDate.get(Calendar.YEAR), date.get(Calendar.YEAR));
        assertEquals(currentDate.get(Calendar.MONTH), date.get(Calendar.MONTH));
        assertEquals(currentDate.get(Calendar.DATE), date.get(Calendar.DATE));
        assertEquals(13, date.get(Calendar.HOUR_OF_DAY));
        assertEquals(17, date.get(Calendar.MINUTE));
        assertEquals(50, date.get(Calendar.SECOND));
    }
    	
	@Test(expected=ParseException.class)
	public void parseEmptyDateTimeTest() throws ParseException {
		ParametersConverter.parseDateTime("");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void parseNullDateTestTime() throws ParseException {
		ParametersConverter.parseDateTime(null);
	}

    @Test
    public void parseDateTimeTest() throws ParseException {
        final String input = "2016-12-21T07:00:00" + new SimpleDateFormat("Z").format(new Date());

        final Calendar date = Calendar.getInstance();
        date.setTime(ParametersConverter.parseDateTime(input));

        assertEquals(2016, date.get(Calendar.YEAR));
        assertEquals(Calendar.DECEMBER, date.get(Calendar.MONTH));
        assertEquals(21, date.get(Calendar.DATE));
        assertEquals(7, date.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, date.get(Calendar.MINUTE));
    }
	
	@Test(expected=ParseException.class)
	public void parseEmptyDateTest() throws ParseException {
		ParametersConverter.parseDate("");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void parseNullDateTest() throws ParseException {
		ParametersConverter.parseDate(null);
	}
	
	@Test
    public void parseDateTest() throws ParseException {
        final String input = "2015-03-21";

        final Calendar date = Calendar.getInstance();
        date.setTime(ParametersConverter.parseDate(input));

        assertEquals(2015, date.get(Calendar.YEAR));
        assertEquals(Calendar.MARCH, date.get(Calendar.MONTH));
        assertEquals(21, date.get(Calendar.DATE));
    }

	@Test(expected=ParseException.class)
	public void parseEmptyPartialDateTest() throws ParseException {
		ParametersConverter.parsePartialDate("");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void parseNullPartialDateTest() throws ParseException {
		ParametersConverter.parsePartialDate(null);
	}
	
	@Test(expected=ParseException.class)
	public void parseIncompletePartialDateTest() throws ParseException {
		ParametersConverter.parsePartialDate("uuuu-uu");
	}
	
	@Test
	public void parseKnownPartialDateTest() throws ParseException {
		PartialDate date = ParametersConverter.parsePartialDate("2015-03-21");
		assertEquals(2015, date.get(Calendar.YEAR));
        assertEquals(Calendar.MARCH, date.get(Calendar.MONTH));
        assertEquals(21, date.get(Calendar.DATE));
	}

    @Test
    public void parsePartialDateTest() throws ParseException {
        // date in format "yyyy-MM-dd"
        final String unknownDate = "1999-05-uu";
        final String unknownMonth = "2005-uu-17";
        final String unknownYear = "uuuu-07-23";
        final String unknownMonthDate = "2008-uu-uu";

        PartialDate date = ParametersConverter.parsePartialDate(unknownDate);

        assertEquals(1999, date.get(Calendar.YEAR).intValue());
        assertEquals(5, date.get(Calendar.MONTH).intValue());
        assertEquals(PartialDate.UNSPECIFIED_VALUE, date.get(Calendar.DATE));
        assertEquals(PartialDate.UNSPECIFIED_VALUE, date.get(Calendar.DAY_OF_WEEK));
        assertEquals(PartialDate.UNSPECIFIED_VALUE, date.get(Calendar.DAY_OF_YEAR));
        assertEquals(unknownDate, date.toString());

        date = ParametersConverter.parsePartialDate(unknownMonth);
        assertEquals(2005, date.get(Calendar.YEAR).intValue());
        assertEquals(PartialDate.UNSPECIFIED_VALUE, date.get(Calendar.MONTH));
        assertEquals(17, date.get(Calendar.DATE).intValue());
        assertEquals(unknownMonth, date.toString());

        date = ParametersConverter.parsePartialDate(unknownYear);
        assertEquals(PartialDate.UNSPECIFIED_VALUE, date.get(Calendar.YEAR));
        assertEquals(7, date.get(Calendar.MONTH).intValue());
        assertEquals(23, date.get(Calendar.DATE).intValue());
        assertEquals(unknownYear, date.toString());

        date = ParametersConverter.parsePartialDate(unknownMonthDate);
        assertEquals(2008, date.get(Calendar.YEAR).intValue());
        assertEquals(PartialDate.UNSPECIFIED_VALUE, date.get(Calendar.MONTH));
        assertEquals(PartialDate.UNSPECIFIED_VALUE, date.get(Calendar.DATE));
        assertEquals(unknownMonthDate, date.toString());
    }

	@Test(expected=NumberFormatException.class)
	public void parseEmptyIntegerTest() throws NumberFormatException {
		ParametersConverter.parseInteger("");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void parseNullIntegerTest() throws NumberFormatException {
		ParametersConverter.parseInteger(null);
	}
	
	@Test
	public void parseIntegerTest() throws NumberFormatException {
		assertEquals(10, ParametersConverter.parseInteger("10"));
	}
	
	@Test(expected=NumberFormatException.class)
	public void parseEmptyFloatTest() throws NumberFormatException {
		ParametersConverter.parseFloat("");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void parseNullFloatTest() throws NumberFormatException {
		ParametersConverter.parseFloat(null);
	}
	
	@Test
	public void parseFloatTest() throws NumberFormatException {
		assertEquals(10.5F, ParametersConverter.parseFloat("10.5"), 1e-2);
	}
}
