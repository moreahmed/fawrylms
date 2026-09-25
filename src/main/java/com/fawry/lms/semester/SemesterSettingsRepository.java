package com.fawry.lms.semester;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SemesterSettingsRepository extends JpaRepository<SemesterSettings, Long> {
	List<SemesterSettings> findAllByStartsAtLessThanEqualAndEndsAtGreaterThanEqual(
		LocalDate latestStart, LocalDate earliestEnd);

	Optional<SemesterSettings> findFirstByStartsAtLessThanEqualAndEndsAtGreaterThanEqualOrderByStartsAtDesc(
		LocalDate startsAt, LocalDate endsAt);
}
