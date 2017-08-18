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

/**
 * A small set of string utils functions
 */
public class StringUtils {
	/**
     * Checks if a CharSequence is null or has no characters.
     * @param value  the CharSequence to check
     * @return {@code true} if the CharSequence is null or null
     */
	public static boolean isEmpty(final CharSequence value) {
		return (value == null) || (value.length() == 0);
	}
}
