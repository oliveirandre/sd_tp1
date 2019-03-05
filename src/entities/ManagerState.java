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
public enum ManagerState {
    
    ATTENDING_CUSTOMER {
        @Override
        public String toString() {
            return "Attending customer.";
        }
    },
    
    CHECKING_WHAT_TO_DO {
        @Override
        public String toString() {
            return "Checking what to do.";
        }
    },
    
    
    GETTING_NEW_PARTS {
        @Override
        public String toString() {
            return "Getting new parts.";
        }
    },
    
    POSTING_JOB {
        @Override
        public String toString() {
            return "Posting job.";
        }
    },
    
    ALERTING_CUSTOMER {
        @Override
        public String toString() {
            return "Alerting customer.";
        }
    },
    
    REPLENISH_STOCK {
        @Override
        public String toString() {
            return "Replenish stock.";
        }
    }
}
