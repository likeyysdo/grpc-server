package org.acme.statement;

import io.quarkus.remote.ClientStatus;

/**
 * @Classname State
 * @Description TODO
 * @Date 2022/7/17 11:39
 * @Created by byco
 */
public enum State {
    UNINITIALIZED{
        @Override
        public boolean check(ClientStatus action) {
            switch (action){
                case CLIENT_STATUS_INITIALIZE : return true;
                case CLIENT_STATUS_SEND_STATEMENT : return false;
                case CLIENT_STATUS_RECEIVE_DATA : return false;
                case CLIENT_STATUS_FINISHED : return false;
                case CLIENT_STATUS_UNKNOWN : return false;
                case CLIENT_STATUS_CANCEL : return true;
                case CLIENT_STATUS_ERROR : return true;
                default: return false;
            }
        }

        @Override
        public State doAction(ClientStatus action) {
            switch (action){
                case CLIENT_STATUS_INITIALIZE : return State.INITIALIZED;
                case CLIENT_STATUS_CANCEL : return State.CANCELED;
                case CLIENT_STATUS_ERROR : throw new IllegalStateException("received ERROR action : "
                    + ClientStatus.CLIENT_STATUS_ERROR.name());
                default :throw new IllegalStateException(this,action);
            }
        }
    },
    INITIALIZED {
        @Override
        public boolean check(ClientStatus action) {
            switch (action){
                case CLIENT_STATUS_INITIALIZE : return false;
                case CLIENT_STATUS_SEND_STATEMENT : return true;
                case CLIENT_STATUS_RECEIVE_DATA : return false;
                case CLIENT_STATUS_FINISHED : return false;


                case CLIENT_STATUS_UNKNOWN : return false;
                case CLIENT_STATUS_CANCEL : return true;
                case CLIENT_STATUS_ERROR : return true;
                default: return false;
            }
        }

        @Override
        public State doAction(ClientStatus action) {
            switch (action){
                case CLIENT_STATUS_SEND_STATEMENT : return State.STATEMENT_PREPARED;
                case CLIENT_STATUS_CANCEL : return State.CANCELED;
                case CLIENT_STATUS_ERROR : throw new IllegalStateException("received ERROR action : "
                    + ClientStatus.CLIENT_STATUS_ERROR.name());
                default :throw new IllegalStateException(this,action);
            }
        }
    },
    STATEMENT_PREPARED {
        @Override
        public boolean check(ClientStatus action) {
            switch (action){
                case CLIENT_STATUS_INITIALIZE : return false;
                case CLIENT_STATUS_SEND_STATEMENT : return false;
                case CLIENT_STATUS_RECEIVE_DATA : return true;
                case CLIENT_STATUS_FINISHED : return false;

                case CLIENT_STATUS_UNKNOWN : return false;
                case CLIENT_STATUS_CANCEL : return true;
                case CLIENT_STATUS_ERROR : return true;
                default: return false;
            }
        }

        @Override
        public State doAction(ClientStatus action) {
            switch (action){
                case CLIENT_STATUS_RECEIVE_DATA : return State.TRANSMITTING;
                case CLIENT_STATUS_CANCEL : return State.CANCELED;
                case CLIENT_STATUS_ERROR : throw new IllegalStateException("received ERROR action : "
                    + ClientStatus.CLIENT_STATUS_ERROR.name());
                default :throw new IllegalStateException(this,action);
            }
        }
    },
    TRANSMITTING {
        @Override
        public boolean check(ClientStatus action) {
            switch (action){
                case CLIENT_STATUS_INITIALIZE : return false;
                case CLIENT_STATUS_SEND_STATEMENT : return false;
                case CLIENT_STATUS_RECEIVE_DATA : return true;
                case CLIENT_STATUS_FINISHED : return false;

                case CLIENT_STATUS_UNKNOWN : return false;
                case CLIENT_STATUS_CANCEL : return true;
                case CLIENT_STATUS_ERROR : return true;
                default: return false;
            }
        }

        @Override
        public State doAction(ClientStatus action) {
            switch (action){
                case CLIENT_STATUS_RECEIVE_DATA : return State.TRANSMITTING;
                case CLIENT_STATUS_CANCEL : return State.CANCELED;
                case CLIENT_STATUS_ERROR : throw new IllegalStateException("received ERROR action : "
                    + ClientStatus.CLIENT_STATUS_ERROR.name());
                default :throw new IllegalStateException(this,action);
            }
        }
    },
    TRANSMIT_FINISHED {
        @Override
        public boolean check(ClientStatus action) {
            switch (action){
                case CLIENT_STATUS_INITIALIZE : return false;
                case CLIENT_STATUS_SEND_STATEMENT : return true;
                case CLIENT_STATUS_RECEIVE_DATA : return true;
                case CLIENT_STATUS_FINISHED : return true;

                case CLIENT_STATUS_UNKNOWN : return false;
                case CLIENT_STATUS_CANCEL : return true;
                case CLIENT_STATUS_ERROR : return true;
                default: return false;
            }
        }

        @Override
        public State doAction(ClientStatus action) {
            switch (action){
                case CLIENT_STATUS_SEND_STATEMENT : return State.STATEMENT_PREPARED;
                case CLIENT_STATUS_FINISHED : return State.FINISHED;
                case CLIENT_STATUS_CANCEL : return State.CANCELED;
                case CLIENT_STATUS_ERROR : throw new IllegalStateException("received ERROR action : "
                    + ClientStatus.CLIENT_STATUS_ERROR.name());
                default :throw new IllegalStateException(this,action);
            }
        }
    },
    FINISHED {
        @Override
        public boolean check(ClientStatus action) {
            switch (action){
                case CLIENT_STATUS_INITIALIZE : return false;
                case CLIENT_STATUS_SEND_STATEMENT : return false;
                case CLIENT_STATUS_RECEIVE_DATA : return false;
                case CLIENT_STATUS_FINISHED : return true;

                case CLIENT_STATUS_UNKNOWN : return false;
                case CLIENT_STATUS_CANCEL : return true;
                case CLIENT_STATUS_ERROR : return true;
                default: return false;
            }
        }

        @Override
        public State doAction(ClientStatus action) {
            switch (action){
                case CLIENT_STATUS_FINISHED : return State.TRANSMIT_FINISHED;
                case CLIENT_STATUS_CANCEL : return State.CANCELED;
                case CLIENT_STATUS_ERROR : throw new IllegalStateException("received ERROR action : "
                    + ClientStatus.CLIENT_STATUS_ERROR.name());
                default :throw new IllegalStateException(this,action);
            }
        }
    },
    CANCELED {
        @Override
        public boolean check(ClientStatus action) {
            switch (action){
                case CLIENT_STATUS_INITIALIZE : return true;
                case CLIENT_STATUS_SEND_STATEMENT : return true;
                case CLIENT_STATUS_RECEIVE_DATA : return true;
                case CLIENT_STATUS_FINISHED : return true;

                case CLIENT_STATUS_UNKNOWN : return false;
                case CLIENT_STATUS_CANCEL : return true;
                case CLIENT_STATUS_ERROR : return true;
                default: return false;
            }
        }

        @Override
        public State doAction(ClientStatus action) {
            switch (action){
                case CLIENT_STATUS_INITIALIZE :
                case CLIENT_STATUS_SEND_STATEMENT :
                case CLIENT_STATUS_RECEIVE_DATA :
                case CLIENT_STATUS_FINISHED :
                case CLIENT_STATUS_CANCEL : return State.CANCELED;
                case CLIENT_STATUS_ERROR : throw new IllegalStateException("received ERROR action : "
                    + ClientStatus.CLIENT_STATUS_ERROR.name());
                default :throw new IllegalStateException(this,action);
            }
        }
    },
    ERROR {
        @Override
        public boolean check(ClientStatus action) {
            switch (action){
                case CLIENT_STATUS_INITIALIZE : return true;
                case CLIENT_STATUS_SEND_STATEMENT : return true;
                case CLIENT_STATUS_RECEIVE_DATA : return true;
                case CLIENT_STATUS_FINISHED : return true;

                case CLIENT_STATUS_UNKNOWN : return true;
                case CLIENT_STATUS_CANCEL : return true;
                case CLIENT_STATUS_ERROR : return true;
                default: return false;
            }
        }

        @Override
        public State doAction(ClientStatus action) {
            throw new IllegalStateException("Server state is in Error State : "
                + State.ERROR.name());
        }
    };



    public abstract boolean check(ClientStatus action);
    public abstract State doAction(ClientStatus action);



}
