package com.xebia.fulfillment.v2.rest;

import com.xebia.fulfillment.v2.domain.Shipment;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

public class ShipmentResourceAssembler extends ResourceAssemblerSupport<Shipment, com.xebia.fulfillment.v2.rest.ShipmentResource> {

    public ShipmentResourceAssembler() {
        super(com.xebia.fulfillment.v2.rest.ShipmentController.class, com.xebia.fulfillment.v2.rest.ShipmentResource.class);
    }

    @Override
    public com.xebia.fulfillment.v2.rest.ShipmentResource toResource(Shipment shipment) {

        com.xebia.fulfillment.v2.rest.ShipmentResource resource = createResourceWithId(shipment.getUuid(), shipment);
        BeanUtils.copyProperties(shipment, resource);

        return resource;
    }

}
