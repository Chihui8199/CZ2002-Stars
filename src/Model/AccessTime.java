package Model;

import java.io.Serializable;
import java.time.LocalDateTime;

import Exception.InvalidAccessTimeException;

public class AccessTime implements Serializable {

    private LocalDateTime start;
    private LocalDateTime end;

    public AccessTime(LocalDateTime start, LocalDateTime end) throws InvalidAccessTimeException {
        if (start.isAfter(end) || start.isEqual(end)) {
            throw new InvalidAccessTimeException();
        }
        this.start = start;
        this.end = end;
    }

    public boolean isWithinAccessTime(LocalDateTime localDateTime) {
        if (localDateTime.isAfter(start) && localDateTime.isBefore(end)) {
            return true;
        }

        return false;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return start.toString() + " to " + end.toString();
    }
}
