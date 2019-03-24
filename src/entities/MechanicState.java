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
            return "wfw"; // return "Waiting for work.";
        }
    },
    FIXING_CAR {
        @Override
        public String toString() {
            return "ftc"; // return "Fixing car.";
        }
    },
    ALERTING_MANAGER {
        @Override
        public String toString() {
            return "amg"; // return "Alerting manager.";
        }
    },
    CHECKING_STOCK {
        @Override
        public String toString() {
            return "cks"; // return "Checking stock.";
        }
    },;
}
