package entities;

/**
 *
 * @author andre e joao
 */
public enum CustomerState {
    
	//Customer stats: lwc, prk, wrc, rcp, lnc
	
    NORMAL_LIFE_WITH_CAR {
        @Override
        public String toString() {
            return "lwc"; //"Normal life with car.";
        }
    },
    
    PARK {
        @Override
        public String toString() {
            return "prk"; // "Park.";
        }
    },
    
    WAITING_FOR_REPLACE_CAR {
        @Override
        public String toString() {
            return "wrc"; //return "Waiting for replace car.";
        }
    },
    
    RECEPTION {
        @Override
        public String toString() {
            return "rcp"; // return "Reception.";
        }
    },
    
    NORMAL_LIFE_WITHOUT_CAR {
        @Override
        public String toString() {
            return "lnc"; // return "Normal life without car.";
        }
    };
}
