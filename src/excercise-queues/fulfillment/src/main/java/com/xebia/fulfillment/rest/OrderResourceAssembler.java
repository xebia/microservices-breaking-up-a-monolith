package com.xebia.fulfillment.rest;


import com.xebia.fulfillment.domain.Orderr;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

public class OrderResourceAssembler extends ResourceAssemblerSupport<Orderr, OrderResource> {

    public OrderResourceAssembler() {
        super(ShipmentController.class, OrderResource.class);
    }

    @Override
    public OrderResource toResource(Orderr orderr) {

        OrderResource resource = createResourceWithId(orderr.getUuid(), orderr);
        BeanUtils.copyProperties(orderr, resource);

        return resource;
    }

    public Orderr toClass(OrderResource resource){
        Orderr orderr = new Orderr(resource.getUuid(), resource.getShippingAddress(), resource.getAccount());
        orderr.addLineItems(resource.getLineItems());
        return orderr;
    }
}
