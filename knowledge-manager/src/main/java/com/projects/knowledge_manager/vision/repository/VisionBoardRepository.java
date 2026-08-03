package com.projects.knowledge_manager.vision.repository;

import com.projects.knowledge_manager.vision.entity.VisionBoard;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisionBoardRepository extends JpaRepository<VisionBoard, Long> {

  List<VisionBoard> findAllByOrderByUpdatedAtDesc();
}
