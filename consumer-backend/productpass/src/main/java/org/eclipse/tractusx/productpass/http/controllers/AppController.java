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

package org.eclipse.tractusx.productpass.http.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.juli.logging.Log;
import org.eclipse.tractusx.productpass.config.ProcessConfig;
import org.eclipse.tractusx.productpass.exceptions.ControllerException;
import org.eclipse.tractusx.productpass.managers.ProcessManager;
import org.eclipse.tractusx.productpass.models.dtregistry.DigitalTwin;
import org.eclipse.tractusx.productpass.models.dtregistry.EndPoint;
import org.eclipse.tractusx.productpass.models.dtregistry.SubModel;
import org.eclipse.tractusx.productpass.models.edc.DataPlaneEndpoint;
import org.eclipse.tractusx.productpass.models.edc.Jwt;
import org.eclipse.tractusx.productpass.models.http.Response;
import org.eclipse.tractusx.productpass.models.http.requests.Search;
import org.eclipse.tractusx.productpass.models.manager.History;
import org.eclipse.tractusx.productpass.models.passports.Passport;
import org.eclipse.tractusx.productpass.services.AasService;
import org.eclipse.tractusx.productpass.services.DataPlaneService;
import org.sonarsource.scanner.api.internal.shaded.minimaljson.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import utils.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@Tag(name = "Public Controller")
public class AppController {

    private @Autowired HttpServletRequest httpRequest;
    private @Autowired HttpServletResponse httpResponse;

    @Autowired
    HttpUtil httpUtil;
    @Autowired
    EdcUtil edcUtil;
    @Autowired
    JsonUtil jsonUtil;
    @Autowired
    Environment env;
    @Autowired
    PassportUtil passportUtil;
    @Autowired
    AasService aasService;
    @Autowired
    DataPlaneService dataPlaneService;

    @Autowired
    ProcessManager processManager;
    private @Autowired ProcessConfig processConfig;

    @GetMapping("/")
    @Hidden                     // hides this endpoint from api documentation - swagger-ui
    public Response index() {
        httpUtil.redirect(httpResponse, "/passport");
        return httpUtil.getResponse("Redirect to UI");
    }


    @GetMapping("/health")
    @Operation(summary = "Returns the backend health status", responses = {
            @ApiResponse(description = "Gets the application health", responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)))
    })
    public Response health() {
        Response response = httpUtil.getResponse(
                "RUNNING",
                200
        );
        response.data = DateTimeUtil.getDateTimeFormatted(null);
        return response;
    }

    @RequestMapping(value = "/endpoint/{processId}/{version}/{serializedId}", method = RequestMethod.POST)
    public Response getDigitalTwin(@RequestBody Object body, @PathVariable String processId, @PathVariable String version, @PathVariable String serializedId, @RequestParam String registryUrl, @RequestParam(defaultValue = "partInstanceId") String idType, @RequestParam(defaultValue = "0") Integer dtIndex, @RequestParam(defaultValue = "batteryPass") String idShort) {
        try {
            DataPlaneEndpoint endpointData = null;
            try {
                endpointData = this.getEndpointData(body);
            } catch (Exception e) {
                return httpUtil.buildResponse(httpUtil.getBadRequest(e.getMessage()), httpResponse);
            }
            if (endpointData == null) {
                return httpUtil.buildResponse(httpUtil.getBadRequest("Failed to get data plane endpoint data"), httpResponse);
            }

            if (!processManager.checkProcess(processId)) {
                return httpUtil.buildResponse(httpUtil.getNotFound("Process not found!"), httpResponse);
            }


            // Start Digital Twin Query
            AasService.DigitalTwinRegistryQueryById digitalTwinRegistry = aasService.new DecentralDigitalTwinRegistryQueryById(
                    new Search(
                            processId,
                            serializedId,
                            version,
                            idType,
                            dtIndex,
                            idShort
                    ),
                    registryUrl,
                    endpointData
            );
            Long dtRequestTime = DateTimeUtil.getTimestamp();
            Thread digitalTwinRegistryThread = ThreadUtil.runThread(digitalTwinRegistry);
            // Wait for digital twin query
            digitalTwinRegistryThread.join();
            DigitalTwin digitalTwin = null;
            SubModel subModel = null;
            String connectorId = null;
            String connectorAddress = null;
            try {
                digitalTwin = digitalTwinRegistry.getDigitalTwin();
                subModel = digitalTwinRegistry.getSubModel();
                connectorId = subModel.getIdShort();
                EndPoint endpoint = subModel.getEndpoints().stream().filter(obj -> obj.getInterfaceName().equals("EDC")).findFirst().orElse(null);
                if (endpoint == null) {
                    throw new ControllerException(this.getClass().getName(), "No EDC endpoint found in DTR SubModel!");
                }
                connectorAddress = endpoint.getProtocolInformation().getEndpointAddress();
            } catch (Exception e) {
                LogUtil.printException(e,"Failed to get the submodel from the digital twin registry!");
            }
            if (connectorId.isEmpty() || connectorAddress.isEmpty()) {
                LogUtil.printError("Failed to get connectorId and connectorAddress!");
            }
            try {
                connectorAddress = CatenaXUtil.buildEndpoint(connectorAddress);
            } catch (Exception e) {
                LogUtil.printException(e,"Failed to build endpoint url to [" + connectorAddress + "]!");
            }
            if (connectorAddress.isEmpty()) {
                LogUtil.printError("Failed to parse endpoint [" + connectorAddress + "]!");
            }

            LogUtil.printMessage(jsonUtil.toJson(digitalTwin, true));
            LogUtil.printMessage(jsonUtil.toJson(subModel, true));
            processManager.saveDigitalTwin(processId, digitalTwin, dtRequestTime);
            LogUtil.printDebug("[PROCESS " + processId + "] Digital Twin [" + digitalTwin.getIdentification() + "] and Submodel [" + subModel.getIdentification() + "] with EDC endpoint [" + connectorAddress + "] retrieved from DTR");
            String assetId = String.join("-", digitalTwin.getIdentification(), subModel.getIdentification());
            processManager.setStatus(processId, "digital-twin-found", new History(
                    assetId,
                    "READY"
            ));

        } catch (Exception e) {
            LogUtil.printException(e, "This request is not allowed! It must contain the valid attributes from an EDC endpoint");
            return httpUtil.buildResponse(httpUtil.getForbiddenResponse(), httpResponse);
        }
        return httpUtil.buildResponse(httpUtil.getResponse("ok"), httpResponse);
    }

    public DataPlaneEndpoint getEndpointData(Object body) throws ControllerException {
        DataPlaneEndpoint endpointData = edcUtil.parseDataPlaneEndpoint(body);
        if (endpointData == null) {
            throw new ControllerException(this.getClass().getName(), "The endpoint data request is empty!");
        }
        if (endpointData.getEndpoint().isEmpty()) {
            throw new ControllerException(this.getClass().getName(), "The data plane endpoint address is empty!");
        }
        if (endpointData.getAuthCode().isEmpty()) {
            throw new ControllerException(this.getClass().getName(), "The authorization code is empty!");
        }
        if (!endpointData.offerIdExists()) {
            Jwt token = httpUtil.parseToken(endpointData.getAuthCode());
            if (!token.getPayload().containsKey("cid") || token.getPayload().get("cid").equals("")) {
                throw new ControllerException(this.getClass().getName(), "The Offer Id is empty!");
            }
        } else {
            if (endpointData.getOfferId().isEmpty()) {
                throw new ControllerException(this.getClass().getName(), "The authorization code is empty!");
            }
        }

        return endpointData;
    }

    @RequestMapping(value = "/endpoint/{processId}", method = RequestMethod.POST)
    public Response endpoint(@RequestBody Object body, @PathVariable String processId) {
        try {
            DataPlaneEndpoint endpointData = null;
            try {
                endpointData = this.getEndpointData(body);
            } catch (Exception e) {
                return httpUtil.buildResponse(httpUtil.getBadRequest(e.getMessage()), httpResponse);
            }
            if (endpointData == null) {
                return httpUtil.buildResponse(httpUtil.getBadRequest("Failed to get data plane endpoint data"), httpResponse);
            }

            if (!processManager.checkProcess(processId)) {
                return httpUtil.buildResponse(httpUtil.getNotFound("Process not found!"), httpResponse);
            }

            Passport passport = dataPlaneService.getPassport(endpointData);
            if (passport == null) {
                return httpUtil.buildResponse(httpUtil.getNotFound("Passport not found in data plane!"), httpResponse);
            }
            String passportPath = processManager.savePassport(processId, endpointData, passport);

            LogUtil.printMessage("[EDC] Passport Transfer Data [" + endpointData.getId() + "] Saved Successfully in [" + passportPath + "]!");
        } catch (Exception e) {
            LogUtil.printException(e, "This request is not allowed! It must contain the valid attributes from an EDC endpoint");
            return httpUtil.buildResponse(httpUtil.getForbiddenResponse(), httpResponse);
        }
        return httpUtil.buildResponse(httpUtil.getResponse("ok"), httpResponse);
    }


}
