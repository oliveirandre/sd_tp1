/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

/**
 *
 * @author andre
 */
public enum CustomerState {
    
    NORMAL_LIFE_WITH_CAR {
        @Override
        public String toString() {
            return "Normal life with car.";
        }
    },
    
    PARK {
        @Override
        public String toString() {
            return "Park.";
        }
    },
    
    WAITING_FOR_REPLACE_CAR {
        @Override
        public String toString() {
            return "Waiting for replace car.";
        }
    },
    
    RECEPTION {
        @Override
        public String toString() {
            return "Reception.";
        }
    },
    
    NORMAL_LIFE_WITHOUT_CAR {
        @Override
        public String toString() {
            return "Normal life without car.";
        }
    }
    
}
