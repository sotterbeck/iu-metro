package de.sotterbeck.iumetro.domain.ticket;

public record ValidationResult(boolean allowGate,
                               boolean recordUsage,
                               boolean removeTicket,
                               String reason) {

    public static ValidationResult allowAndRecord() {
        return new ValidationResult(true, true, false, null);
    }

    public static ValidationResult deny(String reason) {
        return new ValidationResult(false, false, false, reason);
    }

    public static ValidationResult allowRecordRemove(String reason) {
        return new ValidationResult(true, true, true, reason);
    }

    public static ValidationResult allowNoRecord(String reason) {
        return new ValidationResult(true, false, false, reason);
    }

}
