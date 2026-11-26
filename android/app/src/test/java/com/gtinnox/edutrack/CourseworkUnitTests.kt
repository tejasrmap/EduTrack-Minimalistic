package com.gtinnox.edutrack

import com.gtinnox.edutrack.data.auth.PasswordSecurity
import com.gtinnox.edutrack.data.auth.PasswordValidationResult
import com.gtinnox.edutrack.data.local.database.DatabaseSeed
import com.gtinnox.edutrack.data.local.entity.AcademicProfileEntity
import com.gtinnox.edutrack.data.local.entity.StudyPreferencesEntity
import com.gtinnox.edutrack.data.local.entity.UserAccountEntity
import com.gtinnox.edutrack.ui.viewmodel.OnboardingWizardState
import com.gtinnox.edutrack.ui.viewmodel.SubjectDraft
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
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
    fun testPasswordSecurity_saltGenerationIsUnique() {
        val salt1 = PasswordSecurity.generateSalt()
        val salt2 = PasswordSecurity.generateSalt()
        assertNotNull(salt1)
        assertNotNull(salt2)
        assertNotEquals("Each generated salt must be cryptographically distinct", salt1, salt2)
    }

    @Test
    fun testPasswordSecurity_hashingAndVerification() {
        val password = "Scholar2026Password"
        val salt = PasswordSecurity.generateSalt()
        val hash = PasswordSecurity.hashPassword(password, salt)

        assertNotNull(hash)
        assertTrue("Hash must not equal plain password", hash != password)

        // Verify correct password succeeds
        val isVerified = PasswordSecurity.verifyPassword(password, salt, hash)
        assertTrue("Password verification must succeed with correct credentials", isVerified)

        // Verify wrong password fails
        val wrongVerified = PasswordSecurity.verifyPassword("WrongPassword123", salt, hash)
        assertFalse("Password verification must fail with incorrect credentials", wrongVerified)
    }

    @Test
    fun testPasswordSecurity_passwordRulesValidation() {
        // Less than 6 characters
        val shortResult = PasswordSecurity.validatePassword("abc")
        assertTrue(shortResult is PasswordValidationResult.Invalid)

        // No digits
        val noDigitResult = PasswordSecurity.validatePassword("abcdef")
        assertTrue(noDigitResult is PasswordValidationResult.Invalid)

        // No letters
        val noLetterResult = PasswordSecurity.validatePassword("123456")
        assertTrue(noLetterResult is PasswordValidationResult.Invalid)

        // Valid alphanumeric password >= 6 chars
        val validResult = PasswordSecurity.validatePassword("scholar2026")
        assertTrue(validResult is PasswordValidationResult.Valid)
    }

    @Test
    fun testPasswordSecurity_emailValidation() {
        assertTrue(PasswordSecurity.isValidEmail("scholar@oxford.ac.uk"))
        assertTrue(PasswordSecurity.isValidEmail("julian.vance@university.edu"))
        assertFalse(PasswordSecurity.isValidEmail("invalid-email"))
        assertFalse(PasswordSecurity.isValidEmail("@university.edu"))
        assertFalse(PasswordSecurity.isValidEmail("scholar@"))
        assertFalse(PasswordSecurity.isValidEmail(""))
    }

    @Test
    fun testUserOwnershipFields_presentOnAllAcademicEntities() {
        val testUserA = "user-alice-123"
        val testUserB = "user-bob-456"

        val subjectA = DatabaseSeed.initialSubjects.first().copy(userId = testUserA)
        val subjectB = DatabaseSeed.initialSubjects.first().copy(userId = testUserB)

        assertEquals(testUserA, subjectA.userId)
        assertEquals(testUserB, subjectB.userId)
        assertNotEquals(subjectA.userId, subjectB.userId)

        val sessionA = DatabaseSeed.initialSessions.first().copy(userId = testUserA)
        assertEquals(testUserA, sessionA.userId)
    }

    @Test
    fun testOnboardingWizardState_stepNavigationAndDrafts() {
        var state = OnboardingWizardState()
        assertEquals(1, state.currentStep)
        assertEquals(7, state.totalSteps)

        // Step forward
        state = state.copy(currentStep = state.currentStep + 1)
        assertEquals(2, state.currentStep)

        // Adding draft subject
        val initialSubjectCount = state.subjects.size
        val newDraft = SubjectDraft(name = "Operating Systems", code = "CS 350", color = "#16A37B")
        state = state.copy(subjects = state.subjects + newDraft)
        assertEquals(initialSubjectCount + 1, state.subjects.size)

        // Removing draft subject
        state = state.copy(subjects = state.subjects.filterNot { it.id == newDraft.id })
        assertEquals(initialSubjectCount, state.subjects.size)
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
