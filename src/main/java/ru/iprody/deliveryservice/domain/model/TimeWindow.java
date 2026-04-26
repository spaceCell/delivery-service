package ru.iprody.deliveryservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalTime;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeWindow {

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    public TimeWindow(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Time window boundaries must be provided");
        }
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("Time window end must be after start");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
