package com.xebia.fulfillment.rest;

import com.xebia.fulfillment.domain.Shipment;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

public class ShipmentResourceAssembler extends ResourceAssemblerSupport<Shipment, ShipmentResource> {

    public ShipmentResourceAssembler() {
        super(ShipmentController.class, ShipmentResource.class);
    }

    @Override
    public ShipmentResource toResource(Shipment shipment) {

        ShipmentResource resource = createResourceWithId(shipment.getUuid(), shipment);
        BeanUtils.copyProperties(shipment, resource);

        return resource;
    }

}
