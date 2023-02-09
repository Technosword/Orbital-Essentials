package it.thedarksword.essentialsvc.messaging;

import it.thedarksword.essentialsvc.handler.MessageHandler;
import it.thedarksword.essentialsvc.handler.MessageListener;
import it.thedarksword.essentialsvc.messaging.client.*;
import it.thedarksword.essentialsvc.messaging.common.TeleportStateMessage;
import it.thedarksword.essentialsvc.messaging.common.UpdateBackMessage;
import it.thedarksword.essentialsvc.messaging.server.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class AbstractMessenger {

    public static final String CHANNEL = "essentialsvc:channel";

    private final Map<Integer, Class<? extends Message>> incoming = new HashMap<>();
    private final Map<Class<? extends Message>, Integer> outgoing = new HashMap<>();

    private final Map<Integer, Class<? extends Message>> outgoingClasses = new HashMap<>();

    private final Map<Class<? extends Message>, Method> handler = new HashMap<>();

    private final MessageListener messageListener;

    protected AbstractMessenger(MessageListener messageListener) {
        this(false, messageListener);
    }

    protected AbstractMessenger(boolean bungee, MessageListener messageListener) {
        if (bungee) initMessages(this::registerOutgoing, this::registerIncoming);
        else initMessages(this::registerIncoming, this::registerOutgoing);
        this.messageListener = messageListener;
        setupListener();
    }

    private void setupListener() {
        for(Method method : messageListener.getClass().getMethods()) {
            if(method.isAnnotationPresent(MessageHandler.class)) {
                handler.put(method.getAnnotation(MessageHandler.class).value(), method);
            }
        }
    }

    private void initMessages(BiConsumer<Integer, Class<? extends Message>> spigotboundMessages, BiConsumer<Integer, Class<? extends Message>> bungeeboundMessages) {
        spigotboundMessages.accept(0x1, ServerTeleportToLocationMessage.class);
        spigotboundMessages.accept(0x2, ServerHomeLocationMessage.class);
        spigotboundMessages.accept(0x3, ServerUpdatePlayersMessage.class);
        spigotboundMessages.accept(0x4, ServerUpdatePlayerListMessage.class);
        spigotboundMessages.accept(0x5, ServerTeleportToPlayerMessage.class);

        bungeeboundMessages.accept(0x1, ClientSendConfigMessage.class);
        bungeeboundMessages.accept(0x2, ClientSpawnMessage.class);
        bungeeboundMessages.accept(0x3, ClientMsgMessage.class);
        bungeeboundMessages.accept(0x4, ClientHomeMessage.class);
        bungeeboundMessages.accept(0x5, ClientHomeSetMessage.class);
        bungeeboundMessages.accept(0x6, ClientHomeListMessage.class);
        bungeeboundMessages.accept(0x7, ClientHelpopMessage.class);
        bungeeboundMessages.accept(0x8, ClientRequestPlayerListMessage.class);
        bungeeboundMessages.accept(0x9, ClientTeleportToPlayerMessage.class);
        bungeeboundMessages.accept(0xA, ClientUpdateBackMessage.class);
        bungeeboundMessages.accept(0xB, ClientBackMessage.class);
        bungeeboundMessages.accept(0xD, ClientHelpMessage.class);
        bungeeboundMessages.accept(0xE, ServerContainsPluginMessage.class);

        register(0x100, TeleportStateMessage.class);
        register(0x101, UpdateBackMessage.class);
    }

    private void register(Integer id, Class<? extends Message> message) {
        this.registerIncoming(id, message);
        this.registerOutgoing(id, message);
    }

    private void registerIncoming(int id, Class<? extends Message> packet) {
        this.incoming.put(id, packet);
        try {
            this.createIncomingMessage(id);
        } catch (IllegalStateException e) {
            this.incoming.remove(id);
            throw new IllegalArgumentException(e.getMessage(), e.getCause());
        }
    }

    private void registerOutgoing(int id, Class<? extends Message> message) {
        this.outgoing.put(message, id);
        this.outgoingClasses.put(id, message);
    }

    public final Message createIncomingMessage(int id) {
        Class<? extends Message> message = this.incoming.get(id);
        if (message == null) {
            throw new IllegalArgumentException("Invalid packet id: " + id);
        }

        try {
            Constructor<? extends Message> constructor = message.getDeclaredConstructor();
            if(!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }

            return constructor.newInstance();
        } catch(NoSuchMethodError e) {
            throw new IllegalStateException("Message \"" + id + ", " + message.getName() + "\" does not have a no-params constructor for instantiation.");
        } catch(Exception e) {
            throw new IllegalStateException("Failed to instantiate message \"" + id + ", " + message.getName() + "\".", e);
        }
    }

    public void invokeHandler(Object... params) {
        if(params.length == 0) {
            throw new IllegalArgumentException("Invalid params length");
        }
        if(!(params[0] instanceof Message)) {
            throw new IllegalArgumentException("Invalid first param, he need to be a message instead of \"" + params[0].getClass().getSimpleName() + "\"");
        }
        Message message = (Message) params[0];
        Method method = handler.get(message.getClass());
        if(method == null) {
            throw new IllegalArgumentException("Invalid message class: " + message.getClass().getSimpleName());
        }
        try {
            method.invoke(messageListener, params);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Method " + method.getName() + " of message " + message.getClass().getSimpleName() + " is not public");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public final int getOutgoingId(Class<? extends Message> messageClass) {
        Integer messageId = this.outgoing.get(messageClass);
        if(messageId == null) {
            throw new IllegalArgumentException("Unregistered outgoing message class: " + messageClass.getName());
        }

        return messageId;
    }

    public final int getOutgoingId(Message message) {
        return getOutgoingId(message.getClass());
    }

    public final Class<? extends Message> getOutgoingClass(int id) {
        Class<? extends Message> message = this.outgoingClasses.get(id);
        if(message == null) {
            throw new IllegalArgumentException("Invalid message id: " + id);
        }

        return message;
    }
}
