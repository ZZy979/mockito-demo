package com.example;

import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.*;

public class ListTest {
    @Test
    public void verifyBehaviour() {
        // mock creation
        List mockList = mock(List.class);

        // using mock object
        mockList.add("one");
        mockList.clear();

        // verification
        verify(mockList).add("one");
        verify(mockList).clear();
    }
}
