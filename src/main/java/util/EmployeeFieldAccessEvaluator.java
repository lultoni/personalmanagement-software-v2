package util;

import model.db.Employee;

public class EmployeeFieldAccessEvaluator {

    /** Diese Klasse prüft, ob bestimmte Felder eines Mitarbeiters für einen adneren Mitarbeiter sichtbar sind
     * (wer kann welche persönlichen Daten von wem sehen?)
      */

        // Rollen mit uneingeschränktem Zugang
        private static final String CEO_ROLE = "role-ceo";

        /**
         * Kann der viewer das vollständige Mitarbeiterprofil sehen?
         */
        public static boolean canViewExtendedData(Employee viewer, Employee target) {
            if (viewer == null || target == null) return false;

            // 1. Eigene Daten
            if (viewer.getId() == target.getId()) return true;

            // 2. CEO, HR-Head, IT-Admin
            if (viewer.getRoleId().equals(CEO_ROLE) || viewer.isHrHead() || viewer.isItAdmin()) {
                return true;
            }

            // 3. HR-Mitarbeiter
            if (viewer.isHr()) return true;

            // 4. Vorgesetzter in der gleichen Abteilung
            if (viewer.isManager()
                    && viewer.getDepartmentId() != null
                    && viewer.getDepartmentId().equals(target.getDepartmentId())) {
                return true;
            }

            return false;
        }

        /**
         * Jeder darf Basisinformationen sehen.
         */
        public static boolean canViewBasicData(Employee viewer, Employee target) {
            return viewer != null && target != null;
        }

        /**
         * Beispiel: Darf Telefonnummer angezeigt werden?
         */
        public static boolean canViewPhoneNumber(Employee viewer, Employee target) {
            return canViewExtendedData(viewer, target);
        }

        public static boolean canViewAddress(Employee viewer, Employee target) {
            return canViewExtendedData(viewer, target);
        }

        public static boolean canViewCompletedTrainings(Employee viewer, Employee target) {
            return canViewExtendedData(viewer, target);
        }

        public static boolean canViewQualifications(Employee viewer, Employee target) {
            return canViewExtendedData(viewer, target);
        }

        // ... du kannst weitere Detailmethoden ergänzen, z. B.:
        public static boolean canViewHireDate(Employee viewer, Employee target) {
            return canViewExtendedData(viewer, target);
        }
    }

