/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package af.asr.customer.v1;

public interface CatalogEventConstants {

  String DESTINATION = "nun-v1";

  String SELECTOR_NAME = "action";

  String POST_CATALOG = "post-catalog";
  String SELECTOR_POST_CATALOG = SELECTOR_NAME + " = '" + POST_CATALOG + "'";
  String DELETE_CATALOG = "delete-catalog";
  String SELECTOR_DELETE_CATALOG = SELECTOR_NAME + " = '" + DELETE_CATALOG + "'";
  String DELETE_FIELD = "delete-field";
  String SELECTOR_DELETE_FIELD = SELECTOR_NAME + " = '" + DELETE_FIELD + "'";
  String PUT_FIELD = "put-field";
  String SELECTOR_PUT_FIELD = SELECTOR_NAME + " = '" + PUT_FIELD + "'";
}
