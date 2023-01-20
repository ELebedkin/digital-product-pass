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

package net.catenax.ce.productpass.models.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    @JsonProperty("message")
    public String message = null;
    @JsonProperty("status")
    public Integer status = 200;
    @JsonProperty("statusText")
    public String statusText = null;

    @JsonProperty("data")
    public Object data = null;

    public Response(String message, Integer status, String statusText) {
        this.message = message;
        this.status = status;
        this.statusText = statusText;
    }
    public Response(String message, Integer status, String statusText, Object data) {
        this.message = message;
        this.status = status;
        this.statusText = statusText;
        this.data = data;
    }
    public Response(String message, Integer status, Object data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }
    public Response(String message, Integer status) {
        this.message = message;
        this.status = status;
    }
    public Response() {
    }

    public Response mapError(Map<String, Object> errorAttributes) {
        this.message = errorAttributes.getOrDefault("message", "An error occurred in the server").toString();
        this.status = (Integer) errorAttributes.getOrDefault("status", 500);
        this.statusText = errorAttributes.getOrDefault("error", "Internal error").toString();
        return this;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String errorString(){
        return "["+this.statusText+"]: "+this.message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
