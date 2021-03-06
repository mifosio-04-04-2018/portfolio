/*
 * Copyright 2017 The Mifos Initiative.
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
package io.mifos.portfolio.api.v1.domain;

import io.mifos.core.lang.validation.constraints.ValidLocalDateTimeString;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * @author Myrle Krantz
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class Command {
  @Valid
  private List<AccountAssignment> oneTimeAccountAssignments;

  @Valid
  @Nullable
  private BigDecimal paymentSize;

  private String note;

  @ValidLocalDateTimeString
  @NotNull
  private String createdOn;

  private String createdBy;

  public Command() {
  }

  public List<AccountAssignment> getOneTimeAccountAssignments() {
    return oneTimeAccountAssignments;
  }

  public void setOneTimeAccountAssignments(List<AccountAssignment> oneTimeAccountAssignments) {
    this.oneTimeAccountAssignments = oneTimeAccountAssignments;
  }

  @Nullable
  public BigDecimal getPaymentSize() {
    return paymentSize;
  }

  public void setPaymentSize(@Nullable BigDecimal paymentSize) {
    this.paymentSize = paymentSize;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Command command = (Command) o;
    return Objects.equals(oneTimeAccountAssignments, command.oneTimeAccountAssignments) &&
        Objects.equals(paymentSize, command.paymentSize) &&
        Objects.equals(note, command.note);
  }

  @Override
  public int hashCode() {
    return Objects.hash(oneTimeAccountAssignments, paymentSize, note);
  }

  @Override
  public String toString() {
    return "Command{" +
        "oneTimeAccountAssignments=" + oneTimeAccountAssignments +
        ", paymentSize=" + paymentSize +
        ", note='" + note + '\'' +
        ", createdOn='" + createdOn + '\'' +
        ", createdBy='" + createdBy + '\'' +
        '}';
  }
}
