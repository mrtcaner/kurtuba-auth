package com.kurtuba.auth.data.model;

public enum ClientType {

    MOBILE_CLIENT("mobile-client"),
    ADM_WEB_CLIENT("adm-web-client");

    private String clientTypeName;

    ClientType(String clientTypeName){
        this.clientTypeName = clientTypeName;
    }

    public String getClientTypeName(){
        return clientTypeName;
    }

    public static ClientType fromClientTypeName(String clientTypeName){
        if(ClientType.MOBILE_CLIENT.getClientTypeName().equals(clientTypeName))
            return ClientType.MOBILE_CLIENT;
        if(ClientType.ADM_WEB_CLIENT.getClientTypeName().equals(clientTypeName))
            return ClientType.ADM_WEB_CLIENT;
        return null;
    }

    @Override
    public String toString(){
        return this.clientTypeName;
    }
}
