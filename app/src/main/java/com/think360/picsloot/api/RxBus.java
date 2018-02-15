package com.think360.picsloot.api;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

public class RxBus {

    public RxBus() {
    }

    private PublishSubject<Object> bus = PublishSubject.create();

    public void send(Object o) {
        bus.onNext(o);

    }

    public Observable<Object> toObservable() {
        return bus;
    }

    public boolean hasObservable() {
        return bus.hasObservers();
    }

}
