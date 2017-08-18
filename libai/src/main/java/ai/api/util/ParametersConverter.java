/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package ai.api.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Helper to extract possible response parameters values
 */
public final class ParametersConverter {

    public static final String PROTOCOL_DATE_FORMAT = "yyyy-MM-dd";
    public static final String PROTOCOL_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String PROTOCOL_TIME_FORMAT = "HH:mm:ss";
    
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat(PROTOCOL_DATE_FORMAT, Locale.US);
    private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat(PROTOCOL_DATE_TIME_FORMAT, Locale.US);
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat(PROTOCOL_TIME_FORMAT, Locale.US);

    /**
     * Constructor is hidden to make the class a static used only 
     */
    private ParametersConverter() {
    }

    /**
     * @param parameter Cannot be <code>null</code>
     * @return Never <code>null</code>
     * @throws ParseException
     */
    public static Date parseDateTime(final String parameter) throws ParseException {
    	if (parameter == null) {
            throw new IllegalArgumentException("Parameter must not be null");
        }
        return DATE_TIME_FORMAT.parse(parameter);
    }

    /**
     * @param parameter Cannot be <code>null</code>
     * @return Never <code>null</code>
     * @throws ParseException
     */
    public static Date parseDate(final String parameter) throws ParseException {
    	if (parameter == null) {
            throw new IllegalArgumentException("Parameter must not be null");
        }
        return DATE_FORMAT.parse(parameter);
    }

	/**

	 * @param parameter Cannot be <code>null</code>
	 * @return Never <code>null</code>
	 * @throws ParseException
	 */
    public static Date parseTime(final String parameter) throws ParseException {
    	if (parameter == null) {
            throw new IllegalArgumentException("Parameter must not be null");
        }
    	final Calendar timeParameter = Calendar.getInstance();
    	timeParameter.setTime(TIME_FORMAT.parse(parameter));

        final Calendar taskDueDate = Calendar.getInstance();
        taskDueDate.set(Calendar.HOUR_OF_DAY, timeParameter.get(Calendar.HOUR_OF_DAY));
        taskDueDate.set(Calendar.MINUTE, timeParameter.get(Calendar.MINUTE));
        taskDueDate.set(Calendar.SECOND, timeParameter.get(Calendar.SECOND));

        return taskDueDate.getTime();
    }

    /**
     * 
     * @param parameter Cannot be <code>null</code>
     * @return Never <code>null</code>
     * @throws ParseException
     */
    public static PartialDate parsePartialDate(final String parameter) throws ParseException {
        if (parameter == null) {
            throw new IllegalArgumentException("Parameter must not be empty");
        }
        if (parameter.length() == 0) {
            throw new ParseException("Parameter must not be empty", 0);
        }

        if (parameter.contains("u")) {
            // if date contains unknown parts
            final String[] parts = parameter.split("-");
            if (parts.length != 3) {
                throw new ParseException(String.format("Partial date must have 3 parts, but have %s: %s", parts.length, parameter), 0);
            }

            // check each part for unknown
            // each part must contains all digits or all 'u' without mixing
            final Integer year = parsePart(parts[0]);
            final Integer month = parsePart(parts[1]);
            final Integer day = parsePart(parts[2]);

            final PartialDate result = new PartialDate();
            result.set(Calendar.YEAR, year);
            result.set(Calendar.MONTH, month);
            result.set(Calendar.DATE, day);

            return result;
        } else {
            // parse as normal date
            return new PartialDate(DATE_FORMAT.parse(parameter));
        }
    }

    /**
     * 
     * @param parameter Cannot be <code>null</code>
     * @return Never <code>null</code>
     * @throws NumberFormatException
     */
    public static int parseInteger(final String parameter) throws NumberFormatException {
        return Integer.parseInt(parameter);
    }

    /**
     * 
     * @param parameter Cannot be <code>null</code>
     * @return Never <code>null</code>
     */
    public static float parseFloat(final String parameter) {
    	if (parameter == null) {
            throw new IllegalArgumentException("Parameter must not be empty");
        }
        return Float.parseFloat(parameter);
    }
    
    /**
     * 
     * @param part Cannot be <code>null</code>
     * @return Never <code>null</code>
     */
    private static Integer parsePart(final String part) {
    	if (part == null) {
    		throw new IllegalArgumentException("part");
    	}
        if (part.indexOf('u') >= 0) {
            return PartialDate.UNSPECIFIED_VALUE;
        } else {
            return Integer.parseInt(part);
        }
    }
}
