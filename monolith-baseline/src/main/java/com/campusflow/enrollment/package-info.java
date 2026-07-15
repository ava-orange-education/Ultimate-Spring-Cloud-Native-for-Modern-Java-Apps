/**
 * Enrollment domain — registers students in classes and enforces eligibility rules.
 * <p>
 * Role in the monolith: coordination workflow linking students and classes.
 * Publishes {@link com.campusflow.enrollment.event.StudentEnrolledInClassEvent} after
 * successful enrollment. Depends on student and schoolclass modules.
 */
package com.campusflow.enrollment;
