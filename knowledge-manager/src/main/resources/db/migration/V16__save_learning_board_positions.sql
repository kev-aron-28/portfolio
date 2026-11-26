ALTER TABLE learning_section
    ADD COLUMN board_x INTEGER,
    ADD COLUMN board_y INTEGER,
    ADD CHECK ((board_x IS NULL) = (board_y IS NULL));

ALTER TABLE learning_node
    ADD COLUMN board_x INTEGER,
    ADD COLUMN board_y INTEGER,
    ADD CHECK ((board_x IS NULL) = (board_y IS NULL));
