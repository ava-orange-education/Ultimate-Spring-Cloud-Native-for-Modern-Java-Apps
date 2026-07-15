/**
 * Attendance domain — records daily presence or absence per student and class.
 * <p>
 * Role in the monolith: operational workflow that validates enrollment before recording.
 * Depends on student, schoolclass, and enrollment data. Strong extraction candidate,
 * but more complex than notifications because it must verify enrollment across a boundary.
 */
package com.campusflow.attendance;
