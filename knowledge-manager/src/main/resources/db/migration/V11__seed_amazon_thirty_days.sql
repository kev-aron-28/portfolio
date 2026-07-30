-- Seed Amazon topic with LeetCode 30-day frequency problems (frequency <= 50).
-- Source: 1. Thirty Days.csv

BEGIN;

INSERT INTO topics (name, description, color, created_at, updated_at)
VALUES (
    'Amazon',
    'Amazon interview practice from the 30-day frequency list (frequency <= 50).',
    '#ff9900',
    NOW(),
    NOW()
)
ON CONFLICT (name) DO UPDATE
SET description = EXCLUDED.description,
    color = EXCLUDED.color,
    updated_at = NOW();

INSERT INTO tags (name, created_at) VALUES ('Array', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Backtracking', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Binary Search', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Binary Search Tree', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Binary Tree', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Bit Manipulation', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Breadth-First Search', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Bucket Sort', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Combinatorics', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Counting', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Database', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Depth-First Search', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Design', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Divide and Conquer', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Dynamic Programming', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Enumeration', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Geometry', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Graph Theory', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Greedy', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Hash Function', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Hash Table', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Heap (Priority Queue)', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Linked List', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Math', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Matrix', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Merge Sort', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Monotonic Queue', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Monotonic Stack', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Prefix Sum', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Queue', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Quickselect', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Randomized', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Recursion', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Simulation', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Sliding Window', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Sorting', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Stack', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('String', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Topological Sort', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Tree', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Trie', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Two Pointers', NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO tags (name, created_at) VALUES ('Union-Find', NOW())
ON CONFLICT (name) DO NOTHING;

-- Rotting Oranges (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Rotting Oranges',
    'https://leetcode.com/problems/rotting-oranges',
    'MEDIUM',
    '# Rotting Oranges

Solve the LeetCode problem **Rotting Oranges**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Array, Breadth-First Search, Matrix

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Rotting Oranges');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Rotting Oranges
}
',
       'Work through Rotting Oranges as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Rotting Oranges'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Rotting Oranges' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Rotting Oranges' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Rotting Oranges' AND tg.name = 'Breadth-First Search'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Rotting Oranges' AND tg.name = 'Matrix'
ON CONFLICT DO NOTHING;

-- Permutations (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Permutations',
    'https://leetcode.com/problems/permutations',
    'MEDIUM',
    '# Permutations

Solve the LeetCode problem **Permutations**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Array, Backtracking

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Permutations');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Permutations
}
',
       'Work through Permutations as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Permutations'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Permutations' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Permutations' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Permutations' AND tg.name = 'Backtracking'
ON CONFLICT DO NOTHING;

-- Regular Expression Matching (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Regular Expression Matching',
    'https://leetcode.com/problems/regular-expression-matching',
    'HARD',
    '# Regular Expression Matching

Solve the LeetCode problem **Regular Expression Matching**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: String, Dynamic Programming, Recursion

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Regular Expression Matching');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Regular Expression Matching
}
',
       'Work through Regular Expression Matching as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Regular Expression Matching'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Regular Expression Matching' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Regular Expression Matching' AND tg.name = 'String'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Regular Expression Matching' AND tg.name = 'Dynamic Programming'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Regular Expression Matching' AND tg.name = 'Recursion'
ON CONFLICT DO NOTHING;

-- Remove Duplicates from Sorted Array (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Remove Duplicates from Sorted Array',
    'https://leetcode.com/problems/remove-duplicates-from-sorted-array',
    'EASY',
    '# Remove Duplicates from Sorted Array

Solve the LeetCode problem **Remove Duplicates from Sorted Array**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Array, Two Pointers

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Remove Duplicates from Sorted Array');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Remove Duplicates from Sorted Array
}
',
       'Work through Remove Duplicates from Sorted Array as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Remove Duplicates from Sorted Array'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Remove Duplicates from Sorted Array' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Remove Duplicates from Sorted Array' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Remove Duplicates from Sorted Array' AND tg.name = 'Two Pointers'
ON CONFLICT DO NOTHING;

-- Interleaving String (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Interleaving String',
    'https://leetcode.com/problems/interleaving-string',
    'MEDIUM',
    '# Interleaving String

Solve the LeetCode problem **Interleaving String**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: String, Dynamic Programming

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Interleaving String');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Interleaving String
}
',
       'Work through Interleaving String as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Interleaving String'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Interleaving String' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Interleaving String' AND tg.name = 'String'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Interleaving String' AND tg.name = 'Dynamic Programming'
ON CONFLICT DO NOTHING;

-- Merge Two Sorted Lists (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Merge Two Sorted Lists',
    'https://leetcode.com/problems/merge-two-sorted-lists',
    'EASY',
    '# Merge Two Sorted Lists

Solve the LeetCode problem **Merge Two Sorted Lists**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Linked List, Recursion

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Merge Two Sorted Lists');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Merge Two Sorted Lists
}
',
       'Work through Merge Two Sorted Lists as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Merge Two Sorted Lists'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Merge Two Sorted Lists' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Merge Two Sorted Lists' AND tg.name = 'Linked List'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Merge Two Sorted Lists' AND tg.name = 'Recursion'
ON CONFLICT DO NOTHING;

-- Largest Rectangle in Histogram (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Largest Rectangle in Histogram',
    'https://leetcode.com/problems/largest-rectangle-in-histogram',
    'HARD',
    '# Largest Rectangle in Histogram

Solve the LeetCode problem **Largest Rectangle in Histogram**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Array, Stack, Monotonic Stack

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Largest Rectangle in Histogram');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Largest Rectangle in Histogram
}
',
       'Work through Largest Rectangle in Histogram as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Largest Rectangle in Histogram'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Largest Rectangle in Histogram' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Largest Rectangle in Histogram' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Largest Rectangle in Histogram' AND tg.name = 'Stack'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Largest Rectangle in Histogram' AND tg.name = 'Monotonic Stack'
ON CONFLICT DO NOTHING;

-- Word Search (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Word Search',
    'https://leetcode.com/problems/word-search',
    'MEDIUM',
    '# Word Search

Solve the LeetCode problem **Word Search**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Array, String, Backtracking, Depth-First Search, Matrix

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Word Search');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Word Search
}
',
       'Work through Word Search as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Word Search'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Word Search' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Word Search' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Word Search' AND tg.name = 'String'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Word Search' AND tg.name = 'Backtracking'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Word Search' AND tg.name = 'Depth-First Search'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Word Search' AND tg.name = 'Matrix'
ON CONFLICT DO NOTHING;

-- Word Ladder (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Word Ladder',
    'https://leetcode.com/problems/word-ladder',
    'HARD',
    '# Word Ladder

Solve the LeetCode problem **Word Ladder**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Hash Table, String, Breadth-First Search

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Word Ladder');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Word Ladder
}
',
       'Work through Word Ladder as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Word Ladder'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Word Ladder' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Word Ladder' AND tg.name = 'Hash Table'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Word Ladder' AND tg.name = 'String'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Word Ladder' AND tg.name = 'Breadth-First Search'
ON CONFLICT DO NOTHING;

-- First Missing Positive (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'First Missing Positive',
    'https://leetcode.com/problems/first-missing-positive',
    'HARD',
    '# First Missing Positive

Solve the LeetCode problem **First Missing Positive**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Array, Hash Table

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'First Missing Positive');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for First Missing Positive
}
',
       'Work through First Missing Positive as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'First Missing Positive'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'First Missing Positive' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'First Missing Positive' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'First Missing Positive' AND tg.name = 'Hash Table'
ON CONFLICT DO NOTHING;

-- Find the Duplicate Number (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Find the Duplicate Number',
    'https://leetcode.com/problems/find-the-duplicate-number',
    'MEDIUM',
    '# Find the Duplicate Number

Solve the LeetCode problem **Find the Duplicate Number**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Array, Two Pointers, Binary Search, Bit Manipulation

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Find the Duplicate Number');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Find the Duplicate Number
}
',
       'Work through Find the Duplicate Number as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Find the Duplicate Number'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Find the Duplicate Number' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Find the Duplicate Number' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Find the Duplicate Number' AND tg.name = 'Two Pointers'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Find the Duplicate Number' AND tg.name = 'Binary Search'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Find the Duplicate Number' AND tg.name = 'Bit Manipulation'
ON CONFLICT DO NOTHING;

-- 3Sum Closest (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    '3Sum Closest',
    'https://leetcode.com/problems/3sum-closest',
    'MEDIUM',
    '# 3Sum Closest

Solve the LeetCode problem **3Sum Closest**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Array, Two Pointers, Sorting

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = '3Sum Closest');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for 3Sum Closest
}
',
       'Work through 3Sum Closest as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = '3Sum Closest'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = '3Sum Closest' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = '3Sum Closest' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = '3Sum Closest' AND tg.name = 'Two Pointers'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = '3Sum Closest' AND tg.name = 'Sorting'
ON CONFLICT DO NOTHING;

-- Valid Anagram (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Valid Anagram',
    'https://leetcode.com/problems/valid-anagram',
    'EASY',
    '# Valid Anagram

Solve the LeetCode problem **Valid Anagram**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Hash Table, String, Sorting

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Valid Anagram');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Valid Anagram
}
',
       'Work through Valid Anagram as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Valid Anagram'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Valid Anagram' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Valid Anagram' AND tg.name = 'Hash Table'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Valid Anagram' AND tg.name = 'String'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Valid Anagram' AND tg.name = 'Sorting'
ON CONFLICT DO NOTHING;

-- Merge Sorted Array (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Merge Sorted Array',
    'https://leetcode.com/problems/merge-sorted-array',
    'EASY',
    '# Merge Sorted Array

Solve the LeetCode problem **Merge Sorted Array**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Array, Two Pointers, Sorting

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Merge Sorted Array');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Merge Sorted Array
}
',
       'Work through Merge Sorted Array as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Merge Sorted Array'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Merge Sorted Array' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Merge Sorted Array' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Merge Sorted Array' AND tg.name = 'Two Pointers'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Merge Sorted Array' AND tg.name = 'Sorting'
ON CONFLICT DO NOTHING;

-- Max Consecutive Ones (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Max Consecutive Ones',
    'https://leetcode.com/problems/max-consecutive-ones',
    'EASY',
    '# Max Consecutive Ones

Solve the LeetCode problem **Max Consecutive Ones**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Array

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Max Consecutive Ones');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Max Consecutive Ones
}
',
       'Work through Max Consecutive Ones as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Max Consecutive Ones'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Max Consecutive Ones' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Max Consecutive Ones' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;

-- Unique Paths II (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Unique Paths II',
    'https://leetcode.com/problems/unique-paths-ii',
    'MEDIUM',
    '# Unique Paths II

Solve the LeetCode problem **Unique Paths II**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Array, Dynamic Programming, Matrix

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Unique Paths II');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Unique Paths II
}
',
       'Work through Unique Paths II as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Unique Paths II'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Unique Paths II' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Unique Paths II' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Unique Paths II' AND tg.name = 'Dynamic Programming'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Unique Paths II' AND tg.name = 'Matrix'
ON CONFLICT DO NOTHING;

-- Number of Ways to Assign Edge Weights I (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Number of Ways to Assign Edge Weights I',
    'https://leetcode.com/problems/number-of-ways-to-assign-edge-weights-i',
    'MEDIUM',
    '# Number of Ways to Assign Edge Weights I

Solve the LeetCode problem **Number of Ways to Assign Edge Weights I**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Math, Tree, Depth-First Search

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Number of Ways to Assign Edge Weights I');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Number of Ways to Assign Edge Weights I
}
',
       'Work through Number of Ways to Assign Edge Weights I as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Number of Ways to Assign Edge Weights I'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Number of Ways to Assign Edge Weights I' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Number of Ways to Assign Edge Weights I' AND tg.name = 'Math'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Number of Ways to Assign Edge Weights I' AND tg.name = 'Tree'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Number of Ways to Assign Edge Weights I' AND tg.name = 'Depth-First Search'
ON CONFLICT DO NOTHING;

-- Palindrome Partitioning II (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Palindrome Partitioning II',
    'https://leetcode.com/problems/palindrome-partitioning-ii',
    'HARD',
    '# Palindrome Partitioning II

Solve the LeetCode problem **Palindrome Partitioning II**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: String, Dynamic Programming

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Palindrome Partitioning II');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Palindrome Partitioning II
}
',
       'Work through Palindrome Partitioning II as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Palindrome Partitioning II'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Palindrome Partitioning II' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Palindrome Partitioning II' AND tg.name = 'String'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Palindrome Partitioning II' AND tg.name = 'Dynamic Programming'
ON CONFLICT DO NOTHING;

-- Majority Element II (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Majority Element II',
    'https://leetcode.com/problems/majority-element-ii',
    'MEDIUM',
    '# Majority Element II

Solve the LeetCode problem **Majority Element II**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Array, Hash Table, Sorting, Counting

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Majority Element II');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Majority Element II
}
',
       'Work through Majority Element II as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Majority Element II'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Majority Element II' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Majority Element II' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Majority Element II' AND tg.name = 'Hash Table'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Majority Element II' AND tg.name = 'Sorting'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Majority Element II' AND tg.name = 'Counting'
ON CONFLICT DO NOTHING;

-- Two Sum II - Input Array Is Sorted (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Two Sum II - Input Array Is Sorted',
    'https://leetcode.com/problems/two-sum-ii-input-array-is-sorted',
    'MEDIUM',
    '# Two Sum II - Input Array Is Sorted

Solve the LeetCode problem **Two Sum II - Input Array Is Sorted**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Array, Two Pointers, Binary Search

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Two Sum II - Input Array Is Sorted');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Two Sum II - Input Array Is Sorted
}
',
       'Work through Two Sum II - Input Array Is Sorted as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Two Sum II - Input Array Is Sorted'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Two Sum II - Input Array Is Sorted' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Two Sum II - Input Array Is Sorted' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Two Sum II - Input Array Is Sorted' AND tg.name = 'Two Pointers'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Two Sum II - Input Array Is Sorted' AND tg.name = 'Binary Search'
ON CONFLICT DO NOTHING;

-- Rotate Image (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Rotate Image',
    'https://leetcode.com/problems/rotate-image',
    'MEDIUM',
    '# Rotate Image

Solve the LeetCode problem **Rotate Image**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Array, Math, Matrix

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Rotate Image');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Rotate Image
}
',
       'Work through Rotate Image as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Rotate Image'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Rotate Image' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Rotate Image' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Rotate Image' AND tg.name = 'Math'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Rotate Image' AND tg.name = 'Matrix'
ON CONFLICT DO NOTHING;

-- Search a 2D Matrix II (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Search a 2D Matrix II',
    'https://leetcode.com/problems/search-a-2d-matrix-ii',
    'MEDIUM',
    '# Search a 2D Matrix II

Solve the LeetCode problem **Search a 2D Matrix II**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Array, Binary Search, Divide and Conquer, Matrix

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Search a 2D Matrix II');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Search a 2D Matrix II
}
',
       'Work through Search a 2D Matrix II as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Search a 2D Matrix II'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Search a 2D Matrix II' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Search a 2D Matrix II' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Search a 2D Matrix II' AND tg.name = 'Binary Search'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Search a 2D Matrix II' AND tg.name = 'Divide and Conquer'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Search a 2D Matrix II' AND tg.name = 'Matrix'
ON CONFLICT DO NOTHING;

-- Balanced Binary Tree (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Balanced Binary Tree',
    'https://leetcode.com/problems/balanced-binary-tree',
    'EASY',
    '# Balanced Binary Tree

Solve the LeetCode problem **Balanced Binary Tree**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Tree, Depth-First Search, Binary Tree

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Balanced Binary Tree');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Balanced Binary Tree
}
',
       'Work through Balanced Binary Tree as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Balanced Binary Tree'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Balanced Binary Tree' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Balanced Binary Tree' AND tg.name = 'Tree'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Balanced Binary Tree' AND tg.name = 'Depth-First Search'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Balanced Binary Tree' AND tg.name = 'Binary Tree'
ON CONFLICT DO NOTHING;

-- Squares of a Sorted Array (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Squares of a Sorted Array',
    'https://leetcode.com/problems/squares-of-a-sorted-array',
    'EASY',
    '# Squares of a Sorted Array

Solve the LeetCode problem **Squares of a Sorted Array**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Array, Two Pointers, Sorting

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Squares of a Sorted Array');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Squares of a Sorted Array
}
',
       'Work through Squares of a Sorted Array as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Squares of a Sorted Array'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Squares of a Sorted Array' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Squares of a Sorted Array' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Squares of a Sorted Array' AND tg.name = 'Two Pointers'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Squares of a Sorted Array' AND tg.name = 'Sorting'
ON CONFLICT DO NOTHING;

-- Merge k Sorted Lists (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Merge k Sorted Lists',
    'https://leetcode.com/problems/merge-k-sorted-lists',
    'HARD',
    '# Merge k Sorted Lists

Solve the LeetCode problem **Merge k Sorted Lists**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Linked List, Divide and Conquer, Heap (Priority Queue), Merge Sort

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Merge k Sorted Lists');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Merge k Sorted Lists
}
',
       'Work through Merge k Sorted Lists as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Merge k Sorted Lists'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Merge k Sorted Lists' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Merge k Sorted Lists' AND tg.name = 'Linked List'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Merge k Sorted Lists' AND tg.name = 'Divide and Conquer'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Merge k Sorted Lists' AND tg.name = 'Heap (Priority Queue)'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Merge k Sorted Lists' AND tg.name = 'Merge Sort'
ON CONFLICT DO NOTHING;

-- Reverse Nodes in k-Group (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Reverse Nodes in k-Group',
    'https://leetcode.com/problems/reverse-nodes-in-k-group',
    'HARD',
    '# Reverse Nodes in k-Group

Solve the LeetCode problem **Reverse Nodes in k-Group**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Linked List, Recursion

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Reverse Nodes in k-Group');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Reverse Nodes in k-Group
}
',
       'Work through Reverse Nodes in k-Group as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Reverse Nodes in k-Group'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Reverse Nodes in k-Group' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Reverse Nodes in k-Group' AND tg.name = 'Linked List'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Reverse Nodes in k-Group' AND tg.name = 'Recursion'
ON CONFLICT DO NOTHING;

-- Find Missing and Repeated Values (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Find Missing and Repeated Values',
    'https://leetcode.com/problems/find-missing-and-repeated-values',
    'EASY',
    '# Find Missing and Repeated Values

Solve the LeetCode problem **Find Missing and Repeated Values**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Array, Hash Table, Math, Matrix

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Find Missing and Repeated Values');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Find Missing and Repeated Values
}
',
       'Work through Find Missing and Repeated Values as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Find Missing and Repeated Values'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Find Missing and Repeated Values' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Find Missing and Repeated Values' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Find Missing and Repeated Values' AND tg.name = 'Hash Table'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Find Missing and Repeated Values' AND tg.name = 'Math'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Find Missing and Repeated Values' AND tg.name = 'Matrix'
ON CONFLICT DO NOTHING;

-- Binary Tree Maximum Path Sum (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Binary Tree Maximum Path Sum',
    'https://leetcode.com/problems/binary-tree-maximum-path-sum',
    'HARD',
    '# Binary Tree Maximum Path Sum

Solve the LeetCode problem **Binary Tree Maximum Path Sum**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Dynamic Programming, Tree, Depth-First Search, Binary Tree

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Binary Tree Maximum Path Sum');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Binary Tree Maximum Path Sum
}
',
       'Work through Binary Tree Maximum Path Sum as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Binary Tree Maximum Path Sum'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Binary Tree Maximum Path Sum' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Binary Tree Maximum Path Sum' AND tg.name = 'Dynamic Programming'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Binary Tree Maximum Path Sum' AND tg.name = 'Tree'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Binary Tree Maximum Path Sum' AND tg.name = 'Depth-First Search'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Binary Tree Maximum Path Sum' AND tg.name = 'Binary Tree'
ON CONFLICT DO NOTHING;

-- Coin Change (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Coin Change',
    'https://leetcode.com/problems/coin-change',
    'MEDIUM',
    '# Coin Change

Solve the LeetCode problem **Coin Change**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Array, Dynamic Programming, Breadth-First Search

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Coin Change');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Coin Change
}
',
       'Work through Coin Change as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Coin Change'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Coin Change' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Coin Change' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Coin Change' AND tg.name = 'Dynamic Programming'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Coin Change' AND tg.name = 'Breadth-First Search'
ON CONFLICT DO NOTHING;

-- Word Search II (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Word Search II',
    'https://leetcode.com/problems/word-search-ii',
    'HARD',
    '# Word Search II

Solve the LeetCode problem **Word Search II**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Array, String, Backtracking, Trie, Matrix

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Word Search II');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Word Search II
}
',
       'Work through Word Search II as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Word Search II'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Word Search II' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Word Search II' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Word Search II' AND tg.name = 'String'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Word Search II' AND tg.name = 'Backtracking'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Word Search II' AND tg.name = 'Trie'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Word Search II' AND tg.name = 'Matrix'
ON CONFLICT DO NOTHING;

-- Daily Temperatures (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Daily Temperatures',
    'https://leetcode.com/problems/daily-temperatures',
    'MEDIUM',
    '# Daily Temperatures

Solve the LeetCode problem **Daily Temperatures**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Array, Stack, Monotonic Stack

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Daily Temperatures');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Daily Temperatures
}
',
       'Work through Daily Temperatures as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Daily Temperatures'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Daily Temperatures' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Daily Temperatures' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Daily Temperatures' AND tg.name = 'Stack'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Daily Temperatures' AND tg.name = 'Monotonic Stack'
ON CONFLICT DO NOTHING;

-- Product of Array Except Self (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Product of Array Except Self',
    'https://leetcode.com/problems/product-of-array-except-self',
    'MEDIUM',
    '# Product of Array Except Self

Solve the LeetCode problem **Product of Array Except Self**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Array, Prefix Sum

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Product of Array Except Self');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Product of Array Except Self
}
',
       'Work through Product of Array Except Self as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Product of Array Except Self'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Product of Array Except Self' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Product of Array Except Self' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Product of Array Except Self' AND tg.name = 'Prefix Sum'
ON CONFLICT DO NOTHING;

-- Remove Nth Node From End of List (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Remove Nth Node From End of List',
    'https://leetcode.com/problems/remove-nth-node-from-end-of-list',
    'MEDIUM',
    '# Remove Nth Node From End of List

Solve the LeetCode problem **Remove Nth Node From End of List**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Linked List, Two Pointers

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Remove Nth Node From End of List');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Remove Nth Node From End of List
}
',
       'Work through Remove Nth Node From End of List as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Remove Nth Node From End of List'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Remove Nth Node From End of List' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Remove Nth Node From End of List' AND tg.name = 'Linked List'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Remove Nth Node From End of List' AND tg.name = 'Two Pointers'
ON CONFLICT DO NOTHING;

-- Sum of Total Strength of Wizards (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Sum of Total Strength of Wizards',
    'https://leetcode.com/problems/sum-of-total-strength-of-wizards',
    'HARD',
    '# Sum of Total Strength of Wizards

Solve the LeetCode problem **Sum of Total Strength of Wizards**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Array, Stack, Monotonic Stack, Prefix Sum

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Sum of Total Strength of Wizards');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Sum of Total Strength of Wizards
}
',
       'Work through Sum of Total Strength of Wizards as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Sum of Total Strength of Wizards'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Sum of Total Strength of Wizards' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Sum of Total Strength of Wizards' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Sum of Total Strength of Wizards' AND tg.name = 'Stack'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Sum of Total Strength of Wizards' AND tg.name = 'Monotonic Stack'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Sum of Total Strength of Wizards' AND tg.name = 'Prefix Sum'
ON CONFLICT DO NOTHING;

-- Maximum Number of Balloons (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Maximum Number of Balloons',
    'https://leetcode.com/problems/maximum-number-of-balloons',
    'EASY',
    '# Maximum Number of Balloons

Solve the LeetCode problem **Maximum Number of Balloons**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Hash Table, String, Counting

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Maximum Number of Balloons');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Maximum Number of Balloons
}
',
       'Work through Maximum Number of Balloons as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Maximum Number of Balloons'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Maximum Number of Balloons' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Maximum Number of Balloons' AND tg.name = 'Hash Table'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Maximum Number of Balloons' AND tg.name = 'String'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Maximum Number of Balloons' AND tg.name = 'Counting'
ON CONFLICT DO NOTHING;

-- Number of ZigZag Arrays I (freq 49.4)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Number of ZigZag Arrays I',
    'https://leetcode.com/problems/number-of-zigzag-arrays-i',
    'HARD',
    '# Number of ZigZag Arrays I

Solve the LeetCode problem **Number of ZigZag Arrays I**.

- Company focus: Amazon (30-day list)
- Frequency: 49.4
- Related topics: Dynamic Programming, Prefix Sum

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Number of ZigZag Arrays I');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Number of ZigZag Arrays I
}
',
       'Work through Number of ZigZag Arrays I as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Number of ZigZag Arrays I'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Number of ZigZag Arrays I' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Number of ZigZag Arrays I' AND tg.name = 'Dynamic Programming'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Number of ZigZag Arrays I' AND tg.name = 'Prefix Sum'
ON CONFLICT DO NOTHING;

-- Copy List with Random Pointer (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Copy List with Random Pointer',
    'https://leetcode.com/problems/copy-list-with-random-pointer',
    'MEDIUM',
    '# Copy List with Random Pointer

Solve the LeetCode problem **Copy List with Random Pointer**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Hash Table, Linked List

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Copy List with Random Pointer');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Copy List with Random Pointer
}
',
       'Work through Copy List with Random Pointer as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Copy List with Random Pointer'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Copy List with Random Pointer' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Copy List with Random Pointer' AND tg.name = 'Hash Table'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Copy List with Random Pointer' AND tg.name = 'Linked List'
ON CONFLICT DO NOTHING;

-- Unique Paths (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Unique Paths',
    'https://leetcode.com/problems/unique-paths',
    'MEDIUM',
    '# Unique Paths

Solve the LeetCode problem **Unique Paths**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Math, Dynamic Programming, Combinatorics

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Unique Paths');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Unique Paths
}
',
       'Work through Unique Paths as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Unique Paths'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Unique Paths' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Unique Paths' AND tg.name = 'Math'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Unique Paths' AND tg.name = 'Dynamic Programming'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Unique Paths' AND tg.name = 'Combinatorics'
ON CONFLICT DO NOTHING;

-- Asteroid Collision (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Asteroid Collision',
    'https://leetcode.com/problems/asteroid-collision',
    'MEDIUM',
    '# Asteroid Collision

Solve the LeetCode problem **Asteroid Collision**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Stack, Simulation

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Asteroid Collision');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Asteroid Collision
}
',
       'Work through Asteroid Collision as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Asteroid Collision'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Asteroid Collision' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Asteroid Collision' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Asteroid Collision' AND tg.name = 'Stack'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Asteroid Collision' AND tg.name = 'Simulation'
ON CONFLICT DO NOTHING;

-- Contains Duplicate (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Contains Duplicate',
    'https://leetcode.com/problems/contains-duplicate',
    'EASY',
    '# Contains Duplicate

Solve the LeetCode problem **Contains Duplicate**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Hash Table, Sorting

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Contains Duplicate');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Contains Duplicate
}
',
       'Work through Contains Duplicate as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Contains Duplicate'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Contains Duplicate' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Contains Duplicate' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Contains Duplicate' AND tg.name = 'Hash Table'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Contains Duplicate' AND tg.name = 'Sorting'
ON CONFLICT DO NOTHING;

-- Check if Array Is Sorted and Rotated (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Check if Array Is Sorted and Rotated',
    'https://leetcode.com/problems/check-if-array-is-sorted-and-rotated',
    'EASY',
    '# Check if Array Is Sorted and Rotated

Solve the LeetCode problem **Check if Array Is Sorted and Rotated**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Check if Array Is Sorted and Rotated');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Check if Array Is Sorted and Rotated
}
',
       'Work through Check if Array Is Sorted and Rotated as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Check if Array Is Sorted and Rotated'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Check if Array Is Sorted and Rotated' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Check if Array Is Sorted and Rotated' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;

-- Isomorphic Strings (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Isomorphic Strings',
    'https://leetcode.com/problems/isomorphic-strings',
    'EASY',
    '# Isomorphic Strings

Solve the LeetCode problem **Isomorphic Strings**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Hash Table, String

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Isomorphic Strings');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Isomorphic Strings
}
',
       'Work through Isomorphic Strings as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Isomorphic Strings'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Isomorphic Strings' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Isomorphic Strings' AND tg.name = 'Hash Table'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Isomorphic Strings' AND tg.name = 'String'
ON CONFLICT DO NOTHING;

-- Sliding Window Maximum (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Sliding Window Maximum',
    'https://leetcode.com/problems/sliding-window-maximum',
    'HARD',
    '# Sliding Window Maximum

Solve the LeetCode problem **Sliding Window Maximum**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Queue, Sliding Window, Heap (Priority Queue), Monotonic Queue

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Sliding Window Maximum');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Sliding Window Maximum
}
',
       'Work through Sliding Window Maximum as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Sliding Window Maximum'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Sliding Window Maximum' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Sliding Window Maximum' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Sliding Window Maximum' AND tg.name = 'Queue'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Sliding Window Maximum' AND tg.name = 'Sliding Window'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Sliding Window Maximum' AND tg.name = 'Heap (Priority Queue)'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Sliding Window Maximum' AND tg.name = 'Monotonic Queue'
ON CONFLICT DO NOTHING;

-- Subarray Sum Equals K (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Subarray Sum Equals K',
    'https://leetcode.com/problems/subarray-sum-equals-k',
    'MEDIUM',
    '# Subarray Sum Equals K

Solve the LeetCode problem **Subarray Sum Equals K**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Hash Table, Prefix Sum

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Subarray Sum Equals K');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Subarray Sum Equals K
}
',
       'Work through Subarray Sum Equals K as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Subarray Sum Equals K'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Subarray Sum Equals K' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Subarray Sum Equals K' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Subarray Sum Equals K' AND tg.name = 'Hash Table'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Subarray Sum Equals K' AND tg.name = 'Prefix Sum'
ON CONFLICT DO NOTHING;

-- Capacity To Ship Packages Within D Days (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Capacity To Ship Packages Within D Days',
    'https://leetcode.com/problems/capacity-to-ship-packages-within-d-days',
    'MEDIUM',
    '# Capacity To Ship Packages Within D Days

Solve the LeetCode problem **Capacity To Ship Packages Within D Days**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Binary Search

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Capacity To Ship Packages Within D Days');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Capacity To Ship Packages Within D Days
}
',
       'Work through Capacity To Ship Packages Within D Days as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Capacity To Ship Packages Within D Days'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Capacity To Ship Packages Within D Days' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Capacity To Ship Packages Within D Days' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Capacity To Ship Packages Within D Days' AND tg.name = 'Binary Search'
ON CONFLICT DO NOTHING;

-- 4Sum (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    '4Sum',
    'https://leetcode.com/problems/4sum',
    'MEDIUM',
    '# 4Sum

Solve the LeetCode problem **4Sum**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Two Pointers, Sorting

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = '4Sum');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for 4Sum
}
',
       'Work through 4Sum as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = '4Sum'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = '4Sum' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = '4Sum' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = '4Sum' AND tg.name = 'Two Pointers'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = '4Sum' AND tg.name = 'Sorting'
ON CONFLICT DO NOTHING;

-- Set Matrix Zeroes (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Set Matrix Zeroes',
    'https://leetcode.com/problems/set-matrix-zeroes',
    'MEDIUM',
    '# Set Matrix Zeroes

Solve the LeetCode problem **Set Matrix Zeroes**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Hash Table, Matrix

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Set Matrix Zeroes');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Set Matrix Zeroes
}
',
       'Work through Set Matrix Zeroes as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Set Matrix Zeroes'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Set Matrix Zeroes' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Set Matrix Zeroes' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Set Matrix Zeroes' AND tg.name = 'Hash Table'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Set Matrix Zeroes' AND tg.name = 'Matrix'
ON CONFLICT DO NOTHING;

-- Earliest Finish Time for Land and Water Rides II (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Earliest Finish Time for Land and Water Rides II',
    'https://leetcode.com/problems/earliest-finish-time-for-land-and-water-rides-ii',
    'MEDIUM',
    '# Earliest Finish Time for Land and Water Rides II

Solve the LeetCode problem **Earliest Finish Time for Land and Water Rides II**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Two Pointers, Binary Search, Greedy, Sorting

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Earliest Finish Time for Land and Water Rides II');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Earliest Finish Time for Land and Water Rides II
}
',
       'Work through Earliest Finish Time for Land and Water Rides II as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Earliest Finish Time for Land and Water Rides II'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Earliest Finish Time for Land and Water Rides II' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Earliest Finish Time for Land and Water Rides II' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Earliest Finish Time for Land and Water Rides II' AND tg.name = 'Two Pointers'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Earliest Finish Time for Land and Water Rides II' AND tg.name = 'Binary Search'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Earliest Finish Time for Land and Water Rides II' AND tg.name = 'Greedy'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Earliest Finish Time for Land and Water Rides II' AND tg.name = 'Sorting'
ON CONFLICT DO NOTHING;

-- Earliest Finish Time for Land and Water Rides I (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Earliest Finish Time for Land and Water Rides I',
    'https://leetcode.com/problems/earliest-finish-time-for-land-and-water-rides-i',
    'EASY',
    '# Earliest Finish Time for Land and Water Rides I

Solve the LeetCode problem **Earliest Finish Time for Land and Water Rides I**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Two Pointers, Binary Search, Greedy, Sorting

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Earliest Finish Time for Land and Water Rides I');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Earliest Finish Time for Land and Water Rides I
}
',
       'Work through Earliest Finish Time for Land and Water Rides I as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Earliest Finish Time for Land and Water Rides I'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Earliest Finish Time for Land and Water Rides I' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Earliest Finish Time for Land and Water Rides I' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Earliest Finish Time for Land and Water Rides I' AND tg.name = 'Two Pointers'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Earliest Finish Time for Land and Water Rides I' AND tg.name = 'Binary Search'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Earliest Finish Time for Land and Water Rides I' AND tg.name = 'Greedy'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Earliest Finish Time for Land and Water Rides I' AND tg.name = 'Sorting'
ON CONFLICT DO NOTHING;

-- Spiral Matrix (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Spiral Matrix',
    'https://leetcode.com/problems/spiral-matrix',
    'MEDIUM',
    '# Spiral Matrix

Solve the LeetCode problem **Spiral Matrix**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Matrix, Simulation

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Spiral Matrix');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Spiral Matrix
}
',
       'Work through Spiral Matrix as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Spiral Matrix'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Spiral Matrix' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Spiral Matrix' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Spiral Matrix' AND tg.name = 'Matrix'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Spiral Matrix' AND tg.name = 'Simulation'
ON CONFLICT DO NOTHING;

-- Minimum Operations to Sort a Permutation (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Minimum Operations to Sort a Permutation',
    'https://leetcode.com/problems/minimum-operations-to-sort-a-permutation',
    'MEDIUM',
    '# Minimum Operations to Sort a Permutation

Solve the LeetCode problem **Minimum Operations to Sort a Permutation**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Minimum Operations to Sort a Permutation');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Minimum Operations to Sort a Permutation
}
',
       'Work through Minimum Operations to Sort a Permutation as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Minimum Operations to Sort a Permutation'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Minimum Operations to Sort a Permutation' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Minimum Operations to Sort a Permutation' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;

-- Min Stack (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Min Stack',
    'https://leetcode.com/problems/min-stack',
    'MEDIUM',
    '# Min Stack

Solve the LeetCode problem **Min Stack**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Stack, Design

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Min Stack');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Min Stack
}
',
       'Work through Min Stack as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Min Stack'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Min Stack' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Min Stack' AND tg.name = 'Stack'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Min Stack' AND tg.name = 'Design'
ON CONFLICT DO NOTHING;

-- Symmetric Tree (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Symmetric Tree',
    'https://leetcode.com/problems/symmetric-tree',
    'EASY',
    '# Symmetric Tree

Solve the LeetCode problem **Symmetric Tree**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Tree, Depth-First Search, Breadth-First Search, Binary Tree

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Symmetric Tree');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Symmetric Tree
}
',
       'Work through Symmetric Tree as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Symmetric Tree'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Symmetric Tree' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Symmetric Tree' AND tg.name = 'Tree'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Symmetric Tree' AND tg.name = 'Depth-First Search'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Symmetric Tree' AND tg.name = 'Breadth-First Search'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Symmetric Tree' AND tg.name = 'Binary Tree'
ON CONFLICT DO NOTHING;

-- Serialize and Deserialize Binary Tree (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Serialize and Deserialize Binary Tree',
    'https://leetcode.com/problems/serialize-and-deserialize-binary-tree',
    'HARD',
    '# Serialize and Deserialize Binary Tree

Solve the LeetCode problem **Serialize and Deserialize Binary Tree**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: String, Tree, Depth-First Search, Breadth-First Search, Design, Binary Tree

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Serialize and Deserialize Binary Tree');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Serialize and Deserialize Binary Tree
}
',
       'Work through Serialize and Deserialize Binary Tree as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Serialize and Deserialize Binary Tree'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Serialize and Deserialize Binary Tree' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Serialize and Deserialize Binary Tree' AND tg.name = 'String'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Serialize and Deserialize Binary Tree' AND tg.name = 'Tree'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Serialize and Deserialize Binary Tree' AND tg.name = 'Depth-First Search'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Serialize and Deserialize Binary Tree' AND tg.name = 'Breadth-First Search'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Serialize and Deserialize Binary Tree' AND tg.name = 'Design'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Serialize and Deserialize Binary Tree' AND tg.name = 'Binary Tree'
ON CONFLICT DO NOTHING;

-- Next Permutation (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Next Permutation',
    'https://leetcode.com/problems/next-permutation',
    'MEDIUM',
    '# Next Permutation

Solve the LeetCode problem **Next Permutation**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Two Pointers

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Next Permutation');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Next Permutation
}
',
       'Work through Next Permutation as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Next Permutation'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Next Permutation' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Next Permutation' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Next Permutation' AND tg.name = 'Two Pointers'
ON CONFLICT DO NOTHING;

-- Longest Arithmetic Sequence After Changing At Most One Element (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Longest Arithmetic Sequence After Changing At Most One Element',
    'https://leetcode.com/problems/longest-arithmetic-sequence-after-changing-at-most-one-element',
    'MEDIUM',
    '# Longest Arithmetic Sequence After Changing At Most One Element

Solve the LeetCode problem **Longest Arithmetic Sequence After Changing At Most One Element**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Enumeration

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Longest Arithmetic Sequence After Changing At Most One Element');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Longest Arithmetic Sequence After Changing At Most One Element
}
',
       'Work through Longest Arithmetic Sequence After Changing At Most One Element as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Longest Arithmetic Sequence After Changing At Most One Element'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Longest Arithmetic Sequence After Changing At Most One Element' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Longest Arithmetic Sequence After Changing At Most One Element' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Longest Arithmetic Sequence After Changing At Most One Element' AND tg.name = 'Enumeration'
ON CONFLICT DO NOTHING;

-- Binary Tree Level Order Traversal (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Binary Tree Level Order Traversal',
    'https://leetcode.com/problems/binary-tree-level-order-traversal',
    'MEDIUM',
    '# Binary Tree Level Order Traversal

Solve the LeetCode problem **Binary Tree Level Order Traversal**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Tree, Breadth-First Search, Binary Tree

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Binary Tree Level Order Traversal');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Binary Tree Level Order Traversal
}
',
       'Work through Binary Tree Level Order Traversal as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Binary Tree Level Order Traversal'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Binary Tree Level Order Traversal' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Binary Tree Level Order Traversal' AND tg.name = 'Tree'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Binary Tree Level Order Traversal' AND tg.name = 'Breadth-First Search'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Binary Tree Level Order Traversal' AND tg.name = 'Binary Tree'
ON CONFLICT DO NOTHING;

-- Minimum Cost of Buying Candies With Discount (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Minimum Cost of Buying Candies With Discount',
    'https://leetcode.com/problems/minimum-cost-of-buying-candies-with-discount',
    'EASY',
    '# Minimum Cost of Buying Candies With Discount

Solve the LeetCode problem **Minimum Cost of Buying Candies With Discount**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Greedy, Sorting

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Minimum Cost of Buying Candies With Discount');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Minimum Cost of Buying Candies With Discount
}
',
       'Work through Minimum Cost of Buying Candies With Discount as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Minimum Cost of Buying Candies With Discount'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Minimum Cost of Buying Candies With Discount' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Minimum Cost of Buying Candies With Discount' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Minimum Cost of Buying Candies With Discount' AND tg.name = 'Greedy'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Minimum Cost of Buying Candies With Discount' AND tg.name = 'Sorting'
ON CONFLICT DO NOTHING;

-- Kth Missing Positive Number (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Kth Missing Positive Number',
    'https://leetcode.com/problems/kth-missing-positive-number',
    'EASY',
    '# Kth Missing Positive Number

Solve the LeetCode problem **Kth Missing Positive Number**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Binary Search

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Kth Missing Positive Number');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Kth Missing Positive Number
}
',
       'Work through Kth Missing Positive Number as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Kth Missing Positive Number'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Kth Missing Positive Number' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Kth Missing Positive Number' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Kth Missing Positive Number' AND tg.name = 'Binary Search'
ON CONFLICT DO NOTHING;

-- Find Minimum in Rotated Sorted Array (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Find Minimum in Rotated Sorted Array',
    'https://leetcode.com/problems/find-minimum-in-rotated-sorted-array',
    'MEDIUM',
    '# Find Minimum in Rotated Sorted Array

Solve the LeetCode problem **Find Minimum in Rotated Sorted Array**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Binary Search

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Find Minimum in Rotated Sorted Array');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Find Minimum in Rotated Sorted Array
}
',
       'Work through Find Minimum in Rotated Sorted Array as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Find Minimum in Rotated Sorted Array'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Find Minimum in Rotated Sorted Array' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Find Minimum in Rotated Sorted Array' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Find Minimum in Rotated Sorted Array' AND tg.name = 'Binary Search'
ON CONFLICT DO NOTHING;

-- Search in Rotated Sorted Array II (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Search in Rotated Sorted Array II',
    'https://leetcode.com/problems/search-in-rotated-sorted-array-ii',
    'MEDIUM',
    '# Search in Rotated Sorted Array II

Solve the LeetCode problem **Search in Rotated Sorted Array II**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Binary Search

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Search in Rotated Sorted Array II');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Search in Rotated Sorted Array II
}
',
       'Work through Search in Rotated Sorted Array II as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Search in Rotated Sorted Array II'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Search in Rotated Sorted Array II' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Search in Rotated Sorted Array II' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Search in Rotated Sorted Array II' AND tg.name = 'Binary Search'
ON CONFLICT DO NOTHING;

-- Optimal Partition of String (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Optimal Partition of String',
    'https://leetcode.com/problems/optimal-partition-of-string',
    'MEDIUM',
    '# Optimal Partition of String

Solve the LeetCode problem **Optimal Partition of String**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Hash Table, String, Greedy

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Optimal Partition of String');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Optimal Partition of String
}
',
       'Work through Optimal Partition of String as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Optimal Partition of String'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Optimal Partition of String' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Optimal Partition of String' AND tg.name = 'Hash Table'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Optimal Partition of String' AND tg.name = 'String'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Optimal Partition of String' AND tg.name = 'Greedy'
ON CONFLICT DO NOTHING;

-- Remove Element (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Remove Element',
    'https://leetcode.com/problems/remove-element',
    'EASY',
    '# Remove Element

Solve the LeetCode problem **Remove Element**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Two Pointers

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Remove Element');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Remove Element
}
',
       'Work through Remove Element as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Remove Element'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Remove Element' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Remove Element' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Remove Element' AND tg.name = 'Two Pointers'
ON CONFLICT DO NOTHING;

-- Find Pivot Index (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Find Pivot Index',
    'https://leetcode.com/problems/find-pivot-index',
    'EASY',
    '# Find Pivot Index

Solve the LeetCode problem **Find Pivot Index**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Prefix Sum

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Find Pivot Index');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Find Pivot Index
}
',
       'Work through Find Pivot Index as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Find Pivot Index'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Find Pivot Index' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Find Pivot Index' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Find Pivot Index' AND tg.name = 'Prefix Sum'
ON CONFLICT DO NOTHING;

-- Remove Outermost Parentheses (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Remove Outermost Parentheses',
    'https://leetcode.com/problems/remove-outermost-parentheses',
    'EASY',
    '# Remove Outermost Parentheses

Solve the LeetCode problem **Remove Outermost Parentheses**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: String, Stack

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Remove Outermost Parentheses');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Remove Outermost Parentheses
}
',
       'Work through Remove Outermost Parentheses as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Remove Outermost Parentheses'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Remove Outermost Parentheses' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Remove Outermost Parentheses' AND tg.name = 'String'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Remove Outermost Parentheses' AND tg.name = 'Stack'
ON CONFLICT DO NOTHING;

-- Maximum Product Subarray (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Maximum Product Subarray',
    'https://leetcode.com/problems/maximum-product-subarray',
    'MEDIUM',
    '# Maximum Product Subarray

Solve the LeetCode problem **Maximum Product Subarray**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Dynamic Programming

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Maximum Product Subarray');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Maximum Product Subarray
}
',
       'Work through Maximum Product Subarray as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Maximum Product Subarray'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Maximum Product Subarray' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Maximum Product Subarray' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Maximum Product Subarray' AND tg.name = 'Dynamic Programming'
ON CONFLICT DO NOTHING;

-- Parallel Courses III (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Parallel Courses III',
    'https://leetcode.com/problems/parallel-courses-iii',
    'HARD',
    '# Parallel Courses III

Solve the LeetCode problem **Parallel Courses III**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Dynamic Programming, Graph Theory, Topological Sort

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Parallel Courses III');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Parallel Courses III
}
',
       'Work through Parallel Courses III as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Parallel Courses III'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Parallel Courses III' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Parallel Courses III' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Parallel Courses III' AND tg.name = 'Dynamic Programming'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Parallel Courses III' AND tg.name = 'Graph Theory'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Parallel Courses III' AND tg.name = 'Topological Sort'
ON CONFLICT DO NOTHING;

-- Partition Array According to Given Pivot (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Partition Array According to Given Pivot',
    'https://leetcode.com/problems/partition-array-according-to-given-pivot',
    'MEDIUM',
    '# Partition Array According to Given Pivot

Solve the LeetCode problem **Partition Array According to Given Pivot**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Two Pointers, Simulation

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Partition Array According to Given Pivot');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Partition Array According to Given Pivot
}
',
       'Work through Partition Array According to Given Pivot as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Partition Array According to Given Pivot'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Partition Array According to Given Pivot' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Partition Array According to Given Pivot' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Partition Array According to Given Pivot' AND tg.name = 'Two Pointers'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Partition Array According to Given Pivot' AND tg.name = 'Simulation'
ON CONFLICT DO NOTHING;

-- Edit Distance (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Edit Distance',
    'https://leetcode.com/problems/edit-distance',
    'MEDIUM',
    '# Edit Distance

Solve the LeetCode problem **Edit Distance**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: String, Dynamic Programming

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Edit Distance');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Edit Distance
}
',
       'Work through Edit Distance as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Edit Distance'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Edit Distance' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Edit Distance' AND tg.name = 'String'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Edit Distance' AND tg.name = 'Dynamic Programming'
ON CONFLICT DO NOTHING;

-- Decode String (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Decode String',
    'https://leetcode.com/problems/decode-string',
    'MEDIUM',
    '# Decode String

Solve the LeetCode problem **Decode String**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: String, Stack, Recursion

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Decode String');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Decode String
}
',
       'Work through Decode String as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Decode String'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Decode String' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Decode String' AND tg.name = 'String'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Decode String' AND tg.name = 'Stack'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Decode String' AND tg.name = 'Recursion'
ON CONFLICT DO NOTHING;

-- Multiply Strings (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Multiply Strings',
    'https://leetcode.com/problems/multiply-strings',
    'MEDIUM',
    '# Multiply Strings

Solve the LeetCode problem **Multiply Strings**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Math, String, Simulation

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Multiply Strings');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Multiply Strings
}
',
       'Work through Multiply Strings as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Multiply Strings'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Multiply Strings' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Multiply Strings' AND tg.name = 'Math'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Multiply Strings' AND tg.name = 'String'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Multiply Strings' AND tg.name = 'Simulation'
ON CONFLICT DO NOTHING;

-- Subsets (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Subsets',
    'https://leetcode.com/problems/subsets',
    'MEDIUM',
    '# Subsets

Solve the LeetCode problem **Subsets**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Backtracking, Bit Manipulation

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Subsets');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Subsets
}
',
       'Work through Subsets as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Subsets'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Subsets' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Subsets' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Subsets' AND tg.name = 'Backtracking'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Subsets' AND tg.name = 'Bit Manipulation'
ON CONFLICT DO NOTHING;

-- Permutations II (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Permutations II',
    'https://leetcode.com/problems/permutations-ii',
    'MEDIUM',
    '# Permutations II

Solve the LeetCode problem **Permutations II**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Backtracking, Sorting

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Permutations II');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Permutations II
}
',
       'Work through Permutations II as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Permutations II'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Permutations II' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Permutations II' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Permutations II' AND tg.name = 'Backtracking'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Permutations II' AND tg.name = 'Sorting'
ON CONFLICT DO NOTHING;

-- Maximum K to Sort a Permutation (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Maximum K to Sort a Permutation',
    'https://leetcode.com/problems/maximum-k-to-sort-a-permutation',
    'MEDIUM',
    '# Maximum K to Sort a Permutation

Solve the LeetCode problem **Maximum K to Sort a Permutation**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Bit Manipulation

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Maximum K to Sort a Permutation');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Maximum K to Sort a Permutation
}
',
       'Work through Maximum K to Sort a Permutation as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Maximum K to Sort a Permutation'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Maximum K to Sort a Permutation' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Maximum K to Sort a Permutation' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Maximum K to Sort a Permutation' AND tg.name = 'Bit Manipulation'
ON CONFLICT DO NOTHING;

-- N-Queens (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'N-Queens',
    'https://leetcode.com/problems/n-queens',
    'HARD',
    '# N-Queens

Solve the LeetCode problem **N-Queens**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Backtracking

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'N-Queens');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for N-Queens
}
',
       'Work through N-Queens as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'N-Queens'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'N-Queens' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'N-Queens' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'N-Queens' AND tg.name = 'Backtracking'
ON CONFLICT DO NOTHING;

-- Insert Interval (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Insert Interval',
    'https://leetcode.com/problems/insert-interval',
    'MEDIUM',
    '# Insert Interval

Solve the LeetCode problem **Insert Interval**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Insert Interval');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Insert Interval
}
',
       'Work through Insert Interval as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Insert Interval'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Insert Interval' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Insert Interval' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;

-- Process String with Special Operations I (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Process String with Special Operations I',
    'https://leetcode.com/problems/process-string-with-special-operations-i',
    'MEDIUM',
    '# Process String with Special Operations I

Solve the LeetCode problem **Process String with Special Operations I**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: String, Simulation

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Process String with Special Operations I');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Process String with Special Operations I
}
',
       'Work through Process String with Special Operations I as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Process String with Special Operations I'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Process String with Special Operations I' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Process String with Special Operations I' AND tg.name = 'String'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Process String with Special Operations I' AND tg.name = 'Simulation'
ON CONFLICT DO NOTHING;

-- User Activity for the Past 30 Days I (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'User Activity for the Past 30 Days I',
    'https://leetcode.com/problems/user-activity-for-the-past-30-days-i',
    'EASY',
    '# User Activity for the Past 30 Days I

Solve the LeetCode problem **User Activity for the Past 30 Days I**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Database

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'User Activity for the Past 30 Days I');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for User Activity for the Past 30 Days I
}
',
       'Work through User Activity for the Past 30 Days I as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'User Activity for the Past 30 Days I'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'User Activity for the Past 30 Days I' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'User Activity for the Past 30 Days I' AND tg.name = 'Database'
ON CONFLICT DO NOTHING;

-- Process String with Special Operations II (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Process String with Special Operations II',
    'https://leetcode.com/problems/process-string-with-special-operations-ii',
    'HARD',
    '# Process String with Special Operations II

Solve the LeetCode problem **Process String with Special Operations II**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: String, Simulation

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Process String with Special Operations II');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Process String with Special Operations II
}
',
       'Work through Process String with Special Operations II as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Process String with Special Operations II'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Process String with Special Operations II' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Process String with Special Operations II' AND tg.name = 'String'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Process String with Special Operations II' AND tg.name = 'Simulation'
ON CONFLICT DO NOTHING;

-- Number of Operations to Make Network Connected (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Number of Operations to Make Network Connected',
    'https://leetcode.com/problems/number-of-operations-to-make-network-connected',
    'MEDIUM',
    '# Number of Operations to Make Network Connected

Solve the LeetCode problem **Number of Operations to Make Network Connected**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Depth-First Search, Breadth-First Search, Union-Find, Graph Theory

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Number of Operations to Make Network Connected');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Number of Operations to Make Network Connected
}
',
       'Work through Number of Operations to Make Network Connected as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Number of Operations to Make Network Connected'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Number of Operations to Make Network Connected' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Number of Operations to Make Network Connected' AND tg.name = 'Depth-First Search'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Number of Operations to Make Network Connected' AND tg.name = 'Breadth-First Search'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Number of Operations to Make Network Connected' AND tg.name = 'Union-Find'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Number of Operations to Make Network Connected' AND tg.name = 'Graph Theory'
ON CONFLICT DO NOTHING;

-- Merge Strings Alternately (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Merge Strings Alternately',
    'https://leetcode.com/problems/merge-strings-alternately',
    'EASY',
    '# Merge Strings Alternately

Solve the LeetCode problem **Merge Strings Alternately**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Two Pointers, String

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Merge Strings Alternately');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Merge Strings Alternately
}
',
       'Work through Merge Strings Alternately as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Merge Strings Alternately'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Merge Strings Alternately' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Merge Strings Alternately' AND tg.name = 'Two Pointers'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Merge Strings Alternately' AND tg.name = 'String'
ON CONFLICT DO NOTHING;

-- Random Pick with Weight (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Random Pick with Weight',
    'https://leetcode.com/problems/random-pick-with-weight',
    'MEDIUM',
    '# Random Pick with Weight

Solve the LeetCode problem **Random Pick with Weight**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Math, Binary Search, Prefix Sum, Randomized

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Random Pick with Weight');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Random Pick with Weight
}
',
       'Work through Random Pick with Weight as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Random Pick with Weight'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Random Pick with Weight' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Random Pick with Weight' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Random Pick with Weight' AND tg.name = 'Math'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Random Pick with Weight' AND tg.name = 'Binary Search'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Random Pick with Weight' AND tg.name = 'Prefix Sum'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Random Pick with Weight' AND tg.name = 'Randomized'
ON CONFLICT DO NOTHING;

-- Reverse Linked List II (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Reverse Linked List II',
    'https://leetcode.com/problems/reverse-linked-list-ii',
    'MEDIUM',
    '# Reverse Linked List II

Solve the LeetCode problem **Reverse Linked List II**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Linked List

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Reverse Linked List II');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Reverse Linked List II
}
',
       'Work through Reverse Linked List II as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Reverse Linked List II'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Reverse Linked List II' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Reverse Linked List II' AND tg.name = 'Linked List'
ON CONFLICT DO NOTHING;

-- Intersection of Two Linked Lists (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Intersection of Two Linked Lists',
    'https://leetcode.com/problems/intersection-of-two-linked-lists',
    'EASY',
    '# Intersection of Two Linked Lists

Solve the LeetCode problem **Intersection of Two Linked Lists**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Hash Table, Linked List, Two Pointers

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Intersection of Two Linked Lists');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Intersection of Two Linked Lists
}
',
       'Work through Intersection of Two Linked Lists as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Intersection of Two Linked Lists'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Intersection of Two Linked Lists' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Intersection of Two Linked Lists' AND tg.name = 'Hash Table'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Intersection of Two Linked Lists' AND tg.name = 'Linked List'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Intersection of Two Linked Lists' AND tg.name = 'Two Pointers'
ON CONFLICT DO NOTHING;

-- Surrounded Regions (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Surrounded Regions',
    'https://leetcode.com/problems/surrounded-regions',
    'MEDIUM',
    '# Surrounded Regions

Solve the LeetCode problem **Surrounded Regions**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Depth-First Search, Breadth-First Search, Union-Find, Matrix

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Surrounded Regions');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Surrounded Regions
}
',
       'Work through Surrounded Regions as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Surrounded Regions'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Surrounded Regions' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Surrounded Regions' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Surrounded Regions' AND tg.name = 'Depth-First Search'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Surrounded Regions' AND tg.name = 'Breadth-First Search'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Surrounded Regions' AND tg.name = 'Union-Find'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Surrounded Regions' AND tg.name = 'Matrix'
ON CONFLICT DO NOTHING;

-- Happy Number (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Happy Number',
    'https://leetcode.com/problems/happy-number',
    'EASY',
    '# Happy Number

Solve the LeetCode problem **Happy Number**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Hash Table, Math, Two Pointers

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Happy Number');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Happy Number
}
',
       'Work through Happy Number as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Happy Number'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Happy Number' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Happy Number' AND tg.name = 'Hash Table'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Happy Number' AND tg.name = 'Math'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Happy Number' AND tg.name = 'Two Pointers'
ON CONFLICT DO NOTHING;

-- Rotate Array (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Rotate Array',
    'https://leetcode.com/problems/rotate-array',
    'MEDIUM',
    '# Rotate Array

Solve the LeetCode problem **Rotate Array**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Math, Two Pointers

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Rotate Array');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Rotate Array
}
',
       'Work through Rotate Array as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Rotate Array'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Rotate Array' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Rotate Array' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Rotate Array' AND tg.name = 'Math'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Rotate Array' AND tg.name = 'Two Pointers'
ON CONFLICT DO NOTHING;

-- Find the Highest Altitude (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Find the Highest Altitude',
    'https://leetcode.com/problems/find-the-highest-altitude',
    'EASY',
    '# Find the Highest Altitude

Solve the LeetCode problem **Find the Highest Altitude**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Prefix Sum

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Find the Highest Altitude');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Find the Highest Altitude
}
',
       'Work through Find the Highest Altitude as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Find the Highest Altitude'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Find the Highest Altitude' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Find the Highest Altitude' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Find the Highest Altitude' AND tg.name = 'Prefix Sum'
ON CONFLICT DO NOTHING;

-- Longest Increasing Subsequence (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Longest Increasing Subsequence',
    'https://leetcode.com/problems/longest-increasing-subsequence',
    'MEDIUM',
    '# Longest Increasing Subsequence

Solve the LeetCode problem **Longest Increasing Subsequence**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Binary Search, Dynamic Programming

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Longest Increasing Subsequence');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Longest Increasing Subsequence
}
',
       'Work through Longest Increasing Subsequence as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Longest Increasing Subsequence'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Longest Increasing Subsequence' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Longest Increasing Subsequence' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Longest Increasing Subsequence' AND tg.name = 'Binary Search'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Longest Increasing Subsequence' AND tg.name = 'Dynamic Programming'
ON CONFLICT DO NOTHING;

-- Convert Sorted List to Binary Search Tree (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Convert Sorted List to Binary Search Tree',
    'https://leetcode.com/problems/convert-sorted-list-to-binary-search-tree',
    'MEDIUM',
    '# Convert Sorted List to Binary Search Tree

Solve the LeetCode problem **Convert Sorted List to Binary Search Tree**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Linked List, Divide and Conquer, Tree, Binary Search Tree, Binary Tree

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Convert Sorted List to Binary Search Tree');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Convert Sorted List to Binary Search Tree
}
',
       'Work through Convert Sorted List to Binary Search Tree as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Convert Sorted List to Binary Search Tree'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Convert Sorted List to Binary Search Tree' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Convert Sorted List to Binary Search Tree' AND tg.name = 'Linked List'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Convert Sorted List to Binary Search Tree' AND tg.name = 'Divide and Conquer'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Convert Sorted List to Binary Search Tree' AND tg.name = 'Tree'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Convert Sorted List to Binary Search Tree' AND tg.name = 'Binary Search Tree'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Convert Sorted List to Binary Search Tree' AND tg.name = 'Binary Tree'
ON CONFLICT DO NOTHING;

-- Recyclable and Low Fat Products (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Recyclable and Low Fat Products',
    'https://leetcode.com/problems/recyclable-and-low-fat-products',
    'EASY',
    '# Recyclable and Low Fat Products

Solve the LeetCode problem **Recyclable and Low Fat Products**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Database

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Recyclable and Low Fat Products');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Recyclable and Low Fat Products
}
',
       'Work through Recyclable and Low Fat Products as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Recyclable and Low Fat Products'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Recyclable and Low Fat Products' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Recyclable and Low Fat Products' AND tg.name = 'Database'
ON CONFLICT DO NOTHING;

-- Binary Tree Zigzag Level Order Traversal (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Binary Tree Zigzag Level Order Traversal',
    'https://leetcode.com/problems/binary-tree-zigzag-level-order-traversal',
    'MEDIUM',
    '# Binary Tree Zigzag Level Order Traversal

Solve the LeetCode problem **Binary Tree Zigzag Level Order Traversal**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Tree, Breadth-First Search, Binary Tree

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Binary Tree Zigzag Level Order Traversal');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Binary Tree Zigzag Level Order Traversal
}
',
       'Work through Binary Tree Zigzag Level Order Traversal as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Binary Tree Zigzag Level Order Traversal'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Binary Tree Zigzag Level Order Traversal' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Binary Tree Zigzag Level Order Traversal' AND tg.name = 'Tree'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Binary Tree Zigzag Level Order Traversal' AND tg.name = 'Breadth-First Search'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Binary Tree Zigzag Level Order Traversal' AND tg.name = 'Binary Tree'
ON CONFLICT DO NOTHING;

-- K Closest Points to Origin (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'K Closest Points to Origin',
    'https://leetcode.com/problems/k-closest-points-to-origin',
    'MEDIUM',
    '# K Closest Points to Origin

Solve the LeetCode problem **K Closest Points to Origin**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Math, Divide and Conquer, Geometry, Sorting, Heap (Priority Queue), Quickselect

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'K Closest Points to Origin');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for K Closest Points to Origin
}
',
       'Work through K Closest Points to Origin as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'K Closest Points to Origin'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'K Closest Points to Origin' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'K Closest Points to Origin' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'K Closest Points to Origin' AND tg.name = 'Math'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'K Closest Points to Origin' AND tg.name = 'Divide and Conquer'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'K Closest Points to Origin' AND tg.name = 'Geometry'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'K Closest Points to Origin' AND tg.name = 'Sorting'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'K Closest Points to Origin' AND tg.name = 'Heap (Priority Queue)'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'K Closest Points to Origin' AND tg.name = 'Quickselect'
ON CONFLICT DO NOTHING;

-- Cherry Pickup II (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Cherry Pickup II',
    'https://leetcode.com/problems/cherry-pickup-ii',
    'HARD',
    '# Cherry Pickup II

Solve the LeetCode problem **Cherry Pickup II**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Dynamic Programming, Matrix

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Cherry Pickup II');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Cherry Pickup II
}
',
       'Work through Cherry Pickup II as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Cherry Pickup II'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Cherry Pickup II' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Cherry Pickup II' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Cherry Pickup II' AND tg.name = 'Dynamic Programming'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Cherry Pickup II' AND tg.name = 'Matrix'
ON CONFLICT DO NOTHING;

-- Magnetic Force Between Two Balls (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Magnetic Force Between Two Balls',
    'https://leetcode.com/problems/magnetic-force-between-two-balls',
    'MEDIUM',
    '# Magnetic Force Between Two Balls

Solve the LeetCode problem **Magnetic Force Between Two Balls**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Binary Search, Sorting

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Magnetic Force Between Two Balls');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Magnetic Force Between Two Balls
}
',
       'Work through Magnetic Force Between Two Balls as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Magnetic Force Between Two Balls'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Magnetic Force Between Two Balls' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Magnetic Force Between Two Balls' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Magnetic Force Between Two Balls' AND tg.name = 'Binary Search'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Magnetic Force Between Two Balls' AND tg.name = 'Sorting'
ON CONFLICT DO NOTHING;

-- Consecutive Numbers (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Consecutive Numbers',
    'https://leetcode.com/problems/consecutive-numbers',
    'MEDIUM',
    '# Consecutive Numbers

Solve the LeetCode problem **Consecutive Numbers**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Database

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Consecutive Numbers');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Consecutive Numbers
}
',
       'Work through Consecutive Numbers as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Consecutive Numbers'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Consecutive Numbers' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Consecutive Numbers' AND tg.name = 'Database'
ON CONFLICT DO NOTHING;

-- Allocate Mailboxes (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Allocate Mailboxes',
    'https://leetcode.com/problems/allocate-mailboxes',
    'HARD',
    '# Allocate Mailboxes

Solve the LeetCode problem **Allocate Mailboxes**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Math, Dynamic Programming, Sorting

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Allocate Mailboxes');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Allocate Mailboxes
}
',
       'Work through Allocate Mailboxes as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Allocate Mailboxes'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Allocate Mailboxes' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Allocate Mailboxes' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Allocate Mailboxes' AND tg.name = 'Math'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Allocate Mailboxes' AND tg.name = 'Dynamic Programming'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Allocate Mailboxes' AND tg.name = 'Sorting'
ON CONFLICT DO NOTHING;

-- String to Integer (atoi) (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'String to Integer (atoi)',
    'https://leetcode.com/problems/string-to-integer-atoi',
    'MEDIUM',
    '# String to Integer (atoi)

Solve the LeetCode problem **String to Integer (atoi)**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: String

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'String to Integer (atoi)');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for String to Integer (atoi)
}
',
       'Work through String to Integer (atoi) as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'String to Integer (atoi)'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'String to Integer (atoi)' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'String to Integer (atoi)' AND tg.name = 'String'
ON CONFLICT DO NOTHING;

-- Sum of Subarray Minimums (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Sum of Subarray Minimums',
    'https://leetcode.com/problems/sum-of-subarray-minimums',
    'MEDIUM',
    '# Sum of Subarray Minimums

Solve the LeetCode problem **Sum of Subarray Minimums**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Dynamic Programming, Stack, Monotonic Stack

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Sum of Subarray Minimums');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Sum of Subarray Minimums
}
',
       'Work through Sum of Subarray Minimums as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Sum of Subarray Minimums'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Sum of Subarray Minimums' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Sum of Subarray Minimums' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Sum of Subarray Minimums' AND tg.name = 'Dynamic Programming'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Sum of Subarray Minimums' AND tg.name = 'Stack'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Sum of Subarray Minimums' AND tg.name = 'Monotonic Stack'
ON CONFLICT DO NOTHING;

-- Pascal''s Triangle (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Pascal''s Triangle',
    'https://leetcode.com/problems/pascals-triangle',
    'EASY',
    '# Pascal''s Triangle

Solve the LeetCode problem **Pascal''s Triangle**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Dynamic Programming

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Pascal''s Triangle');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Pascal''s Triangle
}
',
       'Work through Pascal''s Triangle as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Pascal''s Triangle'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Pascal''s Triangle' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Pascal''s Triangle' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Pascal''s Triangle' AND tg.name = 'Dynamic Programming'
ON CONFLICT DO NOTHING;

-- Top K Frequent Elements (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Top K Frequent Elements',
    'https://leetcode.com/problems/top-k-frequent-elements',
    'MEDIUM',
    '# Top K Frequent Elements

Solve the LeetCode problem **Top K Frequent Elements**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Hash Table, Divide and Conquer, Sorting, Heap (Priority Queue), Bucket Sort, Counting, Quickselect

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Top K Frequent Elements');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Top K Frequent Elements
}
',
       'Work through Top K Frequent Elements as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Top K Frequent Elements'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Top K Frequent Elements' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Top K Frequent Elements' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Top K Frequent Elements' AND tg.name = 'Hash Table'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Top K Frequent Elements' AND tg.name = 'Divide and Conquer'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Top K Frequent Elements' AND tg.name = 'Sorting'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Top K Frequent Elements' AND tg.name = 'Heap (Priority Queue)'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Top K Frequent Elements' AND tg.name = 'Bucket Sort'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Top K Frequent Elements' AND tg.name = 'Counting'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Top K Frequent Elements' AND tg.name = 'Quickselect'
ON CONFLICT DO NOTHING;

-- Find First and Last Position of Element in Sorted Array (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Find First and Last Position of Element in Sorted Array',
    'https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array',
    'MEDIUM',
    '# Find First and Last Position of Element in Sorted Array

Solve the LeetCode problem **Find First and Last Position of Element in Sorted Array**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Binary Search

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Find First and Last Position of Element in Sorted Array');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Find First and Last Position of Element in Sorted Array
}
',
       'Work through Find First and Last Position of Element in Sorted Array as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Find First and Last Position of Element in Sorted Array'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Find First and Last Position of Element in Sorted Array' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Find First and Last Position of Element in Sorted Array' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Find First and Last Position of Element in Sorted Array' AND tg.name = 'Binary Search'
ON CONFLICT DO NOTHING;

-- Same Tree (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Same Tree',
    'https://leetcode.com/problems/same-tree',
    'EASY',
    '# Same Tree

Solve the LeetCode problem **Same Tree**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Tree, Depth-First Search, Breadth-First Search, Binary Tree

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Same Tree');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Same Tree
}
',
       'Work through Same Tree as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Same Tree'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Same Tree' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Same Tree' AND tg.name = 'Tree'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Same Tree' AND tg.name = 'Depth-First Search'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Same Tree' AND tg.name = 'Breadth-First Search'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Same Tree' AND tg.name = 'Binary Tree'
ON CONFLICT DO NOTHING;

-- Students and Examinations (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Students and Examinations',
    'https://leetcode.com/problems/students-and-examinations',
    'EASY',
    '# Students and Examinations

Solve the LeetCode problem **Students and Examinations**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Database

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Students and Examinations');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Students and Examinations
}
',
       'Work through Students and Examinations as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Students and Examinations'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Students and Examinations' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Students and Examinations' AND tg.name = 'Database'
ON CONFLICT DO NOTHING;

-- Combine Two Tables (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Combine Two Tables',
    'https://leetcode.com/problems/combine-two-tables',
    'EASY',
    '# Combine Two Tables

Solve the LeetCode problem **Combine Two Tables**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Database

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Combine Two Tables');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Combine Two Tables
}
',
       'Work through Combine Two Tables as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Combine Two Tables'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Combine Two Tables' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Combine Two Tables' AND tg.name = 'Database'
ON CONFLICT DO NOTHING;

-- Design HashMap (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Design HashMap',
    'https://leetcode.com/problems/design-hashmap',
    'EASY',
    '# Design HashMap

Solve the LeetCode problem **Design HashMap**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Hash Table, Linked List, Design, Hash Function

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Design HashMap');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Design HashMap
}
',
       'Work through Design HashMap as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Design HashMap'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Design HashMap' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Design HashMap' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Design HashMap' AND tg.name = 'Hash Table'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Design HashMap' AND tg.name = 'Linked List'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Design HashMap' AND tg.name = 'Design'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Design HashMap' AND tg.name = 'Hash Function'
ON CONFLICT DO NOTHING;

-- Find the Smallest Divisor Given a Threshold (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Find the Smallest Divisor Given a Threshold',
    'https://leetcode.com/problems/find-the-smallest-divisor-given-a-threshold',
    'MEDIUM',
    '# Find the Smallest Divisor Given a Threshold

Solve the LeetCode problem **Find the Smallest Divisor Given a Threshold**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Array, Binary Search

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Find the Smallest Divisor Given a Threshold');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Find the Smallest Divisor Given a Threshold
}
',
       'Work through Find the Smallest Divisor Given a Threshold as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Find the Smallest Divisor Given a Threshold'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Find the Smallest Divisor Given a Threshold' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Find the Smallest Divisor Given a Threshold' AND tg.name = 'Array'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Find the Smallest Divisor Given a Threshold' AND tg.name = 'Binary Search'
ON CONFLICT DO NOTHING;

-- String Compression (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'String Compression',
    'https://leetcode.com/problems/string-compression',
    'MEDIUM',
    '# String Compression

Solve the LeetCode problem **String Compression**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Two Pointers, String

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'String Compression');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for String Compression
}
',
       'Work through String Compression as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'String Compression'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'String Compression' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'String Compression' AND tg.name = 'Two Pointers'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'String Compression' AND tg.name = 'String'
ON CONFLICT DO NOTHING;

-- Valid Parentheses (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Valid Parentheses',
    'https://leetcode.com/problems/valid-parentheses',
    'EASY',
    '# Valid Parentheses

Solve the LeetCode problem **Valid Parentheses**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: String, Stack

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Valid Parentheses');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Valid Parentheses
}
',
       'Work through Valid Parentheses as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Valid Parentheses'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Valid Parentheses' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Valid Parentheses' AND tg.name = 'String'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Valid Parentheses' AND tg.name = 'Stack'
ON CONFLICT DO NOTHING;

-- Sqrt(x) (freq 39.9)
INSERT INTO problems (title, url, difficulty, description, favorite, archived, created_at, updated_at)
SELECT
    'Sqrt(x)',
    'https://leetcode.com/problems/sqrtx',
    'EASY',
    '# Sqrt(x)

Solve the LeetCode problem **Sqrt(x)**.

- Company focus: Amazon (30-day list)
- Frequency: 39.9
- Related topics: Math, Binary Search

Open the link for the full problem statement and constraints.',
    FALSE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM problems p WHERE p.title = 'Sqrt(x)');

INSERT INTO solutions (problem_id, language, source_code, explanation, complexity, mistakes)
SELECT p.id,
       'java',
       'class Solution {
    // TODO: implement your Amazon interview solution for Sqrt(x)
}
',
       'Work through Sqrt(x) as an Amazon interview practice problem. Start with a brute-force approach, then optimize.',
       'Fill in time and space complexity after solving.',
       'Common pitfalls: edge cases, off-by-one errors, and forgetting constraints from the problem statement.'
FROM problems p WHERE p.title = 'Sqrt(x)'
  AND NOT EXISTS (SELECT 1 FROM solutions s WHERE s.problem_id = p.id);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT p.id, t.id
FROM problems p
CROSS JOIN topics t
WHERE p.title = 'Sqrt(x)' AND t.name = 'Amazon'
ON CONFLICT DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Sqrt(x)' AND tg.name = 'Math'
ON CONFLICT DO NOTHING;
INSERT INTO problem_tags (problem_id, tag_id)
SELECT p.id, tg.id
FROM problems p
CROSS JOIN tags tg
WHERE p.title = 'Sqrt(x)' AND tg.name = 'Binary Search'
ON CONFLICT DO NOTHING;

COMMIT;
