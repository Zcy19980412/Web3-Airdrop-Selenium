package org.main;


import org.example.ActiveXAccounts;
import org.r2.ClaimTestTokens;
import org.r2.ExecuteTasks;

public class Start {

    public static void main(String[] args){
        activeAccounts();
        r2Act();
    }

    public static void r2Act(){
        ClaimTestTokens.act();
        ExecuteTasks.act();
    }

    public static void activeAccounts(){
        ActiveXAccounts.act();
    }

}
