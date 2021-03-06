/*
 * Copyright 2017 Kuelap, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mifos.portfolio.listener;

import io.mifos.core.lang.config.TenantHeaderFilter;
import io.mifos.core.test.listener.EventRecorder;
import io.mifos.portfolio.api.v1.events.BalanceSegmentSetEvent;
import io.mifos.portfolio.api.v1.events.EventConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * @author Myrle Krantz
 */
@Component
public class BalanceSegmentSetEventListener {
  private final EventRecorder eventRecorder;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Autowired
  public BalanceSegmentSetEventListener(final EventRecorder eventRecorder) {
    super();
    this.eventRecorder = eventRecorder;
  }

  @JmsListener(
      subscription = EventConstants.DESTINATION,
      destination = EventConstants.DESTINATION,
      selector = EventConstants.SELECTOR_POST_BALANCE_SEGMENT_SET
  )
  public void onCreateBalanceSegmentSet(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                        final String payload) {
    this.eventRecorder.event(tenant, EventConstants.POST_BALANCE_SEGMENT_SET, payload, BalanceSegmentSetEvent.class);
  }

  @JmsListener(
      subscription = EventConstants.DESTINATION,
      destination = EventConstants.DESTINATION,
      selector = EventConstants.SELECTOR_PUT_BALANCE_SEGMENT_SET
  )
  public void onChangeBalanceSegmentSet(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                        final String payload) {
    this.eventRecorder.event(tenant, EventConstants.PUT_BALANCE_SEGMENT_SET, payload, BalanceSegmentSetEvent.class);
  }

  @JmsListener(
      subscription = EventConstants.DESTINATION,
      destination = EventConstants.DESTINATION,
      selector = EventConstants.SELECTOR_DELETE_BALANCE_SEGMENT_SET
  )
  public void onDeleteBalanceSegmentSet(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                        final String payload) {
    this.eventRecorder.event(tenant, EventConstants.DELETE_BALANCE_SEGMENT_SET, payload, BalanceSegmentSetEvent.class);
  }
}