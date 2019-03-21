package entities;

/**
 *
 * @author andre e joao
 */
public enum MechanicState {
    //Mechanic stats: wfw, ftc, amg, cks
    WAITING_FOR_WORK {
        @Override
        public String toString() {
            return "Waiting for work.";
        }
		public String toStringLog() {
            return "wfw";
        }
    },
    
    FIXING_CAR {
        @Override
        public String toString() {
            return "Fixing car.";
        }
		public String toStringLog() {
            return "ftc";
        }
    },
    
    ALERTING_MANAGER {
        @Override
        public String toString() {
            return "Alerting manager.";
        }
		public String toStringLog() {
            return "amg";
        }
    },
    
    CHECKING_STOCK {
        @Override
        public String toString() {
            return "Checking stock.";
        }
		public String toStringLog() {
            return "cks";
        }
    },;
    
	public String toStringLog() {
		return toStringLog();
	}
}
