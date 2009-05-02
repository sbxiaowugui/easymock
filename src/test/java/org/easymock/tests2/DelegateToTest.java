/*
 * Copyright (c) 2003-2009 OFFIS, Henri Tremblay. 
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.tests2;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class DelegateToTest {

    public static interface IMyInterface {
        int getInt(int k);
    }

    @Test
    public void testDelegate() {
        IMyInterface mock = createMock(IMyInterface.class);
        IMyInterface delegateTo = new IMyInterface() {
            private int i = 0;

            public int getInt(int k) {
                return i += k;
            }
        };

        expect(mock.getInt(10)).andDelegateTo(delegateTo);
        expect(mock.getInt(5)).andDelegateTo(delegateTo).andDelegateTo(delegateTo)
                .times(2);

        replay(mock);
        
        assertEquals(10, mock.getInt(10));
        assertEquals(15, mock.getInt(5));
        assertEquals(20, mock.getInt(5));
        assertEquals(25, mock.getInt(5));

        verify(mock);
    }

    @Test
    public void testStubDelegate() {
        IMyInterface mock = createMock(IMyInterface.class);
        IMyInterface delegateTo = new IMyInterface() {
            private int i = 0;

            public int getInt(int k) {
                return ++i;
            }
        };
        expect(mock.getInt(5)).andReturn(3).andStubDelegateTo(delegateTo);
        expect(mock.getInt(20)).andStubDelegateTo(delegateTo);

        replay(mock);

        assertEquals(3, mock.getInt(5));
        assertEquals(1, mock.getInt(5));
        assertEquals(2, mock.getInt(5));
        assertEquals(3, mock.getInt(20));
        assertEquals(4, mock.getInt(20));

        verify(mock);
    }
    
    @Test
    public void testReturnException() {
        IMyInterface m = createMock(IMyInterface.class);
        IMyInterface delegateTo = new IMyInterface() {
            public int getInt(int k) {
                throw new ArithmeticException("Not good!");
            }
        };
        expect(m.getInt(5)).andDelegateTo(delegateTo);
        
        replay(m);

        try {
            m.getInt(5);
            fail();
        } catch (ArithmeticException e) {
            assertEquals("Not good!", e.getMessage());
        }
        
        verify(m);
    }

    @Test
    public void testWrongClass() {
        IMyInterface m = createMock(IMyInterface.class);
        expect(m.getInt(0)).andDelegateTo("allo");
        replay(m);
        try {
            m.getInt(0);
            fail("Should throw an exception");
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "Delegation to object [allo] is not implementing the mocked method [public abstract int org.easymock.tests2.DelegateToTest$IMyInterface.getInt(int)]",
                    e.getMessage());
        }
    }
    
    @Test
    public void nullDelegationNotAllowed() {
        IMyInterface mock = createMock(IMyInterface.class);
        try {
            expect(mock.getInt(1)).andDelegateTo(null);
            fail();
        } catch (NullPointerException expected) {
            assertEquals("delegated to object must not be null", expected
                    .getMessage());
        }
    }

    @Test
    public void nullStubDelegationNotAllowed() {
        IMyInterface mock = createMock(IMyInterface.class);
        try {
            expect(mock.getInt(1)).andStubDelegateTo(null);
            fail();
        } catch (NullPointerException expected) {
            assertEquals("delegated to object must not be null", expected
                    .getMessage());
        }
    }    
}
