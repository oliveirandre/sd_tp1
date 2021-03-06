package entities;

/**
 *
 * @author andre e joao
 */
public enum ManagerState {

    //Manager states: aCtm, cwtd, gnwp, pjob, aCtm, rSto
    CHECKING_WHAT_TO_DO {
        @Override
        public String toString() {
            return "cwtd";
        }
    },
	ATTENDING_CUSTOMER {
        @Override
        public String toString() {
            return "aCtm";
        }
    },
    GETTING_NEW_PARTS {
        @Override
        public String toString() {
            return "gnwp";
        }
    },
    POSTING_JOB {
        @Override
        public String toString() {
            return "pjob";
        }
    },
    ALERTING_CUSTOMER {
        @Override
        public String toString() {
            return "aCtm";
        }
    },
    REPLENISH_STOCK {
        @Override
        public String toString() {
            return "rSto";
        }
    };
}
