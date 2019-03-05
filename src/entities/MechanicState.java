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
public enum MechanicState {
    
    WAITING_FOR_WORK {
        @Override
        public String toString() {
            return "Waiting for work.";
        }
    },
    
    FIXING_CAR {
        @Override
        public String toString() {
            return "Fixing car.";
        }
    },
    
    ALERTING_MANAGER {
        @Override
        public String toString() {
            return "Alerting manager.";
        }
    },
    
    CHECKING_STOCK {
        @Override
        public String toString() {
            return "Checking stock.";
        }
    },
    
}
