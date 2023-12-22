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

package org.eclipse.tractusx.digitalproductpass.models.negotiation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class consists exclusively to define attributes related to the Dataset's distributions property.
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Distribution {

    /** ATTRIBUTES **/
    @JsonProperty("@type")
    String type;
    @JsonProperty("dct:format")
    Format format;
    @JsonProperty("dcat:accessService")
    String accessService;

    /** CONSTRUCTOR(S) **/
    public Distribution(String type, Format format, String accessService) {
        this.type = type;
        this.format = format;
        this.accessService = accessService;
    }
    public Distribution() {
    }

    /** GETTERS AND SETTERS **/
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Format getFormat() {
        return format;
    }
    public void setFormat(Format format) {
        this.format = format;
    }
    @SuppressWarnings("Unused")
    public String getAccessService() {
        return accessService;
    }
    @SuppressWarnings("Unused")
    public void setAccessService(String accessService) {
        this.accessService = accessService;
    }

    /** INNER CLASSES **/
    /**
     * This class consists exclusively to define attributes related to the Distribution's format property.
     **/
    static class Format {
        /** ATTRIBUTES **/
        @JsonProperty("@id")
        String id;

        /** CONSTRUCTOR(S) **/
        @SuppressWarnings("Unused")
        public Format(String id) {
            this.id = id;
        }
        @SuppressWarnings("Unused")
        public Format() {
        }

        /** GETTERS AND SETTERS **/
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
    }


}