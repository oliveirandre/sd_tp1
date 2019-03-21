package entities;

/**
 *
 * @author andre e joao
 */
public enum ManagerState {
    
	//Manager states: aCtm, cwtd, gnwp, pjob, aCtm, rSto
    ATTENDING_CUSTOMER {
        @Override
        public String toString() {
            return "Attending customer.";
        }
		public String toStringLog() {
            return "aCtm";
        }
    },
    
    CHECKING_WHAT_TO_DO {
        @Override
        public String toString() {
            return "Checking what to do.";
        }
		public String toStringLog() {
            return "cwtd";
        }
    },
    
    
    GETTING_NEW_PARTS {
        @Override
        public String toString() {
            return "Getting new parts.";
        }
		public String toStringLog() {
            return "gnwp";
        }
    },
    
    POSTING_JOB {
        @Override
        public String toString() {
            return "Posting job.";
        }
		public String toStringLog() {
            return "pjob";
        }
    },
    
    ALERTING_CUSTOMER {
        @Override
        public String toString() {
            return "Alerting customer.";
        }
		public String toStringLog() {
            return "aCtm";
        }
    },
    
    REPLENISH_STOCK {
        @Override
        public String toString() {
            return "Replenish stock.";
        }
		public String toStringLog() {
            return "rSto";
        }
    };

	public String toStringLog() {
		return toStringLog();
	}
}
