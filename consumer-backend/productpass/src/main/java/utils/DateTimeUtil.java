/*********************************************************************************
 *
 * Catena-X - Product Passport Consumer Backend
 *
 * Copyright (c) 2022, 2023 BASF SE, BMW AG, Henkel AG & Co. KGaA
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the
 * License for the specific language govern in permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package utils;

import utils.exceptions.UtilException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This class consists exclusively of methods to operate on DateTime type instances.
 *
 * <p> The methods defined here are intended to parse datetime formats and get current timestamps for logging purposes.
 *
 */
public final class DateTimeUtil {

    private DateTimeUtil() {
        throw new IllegalStateException("Tool/Utility Class Illegal Initialization");
    }

    /**
     * Gets the current datetime with a given format.
     * <p>
     * @param   pattern
     *          the intended datetime format to apply.
     *
     * @return  a {@code String} object with the current datetime formatted with the given pattern or a default one if the patter is null.
     *
     * @apiNote default Format: "dd/MM/yyyy HH:mm:ss.SSS"
     *
     */
    public static String getDateTimeFormatted(String pattern){
        String defaultPattern = "dd/MM/yyyy HH:mm:ss.SSS";
        if(pattern == null){
            pattern = defaultPattern;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    /**
     * Gets the current datetime with a given format.
     * <p>
     * @param   pattern
     *          the intended datetime format to apply.
     *
     * @return  a {@code String} object with the current datetime formatted with the given pattern or a default one if the patter is null.
     *
     * @apiNote default Format: "dd-MM-yyyy_HHmmss"
     *
     */
    @SuppressWarnings("Unused")
    public static String getFileDateTimeFormatted(String pattern){
        String defaultPattern = "dd-MM-yyyy_HHmmss";
        if(pattern == null){
            pattern = defaultPattern;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    /**
     * Gets the current timestamp.
     * <p>
     *
     * @return  a {@code Long} object representing the current timestamp.
     *
     */
    public static Long getTimestamp(){
        return new Timestamp(System.currentTimeMillis()).getTime();
    }

}
