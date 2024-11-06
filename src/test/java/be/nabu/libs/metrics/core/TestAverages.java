/*
* Copyright (C) 2016 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package be.nabu.libs.metrics.core;

import java.util.Arrays;

import junit.framework.TestCase;
import be.nabu.libs.metrics.core.api.CurrentValueSink;
import be.nabu.libs.metrics.core.sinks.CMASink;
import be.nabu.libs.metrics.core.sinks.EMASink;

/**
 * Based on existing data in: http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:moving_averages
 */
public class TestAverages extends TestCase {

	public void testEMA() {
		long [] values = new long [] { 2227, 2219, 2208, 2217, 2217, 2218, 2223, 2243, 2224, 2229, 2215, 2239, 2238, 2261, 2336, 2405, 2375, 2383, 2395, 2363, 2382, 2387, 2365, 2319, 2310, 2333, 2268, 2310, 2240, 2217 };
		testAverage(new EMASink(10), 2297, Arrays.copyOfRange(values, 0, 17));
		// actual expected is 2233
		testAverage(new EMASink(10), 2234, Arrays.copyOfRange(values, 0, 14));
		// actual expected is 2354
		testAverage(new EMASink(10), 2353, Arrays.copyOfRange(values, 0, 23));
		// actual expected is 2292
		testAverage(new EMASink(10), 2291, values);
		
		testAverage(new CMASink(), 2218, Arrays.copyOfRange(values, 0, 3));
		testAverage(new CMASink(), 2222, Arrays.copyOfRange(values, 0, 10));
		testAverage(new CMASink(), 2285, values);
	}
	
	public void testAverage(CurrentValueSink sink, long expected, long...series) {
		for (long value : series) {
			sink.push(0, value);
		}
		assertEqualsEMA(sink, expected);
	}
	
	public void assertEqualsEMA(CurrentValueSink sink, long ema) {
		assertEquals(ema, (sink.getCurrent().getValue()));
	}
}
