/*********************************************************************************
 *
 * Catena-X - Product Passport Consumer Backend
 *
 * Copyright (c) 2022, 2023 BASF SE, BMW AG, Henkel AG & Co. KGaA
 * Copyright (c) 2022, 2023 Contributors to the CatenaX (ng) GitHub Organisation.
 *
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

package tools;

import tools.exceptions.ToolException;

import java.io.InputStream;
import java.util.Map;

public final class configTools {
    private static final String CONFIGURATION_DIR = "config";

    private static final String CONFIGURATION_FILE_NAME = "configuration";

    public String CONFIGURATION_FILE_PATH;

    private Map<String, Object> configuration;

    public static envTools env = new envTools();
    public configTools(){
        this.CONFIGURATION_FILE_PATH = CONFIGURATION_DIR+"/"+CONFIGURATION_FILE_NAME+"-"+env.getEnvironment()+".yml";
        InputStream fileContent  = fileTools.getResourceContent(this.getClass(), this.CONFIGURATION_FILE_PATH);
        this.configuration = yamlTools.parseYmlStream(fileContent);
    }
    public Map<String, Object> getConfiguration(){
        if (this.configuration == null) {
            throw new ToolException(configTools.class,"[CRITICAL] Configuration file ["+ this.CONFIGURATION_FILE_PATH +"] not loaded!");
        }
        return this.configuration;
    }
    public void loadConfiguration(String configurationFile){
        InputStream configurationContent = fileTools.getResourceContent(this.getClass(), configurationFile);
        this.configuration = yamlTools.parseYmlStream(configurationContent);
    }
    public Object getConfigurationParam(String param){
        if(this.configuration == null){
            return null;
        }
        Object value = configuration.get(param);
        if (value == null) {
            throw new ToolException(configTools.class,"[ERROR] Configuration param ["+param+"] not found!");
        }
        return value;
    }
    public Object getConfigurationParam(String param, String separator, Object defaultValue){
        if(this.configuration == null){
            return defaultValue;
        }
        Object value = jsonTools.getValue(this.configuration, param, separator, null);
        if (value == null) {
            throw new ToolException(configTools.class,"[ERROR] Configuration param ["+param+"] not found!");
        }
        return value;
    }
    public Object getConfigurationParam(Map<String, Object> config, String param, String separator, Object defaultValue){
        if(config == null){
            return defaultValue;
        }
        Object value = jsonTools.getValue(config, param, separator, null);
        if (value == null) {
            throw new ToolException(configTools.class,"[ERROR] Configuration param ["+param+"] not found!");
        }
        return value;
    }
}
