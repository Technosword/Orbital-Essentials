package it.thedarksword.essentialsvc.handler;

import it.thedarksword.essentialsvc.messaging.Message;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MessageHandler {

    Class<? extends Message> value();
}
