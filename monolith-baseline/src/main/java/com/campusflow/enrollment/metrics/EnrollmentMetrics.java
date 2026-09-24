package com.campusflow.enrollment.metrics;

import com.campusflow.enrollment.event.StudentEnrolledInClassEvent;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Application-specific Micrometer metrics for successful CampusFlow enrollments.
 */
@Component
public class EnrollmentMetrics {

    private final Counter enrollmentsCompleted;

    public EnrollmentMetrics(MeterRegistry meterRegistry) {
        this.enrollmentsCompleted = Counter.builder("campusflow.enrollments.completed")
                .description("Number of successful CampusFlow enrollments")
                .register(meterRegistry);
    }

    @EventListener
    public void onStudentEnrolledInClass(StudentEnrolledInClassEvent event) {
        enrollmentsCompleted.increment();
    }
}
