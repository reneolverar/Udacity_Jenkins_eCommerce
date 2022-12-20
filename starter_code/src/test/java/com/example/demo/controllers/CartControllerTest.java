package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;
    private UserRepository userRepository = mock(UserRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);

    @Before
    public void setUp(){
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);

        User user = new User(0L, "testPassword", "testUser", new Cart());
        when(userRepository.findByUsername("testUser")).thenReturn(user);

        Item item0 = new Item(0L, "item0", BigDecimal.valueOf(5), "desc");
        Item item1 = new Item(1L, "item1", BigDecimal.valueOf(10), "desc");
        when(itemRepository.findById(0L)).thenReturn(Optional.of(item0));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
    }

    @Test
    public void addAndRemoveFromCart() {
        Cart cart;
        ModifyCartRequest request = new ModifyCartRequest("testUser", 0L, 1);
        cart = cartController.addTocart(request).getBody();
        assertEquals(BigDecimal.valueOf(5), cart.getTotal());

        request.setQuantity(2);
        cart = cartController.addTocart(request).getBody();
        assertEquals(BigDecimal.valueOf(15), cart.getTotal());

        request.setItemId(1L);
        cart = cartController.addTocart(request).getBody();
        assertEquals(BigDecimal.valueOf(35), cart.getTotal());

        cart = cartController.removeFromcart(request).getBody();
        assertEquals(BigDecimal.valueOf(15), cart.getTotal());

        request.setQuantity(1);
        request.setItemId(0L);
        cart = cartController.removeFromcart(request).getBody();
        assertEquals(BigDecimal.valueOf(10), cart.getTotal());
    }
}
