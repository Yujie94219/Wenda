package com.wenda.async.handler;

import com.wenda.async.EventHandler;
import com.wenda.async.EventModel;
import com.wenda.async.EventType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoginExceptionHandler implements EventHandler {


    @Override
    public void doHandle(EventModel model) {
        
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return null;
    }
}
