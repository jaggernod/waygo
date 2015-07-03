package com.waygo.utils;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;

public class RxUtilsTest {

    @Test(expected = NullPointerException.class)
    public void testToListThrowsExceptionWhenNoItemsAreProvided() {
        //noinspection NullArgumentToVariableArgMethod,ConstantConditions
        RxUtils.toList(null);
    }

    @Test
    public void testToListReturnsCombinedListOfItems() {
        assertEquals(3, RxUtils.toList(new Object[]{"1", "2", "3"}).size());
    }

    @Test
    public void testToListReturnsCombinedListOfItems1() {
        List<Observable<String>> list = Arrays.asList(Observable.just("1"),
                                                      Observable.just("2"),
                                                      Observable.just("1"),
                                                      Observable.just("2"));
        TestSubscriber<List<String>> observer = new TestSubscriber<>();

        RxUtils.toObservableList(list)
               .subscribe(observer);

        observer.awaitTerminalEvent();
        assertEquals("Invalid number of repositories",
                     4,
                     observer.getOnNextEvents().get(0).size());
    }
}
