package com.example.mvc;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @Test
    public void testGetUsername() {
        // create mock
        UserRepository mockRepository = mock(UserRepository.class);

        // stubbing
        when(mockRepository.findById(1)).thenReturn(new User(1, "Alice"));

        // use mock
        UserService userService = new UserService(mockRepository);
        String username = userService.getUsername(1);

        // assert result
        assertEquals("Alice", username);

        // verify invocation
        verify(mockRepository).findById(1);
    }

    @Test
    public void testSaveUser() {
        UserRepository mockRepository = mock(UserRepository.class);

        UserService userService = new UserService(mockRepository);
        User user = new User(2, "Bob");
        userService.saveUser(user);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(mockRepository).save(captor.capture());

        assertEquals(2, captor.getValue().getId());
        assertEquals("Bob", captor.getValue().getName());
    }
}
