package com.gtinnox.edutrack

import com.gtinnox.edutrack.data.local.database.DatabaseSeed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CourseworkUnitTests {

    @Test
    fun testDatabaseSeedCurriculum_containsRequiredSubjects() {
        val subjects = DatabaseSeed.initialSubjects
        assertTrue("Coursework requires multiple subjects", subjects.size >= 5)

        val codes = subjects.map { it.code }
        assertTrue("Contains CS 201 Algorithms", codes.contains("CS 201"))
        assertTrue("Contains MATH 210 Linear Algebra", codes.contains("MATH 210"))
        assertTrue("Contains CS 240 Computer Systems", codes.contains("CS 240"))
    }

    @Test
    fun testDatabaseSeedSessions_linkedToSubjects() {
        val subjects = DatabaseSeed.initialSubjects
        val sessions = DatabaseSeed.initialSessions
        val subjectIds = subjects.map { it.id }.toSet()

        assertTrue("Curriculum must have initial study sessions", sessions.isNotEmpty())
        for (session in sessions) {
            assertTrue("Session subject must exist in subjects table", subjectIds.contains(session.subjectId))
            assertTrue("Session duration must be positive", session.durationMinutes > 0)
            assertTrue("Focus score must be within 0..100", session.focusScore in 0..100)
        }
    }

    @Test
    fun testDatabaseSeedProfile_isPopulated() {
        val profile = DatabaseSeed.initialProfile
        assertEquals("user-default-1", profile.id)
        assertEquals("Julian Vance", profile.name)
        assertTrue(profile.isOnboarded)
        assertTrue(profile.preferredStudyHours > 0)
    }

    @Test
    fun testSubjectValidationLogic() {
        fun validateSubject(name: String, code: String, hours: Int): Boolean {
            return name.isNotBlank() && code.isNotBlank() && hours > 0
        }

        assertTrue(validateSubject("Algorithms", "CS 201", 7))
        assertFalse(validateSubject("", "CS 201", 7))
        assertFalse(validateSubject("Algorithms", "", 7))
        assertFalse(validateSubject("Algorithms", "CS 201", 0))
        assertFalse(validateSubject("Algorithms", "CS 201", -3))
    }

    @Test
    fun testSessionValidationLogic() {
        fun validateSession(subjectId: String, duration: Int): Boolean {
            return subjectId.isNotBlank() && duration > 0
        }

        assertTrue(validateSession("subj-1", 45))
        assertFalse(validateSession("", 45))
        assertFalse(validateSession("subj-1", 0))
        assertFalse(validateSession("subj-1", -15))
    }

    @Test
    fun testScheduleEventValidationLogic() {
        fun validateScheduleEvent(title: String, subjectId: String, startTime: String, endTime: String): Boolean {
            return title.isNotBlank() && subjectId.isNotBlank() && startTime.isNotBlank() && endTime.isNotBlank() && startTime < endTime
        }

        assertTrue(validateScheduleEvent("Algorithms Lecture", "subj-1", "09:00", "10:30"))
        assertTrue(validateScheduleEvent("Lab Session", "subj-2", "14:00", "16:00"))
        assertFalse(validateScheduleEvent("", "subj-1", "09:00", "10:30"))
        assertFalse(validateScheduleEvent("Seminar", "", "09:00", "10:30"))
        assertFalse(validateScheduleEvent("Seminar", "subj-1", "11:00", "10:00"))
        assertFalse(validateScheduleEvent("Seminar", "subj-1", "10:00", "10:00"))
    }

    @Test
    fun testFormatSessionDateTime_doesNotCrash() {
        val formatted = com.gtinnox.edutrack.ui.history.formatSessionDateTime("2026-11-25T09:00:00Z")
        assertTrue("Formatted date time must not be blank", formatted.isNotBlank())
        assertTrue("Must contain formatted time or date", formatted.contains("Nov 25") || formatted.contains("Today") || formatted.contains("Yesterday"))

        val fallback = com.gtinnox.edutrack.ui.history.formatSessionDateTime("2026-11-25")
        assertTrue(fallback.isNotBlank())
    }
}
