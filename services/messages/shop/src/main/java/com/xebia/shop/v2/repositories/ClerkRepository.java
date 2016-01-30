package com.xebia.shop.v2.repositories;

import com.xebia.shop.v2.domain.Clerk;
import com.xebia.shop.v2.domain.Orderr;
import com.xebia.shop.v2.domain.ShoppingCart;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ClerkRepository extends CrudRepository<Clerk, UUID> {
    Clerk findByShoppingCart(ShoppingCart shoppingCart);
    Clerk findByOrderr(Orderr orderr);
}
