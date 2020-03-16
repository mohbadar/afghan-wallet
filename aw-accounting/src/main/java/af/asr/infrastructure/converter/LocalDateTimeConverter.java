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
package af.asr.infrastructure.converter;


import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.apache.fineract.cn.lang.DateConverter;

@Converter
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

  public LocalDateTimeConverter() {
    super();
  }

  @Override
  public Timestamp convertToDatabaseColumn(final LocalDateTime attribute) {
    if (attribute == null) {
      return null;
    } else {
      return new Timestamp(DateConverter.toEpochMillis(attribute));
    }
  }

  @Override
  public LocalDateTime convertToEntityAttribute(final Timestamp dbData) {
    if (dbData == null) {
      return null;
    } else {
      return DateConverter.fromEpochMillis(dbData.getTime());
    }
  }
}
