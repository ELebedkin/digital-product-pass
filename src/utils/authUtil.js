/**
 * Catena-X - Product Passport Consumer Frontend
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
 */

import jsonUtil from "@/utils/jsonUtil.js";
import cryptUtil from "@/utils/cryptUtil";
export default {
    decodeToken(token){
        return jsonUtil.toJson(cryptUtil.fromBase64(String(token).split(".")[1]))
    },
    checkBpn(parsedToken, bpn){
        if(!jsonUtil.exists(parsedToken, "bpn")){
            return false;
        }
        let tokenBpn = jsonUtil.get("bpn",parsedToken, ".", null);
        console.log("CONFIGURATION BPN: " + bpn);
        console.log("TOKEN BPN: " + tokenBpn);
        if(bpn == null){
            return false;
        }
        


        return bpn === tokenBpn;
    }
    
}