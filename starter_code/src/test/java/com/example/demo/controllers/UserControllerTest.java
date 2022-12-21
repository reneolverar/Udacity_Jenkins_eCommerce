package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.splunk.TcpInput;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);
    private TcpInput tcpInput = mock(TcpInput.class);

    @Before
    public void setUp(){
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
        TestUtils.injectObjects(userController, "tcpInput", tcpInput);
    }

    @Test
    public void createUser() throws IOException {
        when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");

        ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());

        when(userRepository.findByUsername("test")).thenReturn(new User("test", "thisIsHashed"));
        u = userController.findByUserName(r.getUsername()).getBody();
        assertEquals("test", u.getUsername());

        Optional<User> newUser = Optional.of(new User(0L, "test", "thisIsHashed", null));
        when(userRepository.findById(0L)).thenReturn(newUser);
        u = userController.findById(response.getBody().getId()).getBody();
        assertEquals(0, u.getId());
    }

    @Test
    public void negativeTests() throws IOException {
        ResponseEntity<User> response;

        response = userController.findByUserName("wrongUser");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        response = userController.createUser(new CreateUserRequest("testUser", "shortP", "shortP"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        response = userController.createUser(new CreateUserRequest("testUser", "differentPassword", "asfasdfs"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
