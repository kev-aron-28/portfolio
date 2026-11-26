package com.projects.job_tracker.domain.observatory;

import java.util.*;

public interface SkillRepository {
    List<SkillVersion> list();
    List<SkillVersion> history(UUID id);
    void create(SkillVersion skill);
    void append(SkillVersion skill, int expectedRevision);
}
