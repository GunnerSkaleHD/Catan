package org.example.catan;

import javafx.scene.paint.Color;

// Real player class
// add play card? plus effect
public class StandardPlayer extends Player{
    
    public StandardPlayer(Color color) {
        super(color);
    }

//    @Override
//    public void buySettlement(Bank bank) {
//        if (bank.canBuildSettlement(this)) {
//            bank.buildSettlement(this);
//            System.out.println(color + " built a settlement");
//        }
//        else{
//            System.out.println(color + " can't built a settlement");
//        }
//    }
//
//    @Override
//    public void buyCity(Bank bank) {
//        if (bank.canBuildCity(this)) {
//            bank.buildCity(this);
//            System.out.println(color + " built a city");
//        }
//        else {
//            System.out.println(color + " can't build a city");
//        }
//    }
//
//    @Override
//    public void buyRoad(Bank bank) {
//        if(bank.canBuildRoad(this)){
//            bank.buildRoad(this);
//            System.out.println(color + " built a road");
//        }
//        else{
//            System.out.println(color + " can't build a road");
//        }
//    }
//
//    @Override
//    public void buyCard(Bank bank) {
//        if (bank.buyDevelopmentCard(this) != null) {
//            System.out.println(color + " bought a development card");
//        }
//        else {
//            System.out.println(color + " can't buy a development card");
//        }
//    }

    // placeholder for trade
//    @Override
//    public void trade(){
//        System.out.println("Not yet");
//    }
}


