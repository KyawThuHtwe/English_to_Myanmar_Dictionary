package com.cu.englishtomyanmardictionary.Model;

public class Data {
    String id,word,state,def,approve,user_id;

    public Data(String id, String word, String state, String def,String approve,String user_id) {
        this.id = id;
        this.word = word;
        this.state = state;
        this.def = def;
        this.approve=approve;
        this.user_id=user_id;
    }

    public String getApprove() {
        return approve;
    }

    public void setApprove(String approve) {
        this.approve = approve;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }
}
