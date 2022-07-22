package com.lncn.rsql;

import io.quarkus.remote.ClientStatus;

/**
 * @Classname IllegalStateException
 * @Description TODO
 * @Date 2022/7/17 12:55
 * @Created by byco
 */
public class IllegalStateException extends IllegalArgumentException {
    public IllegalStateException() {
        super();
    }


    public IllegalStateException(String s) {
        super(s);
    }

    public IllegalStateException(String message, Throwable cause) {
        super(message, cause);
    }


    public IllegalStateException(Throwable cause) {
        super(cause);
    }

    public IllegalStateException(State state, ClientStatus action){
        super("Unsupported action : " + action +  " in state: " + state);
    }


}
