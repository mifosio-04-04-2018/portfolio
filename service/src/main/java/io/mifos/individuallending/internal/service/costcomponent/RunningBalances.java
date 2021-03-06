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
package io.mifos.individuallending.internal.service.costcomponent;

import io.mifos.core.lang.ServiceException;
import io.mifos.individuallending.IndividualLendingPatternFactory;
import io.mifos.individuallending.api.v1.domain.product.AccountDesignators;
import io.mifos.individuallending.internal.service.DataContextOfAction;
import io.mifos.portfolio.api.v1.domain.ChargeDefinition;
import io.mifos.portfolio.api.v1.domain.Pattern;
import io.mifos.portfolio.api.v1.domain.RequiredAccountAssignment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Myrle Krantz
 */
public interface RunningBalances {
  Map<String, BigDecimal> ACCOUNT_SIGNS = new HashMap<String, BigDecimal>() {{
    final BigDecimal negative = BigDecimal.valueOf(-1);
    final BigDecimal positive = BigDecimal.valueOf(1);

    this.put(AccountDesignators.CUSTOMER_LOAN_PRINCIPAL, negative);
    this.put(AccountDesignators.CUSTOMER_LOAN_FEES, negative);
    this.put(AccountDesignators.CUSTOMER_LOAN_INTEREST, negative);
    this.put(AccountDesignators.LOAN_FUNDS_SOURCE, negative);
    this.put(AccountDesignators.PROCESSING_FEE_INCOME, positive);
    this.put(AccountDesignators.ORIGINATION_FEE_INCOME, positive);
    this.put(AccountDesignators.DISBURSEMENT_FEE_INCOME, positive);
    this.put(AccountDesignators.INTEREST_INCOME, positive);
    this.put(AccountDesignators.INTEREST_ACCRUAL, positive);
    this.put(AccountDesignators.LATE_FEE_INCOME, positive);
    this.put(AccountDesignators.LATE_FEE_ACCRUAL, positive);
    this.put(AccountDesignators.PRODUCT_LOSS_ALLOWANCE, negative);
    this.put(AccountDesignators.GENERAL_LOSS_ALLOWANCE, negative);
    this.put(AccountDesignators.EXPENSE, negative);
    this.put(AccountDesignators.ENTRY, positive);
    //TODO: derive signs from IndividualLendingPatternFactory.individualLendingRequiredAccounts instead.
  }};

  Optional<BigDecimal> getAccountBalance(final String accountDesignator);

  BigDecimal getAccruedBalanceForCharge(
      final ChargeDefinition chargeDefinition);

  Optional<LocalDateTime> getStartOfTerm(final DataContextOfAction dataContextOfAction);

  default LocalDateTime getStartOfTermOrThrow(final DataContextOfAction dataContextOfAction) {
    return this.getStartOfTerm(dataContextOfAction)
        .orElseThrow(() -> ServiceException.internalError(
            "Start of term for loan ''{0}'' could not be acquired from accounting.",
            dataContextOfAction.getCompoundIdentifer()));
  }

  default Optional<BigDecimal> getLedgerBalance(final String ledgerDesignator) {
    final Pattern individualLendingPattern = IndividualLendingPatternFactory.individualLendingPattern();
    return individualLendingPattern.getAccountAssignmentsRequired().stream()
        .filter(requiredAccountAssignment -> ledgerDesignator.equals(requiredAccountAssignment.getGroup()))
        .map(RequiredAccountAssignment::getAccountDesignator)
        .map(this::getAccountBalance)
        .reduce(Optional.empty(), (x, y) -> {
          if (x.isPresent() && y.isPresent())
            return Optional.of(x.get().add(y.get()));
          else if (x.isPresent())
            return x;
          else //noinspection OptionalIsPresent
            if (y.isPresent())
            return y;
          else
            return Optional.empty();
        });
  }

  default Optional<BigDecimal> getBalance(final String designator) {
    final Pattern individualLendingPattern = IndividualLendingPatternFactory.individualLendingPattern();
    if (individualLendingPattern.getAccountAssignmentGroups().contains(designator))
      return getLedgerBalance(designator);
    else
      return getAccountBalance(designator);
  }


  /**
   *
   * @param requestedAmount The requested amount is necessary as a parameter, because infinity is
   *                        not available as a return value for BigDecimal.  There is no way to express that there is
   *                        no limit, so when there is no limit, the requestedAmount is what is returned.
   */
  default BigDecimal getAvailableBalance(final String designator, final BigDecimal requestedAmount) {
    return getBalance(designator).orElse(requestedAmount);
  }

  default BigDecimal getMaxDebit(final String accountDesignator, final BigDecimal amount) {
    if (ACCOUNT_SIGNS.get(accountDesignator).signum() == -1)
      return amount;
    else
      return amount.min(getAvailableBalance(accountDesignator, amount));
  }

  default BigDecimal getMaxCredit(final String accountDesignator, final BigDecimal amount) {
    if (accountDesignator.equals(AccountDesignators.EXPENSE) ||
        accountDesignator.equals(AccountDesignators.PRODUCT_LOSS_ALLOWANCE) ||
        accountDesignator.equals(AccountDesignators.GENERAL_LOSS_ALLOWANCE))
      return amount;
    //expense account can achieve a "relative" negative balance, and
    // both loss allowance accounts can achieve an "absolute" negative balance.

    if (ACCOUNT_SIGNS.get(accountDesignator).signum() != -1)
      return amount;
    else
      return amount.min(getAvailableBalance(accountDesignator, amount));
  }
}
