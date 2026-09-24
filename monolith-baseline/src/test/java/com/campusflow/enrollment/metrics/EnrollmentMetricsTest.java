package com.campusflow.enrollment.metrics;

import com.campusflow.enrollment.event.StudentEnrolledInClassEvent;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EnrollmentMetricsTest {

    @Test
    void incrementsCounterWhenStudentEnrolled() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        EnrollmentMetrics metrics = new EnrollmentMetrics(registry);

        Counter counter = registry.find("campusflow.enrollments.completed").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isZero();

        metrics.onStudentEnrolledInClass(new StudentEnrolledInClassEvent(
                1L, 2L, "alex@example.com", "Alex", 3L, "Algebra I", "Fall 2026"));

        assertThat(counter.count()).isEqualTo(1.0);
    }
}
