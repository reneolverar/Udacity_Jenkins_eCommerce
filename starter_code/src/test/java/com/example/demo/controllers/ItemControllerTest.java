package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.splunk.TcpInput;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ItemControllerTest {
    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private TcpInput tcpInput = mock(TcpInput.class);

    @Before
    public void setUp() throws IOException {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
        TestUtils.injectObjects(itemController, "tcpInput", tcpInput);
    }

    @Test
    public void getItems() throws IOException {
        Item item1 = new Item(0L, "item1", BigDecimal.valueOf(5), "desc");
        Item item1_2 = new Item(1L, "item1", BigDecimal.valueOf(5), "desc");
        Item item2 = new Item(2L, "item2", BigDecimal.valueOf(5), "desc");
        List<Item> allItems = Arrays.asList(item1, item1_2, item2);
        List<Item> allItem1s = Arrays.asList(item1, item1_2);
        Item responseItem;
        List<Item> responseItems;

        when(itemRepository.findByName("item1")).thenReturn(allItem1s);
        responseItems = itemController.getItemsByName("item1").getBody();
        assertEquals(2, responseItems.size());
        assertEquals(Long.valueOf(0), responseItems.get(0).getId());
        assertEquals(Long.valueOf(1), responseItems.get(1).getId());

        when(itemRepository.findById(2L)).thenReturn(Optional.of(item2));
        responseItem = itemController.getItemById(2L).getBody();
        assertEquals(Long.valueOf(2), responseItem.getId());

        when(itemRepository.findAll()).thenReturn(allItems);
        responseItems = itemController.getItems().getBody();
        assertEquals(3, responseItems.size());
    }
}
