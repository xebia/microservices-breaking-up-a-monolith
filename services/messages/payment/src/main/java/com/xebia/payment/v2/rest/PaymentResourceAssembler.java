package com.xebia.payment.v2.rest;

import com.xebia.payment.v2.domain.Payment;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import java.util.ArrayList;
import java.util.List;

public class PaymentResourceAssembler extends ResourceAssemblerSupport<Payment, PaymentResource> {

    public PaymentResourceAssembler() {
        super(PaymentController.class, PaymentResource.class);
    }

    @Override
    public PaymentResource toResource(Payment payment) {
        PaymentResource resource = createResourceWithId(payment.getUuid(), payment);
        BeanUtils.copyProperties(payment, resource);
        return resource;
    }

    public List<PaymentResource> toResource(List<Payment> payments) {
        List<PaymentResource> result = new ArrayList<PaymentResource>();
        for (Payment payment:payments) {
            result.add(toResource(payment));
        }
        return result;
    }
}
