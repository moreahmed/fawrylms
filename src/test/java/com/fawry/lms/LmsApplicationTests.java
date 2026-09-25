package com.fawry.lms;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.fawry.lms.semester.SemesterSettings;
import com.fawry.lms.semester.SemesterSettingsRepository;

@SpringBootTest
class LmsApplicationTests {
	@Autowired
	private SemesterSettingsRepository semesterSettingsRepository;

	@Test
	void contextLoads() {
	}

	@Test
	@Transactional
	void savesSemesterAndFindsItAsActive() {
		LocalDate today = LocalDate.now();
		SemesterSettings semester = new SemesterSettings();
		semester.setSemesterName("Persistence test");
		semester.setStartsAt(today);
		semester.setEndsAt(today.plusDays(1));
		semester.setMaxHours(18);
		semester.setMaxFailedAttempts(2);

		SemesterSettings saved = semesterSettingsRepository.saveAndFlush(semester);

		assertNotNull(saved.getId());
		assertTrue(semesterSettingsRepository
			.findFirstByStartsAtLessThanEqualAndEndsAtGreaterThanEqualOrderByStartsAtDesc(today, today)
			.filter(activeSemester -> activeSemester.getId().equals(saved.getId()))
			.isPresent());
	}

}
