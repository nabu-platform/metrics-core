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

package be.nabu.libs.metrics.core.api;

/**
 * The deviation tells us how many (percentage) of the incoming events are below a certain deviation (e.g. 25%) of a certain point (e.g. the average)
 * The percentage is not cumulative but relative to the lower deviations, e.g. if 50% of events falls below 25%, these events are not represented in the 50% deviation
 */
public interface Deviation {
	public double getDeviation();
	public double getPercentage();
}
