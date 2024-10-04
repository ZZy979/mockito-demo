package com.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ListTest {
    @Test
    public void verifyBehaviour() {
        // mock creation
        List mockedList = mock(List.class);

        // using mock object
        mockedList.add("one");
        mockedList.clear();

        // verification
        verify(mockedList).add("one");
        verify(mockedList).clear();
    }

    @Test
    public void stubbing() {
        // You can mock concrete classes, not just interfaces
        LinkedList mockedList = mock(LinkedList.class);

        // stubbing
        when(mockedList.get(0)).thenReturn("first");
        when(mockedList.get(1)).thenThrow(new RuntimeException());

        // following returns "first"
        assertEquals("first", mockedList.get(0));

        // following throws runtime exception
        assertThrows(RuntimeException.class, () -> mockedList.get(1));

        // following returns "null" because get(999) was not stubbed
        assertNull(mockedList.get(999));

        // Although it is possible to verify a stubbed invocation, usually it's just redundant
        // If your code cares what get(0) returns, then something else breaks (often even before verify() gets executed).
        // If your code doesn't care what get(0) returns, then it should not be stubbed.
        verify(mockedList).get(0);
    }

    @Test
    public void argumentMatchers() {
        List<String> mockedList = mock(List.class);

        // stubbing using built-in anyInt() argument matcher
        when(mockedList.get(anyInt())).thenReturn("element");

        // stubbing using custom matcher (let's say isValid() returns your own matcher implementation):
        // when(mockedList.contains(argThat(isValid()))).thenReturn(true);

        // following returns "element"
        assertEquals("element", mockedList.get(999));

        // you can also verify using an argument matcher
        verify(mockedList).get(anyInt());

        // argument matchers can also be written as Java 8 Lambdas
        mockedList.add("element");
        verify(mockedList).add(argThat(s -> s.length() > 5));
    }

    @Test
    public void verifyNumberOfInvocations() {
        List<String> mockedList = mock(List.class);

        // using mock
        mockedList.add("once");

        mockedList.add("twice");
        mockedList.add("twice");

        mockedList.add("three times");
        mockedList.add("three times");
        mockedList.add("three times");

        // following two verifications work exactly the same - times(1) is used by default
        verify(mockedList).add("once");
        verify(mockedList, times(1)).add("once");

        // exact number of invocations verification
        verify(mockedList, times(2)).add("twice");
        verify(mockedList, times(3)).add("three times");

        // verification using never(). never() is an alias to times(0)
        verify(mockedList, never()).add("never happened");

        // verification using atLeast()/atMost()
        verify(mockedList, atMostOnce()).add("once");
        verify(mockedList, atLeastOnce()).add("three times");
        verify(mockedList, atLeast(2)).add("three times");
        verify(mockedList, atMost(5)).add("three times");
    }

    @Test
    public void stubbingVoidMethod() {
        List mockedList = mock(List.class);

        doThrow(new RuntimeException()).when(mockedList).clear();

        // following throws RuntimeException:
        assertThrows(RuntimeException.class, mockedList::clear);
    }

    @Test
    public void verificationInOrderSingleMock() {
        // A. Single mock whose methods must be invoked in a particular order
        List<String> singleMock = mock(List.class);

        // using a single mock
        singleMock.add("was added first");
        singleMock.add("was added second");

        // create an inOrder verifier for a single mock
        InOrder inOrder = inOrder(singleMock);

        // following will make sure that add is first called with "was added first", then with "was added second"
        inOrder.verify(singleMock).add("was added first");
        inOrder.verify(singleMock).add("was added second");
    }

    @Test
    public void verificationInOrderMultipleMocks() {
        // B. Multiple mocks that must be used in a particular order
        List<String> firstMock = mock(List.class);
        List<String> secondMock = mock(List.class);

        // using mocks
        firstMock.add("was called first");
        secondMock.add("was called second");

        // create inOrder object passing any mocks that need to be verified in order
        InOrder inOrder = inOrder(firstMock, secondMock);

        // following will make sure that firstMock was called before secondMock
        inOrder.verify(firstMock).add("was called first");
        inOrder.verify(secondMock).add("was called second");
    }

    @Mock
    private List<String> myMockedList;

    @Test
    public void mockAnnotation() {
        myMockedList.add("foo");
        verify(myMockedList).add("foo");
    }

    @Test
    public void stubbingConsecutiveCalls() {
        List<String> mockedList = mock(List.class);

        when(mockedList.get(0))
                .thenThrow(new RuntimeException())
                .thenReturn("foo");

        // First call: throws runtime exception:
        assertThrows(RuntimeException.class, () -> mockedList.get(0));

        // Second call: returns "foo"
        assertEquals("foo", mockedList.get(0));

        // Any consecutive call: returns "foo" as well (last stubbing wins).
        assertEquals("foo", mockedList.get(0));

        when(mockedList.get(1))
                .thenReturn("one", "two", "three");

        assertEquals("one", mockedList.get(1));
        assertEquals("two", mockedList.get(1));
        assertEquals("three", mockedList.get(1));
        assertEquals("three", mockedList.get(1));
    }

    @Test
    public void stubbingWithCallbacks() {
        List<String> mockedList = mock(List.class);

        when(mockedList.set(anyInt(), anyString())).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            return "called with arguments: " + Arrays.toString(args);
        });

        assertEquals("called with arguments: [1, foo]", mockedList.set(1, "foo"));
    }

    @Test
    public void spying() {
        List<String> list = new LinkedList<>();
        List<String> spy = spy(list);

        // optionally, you can stub out some methods:
        when(spy.size()).thenReturn(100);

        // using the spy calls *real* methods
        spy.add("one");
        spy.add("two");

        // returns "one" - the first element of a list
        assertEquals("one", spy.get(0));

        // size() method was stubbed - 100 is returned
        assertEquals(100, spy.size());

        // optionally, you can verify
        verify(spy).add("one");
        verify(spy).add("two");
    }

    @Test
    public void stubbingSpy() {
        List<String> list = new LinkedList<>();
        List<String> spy = spy(list);

        // Impossible: real method is called so spy.get(0) throws IndexOutOfBoundsException (the list is yet empty)
        // when(spy.get(0)).thenReturn("foo");

        // You have to use doReturn() for stubbing
        doReturn("foo").when(spy).get(0);

        assertEquals("foo", spy.get(0));
    }

    @Test
    public void argumentCaptor() {
        List<Person> mockedList = mock(List.class);
        mockedList.add(new Person("John", 30));

        ArgumentCaptor<Person> argument = ArgumentCaptor.forClass(Person.class);
        verify(mockedList).add(argument.capture());
        assertEquals("John", argument.getValue().getName());
    }
}
