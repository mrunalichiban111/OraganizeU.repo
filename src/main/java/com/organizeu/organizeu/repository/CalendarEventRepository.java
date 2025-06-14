package com.organizeu.organizeu.repository;

import com.organizeu.organizeu.model.CalendarEvent;
import com.organizeu.organizeu.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {
    List<CalendarEvent> findByOwner(User owner);

    @Query("SELECT e FROM CalendarEvent e WHERE e.owner = :owner AND e.startAt BETWEEN :start AND :end")
    List<CalendarEvent> findByOwnerAndStartAtBetween(@Param("owner") User owner,
                                                      @Param("start") LocalDateTime start,
                                                      @Param("end") LocalDateTime end);

    @Query("SELECT e FROM CalendarEvent e WHERE e.owner = :owner AND " +
           "(e.startAt BETWEEN :start AND :end OR " +
           "e.endAt BETWEEN :start AND :end OR " +
           "(e.startAt <= :start AND e.endAt >= :end) OR " +
           "e.recurrenceRule IS NOT NULL)")
    List<CalendarEvent> findByOwnerAndRangeOrRecurring(@Param("owner") User owner,
                                                      @Param("start") LocalDateTime start,
                                                      @Param("end") LocalDateTime end);

    @Query("SELECT e FROM CalendarEvent e WHERE e.owner = :owner AND e.startAt BETWEEN :start AND :end")
    List<CalendarEvent> findByOwnerAndStartTimeBetween(
            @Param("owner") User owner,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    long countByOwner(User owner);
} 