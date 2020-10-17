package com.graphaware.pizzeria.aspect;

import com.graphaware.pizzeria.model.PizzeriaUser;
import com.graphaware.pizzeria.model.Purchase;
import com.graphaware.pizzeria.model.PurchaseState;
import com.graphaware.pizzeria.repository.PurchaseRepository;
import com.graphaware.pizzeria.security.PizzeriaUserPrincipal;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
@Lazy
public class PizzaDiscountAspect {

    @Autowired
    PurchaseRepository purchaseRepository;
    PizzeriaUser currentUser = getCurrentUser();

    @Before(" @annotation(com.graphaware.pizzeria.annotations.PizzaDiscount)")
    public void pizzaDiscountIfOrderedMoreThenThree() throws Throwable {

        List<Purchase> purchases = purchaseRepository.findAllByStateEqualsAndCustomer_Id(PurchaseState.DRAFT, currentUser.getId());
        if (purchases.size() >= 3) {
            purchases = purchases.stream()
                                 .sorted(Comparator.comparing(Purchase::getAmount))
                                 .collect(Collectors.toList());
            purchases.get(0)
                     .setAmount(0.00);
          purchaseRepository.save(purchases.get(0));

        }

    }

    private PizzeriaUser getCurrentUser() {
        return ((PizzeriaUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }
}
