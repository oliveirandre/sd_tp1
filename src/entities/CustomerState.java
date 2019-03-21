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
    
	//Customer stats: lwc, prk, wrc, rcp, lnc
	
    NORMAL_LIFE_WITH_CAR {
        @Override
        public String toString() {
            return "Normal life with car.";
        }
		public String toStringLog() {
            return "lwc";
        }
    },
    
    PARK {
        @Override
        public String toString() {
            return "Park.";
        }
		public String toStringLog() {
            return "prk";
        }
    },
    
    WAITING_FOR_REPLACE_CAR {
        @Override
        public String toString() {
            return "Waiting for replace car.";
        }
		public String toStringLog() {
            return "wrc";
        }
    },
    
    RECEPTION {
        @Override
        public String toString() {
            return "Reception.";
        }
		public String toStringLog() {
            return "rcp";
        }
    },
    
    NORMAL_LIFE_WITHOUT_CAR {
        @Override
        public String toString() {
            return "Normal life without car.";
        }
		public String toStringLog() {
            return "lnc";
        }
    };
    
	public String toStringLog() {
		return toStringLog();
	}
}
