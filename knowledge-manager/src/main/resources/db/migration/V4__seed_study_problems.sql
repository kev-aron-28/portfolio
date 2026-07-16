-- Seed study problems from personal algorithm notes (idempotent)

INSERT INTO topics (name, description, color, created_at, updated_at)
SELECT 'Arrays & Strings', 'Problems focused on arrays & strings.', '#3b82f6', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM topics WHERE name = 'Arrays & Strings');

INSERT INTO topics (name, description, color, created_at, updated_at)
SELECT 'Backtracking', 'Problems focused on backtracking.', '#a855f7', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM topics WHERE name = 'Backtracking');

INSERT INTO topics (name, description, color, created_at, updated_at)
SELECT 'Binary Search', 'Problems focused on binary search.', '#6366f1', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM topics WHERE name = 'Binary Search');

INSERT INTO topics (name, description, color, created_at, updated_at)
SELECT 'Daily Practice', 'Problems focused on daily practice.', '#0ea5e9', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM topics WHERE name = 'Daily Practice');

INSERT INTO topics (name, description, color, created_at, updated_at)
SELECT 'Design', 'Problems focused on design.', '#475569', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM topics WHERE name = 'Design');

INSERT INTO topics (name, description, color, created_at, updated_at)
SELECT 'Graphs', 'Problems focused on graphs.', '#ef4444', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM topics WHERE name = 'Graphs');

INSERT INTO topics (name, description, color, created_at, updated_at)
SELECT 'Greedy', 'Problems focused on greedy.', '#eab308', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM topics WHERE name = 'Greedy');

INSERT INTO topics (name, description, color, created_at, updated_at)
SELECT 'Hash Map / Set', 'Problems focused on hash map / set.', '#8b5cf6', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM topics WHERE name = 'Hash Map / Set');

INSERT INTO topics (name, description, color, created_at, updated_at)
SELECT 'Linked List', 'Problems focused on linked list.', '#ec4899', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM topics WHERE name = 'Linked List');

INSERT INTO topics (name, description, color, created_at, updated_at)
SELECT 'Miscellaneous', 'Problems focused on miscellaneous.', '#78716c', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM topics WHERE name = 'Miscellaneous');

INSERT INTO topics (name, description, color, created_at, updated_at)
SELECT 'Practice Exams', 'Problems focused on practice exams.', '#94a3b8', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM topics WHERE name = 'Practice Exams');

INSERT INTO topics (name, description, color, created_at, updated_at)
SELECT 'Prefix Sum', 'Problems focused on prefix sum.', '#14b8a6', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM topics WHERE name = 'Prefix Sum');

INSERT INTO topics (name, description, color, created_at, updated_at)
SELECT 'Sliding Window', 'Problems focused on sliding window.', '#06b6d4', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM topics WHERE name = 'Sliding Window');

INSERT INTO topics (name, description, color, created_at, updated_at)
SELECT 'Sorting', 'Problems focused on sorting.', '#64748b', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM topics WHERE name = 'Sorting');

INSERT INTO topics (name, description, color, created_at, updated_at)
SELECT 'Stack', 'Problems focused on stack.', '#f97316', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM topics WHERE name = 'Stack');

INSERT INTO topics (name, description, color, created_at, updated_at)
SELECT 'Trees', 'Problems focused on trees.', '#22c55e', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM topics WHERE name = 'Trees');

INSERT INTO topics (name, description, color, created_at, updated_at)
SELECT 'Two Pointers', 'Problems focused on two pointers.', '#f59e0b', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM topics WHERE name = 'Two Pointers');

INSERT INTO tags (name, created_at)
SELECT 'Array', NOW()
WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'Array');

INSERT INTO tags (name, created_at)
SELECT 'BFS', NOW()
WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'BFS');

INSERT INTO tags (name, created_at)
SELECT 'Backtracking', NOW()
WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'Backtracking');

INSERT INTO tags (name, created_at)
SELECT 'Binary Search', NOW()
WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'Binary Search');

INSERT INTO tags (name, created_at)
SELECT 'DFS', NOW()
WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'DFS');

INSERT INTO tags (name, created_at)
SELECT 'Exam', NOW()
WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'Exam');

INSERT INTO tags (name, created_at)
SELECT 'Graph', NOW()
WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'Graph');

INSERT INTO tags (name, created_at)
SELECT 'Hash Map', NOW()
WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'Hash Map');

INSERT INTO tags (name, created_at)
SELECT 'Linked List', NOW()
WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'Linked List');

INSERT INTO tags (name, created_at)
SELECT 'Prefix Sum', NOW()
WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'Prefix Sum');

INSERT INTO tags (name, created_at)
SELECT 'Sliding Window', NOW()
WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'Sliding Window');

INSERT INTO tags (name, created_at)
SELECT 'Sorting', NOW()
WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'Sorting');

INSERT INTO tags (name, created_at)
SELECT 'Stack', NOW()
WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'Stack');

INSERT INTO tags (name, created_at)
SELECT 'Tree', NOW()
WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'Tree');

INSERT INTO tags (name, created_at)
SELECT 'Two Pointers', NOW()
WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'Two Pointers');

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT '3 Sum', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using two pointers techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Two Pointers'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = '3 Sum');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = '3 Sum'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = '3 Sum'
  AND tg.name = 'Two Pointers'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'All Paths From Source To Target', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using bfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Backtracking'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'All Paths From Source To Target');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'All Paths From Source To Target'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'All Paths From Source To Target'
  AND tg.name = 'BFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'All Paths From Source To Target'
  AND tg.name = 'Backtracking'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'All Paths From Source To Target'
  AND tg.name = 'Graph'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Array Partition', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using sorting techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Sorting'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Array Partition');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Array Partition'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Array Partition'
  AND tg.name = 'Sorting'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'As Far From Land As Possible', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using bfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Graphs'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'As Far From Land As Possible');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'As Far From Land As Possible'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'As Far From Land As Possible'
  AND tg.name = 'BFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'As Far From Land As Possible'
  AND tg.name = 'Graph'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Balanced Binary Tree', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Balanced Binary Tree');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Balanced Binary Tree'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Balanced Binary Tree'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Balanced Binary Tree'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Beautiful Arrangement', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using backtracking techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Backtracking'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Beautiful Arrangement');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Beautiful Arrangement'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Beautiful Arrangement'
  AND tg.name = 'Backtracking'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Best Time to Buy and Sell Stock', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using arrays & strings techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Arrays & Strings'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Best Time to Buy and Sell Stock');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Best Time to Buy and Sell Stock'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Binary Search', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using binary search techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Binary Search'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Binary Search');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Binary Search'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Binary Search'
  AND tg.name = 'Binary Search'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Binary Subarrays With Sum', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using prefix sum techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Prefix Sum'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Binary Subarrays With Sum');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Binary Subarrays With Sum'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Binary Subarrays With Sum'
  AND tg.name = 'Prefix Sum'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Binary Tree Inorder Traversal', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Binary Tree Inorder Traversal');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Binary Tree Inorder Traversal'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Binary Tree Inorder Traversal'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Binary Tree Inorder Traversal'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Binary Tree Level Order Traversal', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Binary Tree Level Order Traversal');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Binary Tree Level Order Traversal'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Binary Tree Level Order Traversal'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Binary Tree Level Order Traversal'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Binary Tree Level Order Traversal II', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Binary Tree Level Order Traversal II');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Binary Tree Level Order Traversal II'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Binary Tree Level Order Traversal II'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Binary Tree Level Order Traversal II'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Binary Tree Maximum Path Sum', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Binary Tree Maximum Path Sum');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Binary Tree Maximum Path Sum'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Binary Tree Maximum Path Sum'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Binary Tree Maximum Path Sum'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Binary Tree Post Order Traversal', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Binary Tree Post Order Traversal');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Binary Tree Post Order Traversal'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Binary Tree Post Order Traversal'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Binary Tree Post Order Traversal'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Binary Tree Pre Order Traversal', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Binary Tree Pre Order Traversal');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Binary Tree Pre Order Traversal'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Binary Tree Pre Order Traversal'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Binary Tree Pre Order Traversal'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Binary Tree Right Side View', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Binary Tree Right Side View');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Binary Tree Right Side View'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Binary Tree Right Side View'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Binary Tree Right Side View'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Binary Tree Zigzag Level Order Traversal', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Binary Tree Zigzag Level Order Traversal');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Binary Tree Zigzag Level Order Traversal'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Binary Tree Zigzag Level Order Traversal'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Binary Tree Zigzag Level Order Traversal'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Boats To Save People', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using two pointers techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Two Pointers'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Boats To Save People');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Boats To Save People'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Boats To Save People'
  AND tg.name = 'Two Pointers'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Bus Routes', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using bfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Graphs'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Bus Routes');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Bus Routes'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Bus Routes'
  AND tg.name = 'BFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Bus Routes'
  AND tg.name = 'Graph'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Candy', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using two pointers techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Two Pointers'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Candy');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Candy'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Candy'
  AND tg.name = 'Two Pointers'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Capacity To Ship Packages Within D Days', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using binary search techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Binary Search'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Capacity To Ship Packages Within D Days');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Capacity To Ship Packages Within D Days'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Capacity To Ship Packages Within D Days'
  AND tg.name = 'Binary Search'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Car Fleet', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using sorting techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Sorting'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Car Fleet');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Car Fleet'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Car Fleet'
  AND tg.name = 'Sorting'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Climbing the Leaderboard', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using miscellaneous techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Miscellaneous'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Climbing the Leaderboard');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Climbing the Leaderboard'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Clone Graph', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using bfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Graphs'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Clone Graph');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Clone Graph'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Clone Graph'
  AND tg.name = 'BFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Clone Graph'
  AND tg.name = 'Graph'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Combination Sum', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using backtracking techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Backtracking'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Combination Sum');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Combination Sum'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Combination Sum'
  AND tg.name = 'Backtracking'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Combination Sum II', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using backtracking techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Backtracking'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Combination Sum II');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Combination Sum II'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Combination Sum II'
  AND tg.name = 'Backtracking'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Combination Sum III', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using backtracking techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Backtracking'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Combination Sum III');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Combination Sum III'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Combination Sum III'
  AND tg.name = 'Backtracking'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Combinations', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using backtracking techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Backtracking'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Combinations');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Combinations'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Combinations'
  AND tg.name = 'Backtracking'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Construct Binary Tree from Inorder and Postorder Traversal', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Construct Binary Tree from Inorder and Postorder Traversal');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Construct Binary Tree from Inorder and Postorder Traversal'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Construct Binary Tree from Inorder and Postorder Traversal'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Construct Binary Tree from Inorder and Postorder Traversal'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Construct Binary Tree from Preorder and Inorder Traversal', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Construct Binary Tree from Preorder and Inorder Traversal');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Construct Binary Tree from Preorder and Inorder Traversal'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Construct Binary Tree from Preorder and Inorder Traversal'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Construct Binary Tree from Preorder and Inorder Traversal'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Container With Most Water', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using two pointers techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Two Pointers'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Container With Most Water');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Container With Most Water'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Container With Most Water'
  AND tg.name = 'Two Pointers'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Contains Duplicate', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Contains Duplicate');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Contains Duplicate'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Contains Duplicate'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Contiguous Array', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Contiguous Array');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Contiguous Array'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Contiguous Array'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Contiguous Array', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using prefix sum techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Prefix Sum'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Contiguous Array');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Contiguous Array'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Contiguous Array'
  AND tg.name = 'Prefix Sum'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Continuous Subarray Sum', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Prefix Sum'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Continuous Subarray Sum');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Continuous Subarray Sum'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Continuous Subarray Sum'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Continuous Subarray Sum'
  AND tg.name = 'Prefix Sum'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Continuous Subarray Sum', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using prefix sum techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Prefix Sum'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Continuous Subarray Sum');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Continuous Subarray Sum'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Continuous Subarray Sum'
  AND tg.name = 'Prefix Sum'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Convert Binary Number in a Linked List to Integer', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using linked list techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Linked List'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Convert Binary Number in a Linked List to Integer');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Convert Binary Number in a Linked List to Integer'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Convert Binary Number in a Linked List to Integer'
  AND tg.name = 'Linked List'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Count Complete Tree Nodes', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Count Complete Tree Nodes');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Count Complete Tree Nodes'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Count Complete Tree Nodes'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Count Complete Tree Nodes'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Count Number Of Nice Sub Arrays', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Count Number Of Nice Sub Arrays');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Count Number Of Nice Sub Arrays'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Count Number Of Nice Sub Arrays'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Count Number Of Nice Sub Arrays'
  AND tg.name = 'Sliding Window'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Count Of Range Sum', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using prefix sum techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Prefix Sum'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Count Of Range Sum');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Count Of Range Sum'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Count Of Range Sum'
  AND tg.name = 'Prefix Sum'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Daily Temperatures', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using stack techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Stack'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Daily Temperatures');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Daily Temperatures'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Daily Temperatures'
  AND tg.name = 'Stack'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Defuse The Bomb', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using sliding window techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Sliding Window'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Defuse The Bomb');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Defuse The Bomb'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Defuse The Bomb'
  AND tg.name = 'Sliding Window'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Delete Duplicates II', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using linked list techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Linked List'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Delete Duplicates II');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Delete Duplicates II'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Delete Duplicates II'
  AND tg.name = 'Linked List'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Delete Node', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using linked list techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Linked List'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Delete Node');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Delete Node'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Delete Node'
  AND tg.name = 'Linked List'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Diameter Of Binary Tree', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Diameter Of Binary Tree');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Diameter Of Binary Tree'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Diameter Of Binary Tree'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Diameter Of Binary Tree'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Diet Plan Performance', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using sliding window techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Sliding Window'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Diet Plan Performance');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Diet Plan Performance'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Diet Plan Performance'
  AND tg.name = 'Sliding Window'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Evaluate Division', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using bfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Graphs'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Evaluate Division');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Evaluate Division'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Evaluate Division'
  AND tg.name = 'BFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Evaluate Division'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Evaluate Division'
  AND tg.name = 'Graph'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Evaluate Reverse Polish Notation', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using stack techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Stack'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Evaluate Reverse Polish Notation');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Evaluate Reverse Polish Notation'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Evaluate Reverse Polish Notation'
  AND tg.name = 'Stack'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Find All Anagrams In A String', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Find All Anagrams In A String');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Find All Anagrams In A String'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Find All Anagrams In A String'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Find All Anagrams In A String'
  AND tg.name = 'Sliding Window'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Find First and Last Position of Element in Sorted Array', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using binary search techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Binary Search'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Find First and Last Position of Element in Sorted Array');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Find First and Last Position of Element in Sorted Array'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Find First and Last Position of Element in Sorted Array'
  AND tg.name = 'Binary Search'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Find In Mountain Array', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using binary search techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Binary Search'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Find In Mountain Array');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Find In Mountain Array'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Find In Mountain Array'
  AND tg.name = 'Binary Search'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Find Minimum In Rotated Sorted Array', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using binary search techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Binary Search'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Find Minimum In Rotated Sorted Array');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Find Minimum In Rotated Sorted Array'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Find Minimum In Rotated Sorted Array'
  AND tg.name = 'Binary Search'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Find Peak Element', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using binary search techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Binary Search'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Find Peak Element');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Find Peak Element'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Find Peak Element'
  AND tg.name = 'Binary Search'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Find Peak Element II', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using binary search techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Binary Search'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Find Peak Element II');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Find Peak Element II'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Find Peak Element II'
  AND tg.name = 'Binary Search'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Find Pivot Index', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using prefix sum techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Prefix Sum'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Find Pivot Index');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Find Pivot Index'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Find Pivot Index'
  AND tg.name = 'Prefix Sum'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Find Smallest Letter Greater Than Target', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using binary search techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Binary Search'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Find Smallest Letter Greater Than Target');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Find Smallest Letter Greater Than Target'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Find Smallest Letter Greater Than Target'
  AND tg.name = 'Binary Search'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Find The Difference', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Find The Difference');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Find The Difference'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Find The Difference'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Find The Town Judge', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using bfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Graphs'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Find The Town Judge');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Find The Town Judge'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Find The Town Judge'
  AND tg.name = 'BFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Find The Town Judge'
  AND tg.name = 'Graph'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'First Bad Version', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using binary search techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Binary Search'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'First Bad Version');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'First Bad Version'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'First Bad Version'
  AND tg.name = 'Binary Search'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'First Missing Positive', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using two pointers techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Two Pointers'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'First Missing Positive');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'First Missing Positive'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'First Missing Positive'
  AND tg.name = 'Two Pointers'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'First Unique Character In A String', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'First Unique Character In A String');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'First Unique Character In A String'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'First Unique Character In A String'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Fruit Into Baskets', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Fruit Into Baskets');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Fruit Into Baskets'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Fruit Into Baskets'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Fruit Into Baskets'
  AND tg.name = 'Sliding Window'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Generate Parentheses', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using backtracking techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Backtracking'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Generate Parentheses');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Generate Parentheses'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Generate Parentheses'
  AND tg.name = 'Backtracking'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Group Anagrams', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Group Anagrams');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Group Anagrams'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Group Anagrams'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Grumpy Bookstore Owner', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using sliding window techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Sliding Window'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Grumpy Bookstore Owner');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Grumpy Bookstore Owner'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Grumpy Bookstore Owner'
  AND tg.name = 'Sliding Window'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Guess Number Higher or Lower', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using binary search techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Binary Search'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Guess Number Higher or Lower');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Guess Number Higher or Lower'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Guess Number Higher or Lower'
  AND tg.name = 'Binary Search'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Hand Of Straights', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Hand Of Straights');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Hand Of Straights'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Hand Of Straights'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Happy Number', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Happy Number');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Happy Number'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Happy Number'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Height Checker', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using sorting techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Sorting'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Height Checker');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Height Checker'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Height Checker'
  AND tg.name = 'Sorting'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Insert Interval', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using sorting techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Sorting'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Insert Interval');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Insert Interval'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Insert Interval'
  AND tg.name = 'Sorting'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Intersection Of Two Arrays', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Intersection Of Two Arrays');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Intersection Of Two Arrays'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Intersection Of Two Arrays'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Intersection Of Two Linked List', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using linked list techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Linked List'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Intersection Of Two Linked List');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Intersection Of Two Linked List'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Intersection Of Two Linked List'
  AND tg.name = 'Linked List'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Invert Binary Tree', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Invert Binary Tree');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Invert Binary Tree'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Invert Binary Tree'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Invert Binary Tree'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Is Graph Bipartite', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using bfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Graphs'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Is Graph Bipartite');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Is Graph Bipartite'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Is Graph Bipartite'
  AND tg.name = 'BFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Is Graph Bipartite'
  AND tg.name = 'Graph'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Is Subsequence', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using array techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Arrays & Strings'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Is Subsequence');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Is Subsequence'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Is Subsequence'
  AND tg.name = 'Array'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Is Subsequence'
  AND tg.name = 'Two Pointers'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Keys And Rooms', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using bfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Graphs'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Keys And Rooms');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Keys And Rooms'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Keys And Rooms'
  AND tg.name = 'BFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Keys And Rooms'
  AND tg.name = 'Graph'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Koko Eating Bananas', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using binary search techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Binary Search'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Koko Eating Bananas');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Koko Eating Bananas'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Koko Eating Bananas'
  AND tg.name = 'Binary Search'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Largest Rectangle In Histogram', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using stack techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Stack'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Largest Rectangle In Histogram');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Largest Rectangle In Histogram'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Largest Rectangle In Histogram'
  AND tg.name = 'Stack'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Left And Right Sum Differences', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using prefix sum techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Prefix Sum'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Left And Right Sum Differences');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Left And Right Sum Differences'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Left And Right Sum Differences'
  AND tg.name = 'Prefix Sum'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Letter Case Permutation', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using backtracking techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Backtracking'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Letter Case Permutation');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Letter Case Permutation'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Letter Case Permutation'
  AND tg.name = 'Backtracking'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Letter Combinations Of A Phone Number', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using backtracking techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Backtracking'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Letter Combinations Of A Phone Number');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Letter Combinations Of A Phone Number'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Letter Combinations Of A Phone Number'
  AND tg.name = 'Backtracking'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Letter Tiles Possibilities', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using backtracking techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Backtracking'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Letter Tiles Possibilities');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Letter Tiles Possibilities'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Letter Tiles Possibilities'
  AND tg.name = 'Backtracking'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Linked List Cycle', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using linked list techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Linked List'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Linked List Cycle');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Linked List Cycle'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Linked List Cycle'
  AND tg.name = 'Linked List'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Linked List Cycle II', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using linked list techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Linked List'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Linked List Cycle II');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Linked List Cycle II'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Linked List Cycle II'
  AND tg.name = 'Linked List'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Longest Consecutive Sequence', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Longest Consecutive Sequence');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Longest Consecutive Sequence'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Longest Consecutive Sequence'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Longest Repeating Character Replacement', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using sliding window techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Sliding Window'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Longest Repeating Character Replacement');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Longest Repeating Character Replacement'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Longest Repeating Character Replacement'
  AND tg.name = 'Sliding Window'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Longest Substring With At Most K Distinct Characters', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Longest Substring With At Most K Distinct Characters');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Longest Substring With At Most K Distinct Characters'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Longest Substring With At Most K Distinct Characters'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Longest Substring With At Most K Distinct Characters'
  AND tg.name = 'Sliding Window'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Longest Substring Without Repeating Characters', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using sliding window techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Sliding Window'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Longest Substring Without Repeating Characters');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Longest Substring Without Repeating Characters'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Longest Substring Without Repeating Characters'
  AND tg.name = 'Sliding Window'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Lowest Common Ancestor of a Binary Tree', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Lowest Common Ancestor of a Binary Tree');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Lowest Common Ancestor of a Binary Tree'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Lowest Common Ancestor of a Binary Tree'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Lowest Common Ancestor of a Binary Tree'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Lowest Common Ancestor of a BST', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Lowest Common Ancestor of a BST');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Lowest Common Ancestor of a BST'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Lowest Common Ancestor of a BST'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Lowest Common Ancestor of a BST'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'LRU Cache', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using design techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Design'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'LRU Cache');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'LRU Cache'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Majority Element', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Majority Element');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Majority Element'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Majority Element'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Max Consecutive Ones', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using sliding window techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Sliding Window'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Max Consecutive Ones');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Max Consecutive Ones'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Max Consecutive Ones'
  AND tg.name = 'Sliding Window'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Maximum Average Subarray I', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using sliding window techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Sliding Window'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Maximum Average Subarray I');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Maximum Average Subarray I'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Maximum Average Subarray I'
  AND tg.name = 'Sliding Window'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Maximum Depth of Binary Tree', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Maximum Depth of Binary Tree');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Maximum Depth of Binary Tree'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Maximum Depth of Binary Tree'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Maximum Depth of Binary Tree'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Maximum Number Of Vowels In A String', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using sliding window techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Sliding Window'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Maximum Number Of Vowels In A String');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Maximum Number Of Vowels In A String'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Maximum Number Of Vowels In A String'
  AND tg.name = 'Sliding Window'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Maximum Population Year', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using prefix sum techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Prefix Sum'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Maximum Population Year');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Maximum Population Year'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Maximum Population Year'
  AND tg.name = 'Prefix Sum'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Maximum Size Sub Array Sum Equals K', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Prefix Sum'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Maximum Size Sub Array Sum Equals K');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Maximum Size Sub Array Sum Equals K'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Maximum Size Sub Array Sum Equals K'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Maximum Size Sub Array Sum Equals K'
  AND tg.name = 'Prefix Sum'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Maximum Subarray', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using sliding window techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Sliding Window'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Maximum Subarray');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Maximum Subarray'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Maximum Subarray'
  AND tg.name = 'Sliding Window'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Maximum Sum Subarray of Size K', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Maximum Sum Subarray of Size K');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Maximum Sum Subarray of Size K'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Maximum Sum Subarray of Size K'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Median Of Two Sorted Arrays', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using binary search techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Binary Search'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Median Of Two Sorted Arrays');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Median Of Two Sorted Arrays'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Median Of Two Sorted Arrays'
  AND tg.name = 'Binary Search'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Meeting Rooms', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using sorting techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Sorting'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Meeting Rooms');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Meeting Rooms'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Meeting Rooms'
  AND tg.name = 'Sorting'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Merge Intervals', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using sorting techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Sorting'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Merge Intervals');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Merge Intervals'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Merge Intervals'
  AND tg.name = 'Sorting'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Merge Sorted Array', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using two pointers techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Two Pointers'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Merge Sorted Array');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Merge Sorted Array'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Merge Sorted Array'
  AND tg.name = 'Two Pointers'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Merge Two Sorted Lists', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using linked list techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Linked List'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Merge Two Sorted Lists');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Merge Two Sorted Lists'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Merge Two Sorted Lists'
  AND tg.name = 'Linked List'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Middle Of The Linked List', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using linked list techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Linked List'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Middle Of The Linked List');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Middle Of The Linked List'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Middle Of The Linked List'
  AND tg.name = 'Linked List'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Min Stack', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using stack techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Stack'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Min Stack');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Min Stack'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Min Stack'
  AND tg.name = 'Stack'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Minimum Deletions To Make Character Frequencies Unique', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Minimum Deletions To Make Character Frequencies Unique');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Minimum Deletions To Make Character Frequencies Unique'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Minimum Deletions To Make Character Frequencies Unique'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Minimum Depth Of Binary Tree', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Minimum Depth Of Binary Tree');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Minimum Depth Of Binary Tree'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Minimum Depth Of Binary Tree'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Minimum Depth Of Binary Tree'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Minimum Difference Between Highest and Lowest of K Scores', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using sorting techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Sorting'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Minimum Difference Between Highest and Lowest of K Scores');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Minimum Difference Between Highest and Lowest of K Scores'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Minimum Difference Between Highest and Lowest of K Scores'
  AND tg.name = 'Sorting'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Minimum Genetic Mutation', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using bfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Graphs'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Minimum Genetic Mutation');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Minimum Genetic Mutation'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Minimum Genetic Mutation'
  AND tg.name = 'BFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Minimum Genetic Mutation'
  AND tg.name = 'Graph'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Minimum Number Of Days To Make M Bouquets', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using binary search techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Binary Search'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Minimum Number Of Days To Make M Bouquets');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Minimum Number Of Days To Make M Bouquets'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Minimum Number Of Days To Make M Bouquets'
  AND tg.name = 'Binary Search'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Minimum Size Subarray Sum', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using sliding window techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Sliding Window'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Minimum Size Subarray Sum');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Minimum Size Subarray Sum'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Minimum Size Subarray Sum'
  AND tg.name = 'Sliding Window'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Minimum Window Substring', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using sliding window techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Sliding Window'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Minimum Window Substring');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Minimum Window Substring'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Minimum Window Substring'
  AND tg.name = 'Sliding Window'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Missing Number', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Missing Number');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Missing Number'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Missing Number'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Move Zeroes', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using array techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Arrays & Strings'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Move Zeroes');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Move Zeroes'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Move Zeroes'
  AND tg.name = 'Array'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Move Zeroes'
  AND tg.name = 'Two Pointers'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'N Queens', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using backtracking techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Backtracking'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'N Queens');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'N Queens'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'N Queens'
  AND tg.name = 'Backtracking'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Nearest Exit From Entrance In Maze', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using bfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Graphs'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Nearest Exit From Entrance In Maze');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Nearest Exit From Entrance In Maze'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Nearest Exit From Entrance In Maze'
  AND tg.name = 'BFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Nearest Exit From Entrance In Maze'
  AND tg.name = 'Graph'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Next Permutation', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using backtracking techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Backtracking'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Next Permutation');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Next Permutation'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Next Permutation'
  AND tg.name = 'Backtracking'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Non-overlapping Intervals', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using sorting techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Sorting'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Non-overlapping Intervals');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Non-overlapping Intervals'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Non-overlapping Intervals'
  AND tg.name = 'Sorting'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Number Of Islands', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using bfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Graphs'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Number Of Islands');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Number Of Islands'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Number Of Islands'
  AND tg.name = 'BFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Number Of Islands'
  AND tg.name = 'Graph'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Open The Lock', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using bfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Open The Lock');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Open The Lock'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Open The Lock'
  AND tg.name = 'BFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Open The Lock'
  AND tg.name = 'Graph'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Open The Lock'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Palindrome Linked List', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using linked list techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Linked List'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Palindrome Linked List');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Palindrome Linked List'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Palindrome Linked List'
  AND tg.name = 'Linked List'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Palindrome Partition', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using backtracking techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Backtracking'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Palindrome Partition');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Palindrome Partition'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Palindrome Partition'
  AND tg.name = 'Backtracking'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Partition List', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using linked list techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Linked List'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Partition List');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Partition List'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Partition List'
  AND tg.name = 'Linked List'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Path Sum', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Path Sum');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Path Sum'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Path Sum'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Path Sum'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Path Sum II', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Path Sum II');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Path Sum II'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Path Sum II'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Path Sum II'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Path Sum III', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Path Sum III');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Path Sum III'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Path Sum III'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Path Sum III'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Peak Index in a Mountain Array', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using binary search techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Binary Search'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Peak Index in a Mountain Array');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Peak Index in a Mountain Array'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Peak Index in a Mountain Array'
  AND tg.name = 'Binary Search'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Permutation In String', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using sliding window techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Sliding Window'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Permutation In String');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Permutation In String'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Permutation In String'
  AND tg.name = 'Sliding Window'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Permutation Sequence', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using backtracking techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Backtracking'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Permutation Sequence');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Permutation Sequence'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Permutation Sequence'
  AND tg.name = 'Backtracking'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Permutations', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using backtracking techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Backtracking'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Permutations');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Permutations'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Permutations'
  AND tg.name = 'Backtracking'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Permutations II', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using backtracking techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Backtracking'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Permutations II');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Permutations II'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Permutations II'
  AND tg.name = 'Backtracking'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Practice Exam', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Practice Exam');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Practice Exam'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Practice Exam'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Practice Exam 1', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using exam techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Practice Exams'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Practice Exam 1');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Practice Exam 1'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Practice Exam 1'
  AND tg.name = 'Exam'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Practice Exam 10', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using exam techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Practice Exams'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Practice Exam 10');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Practice Exam 10'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Practice Exam 10'
  AND tg.name = 'Exam'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Practice Exam 11', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using exam techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Practice Exams'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Practice Exam 11');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Practice Exam 11'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Practice Exam 11'
  AND tg.name = 'Exam'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Practice Exam 2', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Practice Exam 2');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Practice Exam 2'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Practice Exam 2'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Practice Exam 3', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using exam techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Practice Exams'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Practice Exam 3');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Practice Exam 3'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Practice Exam 3'
  AND tg.name = 'Exam'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Practice Exam 4', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using exam techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Practice Exams'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Practice Exam 4');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Practice Exam 4'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Practice Exam 4'
  AND tg.name = 'Exam'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Practice Exam 6', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using exam techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Practice Exams'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Practice Exam 6');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Practice Exam 6'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Practice Exam 6'
  AND tg.name = 'Exam'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Practice Exam 7', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using exam techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Practice Exams'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Practice Exam 7');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Practice Exam 7'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Practice Exam 7'
  AND tg.name = 'Exam'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Practice Exam 8', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using exam techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Practice Exams'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Practice Exam 8');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Practice Exam 8'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Practice Exam 8'
  AND tg.name = 'Exam'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Practice Exam 9', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using exam techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Practice Exams'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Practice Exam 9');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Practice Exam 9'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Practice Exam 9'
  AND tg.name = 'Exam'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Product of Array Except Self', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using prefix sum techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Prefix Sum'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Product of Array Except Self');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Product of Array Except Self'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Product of Array Except Self'
  AND tg.name = 'Prefix Sum'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Range Sum Query - Immutable', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Prefix Sum'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Range Sum Query - Immutable');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Range Sum Query - Immutable'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Range Sum Query - Immutable'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Range Sum Query - Immutable'
  AND tg.name = 'Prefix Sum'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Range Sum Query Immutable', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using prefix sum techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Prefix Sum'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Range Sum Query Immutable');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Range Sum Query Immutable'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Range Sum Query Immutable'
  AND tg.name = 'Prefix Sum'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Ransom Note', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Ransom Note');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Ransom Note'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Ransom Note'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Relative Sort Array', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using sorting techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Sorting'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Relative Sort Array');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Relative Sort Array'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Relative Sort Array'
  AND tg.name = 'Sorting'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Remove Duplicates From Linked List', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using linked list techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Linked List'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Remove Duplicates From Linked List');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Remove Duplicates From Linked List'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Remove Duplicates From Linked List'
  AND tg.name = 'Linked List'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Remove Duplicates From Sorted Array', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using two pointers techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Two Pointers'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Remove Duplicates From Sorted Array');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Remove Duplicates From Sorted Array'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Remove Duplicates From Sorted Array'
  AND tg.name = 'Two Pointers'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Remove Element', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using array techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Arrays & Strings'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Remove Element');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Remove Element'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Remove Element'
  AND tg.name = 'Array'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Remove Element'
  AND tg.name = 'Two Pointers'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Remove Linked List Elements', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using linked list techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Linked List'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Remove Linked List Elements');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Remove Linked List Elements'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Remove Linked List Elements'
  AND tg.name = 'Linked List'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Remove Middle Node Of A Linked List', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using linked list techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Linked List'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Remove Middle Node Of A Linked List');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Remove Middle Node Of A Linked List'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Remove Middle Node Of A Linked List'
  AND tg.name = 'Linked List'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Remove Nth Node From End of List', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using linked list techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Linked List'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Remove Nth Node From End of List');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Remove Nth Node From End of List'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Remove Nth Node From End of List'
  AND tg.name = 'Linked List'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Reorder List', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using linked list techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Linked List'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Reorder List');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Reorder List'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Reorder List'
  AND tg.name = 'Linked List'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Reorder Routes To Make All Paths Lead To Zero', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using bfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Graphs'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Reorder Routes To Make All Paths Lead To Zero');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Reorder Routes To Make All Paths Lead To Zero'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Reorder Routes To Make All Paths Lead To Zero'
  AND tg.name = 'BFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Reorder Routes To Make All Paths Lead To Zero'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Reorder Routes To Make All Paths Lead To Zero'
  AND tg.name = 'Graph'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Reorganize String', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Reorganize String');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Reorganize String'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Reorganize String'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Replace Substring With Balanced String', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Replace Substring With Balanced String');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Replace Substring With Balanced String'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Replace Substring With Balanced String'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Restore Ip Addresses', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using backtracking techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Backtracking'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Restore Ip Addresses');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Restore Ip Addresses'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Restore Ip Addresses'
  AND tg.name = 'Backtracking'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Reverse Linked List', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using linked list techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Linked List'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Reverse Linked List');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Reverse Linked List'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Reverse Linked List'
  AND tg.name = 'Linked List'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Reverse String', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using two pointers techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Two Pointers'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Reverse String');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Reverse String'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Reverse String'
  AND tg.name = 'Two Pointers'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Rotting Oranges', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using bfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Graphs'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Rotting Oranges');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Rotting Oranges'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Rotting Oranges'
  AND tg.name = 'BFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Rotting Oranges'
  AND tg.name = 'Graph'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Running Sum Of 1 D Array', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using prefix sum techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Prefix Sum'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Running Sum Of 1 D Array');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Running Sum Of 1 D Array'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Running Sum Of 1 D Array'
  AND tg.name = 'Prefix Sum'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Same Tree', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Same Tree');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Same Tree'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Same Tree'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Same Tree'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Search In Rotated Array', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using binary search techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Binary Search'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Search In Rotated Array');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Search In Rotated Array'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Search In Rotated Array'
  AND tg.name = 'Binary Search'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Search in Rotated Sorted Array II', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using binary search techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Binary Search'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Search in Rotated Sorted Array II');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Search in Rotated Sorted Array II'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Search in Rotated Sorted Array II'
  AND tg.name = 'Binary Search'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Search Insert Position', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using binary search techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Binary Search'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Search Insert Position');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Search Insert Position'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Search Insert Position'
  AND tg.name = 'Binary Search'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Serialize and Deserialize Binary Tree', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Serialize and Deserialize Binary Tree');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Serialize and Deserialize Binary Tree'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Serialize and Deserialize Binary Tree'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Serialize and Deserialize Binary Tree'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Shortest Palindrome', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using two pointers techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Two Pointers'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Shortest Palindrome');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Shortest Palindrome'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Shortest Palindrome'
  AND tg.name = 'Two Pointers'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Shortest Path In Binary Matrix', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using bfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Graphs'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Shortest Path In Binary Matrix');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Shortest Path In Binary Matrix'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Shortest Path In Binary Matrix'
  AND tg.name = 'BFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Shortest Path In Binary Matrix'
  AND tg.name = 'Graph'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Single Number', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Single Number');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Single Number'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Single Number'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Sliding Window Maximum', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using sliding window techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Sliding Window'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Sliding Window Maximum');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Sliding Window Maximum'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Sliding Window Maximum'
  AND tg.name = 'Sliding Window'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Smallest String From Leaf', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Smallest String From Leaf');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Smallest String From Leaf'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Smallest String From Leaf'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Smallest String From Leaf'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Sort Characters By Frequency', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Sort Characters By Frequency');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Sort Characters By Frequency'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Sort Characters By Frequency'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Sort Colors', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using sorting techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Sorting'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Sort Colors');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Sort Colors'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Sort Colors'
  AND tg.name = 'Sorting'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Split Array Largest Sum', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using binary search techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Binary Search'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Split Array Largest Sum');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Split Array Largest Sum'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Split Array Largest Sum'
  AND tg.name = 'Binary Search'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Split String Into Max Unique Substrings', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using backtracking techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Backtracking'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Split String Into Max Unique Substrings');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Split String Into Max Unique Substrings'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Split String Into Max Unique Substrings'
  AND tg.name = 'Backtracking'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Squares of a Sorted Array', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using array techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Arrays & Strings'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Squares of a Sorted Array');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Squares of a Sorted Array'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Squares of a Sorted Array'
  AND tg.name = 'Array'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Squares of a Sorted Array', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using two pointers techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Two Pointers'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Squares of a Sorted Array');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Squares of a Sorted Array'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Squares of a Sorted Array'
  AND tg.name = 'Two Pointers'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Subarray Sum Equals K', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using prefix sum techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Prefix Sum'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Subarray Sum Equals K');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Subarray Sum Equals K'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Subarray Sum Equals K'
  AND tg.name = 'Prefix Sum'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Subsets', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using backtracking techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Backtracking'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Subsets');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Subsets'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Subsets'
  AND tg.name = 'Backtracking'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Subsets II', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using backtracking techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Backtracking'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Subsets II');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Subsets II'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Subsets II'
  AND tg.name = 'Backtracking'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Substring Concatenation Of All Words', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Substring Concatenation Of All Words');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Substring Concatenation Of All Words'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Substring Concatenation Of All Words'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Substring with Concatenation of All Words', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Substring with Concatenation of All Words');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Substring with Concatenation of All Words'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Substring with Concatenation of All Words'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Substrings Of Size K With K Distinct Characters', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Substrings Of Size K With K Distinct Characters');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Substrings Of Size K With K Distinct Characters'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Substrings Of Size K With K Distinct Characters'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Substrings Of Size K With K Distinct Characters'
  AND tg.name = 'Sliding Window'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Sum Root To Leaf Numbers', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Sum Root To Leaf Numbers');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Sum Root To Leaf Numbers'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Sum Root To Leaf Numbers'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Sum Root To Leaf Numbers'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Swap Nodes In Pairs', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using linked list techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Linked List'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Swap Nodes In Pairs');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Swap Nodes In Pairs'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Swap Nodes In Pairs'
  AND tg.name = 'Linked List'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Symmetric Tree', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Symmetric Tree');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Symmetric Tree'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Symmetric Tree'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Symmetric Tree'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Task Scheduler', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Task Scheduler');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Task Scheduler'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Task Scheduler'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Top K Frequent Elements', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Top K Frequent Elements');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Top K Frequent Elements'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Top K Frequent Elements'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Trapping Rain Water', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using two pointers techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Two Pointers'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Trapping Rain Water');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Trapping Rain Water'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Trapping Rain Water'
  AND tg.name = 'Two Pointers'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Two Sum', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using two pointers techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Arrays & Strings'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Two Sum');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Two Sum'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Two Sum'
  AND tg.name = 'Two Pointers'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Two Sum II - Input Array Is Sorted', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Two Sum II - Input Array Is Sorted');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Two Sum II - Input Array Is Sorted'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Two Sum II - Input Array Is Sorted'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Two Sum II - Input Array Is Sorted'
  AND tg.name = 'Two Pointers'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Valid Anagram', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using hash map techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Hash Map / Set'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Valid Anagram');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Valid Anagram'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Valid Anagram'
  AND tg.name = 'Hash Map'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Valid Palindrome', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using two pointers techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Two Pointers'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Valid Palindrome');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Valid Palindrome'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Valid Palindrome'
  AND tg.name = 'Two Pointers'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Valid Parentheses', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using stack techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Stack'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Valid Parentheses');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Valid Parentheses'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Valid Parentheses'
  AND tg.name = 'Stack'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Valid Sudoku', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using arrays & strings techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Arrays & Strings'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Valid Sudoku');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Valid Sudoku'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Validate Binary Search Tree', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using dfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Trees'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Validate Binary Search Tree');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Validate Binary Search Tree'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Validate Binary Search Tree'
  AND tg.name = 'DFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Validate Binary Search Tree'
  AND tg.name = 'Tree'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Word Ladder', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using bfs techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Graphs'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Word Ladder');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Word Ladder'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Word Ladder'
  AND tg.name = 'BFS'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Word Ladder'
  AND tg.name = 'Graph'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );

INSERT INTO problems (title, url, difficulty, description, favorite, archived, topic_id, created_at, updated_at)
SELECT 'Word Search', NULL, 'MEDIUM', 'Study problem from personal notes. Practice using backtracking techniques.', FALSE, FALSE, t.id, NOW(), NOW()
FROM topics t
WHERE t.name = 'Backtracking'
  AND NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Word Search');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id, 'java', NULL, NULL, NULL, NULL
FROM problems p
WHERE p.title = 'Word Search'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p, tags tg
WHERE p.title = 'Word Search'
  AND tg.name = 'Backtracking'
  AND NOT EXISTS (
    SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id AND pt.tag_id = tg.id
  );
