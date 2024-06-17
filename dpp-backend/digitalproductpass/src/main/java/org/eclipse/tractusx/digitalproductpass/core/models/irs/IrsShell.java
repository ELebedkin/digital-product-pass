/*********************************************************************************
 *
 * Tractus-X - Digital Product Pass Application
 *
 * Copyright (c) 2022, 2024 BMW AG, Henkel AG & Co. KGaA
 * Copyright (c) 2022, 2024 CGI Deutschland B.V. & Co. KG
 * Copyright (c) 2022, 2024 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.digitalproductpass.core.models.irs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.tractusx.digitalproductpass.core.models.dtregistry.DigitalTwin;

/**
 * This class consists exclusively to define attributes related to the Job Response coming from the IRS.
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IrsShell {

    /**
     * ATTRIBUTES
     **/
    @JsonProperty("contractAgreementId")
    public String contractAgreementId;
    @JsonProperty("payload")
    public DigitalTwin payload;


    /**
     * CONSTRUCTORS
     **/
    public IrsShell() {
    }

    public IrsShell(String contractAgreementId, DigitalTwin payload) {
        this.contractAgreementId = contractAgreementId;
        this.payload = payload;
    }

    /**
     * GETTERS AND SETTERS
     **/
    public String getContractAgreementId() {
        return contractAgreementId;
    }

    public void setContractAgreementId(String contractAgreementId) {
        this.contractAgreementId = contractAgreementId;
    }

    public DigitalTwin getPayload() {
        return payload;
    }

    public void setPayload(DigitalTwin payload) {
        this.payload = payload;
    }
}
